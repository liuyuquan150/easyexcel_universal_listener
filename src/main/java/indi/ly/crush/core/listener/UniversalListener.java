package indi.ly.crush.core.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import indi.ly.crush.aware.SpringApplicationContextHolder;
import indi.ly.crush.util.BaseValidatorUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * <h2>通用监听器</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
class UniversalListener<T, M>
        extends AnalysisEventListener<T> {
    /**
     * <p>
     *     数据消费商, 用于消费监听数据.
     * </p>
     */
    private final BiConsumer<M, T> dataConsumer;
    /**
     * <p>
     *     当前所监听工作簿中的工作表数量.
     * </p>
     */
    private final Integer sheetNumber;
    /**
     * <p>
     *     作为执行 {@link TransactionStatus#flush()} 方法的边界值,
     *     这个值的大小决定了批处理的执行效率. <br /> <br />
     *
     *
     *     本值受到 JVM 以及 DataBase 的相关参数影响,
     *     如果您对于这些参数的用法很清楚, 那么建议您可以通过调整这些参数来提高批处理的执行速度. <br /> <br />
     *
     *
     *     如果对于该值的设置比较随意, 很容易在大数据的情况导致{@link OutOfMemoryError 堆内存溢出}或是 The table 'xxx表' is full.
     * </p>
     */
    private final Integer flushValue;
    /**
     * <p>
     *     存储违背了业务校验的行数据. <br />
     *
     *     对于这种数据, 我们可以保存并返回给前端进行展示提醒, 方便用户感知导入结果.
     * </p>
     */
    private final List<ErrorRow> errorRows;
    /**
     * <p>
     *     工作表计数.
     * </p>
     */
    private Integer sheetCount;
    /**
     * <p>
     *    成功计数, 这代表有多少条数据是无业务错误的.
     * </p>
     */
    private Integer successCount;

    public UniversalListener(Class<T> type, Class<M> mapperType, BiConsumer<M, T> dataConsumer, Integer sheetNumber, Integer flushValue) {
        Assert.isTrue(type != null, "'type' is not a valid value.");
        Assert.isTrue(mapperType != null, "'mapperType' is not a valid value.");
        Assert.isTrue(dataConsumer != null, "'dataConsumer' is not a valid value.");
        Assert.isTrue(sheetNumber !=  null && sheetNumber > 0, "'sheetNumber' is not a valid value.");
        Assert.isTrue(flushValue !=  null && flushValue > 0, "'flushValue' is not a valid value.");

        this.dataConsumer = dataConsumer;
        this.sheetNumber = sheetNumber;
        this.flushValue = flushValue;
        this.errorRows = new LinkedList<>();

        this.sheetCount = 0;
        this.successCount = 0;

        this.table = new LinkedHashMap<>();
        ReflectionUtils.doWithFields(type, field -> {
            String fieldName = field.getName();
            ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
            int columnIndex = annotation.index() + 1;
            UniversalListener.this.table.put(fieldName, columnIndex);
        }, field -> {
            int modifiers = field.getModifiers();
            return (!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) && field.getAnnotation(ExcelProperty.class) != null;
        });

        SqlSessionFactory sqlSessionFactory = SpringApplicationContextHolder.getBean(SqlSessionFactory.class);
        this.sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        this.mapper = this.sqlSession.getMapper(mapperType);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
       Optional<ErrorRow> optional = BaseValidatorUtil.doValidate(
                data,
                (violation, errorRow) -> errorRow.set(context, violation, this.table),
                ErrorRow::new
        );

       if (optional.isPresent()) {
           ErrorRow errorRow = optional.get();
           this.errorRows.add(errorRow);
           return;
       }

        this.dataConsumer.accept(this.mapper, data);
        this.successCount++;
        // 达到边界时, 将所有 SQL 请求集中起来, 一次性将所有请求发送至数据库.
        if (this.successCount % this.flushValue == 0) {
            this.sqlSession.flushStatements();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 解析工作簿最后一个工作表的处理
        if (this.sheetNumber.equals(++this.sheetCount)) {
            // 处理剩余集中却未达到触发边界的 SQL 请求, 要保证所有请求全部发送至数据库.
            if (this.successCount % this.flushValue != 0) {
                this.sqlSession.flushStatements();
            }
            // 根据因违背业务校验的错误数据的有无决定事物提交还是回滚
            if (this.errorRows.size() > 0) {
                this.sqlSession.rollback(true);
            } else {
                this.sqlSession.commit(true);
            }

            // 记得将 SqlSession 回收进 SqlSessionFactory 连接池 中
            this.sqlSession.close();
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        if (exception instanceof ExcelDataConvertException e) {
            int rowIndex = e.getRowIndex() + 1;
            int columnIndex = e.getColumnIndex() + 1;
            String message = e.getMessage();
            LOG.error("An exception occurred while parsing row %d, column %d, because: '%s'".formatted(rowIndex, columnIndex, message));
        }
        this.sqlSession.rollback(true);
        this.sqlSession.close();
        // 如果发生了程序异常, 就终止整个监听行为.
        throw new RuntimeException("A program exception occurred while listening.", exception);
    }

    private static final Log LOG = LogFactory.getLog(UniversalListener.class);
    private final SqlSession sqlSession;
    private final M mapper;
    /**
     * <p>
     *     用于存储 '属性名称' 与 '该属性上所定义 {@link ExcelProperty#index()} 值' 的映射关系表.
     * </p>
     */
    private final Map<String, Integer> table;
    /**
     * <p>
     *     返回 {@link #errorRows}.
     * </p>
     *
     * @return {@link #errorRows}.
     */
    public List<ErrorRow> getErrorRows() {
        return this.errorRows;
    }
    /**
     * <p>
     *     是否有错误行数据.
     * </p>
     *
     * @return true: 没有错误行数据; false: 有错误行数据.
     */
    public Boolean hasErrorRows() {
        return !this.errorRows.isEmpty();
    }
}

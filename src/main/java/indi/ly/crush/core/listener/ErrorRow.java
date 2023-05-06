package indi.ly.crush.core.listener;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import indi.ly.crush.core.converter.ErrorColumnConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <h2>错误行</h2>
 * <p>
 *     用于记录{@link UniversalListener 监听器}中发生业务错误的行数据.
 * </p>
 *
 * @author 云上的云
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorRow
        implements Serializable {
    @Serial
    private static final long serialVersionUID = -6849794470754667710L;
    /**
     * <p>
     *     工作表名称.
     * </p>
     */
    @ExcelProperty(value = {"导入详情", "工作表名称"}, index = 0)
    private String sheetName;
    /**
     * <p>
     *     行索引.
     * </p>
     */
    @ExcelProperty(value = {"导入详情", "行号"}, index = 1)
    private Integer rowIndex;
    /**
     * <p>
     *     当前行.
     * </p>
     */
    @ExcelIgnore
    private Object row;
    /**
     * <p>
     *     当前行中的所有错误列.
     * </p>
     */
    @ExcelProperty(value = {"导入详情", "业务错误"}, index = 2, converter = ErrorColumnConverter.class)
    private List<ErrorColumn> errorColumns = new LinkedList<>();


    public <T> void set(AnalysisContext context, ConstraintViolation<? super T> violation, Map<String, Integer> table) {
        // 当前操作的工作表
        ReadSheetHolder readSheetHolder = context.readSheetHolder();
        // 当前工作表名称
        this.sheetName = readSheetHolder.getSheetName();
        // 当前操作单元格的所属行
        ReadRowHolder readRowHolder = context.readRowHolder();
        // 当前工作表中包含此单元格的行的行索引. Java 默认从 0 开始, Excel 文件默认从 1 开始, 所以这里是 (0 + 1) 才对.
        this.rowIndex = readRowHolder.getRowIndex() + 1;
        this.row = readRowHolder.getCurrentRowAnalysisResult();

        // 被校验的属性的名称
        String propertyName = violation.getPropertyPath().toString();
        // 校验不通过的消息提示
        String columnMessage = violation.getMessage();
        int columnIndex = table.get(propertyName);
        ErrorColumn errorColumn = new ErrorColumn(columnIndex, columnMessage);
        this.errorColumns.add(errorColumn);
    }
}

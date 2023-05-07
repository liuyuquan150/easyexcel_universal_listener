package indi.ly.crush.util;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.read.builder.AbstractExcelReaderParameterBuilder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.handler.WriteHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h2>EasyExcel 基础工具</h2>
 * <p>
 *     负责为 “{@link BaseEasyExcelReadUtil 读}” 和 “{@link BaseEasyExcelWriteUtil 写}” 的两个工具类提供一些公共的便捷方法.
 * </p>
 *
 * @author 云上的云
 * @see BaseEasyExcelReadUtil
 * @see BaseEasyExcelWriteUtil
 * @since 1.0
 * @formatter:off
 */
public abstract class BaseEasyExcelUtil {
	private static final Log LOG = LogFactory.getLog(BaseEasyExcelUtil.class);
	
	/**
	 * <p>
	 *     03 版 Excel 中的每个工作表的最大数据条目是 65535, 这反过来可以说明一个工作表能写入的最大数据条目是多少. <br /> <br />
	 *
	 *
	 *     请注意: Excel 每个工作表的起始行数是从 1 开始的, 而 Java 中集合或数组是从 0 开始的.
	 * </p>
	 */
	protected static final Integer WRITE_MAX_ENTRIES_03_VERSION = 65535;
	
	/**
	 * <p>
	 *     07 版 Excel 中的每个工作表的最大数据条目是 1048575, 这反过来可以说明一个工作表能写入的最大数据条目是多少. <br /> <br />
	 *
	 *
	 *     请注意: Excel 每个工作表的起始行数是从 1 开始的, 而 Java 中集合或数组是从 0 开始的.
	 * </p>
	 */
	protected static final Integer WRITE_MAX_ENTRIES_07_VERSION = 1048575;
	
	/**
	 * <p>
	 *     不同版本的 Excel 可以写入的最大条目.
	 * </p>
	 */
	protected static final Map<ExcelTypeEnum, Integer> WRITE_MAX_ENTRIES = new HashMap<>(2);
	static {
		WRITE_MAX_ENTRIES.put(ExcelTypeEnum.XLS, WRITE_MAX_ENTRIES_03_VERSION);
		WRITE_MAX_ENTRIES.put(ExcelTypeEnum.XLSX, WRITE_MAX_ENTRIES_07_VERSION);
	}
	
	/**
	 * <p>
	 *     一个 {@link Converter} 空集合.
	 * </p>
	 */
	protected static final List<? extends Converter<?>> EMPTY_CONVERTERS = List.of();
	
	/**
	 * <p>
	 *     一个 {@link WriteHandler} 空集合.
	 * </p>
	 */
	protected static final List<? extends WriteHandler> EMPTY_WRITE_HANDLERS = List.of();
	
	/**
	 * <p>
	 *     根据传入的模型字节码对象, 获取此模型对应的工作表中的头行数. <br /> <br />
	 *
	 *
	 *     我们导入 Excel 文件读取时, 里面每个工作表中的列头行数据是需要进行忽略的. <br />
	 *     {@link AbstractExcelReaderParameterBuilder#headRowNumber EasyExcel 在读取时会默认认为有一行列头, 然后从第 2 行开始读取}. <br />
	 *     如果模型中通过 {@link ExcelProperty} 注解或其它相关 API 方法设置的列头行数大于 1 行, 则 EasyExcel 在读取 Excel 文件时会将第 1 行之后的列头都读取到. <br /> <br />
	 *
	 *
	 *     对于工作表列头的读取, 一般来说都是希望进行忽略的. <br /> <br />
	 *
	 *
	 *     这是一段实际生效效果相同但更容易理解的代码:
	 *     <pre>{@code
	 *                  var maxHeadRows = 0;
	 *                  for (var declaredField : modelClass.getDeclaredFields()) {
	 *                      declaredField.setAccessible(true);
	 *                      if (declaredField.isAnnotationPresent(ExcelProperty.class)) {
	 *                          var excelPropertyAnnotation =
	 *                              declaredField.getAnnotation(ExcelProperty.class);
	 *                          var headRows = excelPropertyAnnotation.value().length;
	 *                          if (maxHeadRows < headRows) {
	 *                              maxHeadRows = headRows;
	 *                          }
	 *                      }
	 *                  }
	 *     }</pre>
	 * </p>
	 *
	 * @param modelClass 工作表对应的模型字节码对象.
	 * @return 模型对应的工作表中的头行数.
	 */
	public static Integer currentSheetHasSeveralRowHeaders(Class<?> modelClass) {
		int maxHeadRows = Arrays
							.stream(modelClass.getDeclaredFields())
							.peek(ReflectionUtils :: makeAccessible)
							.filter(field -> field.isAnnotationPresent(ExcelProperty.class))
							.map(field -> field.getAnnotation(ExcelProperty.class))
							.mapToInt(excelProperty -> excelProperty.value().length)
							.max()
							.orElse(0);
		LOG.debug("%s The number of rows in the column header of the corresponding worksheet is [%d]".formatted(modelClass.getName(), maxHeadRows));
		return maxHeadRows;
	}
}
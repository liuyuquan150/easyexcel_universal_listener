package indi.ly.crush.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.read.listener.ReadListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.IntStream;

/**
 * <h2>EasyExcel 读工具类</h2>
 * <p>
 *     提供了用于将 Excel 文件中的数据读取到内存的便捷方法.
 * </p>
 *
 * @author 云上的云
 * @since 1.0
 * @formatter:off
 */
public abstract class BaseEasyExcelReadUtil
		extends BaseEasyExcelUtil {
	private static final Log LOG = LogFactory.getLog(BaseEasyExcelReadUtil.class);
	
	//---------------------------------------------------------------------
	// easyRead: 简单读
	//---------------------------------------------------------------------
	
	public static void easyRead(
			ExcelReader excelReader, Class<?> modelClass, ReadListener<?> listener
	) {
		easyRead(excelReader, modelClass, true,EMPTY_CONVERTERS, List.of(listener));
	}
	
	public static void easyRead(
			ExcelReader excelReader, Class<?> modelClass, List<? extends ReadListener<?>> listeners
	) {
		easyRead(excelReader, modelClass, true, EMPTY_CONVERTERS, listeners);
	}
	
	public static void easyRead(
			ExcelReader excelReader, Class<?> modelClass, List<? extends Converter<?>> converter, ReadListener<?> listener
	) {
		easyRead(excelReader, modelClass, true,converter, List.of(listener));
	}
	
	public static void easyRead(
			ExcelReader excelReader, Class<?> modelClass, Converter<?> converter, List<? extends ReadListener<?>> listeners
	) {
		easyRead(excelReader, modelClass, true, List.of(converter), listeners);
	}
	
	public static void easyRead(
			ExcelReader excelReader, Class<?> modelClass, Converter<?> converter, ReadListener<?> listener
	) {
		easyRead(excelReader, modelClass, true, List.of(converter), List.of(listener));
	}
	
	public static void easyRead(
			ExcelReader excelReader, Class<?> modelClass, List<? extends Converter<?>> converters, List<? extends ReadListener<?>> listeners
	) {
		easyRead(excelReader, modelClass, true, converters, listeners);
	}
	
	public static void easyRead(
			ExcelReader excelReader, Class<?> modelClass, Boolean autoTrim, Converter<?> converter, ReadListener<?> listener
	) {
		easyRead(excelReader, modelClass, autoTrim, List.of(converter), List.of(listener));
	}
	
	/**
	 * <p>
	 *     简单读操作: {@link Class modelClass} 只允许传入一个, 可能一个 Excel 文件中的每个工作表都需要采用不同的 {@link Class modelClass}. <br /> <br />
	 *
	 *
	 *     请注意, 被读取的 Excel 文件中的每个工作表都将采用这一组结构 ————> 单个 {@link Class modelClass}、一组 {@link Converter}、一组 {@link ReadListener}
	 * </p>
	 * <br />
	 *
	 * <h3>{@link #easyRead(ExcelReader, Class, ReadListener)} && {@link #easyRead(ExcelReader, Class, List)}</h3>
	 * <p>
	 *     凡是这种不需要外界传入 {@link Converter} 的方法,
	 *     它实际上是配合实体类属性上 {@link ExcelProperty} 注解的 {@link ExcelProperty#converter()} 属性来使用的,
	 *     并不是说不需要传入 {@link Converter} 了.
	 * </p>
	 *
	 * @param excelReader Excel 文件读取器.
	 * @param modelClass  Excel 文件工作表对应的模型字节码对象.
	 * @param autoTrim    是否自动修剪(<em>去除空格</em>)工作表名称和内容.
	 * @param converters  读取 Excel 文件所用到的转换器.
	 * @param listeners   读取 Excel 文件所用到的监听器.
	 */
	private static void easyRead(
			ExcelReader excelReader, Class<?> modelClass, Boolean autoTrim,
			List<? extends Converter<?>> converters, List<? extends ReadListener<?>> listeners
	) {
		Assert.notNull(excelReader, "excelReader is null.");
		Assert.notNull(modelClass, "modelClass is null");
		Assert.notNull(autoTrim, "autoTrim is null");
		Assert.notNull(listeners, "listeners is null");
		Assert.notNull(converters, "converters is null");
		Assert.notEmpty(listeners, () -> "Please ensure that at least one listener is passed in.");
		
		// 要读取的 Excel 中的工作表个数
		int sheetNumber = excelReader
								.excelExecutor()
								.sheetList()
								.size();
		LOG.debug("The number of worksheets in the current workbook ------> " + sheetNumber);
		if (sheetNumber > 0) {
			try {
				IntStream
						// 迭代工作表个数, 这个迭代迁移数值作为工作表编号.
						.range(0, sheetNumber)
						.mapToObj(EasyExcel :: readSheet)
						.map(excelReaderSheetBuilder -> {
							excelReaderSheetBuilder
									.autoTrim(autoTrim)
									.head(modelClass)
									// 设置监听器在读取 Excel 文件每个工作表, 忽略每个工作表列头的解析.
									.headRowNumber(currentSheetHasSeveralRowHeaders(modelClass));
							listeners.forEach(excelReaderSheetBuilder :: registerReadListener);
							converters.forEach(excelReaderSheetBuilder :: registerConverter);
							return excelReaderSheetBuilder.build();
						})
						.peek(readSheet -> LOG.debug("ReadSheet ------> " + readSheet.toString()))
						// 采用每个工作表默认公用的配置, 读每个工作表.
						.forEachOrdered(excelReader :: read);
			} finally {
				excelReader.finish();
				LOG.debug("ExcelReader is closed IO");
			}
		}
	}
}

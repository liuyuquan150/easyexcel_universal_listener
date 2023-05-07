package indi.ly.crush.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <h2>EasyExcel 写工具类</h2>
 * <p>
 *     提供了用于将数据写入到 Excel 文件中的便捷方法.
 * </p>
 * <br />
 *
 * <h3>{@link #easyWrite03} 方法弃用原因</h3>
 * <p>
 *     03 版 Excel 中的每个工作表的最大数据条目是 65535 条, 这反过来可以说明一个工作表能写入的最大数据条目只能是 65535 条. <br />
 *
 *     如果超出这个条目限制, 程序就抛出异常 —————— {@link IllegalArgumentException Invalid row number (65536) outside allowable range (0..65535)}. <br />
 *
 *     这并不是由于程序本身原因(<em>如内存不足</em>)导致而抛出的异常, 而是 03 版 Excel 中的每个工作表的最大数据条目的峰值就是 65535.
 * </p>
 *
 * @author 云上的云
 * @since 1.0
 * @formatter:off
 */
public abstract class BaseEasyExcelWriteUtil
		extends BaseEasyExcelUtil {
	private static final Log LOG = LogFactory.getLog(BaseEasyExcelWriteUtil.class);
	static final String SHEET_STR = "Sheet";

	//---------------------------------------------------------------------
	// easyWrite: 简单写
	//---------------------------------------------------------------------

	@Deprecated(since = "1.0")
	public static <T> void easyWrite03(
			List<T> dataSource, String pathName, Class<?> headClass
	) {
		easyWrite(dataSource, pathName, SHEET_STR, headClass, ExcelTypeEnum.XLS, EMPTY_CONVERTERS, EMPTY_WRITE_HANDLERS);
	}

	public static <T> void easyWrite07(
			List<T> dataSource, String pathName, Class<?> headClass
	) {
		easyWrite(dataSource, pathName, SHEET_STR, headClass, ExcelTypeEnum.XLSX, EMPTY_CONVERTERS, EMPTY_WRITE_HANDLERS);
	}

	@Deprecated(since = "1.0")
	public static <T> void easyWrite03(
			List<T> dataSource, String pathName, Class<?> headClass,
			List<? extends Converter<?>> converters, List<? extends WriteHandler> writeHandlers
	) {
		easyWrite(dataSource, pathName, SHEET_STR, headClass, ExcelTypeEnum.XLS, converters, writeHandlers);
	}

	public static <T> void easyWrite07(
			List<T> dataSource, String pathName, Class<?> headClass,
			List<? extends Converter<?>> converters, List<? extends WriteHandler> writeHandlers
	) {
		easyWrite(dataSource, pathName, SHEET_STR, headClass, ExcelTypeEnum.XLSX, converters, writeHandlers);
	}

	@Deprecated(since = "1.0")
	public static <T> void easyWrite03(
			List<T> dataSource, String pathName, String sheetName, Class<?> headClass
	) {
		easyWrite(dataSource, pathName, sheetName, headClass, ExcelTypeEnum.XLS, EMPTY_CONVERTERS, EMPTY_WRITE_HANDLERS);
	}

	public static <T> void easyWrite07(
			List<T> dataSource, String pathName, String sheetName, Class<?> headClass
	) {
		easyWrite(dataSource, pathName, sheetName, headClass, ExcelTypeEnum.XLSX, EMPTY_CONVERTERS, EMPTY_WRITE_HANDLERS);
	}

	@Deprecated(since = "1.0")
	public static <T> void easyWrite03(
			List<T> dataSource, String pathName, String sheetName, Class<?> headClass,
			List<? extends Converter<?>> converters, List<? extends WriteHandler> writeHandlers
	) {
		easyWrite(dataSource, pathName, sheetName, headClass, ExcelTypeEnum.XLS, converters, writeHandlers);
	}

	public static <T> void easyWrite07(
			List<T> dataSource, String pathName, String sheetName, Class<?> headClass,
			List<? extends Converter<?>> converters, List<? extends WriteHandler> writeHandlers
	) {
		easyWrite(dataSource, pathName, sheetName, headClass, ExcelTypeEnum.XLSX, converters, writeHandlers);
	}

	/**
	 * <p>
	 *     简单写入操作: 将数据 1 次写入到 Excel 文件中的 1 个工作表内并生成该 Excel 文件.
	 * </p>
	 *
	 * @param dataSource    写入 Excel 文件的数据源.
	 * @param pathName      生成 Excel 文件的路径.
	 * @param sheetName     生成 Excel 文件内工作表的名称.
	 * @param headClass     Excel 文件列头的映射实体类的 {@link Class} 对象.
	 * @param fileType      生成 Excel 文件的类型.
	 * @param converters    将数据写入到 Excel 文件所用到的转换器.
	 * @param writeHandlers 将数据写入到 Excel 文件所用到的处理器.
	 * @param <T>           写入 Excel 文件中的数据源元素的数据类型.
	 * @apiNote 因为采取的方式是 1 次写入, 如果数据条目大于 Excel 版本的峰值条目, 则会抛出异常.
	 */
	private static <T> void easyWrite(
			List<T> dataSource, String pathName, String sheetName, Class<?> headClass, ExcelTypeEnum fileType,
			List<? extends Converter<?>> converters, List<? extends WriteHandler> writeHandlers
	) {
		Assert.notNull(dataSource, "dataSource is null.");
		Assert.notNull(pathName, "pathName is null");
		Assert.notNull(headClass, "headClass is null");
		Assert.notNull(sheetName, "sheetName is null");

		ExcelWriterSheetBuilder builder = EasyExcel
												.write(pathName, headClass)
												.inMemory(Boolean.TRUE)
												.excelType(fileType)
												.sheet(0,sheetName);
		converters.forEach(builder :: registerConverter);
		writeHandlers.forEach(builder :: registerWriteHandler);

		builder.doWrite(() -> dataSource);
		LOG.debug("ExcelWriter is closed IO");
	}

	//---------------------------------------------------------------------
	// flexibleWrite: 灵活写
	//---------------------------------------------------------------------

	@Deprecated(since = "1.0")
	public static <T> void flexibleWrite03(
			List<T> dataSource, String pathName, Class<?> headClass
	) {
		flexibleWrite(dataSource, pathName, headClass, SHEET_STR, ExcelTypeEnum.XLS, WRITE_MAX_ENTRIES_03_VERSION, EMPTY_CONVERTERS, EMPTY_WRITE_HANDLERS);
	}

	public static <T> void flexibleWrite07(
			List<T> dataSource, String pathName, Class<?> headClass
	) {
		flexibleWrite(dataSource, pathName, headClass, SHEET_STR, ExcelTypeEnum.XLSX, WRITE_MAX_ENTRIES_07_VERSION, EMPTY_CONVERTERS, EMPTY_WRITE_HANDLERS);
	}

	@Deprecated(since = "1.0")
	public static <T> void flexibleWrite03(
			List<T> dataSource, String pathName, Class<?> headClass,
			List<? extends Converter<?>> converters, List<? extends WriteHandler> writeHandlers
	) {
		flexibleWrite(dataSource, pathName, headClass, SHEET_STR, ExcelTypeEnum.XLS, WRITE_MAX_ENTRIES_03_VERSION, converters, writeHandlers);
	}

	public static <T> void flexibleWrite07(
			List<T> dataSource, String pathName, Class<?> headClass,
			List<? extends Converter<?>> converters, List<? extends WriteHandler> writeHandlers
	) {
		flexibleWrite(dataSource, pathName, headClass, SHEET_STR, ExcelTypeEnum.XLSX, WRITE_MAX_ENTRIES_07_VERSION, converters, writeHandlers);
	}

	@Deprecated(since = "1.0")
	public static <T> void flexibleWrite03(
			List<T> dataSource, String pathName, Class<?> headClass, String sheetName
	) {
		flexibleWrite(dataSource, pathName, headClass, sheetName, ExcelTypeEnum.XLS, WRITE_MAX_ENTRIES_03_VERSION, EMPTY_CONVERTERS, EMPTY_WRITE_HANDLERS);
	}

	public static <T> void flexibleWrite07(
			List<T> dataSource, String pathName, Class<?> headClass, String sheetName
	) {
		flexibleWrite(dataSource, pathName, headClass, sheetName, ExcelTypeEnum.XLSX, WRITE_MAX_ENTRIES_07_VERSION, EMPTY_CONVERTERS, EMPTY_WRITE_HANDLERS);
	}

	@Deprecated(since = "1.0")
	public static <T> void flexibleWrite03(
			List<T> dataSource, String pathName, Class<?> headClass, String sheetName,
			List<? extends Converter<?>> converters, List<? extends WriteHandler> writeHandlers
	) {
		flexibleWrite(dataSource, pathName, headClass, sheetName, ExcelTypeEnum.XLS, WRITE_MAX_ENTRIES_03_VERSION, converters, writeHandlers);
	}

	public static <T> void flexibleWrite07(
			List<T> dataSource, String pathName, Class<?> headClass, String sheetName,
			List<? extends Converter<?>> converters, List<? extends WriteHandler> writeHandlers
	) {
		flexibleWrite(dataSource, pathName, headClass, sheetName, ExcelTypeEnum.XLSX, WRITE_MAX_ENTRIES_07_VERSION, converters, writeHandlers);
	}
	
	/**
	 * <p>
	 *     灵活写入操作:
	 *     可以根据数据源的大小来决定每个工作表存储的数据条目是多少,
	 *     从而决定工作表的个数, 这就意味着不会存在超出工作表最大行数的可能.
	 * </p>
	 *
	 * @param dataSource            写入 Excel 文件的数据源.
	 * @param pathName              生成 Excel 文件的路径.
	 * @param sheetName             生成 Excel 文件内工作表的名称.
	 * @param headClass             Excel 文件列头的映射实体类的 {@link Class} 对象.
	 * @param fileType              生成 Excel 文件的类型.
	 * @param worksheetStorageEntry 生成 Excel 文件中每个工作表可存储的最大数据条目.
	 * @param converters            将数据写入到 Excel 文件所用到的转换器.
	 * @param writeHandlers         将数据写入到 Excel 文件所用到的处理器.
	 * @param <T>                   写入 Excel 文件中的数据源元素的数据类型.
	 */
	public static <T> void flexibleWrite(
			List<T> dataSource, String pathName, Class<?> headClass, String sheetName,
			ExcelTypeEnum fileType, Integer worksheetStorageEntry,
			List<? extends Converter<?>> converters, List<? extends WriteHandler> writeHandlers
	) {
		Assert.notNull(dataSource, "dataSource is null.");
		Assert.notNull(pathName, "pathName is null");
		Assert.notNull(headClass, "headClass is null");
		Assert.notNull(sheetName, "sheetName is null");
		Assert.notNull(fileType, "fileType is null");
		Assert.notNull(writeHandlers, "writeHandlers is null");
		Assert.notNull(converters, "converters is null");

		worksheetStorageEntry = checkWorksheetStorageEntry(worksheetStorageEntry, fileType, pathName);

		ExcelWriterBuilder builder = EasyExcel
										.write(pathName, headClass)
										.excelType(fileType);
		converters.forEach(builder :: registerConverter);
		writeHandlers.forEach(builder :: registerWriteHandler);

		splittingDataSourceIntoMultipleAndWritingToMultipleSheets(dataSource, sheetName, builder, worksheetStorageEntry);
	}
	
	private static final Integer DEFAULT_WORKSHEET_STORAGE_ENTRY = 200_000;
	
	/**
	 * <p>
	 *     工作表的存储条目不能大于版本峰值, {@link #splittingDataSourceIntoMultipleAndWritingToMultipleSheets 会导致数据源拆分后的条目仍然是大于版本峰值}, 在写入时会报异常并提示超出了允许的范围.
	 *     因此需要检查工作表的存储条目是否合理, 如果不合理则将其设置为合理. <br /> <br />
	 *
	 *
	 *     这是最早的实现代码:
	 *     <pre>{@code
	 *                  var cvsTemp = worksheetStorageEntry;
	 *                  worksheetStorageEntry = DEFAULT_WORKSHEET_STORAGE_ENTRY > worksheetStorageEntry
	 *                      ? DEFAULT_WORKSHEET_STORAGE_ENTRY : worksheetStorageEntry;
	 *                  if (Objects.equals(ExcelTypeEnum.XLSX,fileTpe)
	 *                      && pathName.endsWith(ExcelTypeEnum.XLSX.getValue())) {
	 *                      worksheetStorageEntry = worksheetStorageEntry >
	 *                          WRITE_MAX_ENTRIES_07_VERSION ? WRITE_MAX_ENTRIES_07_VERSION : worksheetStorageEntry;
	 *                  }
	 *                  else if (Objects.equals(ExcelTypeEnum.XLS,filTye)
	 *                      && pathName.endsWith(ExcelTypeEnum.XLS.getVale())) {
	 *                      worksheetStorageEntry = worksheetStorageEntry >
	 *                          WRITE_MAX_ENTRIES_03_VERSION ? WRITE_MAX_ENTRIES_03_VERSION : worksheetStorageEntry;
	 *                  }
	 *                  else if (Objects.equals(ExcelTypeEnum.CSV,filTye)
	 *                      && pathName.endsWith(ExcelTypeEnum.CSV.getVale())) {
	 *                      worksheetStorageEntry = cvsTemp;
	 *                  }
	 *                  else {
	 *                      throw new
	 *                      IllegalArgumentException(pathName + " and " + fileType + " do not match,
	 *                          Or" + pathName + "This format is not supported.");
	 *                  }
	 *     }</pre>
	 * </p>
	 *
	 * @param worksheetStorageEntry 生成 Excel 文件中每个工作表可存储的最大数据条目.
	 * @param fileType              生成 Excel 文件的类型.
	 * @param pathName              生成 Excel 文件的路径.
	 * @return 检查之后的工作表的存储条目.
	 */
	public static Integer checkWorksheetStorageEntry(Integer worksheetStorageEntry, ExcelTypeEnum fileType, String pathName) {
		int cvsTemp = worksheetStorageEntry;
		worksheetStorageEntry = DEFAULT_WORKSHEET_STORAGE_ENTRY > worksheetStorageEntry ? DEFAULT_WORKSHEET_STORAGE_ENTRY : worksheetStorageEntry;
		WRITE_MAX_ENTRIES.put(ExcelTypeEnum.CSV, cvsTemp);

		for (ExcelTypeEnum type : ExcelTypeEnum.values()) {
			if (Objects.equals(type, fileType) && pathName.endsWith(type.getValue())) {
				int writeMaxEntries  = WRITE_MAX_ENTRIES.get(type);
				return worksheetStorageEntry > writeMaxEntries ? writeMaxEntries : worksheetStorageEntry;
			}
		}
		
		throw new IllegalArgumentException("%s and %s do not match, Or %s This format is not supported.".formatted(pathName, fileType, pathName));
	}

	/**
	 * <p>
	 *     将一个数据源分割为多个并写入多个工作表中, 每个工作表的行数由使用者统一(<em>这是指每个工作表的行数都保持一致</em>)决定.
	 * </p>
	 *
	 * <h3>var reasonableWriteCount = (dataSize / maxRow) + (dataSize % maxRow == 0 ? 0 : 1);</h3>
	 * <p>
	 *     算出 “合理的写入次数”, 如下这种写法效果等价:
	 *     <pre>{@code
	 *                  var reasonableWriteCount =
	 *                  (dataSize % maxRow == 0) ? (dataSize / maxRow) : (dataSize / maxRow + 1);
	 *     }</pre>
	 * </p>
	 * <br />
	 *
	 * <h3>else 内的这段代码在做什么?</h3>
	 * <p>
	 *     根据不同 Excel 版本的最大峰值(<em>若是使用者自行设置了, 则不能大于版本峰值</em>)作为区间来拆分数据源,
	 *     将拆分好的多个小数据源在迭代中写入 Excel 中的多个工作表.
	 *
	 *     <pre>{@code
	 *                  List<T> splitData;
	 *                  var begin = 0;
	 *                  int end = maxRow;
	 *                  for(int i = 0; i < reasonableWriteCount; i++) {
	 *                      splitData = new ArrayList<>(maxRow - 1);
	 *                      if (maxRow > 1) {
	 *                          end--;
	 *                      }
	 *                      splitData.addAll(dataSource.subList(begin, Math.min(end, dataSize)));
	 *                      begin = end;
	 *                      end += maxRow;
	 *
	 *                      var writeSheet = EasyExcel
	 *                                          .writerSheet(i, sheetName + "_" + (i + 1))
	 *                                          .build();
	 *                      excelWriter.write(splitData, writeSheet);
	 *                  }
	 *     }</pre>
	 * </p>
	 * <br />
	 *
	 * <h3>else 内 for 循环这段代码曾经出现过的 Bug 记录</h3>
	 * <p>
	 *     如下是最早的代码形式:
	 *     <pre>{@code
	 *                  for(int i = 0; i < reasonableWriteCount; i++) {
	 *                      splitData = new ArrayList<>(maxRow - 1);
	 *                      splitData.addAll(dataSource.subList(begin, Math.min(end - 1, dataSize)));
	 *                      begin = end;
	 *                      end += maxRow;
	 *
	 *                      var writeSheet = EasyExcel
	 *                                          .writerSheet(i, sheetName + "_" + (i + 1))
	 *                                          .build();
	 *                      excelWriter.write(splitData, writeSheet);
	 *                  }
	 *     }</pre>
	 *     这段代码存在一个问题 —————> 会丢失每个工作表最后一行的写入. <br />
	 *     如果将
	 *     <pre>{@code splitData.addAll(dataSource.subList(begin, Math.min(end - 1, dataSize)));}</pre>
	 *     修改为
	 *     <pre>{@code splitData.addAll(dataSource.subList(begin, Math.min(end, dataSize)));}</pre>
	 *     这又会导致 {@link IllegalArgumentException}: 无效的行号（1048576）超出了允许的范围（0...1048575）. <br /> <br />
	 *
	 *
	 *     经过修改后(<em>现在的代码形式</em>)已解决 {@link List#subList}
	 *     截取导致在工作表峰值(<em>必须恰好是峰值</em>)多写时丢失最后一行数据的写入问题,
	 *     在避免超出范围的同时, 又保证了工作表最后一行数据的写入.
	 * </p>
	 *
	 * @param dataSource 写入 Excel 文件的数据源.
	 * @param sheetName  生成 Excel 文件内工作表的名称.
	 * @param builder    {@link ExcelWriter} 构建器(<em>将数据写入到 Excel 文件的调度者 ———— {@link ExcelWriter}</em>).
	 * @param maxRow     每个工作表能存储的最大数据条目.
	 * @param <T>        写入 Excel 文件中的数据源元素的数据类型.
	 */
	private static <T> void splittingDataSourceIntoMultipleAndWritingToMultipleSheets(
			List<T> dataSource, String sheetName, ExcelWriterBuilder builder, Integer maxRow
	) {
		ExcelWriter excelWriter = builder.build();
		int dataSize = dataSource.size();
		int reasonableWriteCount = (dataSize / maxRow) + (dataSize % maxRow == 0 ? 0 : 1);

		try {
			if (reasonableWriteCount == 1) {
				// 将数据写入到工作表的执行者
				WriteSheet writeSheet = EasyExcel
											.writerSheet(0, sheetName)
											.build();
				excelWriter.write(dataSource, writeSheet);
			} else {
				List<T> splitData;
				int begin = 0;
				int end = maxRow;
				for(int i = 0; i < reasonableWriteCount; i++) {
					splitData = new ArrayList<>(maxRow - 1);
					// 当写入每个工作表的最大行数是 1 时, 则不应自减 —————— 避免丢失每个工作表这一行的写入, writeToFileDataSource.subList(0, Math.min(0, dataSize)).
					if (maxRow > 1) {
						end--;
					}
					splitData.addAll(dataSource.subList(begin, Math.min(end, dataSize)));
					begin = end;
					end += maxRow;

					WriteSheet writeSheet = EasyExcel
												.writerSheet(i, sheetName + "_" + (i + 1))
												.build();
					excelWriter.write(splitData, writeSheet);
				}
			}
		} finally {
			if (excelWriter != null) {
				excelWriter.finish();
				LOG.debug("ExcelWriter is closed IO");
			}
		}
	}
}

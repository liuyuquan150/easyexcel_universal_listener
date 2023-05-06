package indi.ly.crush.core.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.support.ExcelTypeEnum;
import indi.ly.crush.util.BaseEasyExcelReadUtil;
import indi.ly.crush.util.BaseEasyExcelWriteUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * <h2>{@link UniversalListener} 助手</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
public final class UniversalListenerHelper {
    /**
     * <p>
     *     系统临时缓存目录.
     * </p>
     */
    private static final String TEMPORARY_DIRECTORY = System.getProperty("java.io.tmpdir");
    public static final String LOCAL_DATE_TIME_DEFAULT_FORMAT = "yyyy年MM月dd日 HH时mm分ss秒";

    public static <T, M> void executeListenerDoesNotGenerateReport(
            InputStream inputStream, Class<T> type, Class<M> mapperType, BiConsumer<M, T> dataConsumer, Integer flushValue
    ) {
        executeListener(inputStream, type, mapperType, dataConsumer, flushValue);
    }

    public static <T, M> void executeListenerAndGenerateReport(
            InputStream inputStream, Class<T> type, Class<M> mapperType, BiConsumer<M, T> dataConsumer, Integer flushValue,
            String marker
    ) {
        Assert.isTrue(StringUtils.hasText(marker), "'marker' is not a valid value.");

        executeListenerAndGenerateReport(
                inputStream,
                type,
                mapperType,
                dataConsumer,
                flushValue,
                () -> {
                    String now = LocalDateTime
                                        .now()
                                        .format(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_DEFAULT_FORMAT));
                    return TEMPORARY_DIRECTORY + String.join("-", marker, now, "错误详情" + ExcelTypeEnum.XLSX.getValue());
                }
        );
    }

    public static <T, M> void executeListenerAndGenerateReport(
            InputStream inputStream, Class<T> type, Class<M> mapperType, BiConsumer<M, T> dataConsumer, Integer flushValue,
            File reportFilePath
    ) {
        executeListenerAndGenerateReport(
                inputStream,
                type,
                mapperType,
                dataConsumer,
                flushValue,
                reportFilePath::toString
        );
    }

    private static <T, M> void executeListenerAndGenerateReport(
            InputStream inputStream, Class<T> type, Class<M> mapperType, BiConsumer<M, T> dataConsumer, Integer flushValue,
            Supplier<String> reportFilePathNameSupplier
    ) {
        UniversalListener<T, M> universalListener =
                executeListener(inputStream, type, mapperType, dataConsumer, flushValue);

        if (universalListener.hasErrorRows()) {
            String reportFilePathName = reportFilePathNameSupplier.get();
            if (StringUtils.hasText(reportFilePathName)) {
                throw new IllegalArgumentException("'reportFilePathName' is not a valid value.");
            }
            List<ErrorRow> errorRows = universalListener.getErrorRows();

            // 生成错误报告
            BaseEasyExcelWriteUtil.flexibleWrite07(errorRows, reportFilePathName, ErrorRow.class);
        }
    }

    private static <T, M> UniversalListener<T, M> executeListener(
            InputStream inputStream, Class<T> type, Class<M> mapperType, BiConsumer<M, T> dataConsumer, Integer flushValue
    ) {
        // excelReader 将 easyRead 中关闭
        ExcelReader excelReader = EasyExcel
                                    .read(inputStream)
                                    .build();

        int sheetNumber = excelReader
                                .excelExecutor()
                                .sheetList()
                                .size();

        UniversalListener<T, M> listener =
                new UniversalListener<>(type, mapperType, dataConsumer, sheetNumber, flushValue);

        BaseEasyExcelReadUtil.easyRead(excelReader,type, listener);

        return listener;
    }
}

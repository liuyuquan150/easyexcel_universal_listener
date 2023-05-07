package indi.ly.crush.core.handler;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import indi.ly.crush.core.listener.ErrorRow;
import indi.ly.crush.util.BaseEasyExcelUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h2>错误报告-工作表写入处理程序</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
public class ErrorReportSheetWriteHandler
        implements SheetWriteHandler {
    private final Integer dataTotal;
    private final Integer failuresDataCount;

    public ErrorReportSheetWriteHandler(Integer dataTotal, Integer failuresDataCount) {
        this.dataTotal = Objects.requireNonNull(dataTotal);
        this.failuresDataCount = Objects.requireNonNull(failuresDataCount);
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        Workbook workbook = sheet.getWorkbook();

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setWrapText(true);

        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        cellStyle.setFont(font);

        // 最后一个单元格的位置
        int lastColIndex = Arrays
                            .stream(ErrorRow.class.getDeclaredFields())
                            .map(f -> f.getAnnotation(ExcelProperty.class))
                            .filter(Objects::nonNull)
                            .map(ExcelProperty::index)
                            .max(Integer::compare)
                            .orElseThrow();

        int headerSize = BaseEasyExcelUtil.currentSheetHasSeveralRowHeaders(ErrorRow.class);
        Row newRow = sheet.createRow(headerSize);
        Stream
            .iterate(0, i -> ++i)
            .limit(lastColIndex + 1)
            .forEach(i -> {
                Cell newCell = newRow.createCell(i, CellType.STRING);
                // 创建空白单元格, 在合并的时候才不会导致边框线桓混乱
                newCell.setCellValue("");
                if (i == 0) {
                    newCell.setCellValue("导入结果: %d 条数据中有 %d 条校验未通过"
                            .formatted(ErrorReportSheetWriteHandler.this.dataTotal, ErrorReportSheetWriteHandler.this.failuresDataCount));
                }
                // 因为进行了合并单元格, 每个单元格的左边框线和右边框线设置都不会再生效, 只有合并之后单元格的右边框线设置可以生效.
                if (i == lastColIndex) {
                    cellStyle.setBorderRight(BorderStyle.THIN);
                }
                newCell.setCellStyle(cellStyle);
            });

        CellRangeAddress cellRangeAddress = new CellRangeAddress(
                headerSize,
                headerSize,
                0,
                lastColIndex
        );
        // 添加合并单元格区域
        sheet.addMergedRegionUnsafe(cellRangeAddress);
    }
}

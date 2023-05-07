package indi.ly.crush.core.handler;

import com.alibaba.excel.constant.OrderConstant;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.style.AbstractCellStyleStrategy;
import org.apache.poi.ss.usermodel.*;

/**
 * <h2>错误报告-单元格写入处理程序</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
public class ErrorReportCellWriteHandler
        extends AbstractCellStyleStrategy {

    @Override
    public int order() {
        // 如果值比已有实现类(如 FillStyleCellWriteHandler)定义的值小, 则样式会被优先级大的覆盖.
        return OrderConstant.FILL_STYLE + 1;
    }

    @Override
    protected void setHeadCellStyle(Cell cell, Head head, Integer relativeRowIndex) {}

    @Override
    protected void setContentCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
        Workbook workbook = cell
                            .getSheet()
                            .getWorkbook();

        Font font = workbook.createFont();
        font.setFontName("宋体");

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cell.setCellStyle(cellStyle);
    }
}

package indi.ly.crush.core.handler;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import indi.ly.crush.core.listener.ErrorRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * <h2>错误报告-行写入处理程序</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
public class ErrorReportRowWriteHandler
        implements RowWriteHandler {

    private static final String ERROR_COLUMNS_STR = "errorColumns";
    private static final Integer ERROR_COLUMNS_ROW_INDEX;
    private static final Integer ERROR_COLUMNS_CELL_INDEX;

    static {
        Field field =
                ReflectionUtils.findField(ErrorRow.class, ERROR_COLUMNS_STR, List.class);
        if (field == null) {
            throw new NullPointerException(("The attribute with the name '%s' is not found in the ErrorRow, " +
                    "so it is not possible to create an annotation message for the cell represented by the '%s' attribute")
                    .formatted(ERROR_COLUMNS_STR, ERROR_COLUMNS_STR));
        }

        ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
        if (annotation == null) {
            throw new NullPointerException(("No 'ExcelProperty' annotation is found on the attribute named '%s' in the ErrorRow, " +
                    "so no annotation information can be created for the cell represented by the '%s' attribute")
                    .formatted(ERROR_COLUMNS_STR, ERROR_COLUMNS_STR));
        }

        ERROR_COLUMNS_ROW_INDEX = annotation.value().length - 1;
        ERROR_COLUMNS_CELL_INDEX = annotation.index();
    }

    @Override
    public  void afterRowDispose(RowWriteHandlerContext context) {
        // 给 ErrorRow#errorColumns 所表示的的单元格添加批注
        if (isErrorColumnsHead(context)) {
            Sheet sheet = context
                            .getWriteSheetHolder()
                            .getSheet();

            Drawing<?> drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor xssfClientAnchor = new XSSFClientAnchor(
                    0,
                    0,
                    0,
                    0,
                    ERROR_COLUMNS_CELL_INDEX,
                    ERROR_COLUMNS_ROW_INDEX,
                    ERROR_COLUMNS_CELL_INDEX + 1,
                    ERROR_COLUMNS_ROW_INDEX + 1
            );
            // 创建 errorColumns 单元格的批注对象
            Comment microfilm = drawing.createCellComment(xssfClientAnchor);
            XSSFRichTextString xssfRichTextString = new XSSFRichTextString("格式:\n业务错误列的索引-业务错误列的中文描述");
            microfilm.setString(xssfRichTextString);

            Row errorColunmsRow = sheet.getRow(ERROR_COLUMNS_ROW_INDEX);
            Cell errorColunmsRowCell = errorColunmsRow.getCell(ERROR_COLUMNS_CELL_INDEX);
            errorColunmsRowCell.setCellComment(microfilm);
        }
    }

    static Boolean isErrorColumnsHead(RowWriteHandlerContext context) {
        return context.getHead() && context.getRow().getRowNum() == ERROR_COLUMNS_ROW_INDEX;
    }
}

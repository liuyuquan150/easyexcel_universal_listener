package indi.ly.crush.core.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import indi.ly.crush.core.listener.ErrorColumn;
import indi.ly.crush.core.listener.ErrorRow;

import java.util.List;

/**
 * <h2>{@link ErrorRow#getErrorColumns()} 属性转换器</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
public class ErrorColumnConverter
        implements Converter<List<ErrorColumn>> {

    static final String TEXT_LINE_FEED = "\n";

    @Override
    public Class<?> supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<?> convertToExcelData(List<ErrorColumn> value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // 列索引-列消息 \n 列索引-列消息 \n 列索引-列消息 \n ......
        return value
                .stream()
                .map(ErrorColumn::message)
                .reduce((s, s2) -> "%s%s%s".formatted(s, TEXT_LINE_FEED, s2))
                .map(stringValue -> new WriteCellData<String>(stringValue))
                .orElseGet(WriteCellData::new);
    }
}
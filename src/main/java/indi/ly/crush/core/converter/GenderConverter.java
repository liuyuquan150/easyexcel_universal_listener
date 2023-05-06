package indi.ly.crush.core.converter;

import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import indi.ly.crush.model.entity.Employee;
import indi.ly.crush.model.enums.GenderEnum;

/**
 * <h2>性别转换器</h2>
 *
 * @author 云上的云
 * @see Employee#getGender()
 * @since 1.0
 */
public class GenderConverter
        extends IntegerStringConverter {
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String value = cellData.getStringValue();
        Integer databaseValue = GenderEnum.getGenderEnum(
                genderEnum -> genderEnum.getDescription().equals(value),
                GenderEnum::getValue
        );

        if (databaseValue == null) {
            throw new IllegalArgumentException("Cell content [%s] is an illegitimate gender description.".formatted(value));
        }

        return databaseValue;
    }

    @Override
    public WriteCellData<?> convertToExcelData(Integer javaValue, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String description = GenderEnum.getGenderEnum(
                genderEnum -> genderEnum.getValue().equals(javaValue),
                GenderEnum::getDescription
        );
        return new WriteCellData<>(description);
    }
}

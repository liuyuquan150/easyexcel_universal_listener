package indi.ly.crush.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.converters.localdatetime.LocalDateTimeStringConverter;
import indi.ly.crush.core.converter.GenderConverter;
import indi.ly.crush.core.listener.UniversalListenerHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <h2>员工实体</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee
        implements Serializable {
    @Serial
    private static final long serialVersionUID = -6849794470754667710L;
    /**
     * <p>
     *     员工唯一标识符.
     * </p>
     */
    @ExcelIgnore
    private Long id;
    /**
     * <p>
     *     员工姓名.
     * </p>
     */
    @NotNull(message = "员工姓名不能为空")
    @ExcelProperty(value = {"员工主题1", "姓名"}, index = 0)
    private String name;
    /**
     * <p>
     *     员工性别.
     * </p>
     */
	@ExcelProperty(value = {"员工主题1", "性别"}, index = 1, converter = GenderConverter.class)
    private Integer gender;
    /**
     * <p>
     *     员工薪资.
     * </p>
     */
    @ExcelProperty(value = {"员工主题2", "薪资"}, index = 3)
    @NumberFormat(value = "#.##%")
    private BigDecimal salary;
    /**
     * <p>
     *     员工生日.
     * </p>
     */
	@ExcelProperty(value = {"员工主题3", "生日"}, index = 4, converter = LocalDateTimeStringConverter.class)
    @DateTimeFormat(value = UniversalListenerHelper.LOCAL_DATE_TIME_DEFAULT_FORMAT)
    private LocalDateTime birthday;
    /**
     * <p>
     *     员工入职时间.
     * </p>
     */
	@ExcelProperty(value = {"员工主题2", "入职时间"}, index = 2, converter = LocalDateTimeStringConverter.class)
    @DateTimeFormat(value = UniversalListenerHelper.LOCAL_DATE_TIME_DEFAULT_FORMAT)
    private LocalDateTime entryTime;
    /**
     * <p>
     *     这条员工记录的创建时间.
     * </p>
     */
    @ExcelIgnore
    private LocalDateTime gmtCreate;
    /**
     * <p>
     *     这条员工记录的修改时间.
     * </p>
     */
    @ExcelIgnore
    private LocalDateTime gmtModified;
}

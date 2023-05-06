package indi.ly.crush.mapper.api;

import indi.ly.crush.model.entity.Employee;
import org.apache.ibatis.annotations.Param;

/**
 * <h2>员工映射</h>
 *
 * @author 云上的云
 * @since 1.0
 */
public interface IEmployeeMapper {
    /**
     * <p>
     * 新增一条员工数据.
     * </p>
     *
     * @param employee 要添加到数据库中的员工数据.
     */
    void insert(@Param("employee") Employee employee);
}

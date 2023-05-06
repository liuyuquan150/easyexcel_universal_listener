package indi.ly.crush.service.api;

import java.io.InputStream;

/**
 * <h2>员工业务定义</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
public interface IEmployeeService {
    /**
     * <p>
     *     批量保存 {@link InputStream} 中的员工数据.
     * </p>
     *
     * @param fileContentType 上传文件的媒体类型.
     * @param originalName    上传文件的原始名称
     * @param inputStream     提供员工数据的流.
     */
    void batchSave(String fileContentType, String originalName, InputStream inputStream);
}

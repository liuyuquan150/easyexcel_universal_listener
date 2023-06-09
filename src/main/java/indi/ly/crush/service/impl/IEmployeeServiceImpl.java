package indi.ly.crush.service.impl;

import indi.ly.crush.core.listener.UniversalListenerHelper;
import indi.ly.crush.mapper.api.IEmployeeMapper;
import indi.ly.crush.model.entity.Employee;
import indi.ly.crush.model.enums.FileTypeEnum;
import indi.ly.crush.service.api.IEmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * <h2>员工业务实现</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
@Service(value = "IEmployeeServiceImpl")
public class IEmployeeServiceImpl
        implements IEmployeeService {
    @Override
    public void batchSave(String fileContentType, String originalName, InputStream inputStream) {
        if (FileTypeEnum.isNotExcelFile(StringUtils.getFilenameExtension(originalName), fileContentType)) {
            throw new RuntimeException("This is not 1 'xls' or 'xlsx' file.");
        }

        UniversalListenerHelper.executeListenerAndGenerateReport(
                inputStream,
                Employee.class,
                IEmployeeMapper.class,
                IEmployeeMapper::insert,
                // 100 万数据 ——— 30+s
                30000,
                StringUtils.getFilename(originalName)
        );
    }
}

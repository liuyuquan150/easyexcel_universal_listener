package indi.ly.crush.service.impl;

import indi.ly.crush.core.listener.UniversalListenerHelper;
import indi.ly.crush.mapper.api.IEmployeeMapper;
import indi.ly.crush.model.entity.Employee;
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
    private static final String XLS_FILE_EXTENSION_NAME = "xls";
    private static final String XLSX_FILE_EXTENSION_NAME = "xlsx";
    private static final String XLS_FILE_CONTEXT_TYPE = "application/vnd.ms-excel application/x-excel";
    private static final String XLSX_FILE_CONTEXT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    public void batchSave(String fileContentType, String originalName, InputStream inputStream) {
        if (isNotExcelFile(fileContentType, originalName)) {
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

    private static Boolean isExcelFile(String fileContentType, String originalName) {
        String filenameExtension = StringUtils.getFilenameExtension(originalName);
        return (XLS_FILE_CONTEXT_TYPE.equals(fileContentType) && XLS_FILE_EXTENSION_NAME.equalsIgnoreCase(filenameExtension))
                ||
               (XLSX_FILE_CONTEXT_TYPE.equals(fileContentType) && XLSX_FILE_EXTENSION_NAME.equalsIgnoreCase(filenameExtension));
    }

    private static Boolean isNotExcelFile(String fileContentType, String originalName) {
        return !isExcelFile(fileContentType, originalName);
    }
}

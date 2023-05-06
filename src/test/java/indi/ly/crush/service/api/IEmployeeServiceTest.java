package indi.ly.crush.service.api;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author 云上的云
 * @see IEmployeeService
 * @since 1.0
 */
@Log4j2
@SpringBootTest
class IEmployeeServiceTest {

    @Autowired
    IEmployeeService employeeService;

    @SneakyThrows
    @Test
    void batchSave() {
        File file = new File("E:\\employee2.xlsx");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            this.employeeService.batchSave(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "employee2.xlsx",
                    fileInputStream
            );
        }
    }
}
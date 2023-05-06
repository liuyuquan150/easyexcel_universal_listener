package indi.ly.crush.controller;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author 云上的云
 * @see EmployeeController
 * @since 1.0
 */
@Log4j2
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false, print = MockMvcPrint.NONE)
class EmployeeControllerTest {
    @Autowired
    MockMvc mockMvc;
    static final String XLSX_FILE_CONTEXT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @SneakyThrows(value = Exception.class)
    @Test
    void doFileImport() {
        Path path = Path.of("E:\\employee2.xlsx");
        Path fileName = path.getFileName();
        try (InputStream in = Files.newInputStream(path)) {
            MockMultipartFile multipartFile = new MockMultipartFile("file", fileName.toString(), XLSX_FILE_CONTEXT_TYPE, in);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                                                                .multipart("http://localhost:9999/api/v1/employee/import")
                                                                .file(multipartFile)
                                                                .contentType(MediaType.MULTIPART_FORM_DATA);
            this.mockMvc.perform(requestBuilder);
        }
    }
}
package indi.ly.crush.controller;

import indi.ly.crush.service.api.IEmployeeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * <h2>员工控制器</h2>
 *
 * @since 1.0
 * @author 云上的云
 */
@CrossOrigin
@RestController(value = "EmployeeController")
@RequestMapping(value = "/api/")
public class EmployeeController {
	final IEmployeeService iEmployeeServiceImpl;

	public EmployeeController(IEmployeeService iEmployeeServiceImpl) {
		this.iEmployeeServiceImpl = iEmployeeServiceImpl;
	}

	@PostMapping(value = "v1/employee/import")
	public void doFileImport(@RequestParam(value = "file") MultipartFile file) {
		String fileContentType = file.getContentType();
		String originalFilename = file.getOriginalFilename();
		InputStream inputStream;
		try {
			inputStream = file.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.iEmployeeServiceImpl.batchSave(fileContentType, originalFilename, inputStream);
	}
}

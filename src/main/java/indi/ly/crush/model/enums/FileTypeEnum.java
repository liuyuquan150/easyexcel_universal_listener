package indi.ly.crush.model.enums;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * <h2>文件类型枚举类</h2>
 *
 * @since 1.0
 * @author 云上的云
 * @formatter:off
 */
public enum FileTypeEnum {
	/**
	 * <p>
	 *    xls 文件.
	 * </p>
	 */
	XLS("xls", "application/vnd.ms-excel application/x-excel"),
	/**
	 * <p>
	 *     xlsx 文件.
	 * </p>
	 */
	XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	;

	/**
	 * <p>
	 *     文件扩展名, 如 a.xlsx 文件的扩展名是 xlsx.
	 * </p>
	 */
	private final String fileExtensionName;
	/**
	 * <p>
	 *     文件内容类型.
	 * </p>
	 */
	private final String fileContentType;
	
	
	FileTypeEnum(@NonNull String fileExtensionName, @NonNull String fileContentType) {
		this.fileExtensionName = Objects.requireNonNull(fileExtensionName);
		this.fileContentType = Objects.requireNonNull(fileContentType);
	}

	public String getFileExtensionName() {
		return fileExtensionName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public static Boolean is(@Nullable String fileExtensionName, @Nullable String fileContentType, @NonNull FileTypeEnum ... targets) {
		if (fileExtensionName == null || fileContentType == null) {
			return false;
		}

		Assert.isTrue(!ObjectUtils.isEmpty(targets), "'targets' must have a valid element.");

		return Arrays
				.stream(targets)
				.anyMatch(target -> {
					Assert.notNull(target, "'targets' has an element that is null.");
					String extensionName = target.getFileExtensionName();
					String contentType = target.getFileContentType();
					return extensionName.equalsIgnoreCase(fileExtensionName) && contentType.equals(fileContentType);
				});
	}

	public static Boolean isNot(@Nullable String fileExtensionName, @Nullable String fileContentType, @NonNull FileTypeEnum ... targets) {
		return !is(fileExtensionName, fileContentType, targets);
	}

	public static Boolean isExcelFile(@Nullable String fileExtensionName, @Nullable String fileContentType) {
		return is(fileExtensionName, fileContentType,FileTypeEnum.XLS, FileTypeEnum.XLSX);
	}

	public static Boolean isNotExcelFile(@Nullable String fileExtensionName, @Nullable String fileContentType) {
		return !isExcelFile(fileExtensionName, fileContentType);
	}
}

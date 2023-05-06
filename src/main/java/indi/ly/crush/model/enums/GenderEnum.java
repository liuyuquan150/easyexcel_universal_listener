package indi.ly.crush.model.enums;

import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <h2><span style="color: red;">性别枚举类</span></h2>
 *
 * @since 1.0
 * @author 云上的云
 */
public enum GenderEnum {
	/**
	 * <p>
	 *     男.
	 * </p>
	 */
	MAKE(0, "男"),
	/**
	 * <p>
	 *     女.
	 * </p>
	 */
	FEMALE(1, "女")
	;
	/**
	 * <p>
	 *     存储于数据库中的值.
	 * </p>
	 */
	private final Integer value;
	/**
	 * <p>
	 *     展示于前端的描述文本.
	 * </p>
	 */
	private final String description;

	GenderEnum(Integer value, String description) {
		this.value = value;
		this.description = description;
	}

	public Integer getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public static <R> R getGenderEnum(
			Predicate<GenderEnum> genderEnumPredicate,
			Function<GenderEnum, R> resultFunction
	) {
		Assert.notNull(genderEnumPredicate, "'genderEnumPredicate' is not a valid value.");
		Assert.notNull(resultFunction, "'resultFunction' is not a valid value.");

		return Arrays
				.stream(GenderEnum.values())
				.filter(genderEnumPredicate)
				.findFirst()
				.map(resultFunction)
				.orElse(null);
	}
}

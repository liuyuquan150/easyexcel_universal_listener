package indi.ly.crush.util;

import org.hibernate.validator.internal.engine.ValidatorImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * <h2>{@link Validator} 工具</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
public abstract class BaseValidatorUtil {
	/**
	 * <p>
	 *     {@link ValidatorImpl}.
	 * </p>
	 */
	protected static volatile Validator validator;
	static {
		initValidator();
	}

	public static <T, R> Optional<R> doValidate(
			T target, BiConsumer<ConstraintViolation<? super T>, ? super R> action,
			Supplier<? extends R> bearer, Class<?>... groups
	)  {
		return doValidate(validator, target, action, bearer, groups);
	}

	/**
	 * <p>
	 *     进行验证的基础方法抽象, 需要使用者提供{@link Validator 验证器}、验证结果的消费行为以及消费结果的承载容器.<br /> <br />
	 *
	 *
	 *     验证器的验证结果是以 {@link ConstraintViolation} 形式展现的, 该验证结果通过 action 函数让外界自定义消费方式,
	 *     最终的消费结果是体现在 bearer 上.
	 * </p>
	 *
	 * @param validator 验证器.
	 * @param target    验证目标.
	 * @param action    消费 target 验证结果的行为.
	 * @param bearer    承载 target 验证结果的容器.
	 * @param groups    验证目标所属组列表.
	 * @param <T>       target 的数据类型、
	 * @param <R>       bearer 的数据类型.
	 * @return 若 target 没有一个属性违背约束, 则返回 {@link Optional#empty()}, 否则返回含 bearer 的 {@link Optional}.
	 */
	public static <T, R> Optional<R> doValidate(
			Validator validator, T target, BiConsumer<ConstraintViolation<? super T>, ? super R> action,
			Supplier<? extends R> bearer, Class<?>... groups
	)  {
		Set<ConstraintViolation<T>> validateResult = Objects
														.requireNonNullElse(validator, BaseValidatorUtil.validator)
														.validate(target, groups);

		if (validateResult.isEmpty()) {
			return Optional.empty();
		}

		R result = Objects.requireNonNull(bearer.get());
		validateResult.forEach(tConstraintViolation -> action.accept(tConstraintViolation, result));
		return Optional.of(result);
	}

	/**
	 * <p>
	 *     初始化{@link #validator 验证器}.
	 * </p>
	 */
	private static void initValidator() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
		validatorFactory.close();
	}
}

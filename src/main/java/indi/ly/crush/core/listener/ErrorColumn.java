package indi.ly.crush.core.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <h2>错误列</h2>
 * <p>
 *     指未通过业务校验(<em>非程序主动抛出异常</em>)的属性数据.
 * </p>
 *
 * @author 云上的云
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorColumn
        implements Serializable {
    @Serial
    private static final long serialVersionUID = -6849794470754667710L;
    /**
     * <p>
     *     列索引.
     * </p>
     */
    private Integer columnIndex;
    /**
     * <p>
     *     列消息.
     * </p>
     */
    private String columnMessage;

    public String message() {
        return "%d-%s".formatted(this.columnIndex, this.columnMessage);
    }
}

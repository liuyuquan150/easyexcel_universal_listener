package indi.ly.crush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <h2>主启动应用程序</h2>
 * <p>
 *     现有开源组件: <a href="https://github.com/pig-mesh/excel-spring-boot-starter">excel-spring-boot-starter</a>
 * </p>
 *
 * @author 云上的云
 * @since 1.0
 */
@SpringBootApplication(scanBasePackages = "indi.ly.crush")
public class EasyexcelUniversalListenerApplication {
    public static void main(String[] args) {
        new SpringApplication(EasyexcelUniversalListenerApplication.class)
                .run(args);
    }
}

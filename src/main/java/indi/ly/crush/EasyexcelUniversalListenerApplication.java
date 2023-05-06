package indi.ly.crush;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <h2>主启动应用程序</h2>
 *
 * @author 云上的云
 * @since 1.0
 */
@MapperScan(value = "indi.ly.crush.mapper.api")
@SpringBootApplication(scanBasePackages = "indi.ly.crush")
public class EasyexcelUniversalListenerApplication {
    public static void main(String[] args) {
        new SpringApplication(EasyexcelUniversalListenerApplication.class)
                .run(args);
    }
}

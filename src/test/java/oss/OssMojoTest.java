package oss;


import java.io.File;

import org.junit.Test;

import io.github.lavy.plugin.oss.factory.FileStrategyFactory;
import io.github.lavy.plugin.oss.strategy.FileStrategy;

/**
 * @author <a href="lavyoung1325@outlook.com">lavy</a>
 * @date: 2025/7/20
 * @version: v1.0.0
 * @description: OssMojo
 */
public class OssMojoTest {

    @Test
    public void test() {
        FileStrategy fileStrategy = FileStrategyFactory.getFileStrategy("aliyun", System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"),
                System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"), "oss-cn-shanghai.aliyuncs.com");
        // 获取当前target目录下的jar包
        File file = new File("D:\\Project\\oss-maven-plugin\\target\\oss-maven-plugin-1.0.0-SNAPSHOT.jar");
        String url = fileStrategy.uploadFile(new File[]{file}, "test", "redbook-user");
    }
}

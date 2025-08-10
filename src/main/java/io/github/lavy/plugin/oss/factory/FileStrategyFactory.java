package io.github.lavy.plugin.oss.factory;


import io.github.lavy.plugin.oss.strategy.FileStrategy;
import io.github.lavy.plugin.oss.strategy.impl.AliyunOSSFileStrategy;
import io.github.lavy.plugin.oss.strategy.impl.MinioFileStrategy;

/**
 * @author <a href="lavyoung1325@outlook.com">lavy</a>
 * @date: 2025/7/20
 * @version: v1.0.0
 * @description: the file upload strategy
 */
public class FileStrategyFactory {


    /**
     * Get the file upload strategy
     *
     * @return File upload strategy
     */
    public static FileStrategy getFileStrategy(String type, String key, String secret, String endpoint) {
        if ("minio".equals(type)) {
            return new MinioFileStrategy(key, secret, endpoint);
        }
        // The default is Alibaba Cloud
        return new AliyunOSSFileStrategy(key, secret, endpoint);
    }
}

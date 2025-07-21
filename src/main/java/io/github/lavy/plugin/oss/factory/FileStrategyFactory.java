package io.github.lavy.plugin.oss.factory;


import io.github.lavy.plugin.oss.strategy.FileStrategy;
import io.github.lavy.plugin.oss.strategy.impl.AliyunOSSFileStrategy;
import io.github.lavy.plugin.oss.strategy.impl.MinioFileStrategy;

/**
 * @author <a href="lavyoung1325@outlook.com">lavy</a>
 * @date: 2025/7/20
 * @version: v1.0.0
 * @description: 文件上传策略工厂
 */
public class FileStrategyFactory {


    /**
     * 获取文件上传策略
     *
     * @return 文件上传策略
     */
    public static FileStrategy getFileStrategy(String type, String key, String secret, String endpoint) {
        if ("minio".equals(type)) {
            return new MinioFileStrategy(key, secret, endpoint);
        }
        // 默认为阿里云
        return new AliyunOSSFileStrategy(key, secret, endpoint);
    }
}

package io.github.lavy.plugin.oss.strategy.impl;


import java.io.File;
import java.nio.file.Files;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import io.github.lavy.plugin.oss.strategy.FileStrategy;

/**
 * @author <a href="lavyoung1325@outlook.com">lavy</a>
 * @date: 2025/7/20
 * @version: v1.0.0
 * @description: 阿里云OSS文件上传策略
 */
public class AliyunOSSFileStrategy implements FileStrategy {
    private final Log logger = new SystemStreamLog();
    private final OSS ossClient;

    public AliyunOSSFileStrategy(String key, String secret, String endpoint) {
        DefaultCredentialProvider credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(
                key, secret);
        // 创建 OSSClient 实例
        ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);
    }

    @Override
    public void uploadFile(File[] files, String path, String bucketName) {
        if (files == null || files.length == 0 || bucketName == null || bucketName.isEmpty()) {
            throw new RuntimeException("上传文件失败：参数为空");
        }
        if (path != null && path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String firstFileUrl = "";
        for (File file : files) {
            if (file == null || !file.exists() || !file.canRead()) {
                logger.warn("跳过无效文件：" + (file != null ? file.getName() : "null"));
                continue;
            }
            try {
                logger.info("上传文件：" + file.getName());
                ossClient.putObject(bucketName, path + "/" + file.getName(), Files.newInputStream(file.toPath()));
                logger.info("上传文件成功：" + file.getName());
            } catch (Exception e) {
                logger.error("上传文件失败：" + e);
            }
        }
    }

    @Override
    public void shoutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}

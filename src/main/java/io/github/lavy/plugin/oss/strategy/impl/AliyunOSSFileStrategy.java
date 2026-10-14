package io.github.lavy.plugin.oss.strategy.impl;


import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;

import io.github.lavy.plugin.oss.exception.FileException;
import io.github.lavy.plugin.oss.strategy.FileStrategy;
import io.github.lavy.plugin.oss.strategy.ObjectNameBuilder;

/**
 * @author <a href="lavyoung1325@outlook.com">lavy</a>
 * @date: 2025/7/20
 * @version: v1.0.0
 * @description: 阿里云OSS文件上传策略
 */
public class AliyunOSSFileStrategy implements FileStrategy {
    private final Logger logger = LoggerFactory.getLogger(AliyunOSSFileStrategy.class);
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
            throw new FileException("File upload failed: The parameter is empty");
        }
        long startTime = System.currentTimeMillis();
        int failureCount = 0;
        Exception firstFailure = null;
        for (File file : files) {
            if (file == null || !file.exists() || !file.canRead()) {
                logger.warn("Skip invalid files: {}", (file != null ? file.getName() : "null"));
                continue;
            }
            String objectName = ObjectNameBuilder.build(path, file.getName());
            try {
                logger.info("Upload file: {}, size: {}, oss path: {}", file.getName(), Files.size(file.toPath()), objectName);
                try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                    ossClient.putObject(bucketName, objectName, inputStream);
                }
                logger.info("Upload file success: {}", file.getName());
            } catch (Exception e) {
                failureCount++;
                if (firstFailure == null) {
                    firstFailure = e;
                }
                logger.error("Upload file failure: {}", e.getMessage(), e);
            }
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        logger.info("Upload file complete, total time: {}H:{}m:{}s", totalTime / 3600000, totalTime / 60000 % 60, totalTime / 1000 % 60);
        if (failureCount > 0) {
            throw new FileException("File upload failed, failure count: " + failureCount, firstFailure);
        }
    }

    @Override
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}

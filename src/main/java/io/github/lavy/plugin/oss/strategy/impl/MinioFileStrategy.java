package io.github.lavy.plugin.oss.strategy.impl;


import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.lavy.plugin.oss.exception.FileException;
import io.github.lavy.plugin.oss.strategy.ObjectNameBuilder;
import io.github.lavy.plugin.oss.strategy.FileStrategy;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.PutObjectBaseArgs;

/**
 * @author <a href="lavyoung1325@outlook.com">lavy</a>
 * @date: 2025/7/20
 * @version: v1.0.0
 * @description: Minio文件上传策略
 */
public class MinioFileStrategy implements FileStrategy {
    private final Logger logger = LoggerFactory.getLogger(MinioFileStrategy.class);

    private final MinioClient minioClient;

    public MinioFileStrategy(String key, String secret, String endpoint) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(key, secret)
                .build();
    }

    /**
     * 上传文件
     *
     * @param files 文件
     * @param bucketName 存储桶名称
     */
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
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .contentType("application/octet-stream")
                                    .bucket(bucketName)
                                    .stream(inputStream, file.length(), PutObjectBaseArgs.MIN_MULTIPART_SIZE)
                                    .object(objectName)
                                    .build());
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
        if (minioClient != null) {
            try {
                minioClient.close();
            } catch (Exception e) {
                logger.error("Failed to close the Minio client", e);
            }
        }
    }
}

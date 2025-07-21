package io.github.lavy.plugin.oss.strategy.impl;


import java.io.File;
import java.nio.file.Files;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

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
    private final Log logger = new SystemStreamLog();

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
     * @return 文件访问地址
     */
    /**
     * 上传文件
     *
     * @param files 文件
     * @param bucketName 存储桶名称
     * @return 第一个文件的访问地址（示例）
     */
    @Override
    public String uploadFile(File[] files, String path, String bucketName) {
        if (files == null || files.length == 0 || bucketName == null || bucketName.isEmpty()) {
            throw new RuntimeException("上传文件失败：参数为空");
        }
        String firstFileUrl = "";
        for (File file : files) {
            if (file == null || !file.exists() || !file.canRead()) {
                logger.warn("跳过无效文件：" + (file != null ? file.getName() : "null"));
                continue;
            }
            try {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .contentType("application/octet-stream")
                                .bucket(bucketName)
                                .stream(Files.newInputStream(file.toPath()), file.length(),
                                        PutObjectBaseArgs.MIN_MULTIPART_SIZE)
                                .object(path + "/" + file.getName())
                                .build());
                logger.info("上传文件成功：{}" + file.getName());
            } catch (Exception e) {
                logger.error("上传文件失败：{}，错误：{}" + e);
            }
        }
        return firstFileUrl;
    }

    @Override
    public void shoutdown() {
        if (minioClient != null) {
            try {
                minioClient.close();
            } catch (Exception e) {
                logger.error("关闭Minio客户端失败：" + e);
            }
        }
    }
}

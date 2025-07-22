package io.github.lavy.plugin.oss.strategy;

import java.io.File;
import java.util.List;


/**
 * @author <a href="lavyoung1325@outlook.com">lavy</a>
 * @date: 2025/7/20
 * @version: v1.0.0
 * @description: 文件上传策略
 */
public interface FileStrategy {


    /**
     * 文件上传
     *
     * @param files 文件
     * @param bucketName 存储桶名称
     */
    void uploadFile(File[] files, String path, String bucketName);

    /**
     * 关闭连接
     */
    default void shoutdown(){};
}

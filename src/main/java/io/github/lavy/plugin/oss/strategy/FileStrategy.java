package io.github.lavy.plugin.oss.strategy;

import java.io.File;
import java.util.List;


/**
 * @author <a href="lavyoung1325@outlook.com">lavy</a>
 * @date: 2025/7/20
 * @version: v1.0.0
 * @description: file strategy
 */
public interface FileStrategy {


    /**
     * upload file
     *
     * @param files the upload files
     * @param bucketName buckName
     */
    void uploadFile(File[] files, String path, String bucketName);

    /**
     * 关闭连接
     */
    default void shoutdown(){};
}

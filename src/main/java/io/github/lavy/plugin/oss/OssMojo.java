package io.github.lavy.plugin.oss;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.lavy.plugin.oss.factory.FileStrategyFactory;
import io.github.lavy.plugin.oss.strategy.FileStrategy;

/**
 * OssMojo
 *
 * @author <a href="lavyoung1325@outlook.com">lavy</a>
 * @date: 2025/7/20
 * @version: v1.0.0
 * @description: OssMojo
 */
@Mojo(name = "oss-upload", defaultPhase = LifecyclePhase.PACKAGE)
public class OssMojo extends AbstractMojo {

    private static final Logger logger = LoggerFactory.getLogger(OssMojo.class);

    @Parameter(property = "oss.provider", required = true, defaultValue = "aliyun")
    private String provider;
    @Parameter(property = "oss.endpoint", required = true)
    private String endpoint;
    @Parameter(property = "oss.accessKeyId", required = true)
    private String accessKeyId;
    @Parameter(property = "oss.accessKeySecret", required = true)
    private String accessKeySecret;
    @Parameter(property = "oss.bucketName", required = true)
    private String bucketName;
    @Parameter(property = "oss.ossPath", defaultValue = "")
    private String ossPath;
    @Parameter(property = "oss.filePath", defaultValue = "")
    private File filePath;
    @Parameter(property = "oss.fileSuffix", defaultValue = "jar")
    private String fileSuffix;

    /**
     * Execute.
     *
     * @throws MojoExecutionException the mojo execution exception
     * @throws MojoFailureException the mojo failure exception
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // 参数校验
        if (filePath == null || !filePath.exists() || !filePath.isDirectory()) {
            throw new MojoFailureException("Invalid file path: must be a valid directory");
        }
        if (bucketName == null || bucketName.isEmpty()) {
            throw new MojoFailureException("Bucket name is not specified");
        }
        FileStrategy fileStrategy =
                FileStrategyFactory.getFileStrategy(provider, accessKeyId, accessKeySecret, endpoint);
        // logger start
        logger.info("Starting file upload process from: {}", filePath.getAbsolutePath());

        // 1. scan filePath files
        File[] files = filePath.listFiles((dir, name) -> name.endsWith(fileSuffix));
        if (files == null || files.length == 0) {
            logger.warn("No files found matching the criteria.");
            return;
        }

        // 2. oss upload
        fileStrategy.uploadFile(files, ossPath, bucketName);
        // 3. shutdown client
        fileStrategy.shoutdown();
        logger.info("File upload process completed.");
    }
}

package com.atguigu.gmall.common.fastdfs;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 自定义文件服务功能案例小样
 */
@Service
public class DemoCaseWithFile {
    static {
        try {
            // 第一步: 加载classpath下配置文件
            ClassPathResource resource = new ClassPathResource("tracker.conf");

            // 第二步: 初始化连接或者初始化FastDFS对象
            ClientGlobal.init(resource.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义文件上传
     * @param file
     * @return
     * @throws Exception
     */
    public String upload(MultipartFile file) throws Exception {
//        // 第三步: 获取tracker
//        TrackerClient trackerClient = new TrackerClient();
//
//        // 第四步: 获取连接或者获取trackerService
//        TrackerServer connection = trackerClient.getConnection();
//
//        // 第五步: 获取storage
//        StorageClient storageClient = new StorageClient(connection, null);

        // 获取storageClient对象
        StorageClient storageClient = getStorageClient();

        // 第六步: 执行文件操作
        /**
         * 1. 文件的字节数组
         * 2. 文件的扩展名
         * 3. 辅助参数,时间,地点,位置
         */
        String[] strings = storageClient.upload_file(file.getBytes(), StringUtils.getFilenameExtension(file.getOriginalFilename()), null);

        // 第七步: 返回结果
        /**
         * strings: 0,1
         * 0. 组名group1
         * 1. 文件全量路径名
         */
        return strings[0] + "/" + strings[1];
    }

    /**
     * 自定义文件的下载
     */
    public byte[] download(String groupName, String path) throws Exception {
        StorageClient storageClient = getStorageClient();

        // 执行文件下载
        byte[] bytes = storageClient.download_file(groupName, path);

        // 返回结果
        return bytes;
    }

    /**
     * 自定义文件的删除
     */
    public boolean delete(String groupName, String path) throws Exception {
        // 调用getStorageClient()方法, 获取storageClient对象
        StorageClient storageClient = getStorageClient();

        // 执行文件删除
        // i大于或等于0, 删除成功
        int i = storageClient.delete_file(groupName, path);

        // 返回结果
        // 三目运算拿到boolean值
        return i >= 0 ? true : false;

    }

    /**
     * 获取storageClient
     * @return
     * @throws IOException
     */
    private StorageClient getStorageClient() throws IOException {
        // 获取tracker
        TrackerClient trackerClient = new TrackerClient();

        // 获取连接
        TrackerServer trackerServer = trackerClient.getConnection();

        // 获取storage
        return new StorageClient(trackerServer, null);
    }

}

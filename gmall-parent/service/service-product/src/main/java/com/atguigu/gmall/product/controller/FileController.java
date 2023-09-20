package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.fastdfs.DemoCaseWithFile;
import com.atguigu.gmall.common.fastdfs.FastDfsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理的controller
 */
@RestController
@RequestMapping("/admin/product")
public class FileController {

    @Autowired
    private FastDfsClient fastDfsClient;

    @Autowired
    private DemoCaseWithFile demoCaseWithFile;

    @Value("${fileServer.url}")
    private String url;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping(value = "/fileUpload")
    public String fileUpload(MultipartFile file) throws Exception {
        // 第一种方式: 官方定义方式文件上传功能
//        // 第一步: 获取tracker配置文件
//        ClassPathResource resource = new ClassPathResource("tracker.conf");
//
//        // 第二步: 初始化连接
//        ClientGlobal.init(resource.getPath());
//
//        // 第三步: 获取tracker
//        TrackerClient trackerClient = new TrackerClient();
//
//        // 第四步: 获取连接
//        TrackerServer trackerServiceConnection = trackerClient.getConnection();
//
//        // 第五步: 获取storage
//        StorageClient storageClient = new StorageClient(trackerServiceConnection, null);
//
//        // 第六步: 执行文件上传
//        /**
//         * 执行文件上传的三个参数:
//         * 1. 文件的字节类型数组
//         * 2. 文件扩展名
//         * 3. 附加参数，如图片水印信息, 时间, 地点
//         */
//        String filenameExtension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
//
//        /**
//         * strings:
//         * strings包含两个值, string[0], string[1]
//         * string[0], 组名group1
//         * strings[1], 全量路径名M00/00/00/123.jpg
//         */
//        String[] strings = storageClient.upload_file(multipartFile.getBytes(), filenameExtension, null);
//
//        // 第七步: 返回文件上传的结果, 返回结果格式:
//        // group1/M00/00/01/wKjIgGFkWwaAdhqYAAJ5GiY3A9Q706.png
//        return strings[0] + "/" + strings[1];

        // 第二种方式: 使用service-util中定义的文件管理服务
        String path = fastDfsClient.upload(file);

//        // 第三种方式: 使用自定义文件管理功能
//        // path: strings[0] + "/"  + strings[1], groupName/全量路径名
//        String path = demoCaseWithFile.upload(multipartFile);

//        // 返回值:
//        // path: strings[0] + "/"  + strings[1], groupName/全量路径名
//        return path;

        // 返回结果
        // url: ip:port
        // path: strings[0] + "/"  + strings[1], groupName/全量路径名
        return url + path;

    }
}

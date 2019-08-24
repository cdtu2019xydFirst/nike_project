package com.yadong.nike.manage.util;


import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/10 | 9:05
 **/
public class PmsUploadUtil {

    public static String uploadImage(MultipartFile multipartFile) {

        String imgUrl = "http://192.168.255.140";
        /*上传图片到服务器*/
        /*获取配置文件路径this.getClass().getResource("/tracker.conf").getFile();*/
        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();
        try {
            ClientGlobal.init(tracker);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        TrackerClient trackerClient=new TrackerClient();
        TrackerServer trackerServer= null;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageClient storageClient=new StorageClient(trackerServer,null);
        try {
            /*获得上传的二进制对象*/
            byte[] bytes = multipartFile.getBytes();
            /*获得图片原始全名：xxx.jpg   xxx.png ......*/
            String originalFilename = multipartFile.getOriginalFilename();
            System.out.println(originalFilename);
            /*获取文件后缀名  xxx.xxx.xxx.jpg  xxx.xxx.xxx.png .......*/
            int i1 = originalFilename.lastIndexOf(".");//获取最后一个点的位置
            String extName = originalFilename.substring(i1 + 1);//最后一个点加一就是文件后缀名
            String[] upload_file = storageClient.upload_file(bytes, extName, null);
            for (int i = 0; i < upload_file.length; i++) {
                String s = upload_file[i];
                imgUrl += "/" + s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return imgUrl;
    }
}

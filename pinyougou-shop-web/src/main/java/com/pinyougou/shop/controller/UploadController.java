package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

/**
 * 文件上传
 * @author cai
 *
 */
@RestController
public class UploadController {

	//获取服务器的ip地址
	@Value("${FILE_SERVER_URL}")
	private String file_service_url;
	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {
		//获取文件的后缀名
		String string = file.getOriginalFilename();
		String extName = string.substring(string.lastIndexOf(".")+1);
		try {
			//创建fastDFSC客户端
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			//执行文件上传
			String path = fastDFSClient.uploadFile(file.getBytes(), extName);
			//拼接完整的url
			String url = file_service_url+path;
			return new Result(true, url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
	}
}

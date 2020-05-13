package com.sysm.back.service;

import com.sysm.back.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ImgService {
    Map<String, Object> upload(MultipartFile file, String type,String url);

    Image getImage(String fileName) throws IOException;

    Map<String, Object> getUrlList(Integer pageNum,Integer pageSize);

    Map<String, Object> delectImage(Integer id);
}

package com.sysm.back.contorller;

import com.sysm.back.VO.ResultVO;
import com.sysm.back.exception.BusinessException;
import com.sysm.back.model.Image;
import com.sysm.back.service.ImgService;
import com.sysm.back.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author shidun
 */
@Slf4j
@RestController
@RequestMapping("/image")
@Api(tags = "图片上传")
public class ImageController {
    @Autowired
    ImgService imgService;

    /**
     * 文件上传
     * 1. 文件上传后的文件名
     * 2. 上传后的文件路径 , 当前年月日时 如:2018060801  2018年6月8日 01时
     * 3. file po 类需要保存文件信息 ,旧名 ,大小 , 上传时间 , 是否删除 ,
     *
     * @param file
     * @param request
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request, @RequestParam("name") String type, @RequestParam("url") String url) {
        // 判断文件是否为空
        if (file.isEmpty()) {
            return ResultVO.failure("文件不能为空");
        }
        try {
            return imgService.upload(file, type,url);
        } catch (BusinessException e) {
            new BusinessException("上传失败");
        }
        return ResultVO.failure("");
    }

    /**
     * 文件查看
     */
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    @ApiOperation("文件查看")
    public ResponseEntity<InputStreamResource> view(@RequestParam("fileName") String fileName) {
        Image fileInfo = null;
        try {
            fileInfo = imgService.getImage(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileInfo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders header = new HttpHeaders();
        if (FileUtils.match(fileInfo.getFileName(), "jpg", "png", "gif", "bmp", "tif")) {
            header.setContentType(MediaType.IMAGE_JPEG);
        } else if (FileUtils.match(fileInfo.getFileName(), "html", "htm")) {
            header.setContentType(MediaType.TEXT_HTML);
        } else if (FileUtils.match(fileInfo.getFileName(), "pdf")) {
            header.setContentType(MediaType.APPLICATION_PDF);
        } else {
            header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        header.add("X-Filename", fileInfo.getFileName());
        header.add("X-MD5", fileInfo.getMd5());

        return new ResponseEntity<>(new InputStreamResource(fileInfo.getContent()), header, HttpStatus.OK);
    }

    @PostMapping("/getUrlList")
    @ApiOperation("图片列表")

    public Map<String, Object> getUrlList(@RequestParam(value = "pageNum", required = true) Integer pageNum, @RequestParam(value = "pageSize", required = true) Integer pageSize) {

        return imgService.getUrlList(pageNum, pageSize);

    }

    @PostMapping("/dImage")
    @ApiOperation("删除广告")

    public Map<String, Object> delectImage(@RequestParam(value = "id", required = true) Integer id) {

        return imgService.delectImage(id);

    }
}

package com.sysm.back.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sysm.back.VO.ResultVO;
import com.sysm.back.dao.ImageMapper;
import com.sysm.back.model.Image;
import com.sysm.back.model.ImageExample;
import com.sysm.back.service.ImgService;
import com.sysm.back.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ImgServiceImpl implements ImgService {
    @Value("${image.catalog}")
    private String catalog;
    @Autowired
    ImageMapper imageMapper;

    @Override
    public Map<String, Object> upload(MultipartFile file, String name,String url) {
        InetAddress ia = null;
        String localip = "";
        try {
            ia = ia.getLocalHost();

            String localname = ia.getHostName();
            localip = ia.getHostAddress();
            log.info("本机名称是：" + localname);
            log.info("本机的ip是 ：" + localip);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //基础路径  E:/springboot-upload/image/
        String basePath = catalog;
        //获取文件保存路径 \20180608\113339\
        String folder = FileUtils.getFolder();
        // 获取前缀为"FL_" 长度为20 的文件名  FL_eUljOejPseMeDg86h.png
        String fileName = FileUtils.getFileName() + FileUtils.getFileNameSub(file.getOriginalFilename());

        try {
            // E:\springboot-upload\image\20180608\113339
            Path filePath = Files.createDirectories(Paths.get(basePath, folder));
            log.info("path01-->{}", filePath);

            //写入文件  E:\springboot-upload\image\20180608\113339\FL_eUljOejPseMeDg86h.png
            Path fullPath = Paths.get(basePath, folder, fileName);
            log.info("fullPath-->{}", fullPath);
            // E:\springboot-upload\image\20180608\113339\FL_eUljOejPseMeDg86h.png
            Files.write(fullPath, file.getBytes(), StandardOpenOption.CREATE);

            //保存文件信息
            Image fileInfo = new Image();
            fileInfo.setName(name);
            fileInfo.setMd5(url);
            fileInfo.setFileName(fileName);
            fileInfo.setFilePath(filePath.toString());
            fileInfo.setUploadTime(new Date());
            fileInfo.setType(file.getContentType());
            fileInfo.setValid("1");
            imageMapper.insert(fileInfo);
        } catch (Exception e) {
            Path path = Paths.get(basePath, folder);
            log.error("写入文件异常,删除文件。。。。", e);
            try {
                Files.deleteIfExists(path);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return ResultVO.failure(e.getMessage());
        }
        return ResultVO.success("上传成功");
    }

    @Override
    public Image getImage(String fileName) throws IOException {
        log.info("fileName-->{}", fileName);
        ImageExample example = new ImageExample();
        ImageExample.Criteria criteria = example.createCriteria();
        criteria.andFileNameEqualTo(fileName);
        List<Image> fileInfo = imageMapper.selectByExample(example);
        if (fileInfo == null || fileInfo.isEmpty()) {
            return null;
        }
        Image fileInfo1 = fileInfo.get(0);
        Path path = Paths.get(fileInfo1.getFilePath(), fileInfo1.getFileName());
        log.info("path-->{}", path);
        fileInfo1.setContent(Files.newInputStream(path));
        return fileInfo1;
    }

    @Override
    public Map<String, Object> getUrlList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        ImageExample imageExample = new ImageExample();
        imageExample.createCriteria().andValidEqualTo("1");
        imageExample.setOrderByClause("upload_time DESC");
        List<Image> images = imageMapper.selectByExample(imageExample);
        if (images.isEmpty()) {
            ArrayList<Image> arrayList = new ArrayList<>();
            return ResultVO.success(new PageInfo<>(arrayList));
        }
        return ResultVO.success(new PageInfo<>(images));
    }

    @Override
    public Map<String, Object> delectImage(Integer id) {
        Image image = imageMapper.selectByPrimaryKey(id);
        if (null == image) {
            return ResultVO.failure("没有该图片");
        }
        image.setValid("2");
        imageMapper.updateByPrimaryKey(image);
        return ResultVO.success("删除成功!");
    }
}

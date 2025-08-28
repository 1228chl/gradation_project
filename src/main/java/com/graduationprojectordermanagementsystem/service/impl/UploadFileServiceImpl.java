package com.graduationprojectordermanagementsystem.service.impl;

import com.graduationprojectordermanagementsystem.exception.UploadFileEmptyException;
import com.graduationprojectordermanagementsystem.mapper.UploadFileMapper;
import com.graduationprojectordermanagementsystem.pojo.entity.UploadFile;
import com.graduationprojectordermanagementsystem.service.UploadFileService;
import com.graduationprojectordermanagementsystem.util.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class UploadFileServiceImpl implements UploadFileService {

    @Resource
    private UploadFileMapper uploadFileMapper;

    // 配置文件中定义的上传目录（正式目录）
    @Value("${file.upload-dir}")
    private String uploadDir;


    /**
     * 文件上传
     */
    @Override
    public UploadFile uploadFile(MultipartFile file) throws IOException {
        log.info("文件上传开始");

        // 1. 校验文件
        if (file.isEmpty()) {
            throw new UploadFileEmptyException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new UploadFileEmptyException("文件名不能为空");
        }

        // 2. 获取当前用户
        String username = UserContext.getUsername();
        if (username == null) {
            throw new RuntimeException("用户未登录，无法上传文件");
        }

        // 3. 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 4. 生成唯一文件名
        String fileExtension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            fileExtension = originalFilename.substring(lastDotIndex);
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path finalFilePath = uploadPath.resolve(uniqueFileName);

        // 5. 直接将文件写入正式目录
        try {
            file.transferTo(finalFilePath.toFile());
            log.info("✅ 文件已保存到正式目录: {}", finalFilePath);
        } catch (IOException e) {
            log.error("文件保存到磁盘失败", e);
            throw new IOException("文件保存失败，请检查磁盘路径或权限", e);
        }

        // 6. 构建数据库实体并插入
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFileName(originalFilename);
        uploadFile.setFilePath(finalFilePath.toString());
        uploadFile.setFileType(file.getContentType());
        uploadFile.setFileSize(file.getSize());
        uploadFile.setFileUrl("http://localhost:8080/api/common/file/download/" + uniqueFileName);
        uploadFile.setUsername(username);


        // 6. 写数据库（后）
        try {
            uploadFileMapper.insert(uploadFile);
        } catch (Exception e) {
            log.error("数据库插入失败，尝试删除已写入的文件", e);
            try {
                Files.deleteIfExists(finalFilePath);
            } catch (IOException ioException) {
                log.error("无法清理已生成的文件: " + finalFilePath, ioException);
            }
            throw e; // 重新抛出异常
        }

        log.info("✅ 文件记录已保存至数据库，文件ID: {}, 用户: {}", uploadFile.getId(), username);
        log.info("🎉 文件上传成功，文件名: {}", originalFilename);

        return uploadFile;
    }

}

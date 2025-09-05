package com.graduationprojectordermanagementsystem.controller.user;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.contents.CommonContent;
import com.graduationprojectordermanagementsystem.contents.StatusContent;
import com.graduationprojectordermanagementsystem.pojo.entity.UploadFile;
import com.graduationprojectordermanagementsystem.pojo.vo.UploadFileVO;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Tag(name = "文件接口")
@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileController {
    @Resource
    private FileService fileService;
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${file.upload-avatar-dir}")
    private String uploadAvatarDir;
    /**
     * 文件上传
     */
    @RequireAnyRole({"user","admin"})
    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result<UploadFileVO> uploadFile(MultipartFile file) {
        try {
            UploadFile uploadFile = fileService.uploadFile(file);
            UploadFileVO uploadFileVO = new UploadFileVO(
                    uploadFile.getId(),
                    uploadFile.getFileName(),
                    uploadFile.getFileUuid(),
                    uploadFile.getFilePath(),
                    uploadFile.getFileType(),
                    uploadFile.getFileSize());

            return Result.success(uploadFileVO);
        }catch (Exception e){
            log.error("文件上传失败",e);
            return Result.error(CommonContent.FileUploadFailed);//文件上传失败
        }
    }

    /**
     * 头像上传
     */
    @RequireAnyRole({"user","admin"})
    @PostMapping("/upload/avatar")
    @Operation(summary = "头像上传")
    public Result<UploadFileVO> uploadAvatar(MultipartFile file) {
        try {
            UploadFile uploadFile = fileService.uploadAvatar(file);
            UploadFileVO uploadFileVO = new UploadFileVO(
                    uploadFile.getId(),
                    uploadFile.getFileName(),
                    uploadFile.getFileUuid(),
                    uploadFile.getFilePath(),
                    uploadFile.getFileType(),
                    uploadFile.getFileSize());

            return Result.success(uploadFileVO);
        }catch (Exception e){
            log.error("头像上传失败",e);
            return Result.error(CommonContent.FileUploadFailed);//文件上传失败
        }
    }

    /**
     * 文件预览（图片、PDF等）
     */
    @RequireAnyRole({"user","admin"})
    @GetMapping("/avatar/{fileUuid}")
    @Operation(summary = "文件预览")
    public void viewFile(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        viewFileInternal(fileUuid, response, false);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download/{fileUuid}")
    @RequireAnyRole({"user", "admin"})
    @Operation(summary = "文件下载（需登录）")
    public void downloadFile(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        viewFileInternal(fileUuid, response, true);
    }

    /**
     * 内部方法，用于处理文件预览和下载
     */
    private void viewFileInternal(String fileUuid, HttpServletResponse response, boolean asDownload) throws IOException {
        UploadFile file = fileService.selectByUuid(fileUuid);
        if (file == null) {
            response.setStatus(404);
            response.getWriter().write("文件不存在");
            return;
        }

        Path filePath;
        if (Objects.equals(file.getFileOrAvatar(), StatusContent.FILE)) {
            filePath = Paths.get(uploadDir, fileUuid);
        } else if (Objects.equals(file.getFileOrAvatar(), StatusContent.AVATAR)) {
            filePath = Paths.get(uploadAvatarDir, fileUuid);
        } else {
            response.setStatus(400);
            response.getWriter().write("无效文件类型");
            return;
        }

        File fileOnDisk = filePath.toFile();
        if (!fileOnDisk.exists()) {
            response.setStatus(404);
            response.getWriter().write("文件未找到");
            return;
        }

        String contentType = file.getFileType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.setContentType(contentType);
        if (asDownload) {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8));
        } else {
            response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8));
        }
        response.setContentLengthLong(file.getFileSize());

        try (InputStream in = new FileInputStream(fileOnDisk)) {
            FileCopyUtils.copy(in, response.getOutputStream());
        }
    }
}

package com.graduationprojectordermanagementsystem.controller.user;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.contents.CommonContent;
import com.graduationprojectordermanagementsystem.pojo.entity.UploadFile;
import com.graduationprojectordermanagementsystem.pojo.vo.UploadFileVO;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.UploadFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件接口")
@RestController
@RequestMapping("/api/file")
@Slf4j
public class UploadFileController {
    @Resource
    private UploadFileService uploadFileService;

    /**
     * 文件上传
     */
    @RequireAnyRole({"user","admin"})
    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result<UploadFileVO> uploadFile(MultipartFile file) {
        try {
            UploadFile uploadFile = uploadFileService.uploadFile(file);
            UploadFileVO uploadFileVO = new UploadFileVO(
                    uploadFile.getId(),
                    uploadFile.getFileName(),
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
            UploadFile uploadFile = uploadFileService.uploadAvatar(file);
            UploadFileVO uploadFileVO = new UploadFileVO(
                    uploadFile.getId(),
                    uploadFile.getFileName(),
                    uploadFile.getFilePath(),
                    uploadFile.getFileType(),
                    uploadFile.getFileSize());

            return Result.success(uploadFileVO);
        }catch (Exception e){
            log.error("头像上传失败",e);
            return Result.error(CommonContent.FileUploadFailed);//文件上传失败
        }
    }


}

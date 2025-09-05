package com.graduationprojectordermanagementsystem.service;

import com.graduationprojectordermanagementsystem.pojo.entity.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    UploadFile uploadFile(MultipartFile file) throws IOException;

    UploadFile uploadAvatar(MultipartFile file) throws IOException;

    UploadFile selectByUuid(String fileUuid);
}

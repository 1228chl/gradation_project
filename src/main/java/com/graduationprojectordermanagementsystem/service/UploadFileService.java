package com.graduationprojectordermanagementsystem.service;

import com.graduationprojectordermanagementsystem.pojo.entity.UploadFile;
import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadFileService {
    UploadFile uploadFile(MultipartFile file) throws IOException;

    UploadFile uploadAvatar(MultipartFile file) throws IOException;
}

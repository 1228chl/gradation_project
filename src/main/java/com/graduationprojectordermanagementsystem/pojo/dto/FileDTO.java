package com.graduationprojectordermanagementsystem.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.nio.file.Path;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO implements Serializable {
    private Path uploadPath;//文件上传路径
    private String fileExtension;//文件扩展名
    private String uniqueFileName;//文件唯一名称
    private Path finalFilePath;//最终文件保存路径
}

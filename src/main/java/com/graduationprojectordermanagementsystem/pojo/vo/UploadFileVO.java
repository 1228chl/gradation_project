package com.graduationprojectordermanagementsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileVO implements Serializable {
    private Long id;//文件id
    private String fileName;//文件名
    private String fileUuid;//文件UUID
    private String fileUrl;//文件URL
    private String fileType;//文件类型
    private Long fileSize;//文件大小(字节)
}

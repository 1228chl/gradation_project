package com.graduationprojectordermanagementsystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("upload_file")
public class UploadFile implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;//文件id
    private String Username;//文件上传者
    private String fileName;//文件名
    private String fileUuid; // 存储 UUID 文件名，如：a1b2c3.jpg
    private String fileUrl;//文件url
    private String filePath;//文件路径
    private String fileType;//文件类型
    private Long fileSize;//文件大小(字节)
    private Integer fileOrAvatar;//文件类型 0-文件 1-头像

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;//创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;//更新时间

    // 序列化版本号
    @Serial
    private static final long serialVersionUID = 1L;
}

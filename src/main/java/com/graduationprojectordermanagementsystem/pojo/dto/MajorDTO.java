package com.graduationprojectordermanagementsystem.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class MajorDTO {
    @NotBlank(message = "专业名称不能为空")
    private String majorName;//专业名称
    @NotBlank(message = "专业代码不能为空")
    private String majorCode;//专业代码
    @NotBlank(message = "专业描述不能为空")
    private String majorDesc;//专业描述
}

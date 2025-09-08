package com.graduationprojectordermanagementsystem.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseDTO {
    @NotBlank(message = "课程名称不能为空")
    private String courseName;//课程名称
    @NotBlank(message = "课程代码不能为空")
    private String courseCode;//课程代码
    private String courseDesc;//课程描述
    private String courseUrl;//课程图片链接
}

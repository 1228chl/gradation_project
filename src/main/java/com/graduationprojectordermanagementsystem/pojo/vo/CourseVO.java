package com.graduationprojectordermanagementsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseVO implements Serializable {
    private Long id;//课程id
    private String courseName;//课程名称
    private String courseCode;//课程代码
    private String courseDesc;//课程描述
    private String courseUrl;//课程图片链接
    private Long likeCount;//课程点赞数
    private Integer courseStatus;//课程状态
}

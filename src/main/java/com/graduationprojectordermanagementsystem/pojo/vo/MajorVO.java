package com.graduationprojectordermanagementsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MajorVO implements Serializable {
    private Long id;//主键
    private String majorName;//专业名称
    private String majorCode;//专业代码
    private String majorDesc;//专业描述
    private Long likeCount;//喜欢数
    private Integer majorStatus;//专业状态

}

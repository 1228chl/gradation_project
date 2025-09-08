package com.graduationprojectordermanagementsystem.service;

import com.graduationprojectordermanagementsystem.pojo.dto.CourseDTO;
import com.graduationprojectordermanagementsystem.pojo.vo.CourseVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import jakarta.validation.Valid;

public interface CourseService {
    boolean addCourse(@Valid CourseDTO courseDTO);

    boolean updateCourseStatus(Integer status, Long id);

    boolean deleteCourse(Long id);

    PageResult<CourseVO> getCourseList(Integer pageNum, Integer pageSize);
}

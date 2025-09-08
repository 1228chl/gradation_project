package com.graduationprojectordermanagementsystem.controller.user;


import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.pojo.vo.CourseVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "user课程接口")
@RestController("userCourseController")
@RequestMapping("/api/user/course")
public class CourseController {
    @Resource
    private CourseService courseService;

    @Operation(summary = "查询所有课程")
    @RequireAnyRole({"user","admin"})
    @RequestMapping("/list")
    public Result<PageResult<CourseVO>> getCourseList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询所有课程");
        PageResult<CourseVO> courseList = courseService.getCourseList(pageNum, pageSize);
        return courseList != null ? Result.success(courseList) : Result.error("查询课程失败或没有课程");
    }

}

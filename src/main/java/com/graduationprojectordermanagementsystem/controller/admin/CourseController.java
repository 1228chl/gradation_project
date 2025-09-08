package com.graduationprojectordermanagementsystem.controller.admin;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.pojo.dto.CourseDTO;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "admin课程接口")
@RestController("adminCourseController")
@RequestMapping("/api/admin/course")
public class CourseController {
    @Resource
    private CourseService courseService;


    /**
     * 添加课程
     */
    @Operation(summary = "添加课程")
    @RequireAnyRole({"admin"})
    @PostMapping
    public Result<String> addCourse(@Valid @RequestBody CourseDTO courseDTO){
        if (courseService.addCourse(courseDTO)){
            log.info("添加课程成功");
            return Result.success("添加课程成功");
        }
        return Result.error("添加课程失败");
    }

    /**
     * 修改课程状态
     */
    @Operation(summary = "修改课程状态")
    @RequireAnyRole({"admin"})
    @PutMapping("/update/{id}/status/{status}")
    public Result<String> updateCourseStatus(@PathVariable("status") Integer status, @PathVariable("id") Long id){
        if (courseService.updateCourseStatus(status, id)){
            log.info("修改课程状态成功");
            return Result.success("修改课程状态成功");
        }
        return Result.error("修改课程状态失败");
    }

    /**
     * 删除专业
     */
    @Operation(summary = "删除课程")
    @RequireAnyRole({"admin"})
    @DeleteMapping("/{id}")
    public Result<String> deleteCourse(@PathVariable("id") Long id){
        if (courseService.deleteCourse(id)){
            log.info("删除课程成功");
            return Result.success("删除课程成功");
        }
        return Result.error("删除课程失败");
    }

}


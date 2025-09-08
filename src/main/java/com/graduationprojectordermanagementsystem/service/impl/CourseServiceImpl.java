package com.graduationprojectordermanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduationprojectordermanagementsystem.contents.StatusContent;
import com.graduationprojectordermanagementsystem.mapper.CourseMapper;
import com.graduationprojectordermanagementsystem.mapper.UserCourseMapper;
import com.graduationprojectordermanagementsystem.pojo.dto.CourseDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.Course;
import com.graduationprojectordermanagementsystem.pojo.entity.UserCourse;
import com.graduationprojectordermanagementsystem.pojo.vo.CourseVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.service.CourseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private UserCourseMapper userCourseMapper;

    /**
     * 添加课程
     */
    @Override
    public boolean addCourse(CourseDTO courseDTO) {
        log.info("开始添加课程{}",courseDTO.getCourseName());
        Course course = new Course();
        course.setCourseName(courseDTO.getCourseName());
        course.setCourseCode(courseDTO.getCourseCode());
        course.setCourseDesc(courseDTO.getCourseDesc());
        course.setCourseUrl(courseDTO.getCourseUrl());
        course.setCourseStatus(StatusContent.ENABLE);
        try {
            return courseMapper.insert(course) > 0;
        }catch (Exception e){
            log.error("添加课程信息失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 修改课程状态
     */
    @Override
    public boolean updateCourseStatus(Integer status, Long id) {
        log.info("开始修改课程状态，课程ID：{}，目标状态：{}", id, status);

        if (id == null || status == null) return false;
        // 检查课程是否存在
        Course course = courseMapper.selectById(id);
        if (course == null) {
            log.warn("修改课程失败，课程不存在，ID：{}", id);
            throw new IllegalArgumentException("课程不存在");
        }

        return courseMapper.update(null,
                new LambdaUpdateWrapper<Course>()
                        .eq(Course::getId, id)
                        .set(Course::getCourseStatus, status)
        ) > 0;
    }

    /**
     * 删除课程
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 启用事务
    public boolean deleteCourse(Long id) {
        log.info("开始删除课程，课程ID：{}", id);
        // 1. 参数校验
        if (id == null || id <= 0) {
            log.warn("删除课程失败，非法ID：{}", id);
            throw new IllegalArgumentException("课程ID不能为空且必须大于0");
        }

        // 2. 检查专业是否存在
        Course course = courseMapper.selectById(id);
        if (course == null) {
            log.warn("删除课程失败，课程不存在，ID：{}", id);
            throw new IllegalArgumentException("课程不存在");
        }

        // 4. 执行删除
        try {
            int deleteCount = courseMapper.deleteById(id);
            if (deleteCount > 0) {
                log.info("课程删除成功，ID：{}，课程名称：{}", id, course.getCourseName());
                return true;
            } else {
                log.error("课程删除失败，数据库未删除任何记录，ID：{}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除课程时发生异常，ID：{}", id, e);
            throw new RuntimeException("删除课程失败", e);
        }
    }

    @Override
    public PageResult<CourseVO> getCourseList(Integer pageNum, Integer pageSize) {
        // 1. 创建 MP 的分页对象
        Page<Course> page = new Page<>(pageNum, pageSize);

        // 2. 执行分页查询
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        // 可添加查询条件，如状态等
        // wrapper.eq(Course::getStatus, StatusContent.ENABLE);

        Page<Course> coursePage = courseMapper.selectPage(page, wrapper);

        // 3. 提取课程 ID 列表，用于统计 likeCount
        List<Long> courseIdList = coursePage.getRecords().stream()
                .map(Course::getId)
                .collect(Collectors.toList());

        // 如果没有课程，避免后续 SQL 报错
        if (courseIdList.isEmpty()) {
            List<CourseVO> emptyList = new ArrayList<>();
            return PageResult.of(coursePage.getTotal(), pageNum, pageSize, emptyList);
        }

        // 4. 查询每个课程的收藏/喜欢数量
        List<Map<String, Object>> countResult = userCourseMapper.selectMaps(
                new QueryWrapper<UserCourse>()
                        .select("course_id, COUNT(*) as like_count")
                        .in("course_id", courseIdList)
                        .groupBy("course_id")
        );

        // 5. 转换为 Map<courseId, likeCount>
        Map<Long, Long> likeCountMap = new HashMap<>();
        for (Map<String, Object> row : countResult) {
            Long courseId = ((Number) row.get("course_id")).longValue();
            Long count = ((Number) row.get("like_count")).longValue();
            likeCountMap.put(courseId, count);
        }

        // 6. 转换实体为 VO 列表，并填充 likeCount
        List<CourseVO> voList = coursePage.getRecords().stream().map(course -> {
            CourseVO vo = new CourseVO();
            BeanUtils.copyProperties(course, vo);

            // 填充 likeCount，默认为 0
            vo.setLikeCount(likeCountMap.getOrDefault(course.getId(), 0L));

            return vo;
        }).collect(Collectors.toList());

        // 7. 封装并返回 PageResult
        return PageResult.of(
                coursePage.getTotal(),
                (int) coursePage.getCurrent(),
                (int) coursePage.getSize(),
                voList
        );
    }


}

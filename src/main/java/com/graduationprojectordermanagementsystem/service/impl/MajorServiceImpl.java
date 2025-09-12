package com.graduationprojectordermanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduationprojectordermanagementsystem.contents.StatusContent;
import com.graduationprojectordermanagementsystem.exception.BaseException;
import com.graduationprojectordermanagementsystem.mapper.MajorMapper;
import com.graduationprojectordermanagementsystem.pojo.dto.MajorDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.Major;
import com.graduationprojectordermanagementsystem.pojo.vo.MajorVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.result.ResultCode;
import com.graduationprojectordermanagementsystem.service.MajorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class MajorServiceImpl implements MajorService {
    @Resource
    private MajorMapper majorMapper;

    /**
     * 添加专业信息
     */
    @Override
    public boolean addMajor(MajorDTO majorDTO) {
        log.info("开始添加专业信息");
        Major major = new Major();
        major.setMajorName(majorDTO.getMajorName());
        major.setMajorCode(majorDTO.getMajorCode());
        major.setMajorDesc(majorDTO.getMajorDesc());
        major.setMajorStatus(StatusContent.ENABLE);

        try {
            return majorMapper.insert(major) > 0;
        }catch (Exception e){
            log.error("添加专业信息失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 查询所有专业信息
     */
    @Override
    public PageResult<MajorVO> getMajorList(Integer pageNum, Integer pageSize) {
        log.info("开始查询所有专业信息");
        // 1. 创建 MP 的分页对象
        Page<Major> page = new Page<>(pageNum, pageSize);

        // 2. 执行分页查询（你可以加条件，如状态、用户名等）
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        // 示例：只查启用的专业
        // wrapper.eq(User::getStatus, StatusContent.ENABLE);

        Page<Major> majorPage = majorMapper.selectPage(page, wrapper);

        // 3. 提取专业 ID 列表，用于统计 likeCount
        List<Long> courseIdList = majorPage.getRecords().stream()
                .map(Major::getId)
                .toList();

        // 如果没有专业，避免后续 SQL 报错
        if (courseIdList.isEmpty()) {
            List<MajorVO> emptyList = new ArrayList<>();
            return PageResult.of(majorPage.getTotal(), pageNum, pageSize, emptyList);
        }

        // 4. 转换实体为 VO 列表
        List<MajorVO> voList = majorPage.getRecords().stream().map(major -> {
            MajorVO vo = new MajorVO();
            BeanUtils.copyProperties(major, vo);
            // 手动处理 createTime 等字段（如果类型不匹配）
            return vo;
        }).toList();

        // 5. 封装并返回 PageResult
        return PageResult.of(
                majorPage.getTotal(),    // 总数
                (int) majorPage.getCurrent(), // 当前页
                (int) majorPage.getSize(),    // 每页大小
                voList                   // 数据列表
        );
    }

    /**
     * 修改专业状态
     */
    @Override
    public boolean updateMajorStatus(Integer status, Long id) {
        log.info("开始修改专业状态，专业ID：{}，目标状态：{}", id, status);

        if (id == null || status == null) return false;
        // 检查专业是否存在
        Major major = majorMapper.selectById(id);
        if (major == null) {
            log.warn("修改专业失败，专业不存在，ID：{}", id);
            throw new BaseException(ResultCode.VALIDATE_FAILED ,"专业不存在");
        }

        return majorMapper.update(null,
                new LambdaUpdateWrapper<Major>()
                        .eq(Major::getId, id)
                        .set(Major::getMajorStatus, status)
        ) > 0;
    }

    /**
     * 删除专业
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 启用事务
    public boolean deleteMajor(Long id) {
        log.info("开始删除专业，专业ID：{}", id);
        // 1. 参数校验
        if (id == null || id <= 0) {
            log.warn("删除专业失败，非法ID：{}", id);
            throw new BaseException(ResultCode.VALIDATE_FAILED,"专业ID不能为空且必须大于0");
        }

        // 2. 检查专业是否存在
        Major major = majorMapper.selectById(id);
        if (major == null) {
            log.warn("删除专业失败，专业不存在，ID：{}", id);
            throw new BaseException(ResultCode.VALIDATE_FAILED,"专业不存在");
        }

        // 4. 执行删除
        try {
            int deleteCount = majorMapper.deleteById(id);
            if (deleteCount > 0) {
                log.info("专业删除成功，ID：{}，专业名称：{}", id, major.getMajorName());
                return true;
            } else {
                log.error("专业删除失败，数据库未删除任何记录，ID：{}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除专业时发生异常，ID：{}", id, e);
            throw new BaseException("删除专业失败");
        }
    }
}

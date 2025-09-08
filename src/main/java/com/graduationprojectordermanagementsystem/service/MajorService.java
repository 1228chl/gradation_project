package com.graduationprojectordermanagementsystem.service;

import com.graduationprojectordermanagementsystem.pojo.dto.MajorDTO;
import com.graduationprojectordermanagementsystem.pojo.vo.MajorVO;
import com.graduationprojectordermanagementsystem.result.PageResult;


public interface MajorService {
    boolean addMajor(MajorDTO majorDTO);

    boolean updateMajorStatus(Integer status, Long id);

    boolean deleteMajor(Long id);

    PageResult<MajorVO> getMajorList(Integer pageNum, Integer pageSize);
}

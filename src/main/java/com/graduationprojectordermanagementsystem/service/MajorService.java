package com.graduationprojectordermanagementsystem.service;

import com.graduationprojectordermanagementsystem.pojo.dto.MajorDTO;
import com.graduationprojectordermanagementsystem.pojo.vo.MajorVO;

import java.util.List;

public interface MajorService {
    boolean addMajor(MajorDTO majorDTO);

    List<MajorVO> getAllMajor();

    boolean updateMajorStatus(Integer status, Long id);

    boolean deleteMajor(Long id);
}

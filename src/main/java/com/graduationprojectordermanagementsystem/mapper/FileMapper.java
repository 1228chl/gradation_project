package com.graduationprojectordermanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduationprojectordermanagementsystem.pojo.entity.UploadFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<UploadFile> {
    /**
     * 查询所有文件路径（用于脏文件清理）
     */
    @Select("SELECT file_path FROM upload_file")
    List<String> findAllFilepaths();

    int deleteByFileUuid(String oldAvatar);

    UploadFile selectByFileUuid(String oldAvatar);
}

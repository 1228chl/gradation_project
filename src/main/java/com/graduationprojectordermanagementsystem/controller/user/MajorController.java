package com.graduationprojectordermanagementsystem.controller.user;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.pojo.vo.MajorVO;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "user专业接口")
@RestController("userMajorController")
@RequestMapping("/api/user/major")
public class MajorController {
    @Resource
    private MajorService majorService;

    /**
     * 查询所有专业
     */
    @Operation(summary = "查询所有专业")
    @RequireAnyRole({"user","admin"})
    @GetMapping("/list")
    public Result<List<MajorVO>> getAllMajor() {
        log.info("查询所有专业");
        List<MajorVO> majorVOList = majorService.getAllMajor();
        return Result.success(majorVOList);
    }
}

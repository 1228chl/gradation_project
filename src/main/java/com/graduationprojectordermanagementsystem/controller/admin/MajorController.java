package com.graduationprojectordermanagementsystem.controller.admin;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.pojo.dto.MajorDTO;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "admin专业接口")
@RestController("adminMajorController")
@RequestMapping("/api/admin/major")
public class MajorController {

    @Resource
    private MajorService majorService;


    /**
     * 添加专业
     */
    @Operation(summary = "添加专业")
    @RequireAnyRole({"admin"})
    @PostMapping("/add")
    public Result<String> addMajor(@RequestBody MajorDTO majorDTO){
        if (majorService.addMajor(majorDTO)){
            log.info("添加专业成功");
            return Result.success("添加专业成功");
        }
        return Result.error("添加专业失败");
    }

    /**
     * 修改专业状态
     */
    @Operation(summary = "修改专业状态")
    @RequireAnyRole({"admin"})
    @PutMapping("/update/{id}/status/{status}")
    public Result<String> updateMajorStatus(@PathVariable("status") Integer status, @PathVariable("id") Long id){
        if (majorService.updateMajorStatus(status, id)){
            log.info("修改专业状态成功");
            return Result.success("修改专业状态成功");
        }
        return Result.error("修改专业状态失败");
    }

    /**
     * 删除专业
     */
    @Operation(summary = "删除专业")
    @RequireAnyRole({"admin"})
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteMajor(@PathVariable("id") Long id){
        if (majorService.deleteMajor(id)){
            log.info("删除专业成功");
            return Result.success("删除专业成功");
        }
        return Result.error("删除专业失败");
    }
}

package com.graduationprojectordermanagementsystem.task;
import com.graduationprojectordermanagementsystem.mapper.UploadFileMapper; // 你的文件Mapper
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * 定时清理脏文件任务
 * 脏文件 = 文件系统存在，但数据库中无记录
 */
@Component
@Slf4j
public class FileCleanupTask {
    @Value("${file.upload-dir}")
    private String uploadPath;

    @Resource
    private UploadFileMapper uploadFileMapper;

    /**
     * 每天凌晨 2:00 执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOrphanFiles() {
        log.info("开始执行脏文件清理任务，扫描目录: {}", uploadPath);

        Path uploadDir = Paths.get(uploadPath);

        // 检查目录是否存在
        if (!Files.exists(uploadDir)) {
            log.warn("上传目录不存在: {}", uploadPath);
            return;
        }

        if (!Files.isDirectory(uploadDir)) {
            log.error("上传路径不是目录: {}", uploadPath);
            return;
        }

        try {
            // 1. 获取数据库中所有 filepath 的文件名（只取文件名，不带路径）
            List<String> dbFilenames = uploadFileMapper.findAllFilepaths()
                    .stream()
                    .map(path -> Paths.get(path).getFileName().toString()) // 只取文件名
                    .filter(name -> !name.trim().isEmpty())
                    .toList();

            Set<String> dbFilenameSet = Set.copyOf(dbFilenames); // 转为 Set 提高查询效率
            log.info("数据库中记录的文件数量: {}", dbFilenameSet.size());

            // 2. 扫描文件目录下的所有文件
            File dir = uploadDir.toFile();
            File[] files = dir.listFiles(f -> f.isFile() && !f.isHidden()); // 排除隐藏文件

            if (files == null) {
                log.info("目录为空或无法读取: {}", uploadPath);
                return;
            }

            log.info("文件系统中文件数量: {}", files.length);

            int deletedCount = 0;

            // 3. 遍历文件，判断是否为脏文件
            for (File file : files) {
                String filename = file.getName();

                if (!dbFilenameSet.contains(filename)) {
                    try {
                        boolean deleted = file.delete();
                        if (deleted) {
                            log.info("已删除脏文件: {}", filename);
                            deletedCount++;
                        } else {
                            log.warn("无法删除文件（权限问题？）: {}", filename);
                        }
                    } catch (Exception e) {
                        log.error("删除文件失败: {}", filename, e);
                    }
                }
                // else: 文件在数据库中有记录，跳过
            }

            log.info("脏文件清理任务完成，共删除 {} 个文件", deletedCount);

        } catch (Exception e) {
            log.error("执行脏文件清理任务时发生异常", e);
        }
    }
}

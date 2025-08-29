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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 定时清理脏文件任务
 * 脏文件 = 文件系统存在，但数据库中无记录
 */
@Component
@Slf4j
public class FileCleanupTask {
    @Value("${file.upload-dir}")
    private String uploadPath;

    @Value("${file.upload-avatar-dir}")
    private String uploadAvatarDir;

    @Resource
    private UploadFileMapper uploadFileMapper;

    /**
     * 每天凌晨 2:00 执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOrphanFiles() {
        log.info("开始执行脏文件清理任务");

        // 支持多个上传目录
        Map<String, String> uploadDirs = new HashMap<>();
        uploadDirs.put("通用上传目录", uploadPath);
        uploadDirs.put("头像上传目录", uploadAvatarDir);

        // 1. 获取数据库中所有 filepath 的文件名（只取文件名）
        Set<String> dbFilenameSet;
        try {
            List<String> dbFilenames = uploadFileMapper.findAllFilepaths();
            if (dbFilenames == null || dbFilenames.isEmpty()) {
                log.warn("数据库中未查询到任何文件记录");
                dbFilenameSet = Collections.emptySet();
            } else {
                dbFilenameSet = dbFilenames.stream()
                        .map(path -> {
                            if (path == null) return null;
                            return Paths.get(path).getFileName().toString();
                        })
                        .filter(name -> name != null && !name.trim().isEmpty())
                        .collect(Collectors.toSet());
            }
            log.info("数据库中记录的文件数量: {}", dbFilenameSet.size());
        } catch (Exception e) {
            log.error("查询数据库文件记录失败", e);
            return; // 如果数据库都查不了，就不清理了
        }

        int totalDeleted = 0;

        // 2. 遍历每个上传目录
        for (Map.Entry<String, String> entry : uploadDirs.entrySet()) {
            String dirName = entry.getKey();
            String dirPath = entry.getValue();

            log.info("正在扫描 {}: {}", dirName, dirPath);

            Path uploadDir = Paths.get(dirPath);

            // 检查目录是否存在
            if (!Files.exists(uploadDir)) {
                log.warn("{} 不存在: {}", dirName, dirPath);
                continue;
            }

            if (!Files.isDirectory(uploadDir)) {
                log.error("{} 不是目录: {}", dirName, dirPath);
                continue;
            }

            File dir = uploadDir.toFile();
            File[] files = dir.listFiles(f -> f.isFile() && !f.isHidden());
            if (files == null || files.length == 0) {
                log.info("{} 为空或无法读取: {}", dirName, dirPath);
                continue;
            }

            log.info("{} 中文件数量: {}", dirName, files.length);

            int deletedCount = 0;

            // 遍历文件，判断是否为脏文件
            for (File file : files) {
                String filename = file.getName();

                if (!dbFilenameSet.contains(filename)) {
                    try {
                        boolean deleted = file.delete();
                        if (deleted) {
                            log.info("已删除脏文件 [{}]: {}", dirName, filename);
                            deletedCount++;
                            totalDeleted++;
                        } else {
                            log.warn("无法删除文件（权限问题？） [{}]: {}", dirName, filename);
                        }
                    } catch (Exception e) {
                        log.error("删除文件失败 [{}]: {}", dirName, filename, e);
                    }
                }
            }

            log.info("{} 清理完成，共删除 {} 个脏文件", dirName, deletedCount);
        }

        log.info("脏文件清理任务完成，总计删除 {} 个文件", totalDeleted);
    }
}

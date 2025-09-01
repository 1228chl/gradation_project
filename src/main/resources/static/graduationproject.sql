/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80039
 Source Host           : localhost:3306
 Source Schema         : graduation

 Target Server Type    : MySQL
 Target Server Version : 80039
 File Encoding         : 65001

 Date: 01/09/2025 15:04:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for major
-- ----------------------------
DROP TABLE IF EXISTS `major`;
CREATE TABLE `major`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `major_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `major_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `major_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `major_status` int NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `major_name`(`major_name` ASC) USING BTREE,
  UNIQUE INDEX `major_code`(`major_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of major
-- ----------------------------
INSERT INTO `major` VALUES (2, '计算机科学与技术', 'CS101', '学习编程、算法、系统等', 1, '2025-08-29 12:51:55', '2025-08-29 12:51:55');
INSERT INTO `major` VALUES (4, '软件工程', 'SE202', '专注于软件开发与项目管理', 1, '2025-08-29 13:40:40', '2025-08-29 13:40:40');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint UNSIGNED NOT NULL,
  `order_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` int NOT NULL,
  `major_id` int NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `demand` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` int NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  UNIQUE INDEX `order_number`(`order_number` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1961392725710376961, '4-2-1756467441340-831', 2, 4, 456.35, '整甜许多哼代表摔一同', 'id cillum', 0, '2025-08-29 19:37:21', '2025-08-29 19:37:21');
INSERT INTO `orders` VALUES (1961401168936599554, '70-2-1756469454350-127', 2, 4, 286.69, '吸捣没好在生动油门但是全哎出生', 'laboris nostrud', 3, '2025-08-29 20:10:54', '2025-08-30 14:10:10');
INSERT INTO `orders` VALUES (1961675481640108033, '70-2-1756534855594-653', 2, 70, 114.89, '雪崩慢太自从哇塞倒清楚像相当呸', 'ut Lorem in laborum', 0, '2025-08-30 14:20:56', '2025-08-30 14:20:56');

-- ----------------------------
-- Table structure for upload_file
-- ----------------------------
DROP TABLE IF EXISTS `upload_file`;
CREATE TABLE `upload_file`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_size` bigint NULL DEFAULT NULL,
  `file_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `file_or_avatar` int NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of upload_file
-- ----------------------------
INSERT INTO `upload_file` VALUES (1, 'xunzong', '2025届毕业生档案邮寄地址核对.XLSX', 'http://localhost:8080/api/common/file/download/844a7ff3-7fdb-4b3f-b688-fda43685df4b.XLSX', 'G:\\uploads\\844a7ff3-7fdb-4b3f-b688-fda43685df4b.XLSX', 610917, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 0, '2025-08-28 18:02:03', '2025-08-28 18:02:03');
INSERT INTO `upload_file` VALUES (5, 'xunzong', '2025届毕业生档案邮寄地址核对.XLSX', 'http://localhost:8080/api/common/file/download/96eb4fe6-4797-4fb4-8d82-773847063772.XLSX', 'G:\\uploads\\96eb4fe6-4797-4fb4-8d82-773847063772.XLSX', 610917, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 0, '2025-08-28 18:44:23', '2025-08-28 18:44:23');
INSERT INTO `upload_file` VALUES (6, 'xunzong', '2025届毕业生档案邮寄地址核对.XLSX', 'http://localhost:8080/api/common/file/download/40295dc6-0c69-4bdf-b441-f47c641bd9ee.XLSX', 'G:\\uploads\\40295dc6-0c69-4bdf-b441-f47c641bd9ee.XLSX', 610917, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 0, '2025-08-28 18:46:49', '2025-08-28 18:46:49');
INSERT INTO `upload_file` VALUES (7, 'xunzong', '2025届毕业生档案邮寄地址核对.XLSX', 'http://localhost:8080/api/common/file/download/e6f8027f-7284-4d9b-b3af-761a5e61085d.XLSX', 'G:\\uploads\\e6f8027f-7284-4d9b-b3af-761a5e61085d.XLSX', 610917, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 0, '2025-08-28 18:52:15', '2025-08-28 18:52:15');
INSERT INTO `upload_file` VALUES (8, 'xunzong', '2025届毕业生档案邮寄地址核对.XLSX', 'http://localhost:8080/api/common/file/download/3390b525-bc5d-432a-9daf-7061268c5e84.XLSX', 'G:\\uploads\\3390b525-bc5d-432a-9daf-7061268c5e84.XLSX', 610917, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 0, '2025-08-28 18:57:35', '2025-08-28 18:57:35');
INSERT INTO `upload_file` VALUES (9, 'xunzong', '2025届毕业生档案邮寄地址核对.XLSX', 'http://localhost:8080/api/common/file/download/bdfbcc07-6eae-4d03-abca-1fc81534f948.XLSX', 'G:\\uploads\\bdfbcc07-6eae-4d03-abca-1fc81534f948.XLSX', 610917, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 0, '2025-08-28 18:57:54', '2025-08-28 18:57:54');
INSERT INTO `upload_file` VALUES (10, 'xunzong', '2025届毕业生档案邮寄地址核对.XLSX', 'http://localhost:8080/api/common/file/download/8fc2eb91-1f93-4a72-a807-3dba45dd59a6.XLSX', 'G:\\uploads\\8fc2eb91-1f93-4a72-a807-3dba45dd59a6.XLSX', 610917, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 0, '2025-08-28 19:03:40', '2025-08-28 19:03:40');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` int NOT NULL,
  `role` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `last_login_time` datetime NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_pk`(`username` ASC) USING BTREE,
  UNIQUE INDEX `user_pk_2`(`email` ASC) USING BTREE,
  UNIQUE INDEX `user_pk_3`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'xunzong', '$2a$12$wo3s1LLf6uv8Q3x59BvhyuIYdsvsUHsnptgpMbT.p1pYFLb7DGJve', '2124159894@qq.com', '15779138804', 1, 'admin', '2025-08-30 14:29:39', '2025-08-15 14:16:04', '2025-08-15 14:16:04', '');
INSERT INTO `user` VALUES (2, 'xunzong1', '$2a$12$zU.5dJeAudftSmZQ9j9cCuFOk3RzN2NX0UShzSjG9AtvzacuBlEfm', '2124159895@qq.com', '15779138805', 1, 'user', '2025-08-29 10:49:58', '2025-08-23 14:17:24', '2025-08-23 14:17:24', '');
INSERT INTO `user` VALUES (5, '莫梓玥', '$2a$12$55rFrgK57AZ3hUHeahjCvewMSiOEDY0n5pciiPqpxn4AVNhvEEP4y', 'hm1i2v.onw24@yahoo.cn', '10509090551', 1, 'user', NULL, '2025-08-30 12:52:11', '2025-08-30 13:04:26', 'G:file/avatar/defaultAvatar.jpg');
INSERT INTO `user` VALUES (6, '祝乙萍', '$2a$12$TqdYt4p/qyRZNm6gGnR/yO.ALRpCBvIhF.9HFTf2Fw9HfLUBYgNW2', 'ixjlj1.ldk2@gmail.com', '19843953451', 1, 'user', NULL, '2025-08-30 14:28:27', '2025-08-30 14:28:27', 'G:file/avatar/defaultAvatar.jpg');

SET FOREIGN_KEY_CHECKS = 1;

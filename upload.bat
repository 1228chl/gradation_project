
@echo off
:: 设置 UTF-8 编码
chcp 65001 >nul

:: ========================================
:: 一键上传 Spring Boot JAR 到服务器
:: ========================================
setlocal

:: 项目配置
set PROJECT_DIR=G:\code\java\GraduationProjectOrderManagementSystem
set JAR_PATH=%PROJECT_DIR%\target\GraduationProjectOrderManagementSystem-0.0.1-SNAPSHOT.jar
set REMOTE_USER=root
set REMOTE_IP=106.52.6.93
set REMOTE_DIR=/www/graduation/new/
set DEPLOY_SCRIPT=/www/graduation/deploy.sh

echo.
echo [INFO] 开始部署...
echo ----------------------------------------

:: 检查 JAR
if not exist "%JAR_PATH%" (
    echo [ERROR] JAR 文件不存在：%JAR_PATH%
    goto END
)

:: 检查 scp
where scp >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] 未找到 scp 命令，请安装 OpenSSH 客户端。
    goto END
)

:: 清理远程旧文件
echo [INFO] 清理远程旧 JAR...
ssh -n "%REMOTE_USER%@%REMOTE_IP%" "rm -f %REMOTE_DIR%*.jar"

:: 上传
echo [INFO] 上传 JAR 文件...
scp "%JAR_PATH%" "%REMOTE_USER%@%REMOTE_IP%:%REMOTE_DIR%"
if %errorlevel% neq 0 goto END

:: 部署
echo [INFO] 执行远程部署脚本...
ssh -n "%REMOTE_USER%@%REMOTE_IP%" "bash %DEPLOY_SCRIPT%"
if %errorlevel% neq 0 goto END

echo [OK] 🎉 部署成功！

:END
echo.
pause
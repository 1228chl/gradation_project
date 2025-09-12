package com.graduationprojectordermanagementsystem.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.graduationprojectordermanagementsystem.exception.BaseException;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理 @RequestBody 参数校验失败 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMsg.append("[").append(fieldError.getField()).append("]: ").append(fieldError.getDefaultMessage()).append("; ");
        }
        log.warn("参数校验失败: {}", errorMsg);
        return Result.error(ResultCode.VALIDATE_FAILED, errorMsg.toString().trim());
    }

    /**
     * 处理请求参数类型不匹配（如 Long userId = "abc"）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String paramName = e.getParameter().getParameterName();
        Class<?> requiredType = e.getRequiredType();
        String value = e.getValue() != null ? e.getValue().toString() : "null";

        String message = "参数 '" + paramName + "' 类型错误，期望 " +
                (requiredType != null ? requiredType.getSimpleName() : "未知类型") +
                "，但收到值 '" + value + "'";
        log.warn("参数类型不匹配: {}", message);
        return Result.error(ResultCode.VALIDATE_FAILED, message);
    }

    /**
     * 处理必填参数缺失（@RequestParam 或 @PathVariable）
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<String> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        String paramName = e.getParameterName();
        String paramType = e.getParameterType();
        String message = "缺少必需参数：'" + paramName + "'（类型：" + paramType + "）";
        log.warn("缺少参数: {}", message);
        return Result.error(ResultCode.VALIDATE_FAILED, message);
    }

    /**
     * 处理 JSON 解析错误（Jackson 抛出）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("JSON解析失败: {}", e.getMessage());

        Throwable cause = e.getCause();

        if (cause instanceof InvalidFormatException ife) {
            String fieldPath = getFieldPath(ife);
            Object badValue = ife.getValue();
            Class<?> targetType = ife.getTargetType();
            String message = "字段 '" + fieldPath + "' 无法将 '" + badValue + "' 转为 " +
                    (targetType != null ? targetType.getSimpleName() : "目标类型");
            return Result.error(ResultCode.VALIDATE_FAILED, message);
        }

        if (cause instanceof MismatchedInputException mie) {
            if (mie.getMessage().contains("No content to map due to end-of-input")) {
                return Result.error(ResultCode.VALIDATE_FAILED, "请求体为空，请提交有效的 JSON 数据");
            }
            if (mie.getMessage().contains("Missing required creator property")) {
                String fieldName = mie.getPath().get(0).getFieldName();
                return Result.error(ResultCode.VALIDATE_FAILED, "缺少必需字段：'" + fieldName + "'");
            }
            String fieldPath = getFieldPath(mie);
            return Result.error(ResultCode.VALIDATE_FAILED, "JSON 结构错误：字段 '" + fieldPath + "' 类型不匹配");
        }

        if (cause instanceof JsonProcessingException jpe) {
            return Result.error(ResultCode.VALIDATE_FAILED, "JSON格式错误：" + jpe.getOriginalMessage());
        }

        return Result.error(ResultCode.VALIDATE_FAILED, "请求数据无法读取，请检查是否正确发送 JSON 数据");
    }

    /**
     * 提取 Jackson 异常中的字段路径
     */
    private String getFieldPath(JsonMappingException ex) {
        List<JsonMappingException.Reference> path = ex.getPath();
        if (path == null || path.isEmpty()) return "unknown";
        return path.stream()
                .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "index")
                .collect(Collectors.joining("."));
    }

    /**
     * 空指针异常（建议生产环境只返回通用提示）
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<String> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return Result.error(ResultCode.VALIDATE_FAILED, "请求数据包含空值，无法处理");
    }

    /**
     * 数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public Result<String> handleNumberFormatException(NumberFormatException e) {
        log.warn("数字格式错误: {}", e.getMessage());
        return Result.error(ResultCode.VALIDATE_FAILED, "数字格式错误：" + e.getMessage());
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public Result<String> handleClassCastException(ClassCastException e) {
        log.error("类型转换异常", e);
        return Result.error(ResultCode.VALIDATE_FAILED, "数据类型不兼容，无法转换");
    }

    /**
     * 数组越界等索引异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public Result<String> handleIndexOutOfBoundsException(IndexOutOfBoundsException e) {
        log.error("索引越界", e);
        return Result.error(ResultCode.VALIDATE_FAILED, "数据访问越界，请检查输入");
    }

    /**
     * 处理其他 BaseException 子类（通用兜底）
     */
    @ExceptionHandler(BaseException.class)
    public Result<String> handleBaseException(BaseException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(ResultCode.getByCode(e.getCode()),e.getMessage()); // 可扩展为带 code 的构造
    }

    /**
     * 【兜底】处理所有未预期的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleUnexpectedException(Exception e) {
        log.error("系统未捕获异常", e);
        return Result.error("系统繁忙，请稍后重试");
    }
}
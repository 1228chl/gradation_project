package com.graduationprojectordermanagementsystem.handler;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.graduationprojectordermanagementsystem.exception.BaseException;
import com.graduationprojectordermanagementsystem.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;


/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @RequestBody 参数校验失败 (@Valid 校验)
     * 如：@NotBlank, @NotNull 等注解不通过
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String field = fieldError.getField();
            String msg = fieldError.getDefaultMessage();
            errorMsg.append("[").append(field).append("]: ").append(msg).append("; ");
        }
        return Result.error("参数校验失败：" + errorMsg.toString().trim());
    }

    /**
     * 处理请求参数类型不匹配
     * 例如：接口期望 Long userId，但传了 "abc"
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String paramName = e.getParameter().getParameterName();
        Class<?> requiredType = e.getRequiredType();
        String value = e.getValue() != null ? e.getValue().toString() : "null";

        return Result.error("参数类型错误：'" + paramName + "' 期望类型为 " + requiredType.getSimpleName() + "，但收到的值 '" + value + "' 无法转换");
    }

    /**
     * 处理必填请求参数缺失（如 @RequestParam 或 @PathVariable）
     * 注意：@RequestBody 不触发此异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        String paramName = e.getParameterName();
        String paramType = e.getParameterType();
        return Result.error("缺少必需参数：'" + paramName + "'（类型：" + paramType + "）");
    }

    /**
     * 处理 JSON 解析错误（最常见：类型不匹配、格式错误、字段缺失）
     * 包括：
     * - JSON 语法错误
     * - 字符串转数字失败
     * - 枚举值不匹配
     * - 空 JSON
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: {}", e.getMessage());

        Throwable cause = e.getCause();

        // 1. 处理类型转换失败：如 "abc" -> int, 枚举值不匹配
        if (cause instanceof InvalidFormatException ife) {
            String fieldPath = getFieldPath(ife);
            Object badValue = ife.getValue();
            Class<?> targetType = ife.getTargetType();

            String message = "数据类型错误：字段 '" + fieldPath + "' 无法将值 '" + badValue +
                    "' 转换为 " + (targetType != null ? targetType.getSimpleName() : "目标类型") + "。";
            return Result.error(message);
        }

        // 2. 处理输入不匹配：如 null 赋给非空字段、空 body、JSON 结构错误
        if (cause instanceof MismatchedInputException mie) {
            // 情况 1：空请求体
            if (mie.getMessage().contains("No content to map due to end-of-input")) {
                return Result.error("请求体为空：请提交有效的 JSON 数据");
            }

            // 情况 2：缺少必需字段
            if (mie.getMessage().contains("Missing required creator property")) {
                String fieldName = mie.getPath().get(0).getFieldName();
                return Result.error("缺少必需参数：字段 '" + fieldName + "' 不能为空");
            }

            // 情况 3：JSON 类型不匹配，如期望对象却收到数组
            String fieldPath = getFieldPath(mie);
            return Result.error("JSON 结构错误：字段 '" + fieldPath + "' 类型不匹配，请检查数据格式");
        }

        // 3. 兜底：其他 Jackson 解析错误
        if (cause instanceof JsonProcessingException jpe) {
            return Result.error("JSON 解析失败：" + jpe.getOriginalMessage());
        }

        // 4. 非 Jackson 错误（如流关闭、body 缺失）
        return Result.error("请求数据无法读取，请检查是否正确发送 JSON 数据");
    }

    private String getFieldPath(JsonMappingException ex) {
        List<JsonMappingException.Reference> path = ex.getPath();
        if (path == null || path.isEmpty()) {
            return "unknown";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            String fieldName = path.get(i).getFieldName();
            if (fieldName != null) {
                sb.append(fieldName);
                if (i < path.size() - 1) {
                    sb.append(".");
                }
            }
        }
        return sb.toString();
    }


    /**
     * 捕获空指针异常（防止NPE导致500）
     * 可根据业务细化处理
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleNullPointerException(NullPointerException e) {
        // 生产环境不建议暴露具体位置
        return Result.error("请求数据包含空值，无法处理"+ e.getMessage());
    }

    /**
     * 数字格式异常（如 Integer.parseInt("abc")）
     */
    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleNumberFormatException(NumberFormatException e) {
        return Result.error("数字格式错误：无法解析数值 '" + e.getMessage() + "'");
    }

    /**
     * 类型转换异常（泛化兜底）
     */
    @ExceptionHandler(ClassCastException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleClassCastException(ClassCastException e) {
        return Result.error("数据类型不兼容，无法转换"+ e.getMessage());
    }

    /**
     * 数组越界等（防止意外崩溃）
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleIndexOutOfBoundsException(IndexOutOfBoundsException e) {
        return Result.error("数据越界，请检查输入内容"+ e.getMessage());
    }

    /**
     * 捕获业务异常
     */
    @ExceptionHandler
    public Result<String> exceptionHandler(BaseException e) {
        log.error("异常信息:{}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理所有未捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleUnexpectedException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error("系统内部错误，请联系管理员");
    }

}

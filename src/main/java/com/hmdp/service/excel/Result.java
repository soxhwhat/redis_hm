package com.hmdp.service.excel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private int errorCode;
    private String errorMessage;
    private T data;
    private boolean hasErrors;

    public Result() {
    }

    public Result(int errorCode, String errorMessage, boolean hasErrors) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.hasErrors = hasErrors;
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.errorCode = Status.SUCCESS.code;
        result.errorMessage = Status.SUCCESS.message;
        result.hasErrors = false;
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.data = data;
        result.errorCode = Status.SUCCESS.code;
        result.errorMessage = Status.SUCCESS.message;
        result.hasErrors = false;
        return result;
    }

    public static <T> Result<T> fail(T data) {
        Result<T> result = new Result<>();
        result.data = data;
        result.errorCode = Status.FAIL.code;
        result.errorMessage = Status.FAIL.message;
        result.hasErrors = true;
        return result;
    }

    public static <T> Result<T> error(T data) {
        Result<T> result = new Result<>();
        result.data = data;
        result.errorCode = Status.ERROR.code;
        result.errorMessage = Status.ERROR.message;
        result.hasErrors = true;
        return result;
    }
//    public static <T> Result<T> error(String message) {
//        Result<T> result = new Result<>();
//        result.errorCode = Status.ERROR.code;
//        result.errorMessage = message;
//        result.hasErrors = true;
//        return result;
//    }

    public static <T> Result<T> forbidden(T data) {
        Result<T> result = new Result<>();
        result.data = data;
        result.errorCode = Status.FORBIDDEN.code;
        result.errorMessage = Status.FORBIDDEN.message;
        result.hasErrors = true;
        return result;
    }

    public static <T> Result<T> duplicate(T data) {
        Result<T> result = new Result<>();
        result.data = data;
        result.errorCode = Status.DUPLICATE.code;
        result.errorMessage = Status.DUPLICATE.message;
        result.hasErrors = true;
        return result;
    }

    public enum Status {

        SUCCESS(1000, "请求成功"),
        FAIL(1001, "请求失败"),
        ERROR(1002, "内部错误"),
        ABSENCE(1003, "参数缺失"),
        ILLEGAL(1004, "参数错误"),
        DUPLICATE(1005, "数据重复"),
        FORBIDDEN(1006, "没有权限");

        private final int code;
        private final String message;

        Status(int value, String message) {
            this.code = value;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public boolean is(Result result) {
            return result != null && this.code == result.getErrorCode();
        }

    }

}

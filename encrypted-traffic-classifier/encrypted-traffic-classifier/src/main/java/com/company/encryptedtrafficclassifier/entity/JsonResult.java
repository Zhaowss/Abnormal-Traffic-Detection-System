package com.company.encryptedtrafficclassifier.entity;

import java.io.Serializable;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    private Object data;

    private Long count;

    public JsonResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public JsonResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static JsonResult error() {
        return new JsonResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "操作失败");
    }

    public static JsonResult error(Integer code) {
        return new JsonResult(code, "操作失败");
    }

    public static JsonResult error(String msg) {
        return new JsonResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static JsonResult error(Integer code, String msg) {
        return new JsonResult(code, msg);
    }

    public static JsonResult error(Integer code, String msg, Object data) {
        return new JsonResult(code, msg, data);
    }

    public static JsonResult ok() {
        return new JsonResult(HttpStatus.OK.value(), "操作成功");
    }

    public static JsonResult ok(String msg) {
        return new JsonResult(HttpStatus.OK.value(), msg);
    }

    public static JsonResult ok(Object data) {
        return new JsonResult(HttpStatus.OK.value(), "操作成功", data);
    }

    public static JsonResult ok(String msg, Object data) {
        return new JsonResult(HttpStatus.OK.value(), msg, data);
    }

    public static JsonResult ok(String msg, Object data, Long count) {
        return new JsonResult(HttpStatus.OK.value(), msg, data, count);
    }

}
package com.panda.aoodds.esport.api.entity;

/**
 * @Author carson
 * @DATE 2022/3/7 13:20
 **/
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败!"),
    VALIDATE_FAILED(404, "参数检验失败!"),
    UNAUTHORIZED(401, "暂未登录或token已经过期!"),
    FORBIDDEN(403, "没有相关权限!"),
    NEEDCLOSE(600,"需要关盘"),
    NONEEDCLOSE(601,"不需要关盘"),
    CHANGETRADEFAILED(602, "包含新增玩法，切换操盘方式失败!"),
    DUPLICATE_KEY(999, "唯一主键冲突!");

    private Integer code;
    private String message;

    private ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}

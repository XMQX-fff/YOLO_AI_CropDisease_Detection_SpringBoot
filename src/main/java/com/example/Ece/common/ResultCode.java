package com.example.Ece.common;

import lombok.Getter;

/**
 * 返回状态码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 参数错误 4xx
    PARAM_ERROR(400, "参数错误"),
    PARAM_IS_NULL(4001, "参数为空"),
    PARAM_TYPE_ERROR(4002, "参数类型错误"),

    // 认证授权错误 4xx
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    TOKEN_EXPIRED(4011, "Token已过期"),
    TOKEN_INVALID(4012, "Token无效"),

    // 资源错误 4xx
    NOT_FOUND(404, "资源不存在"),

    // 业务错误 5xx
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // AI 相关错误 6xx
    AI_API_ERROR(6001, "AI API调用失败"),
    AI_MODEL_NOT_FOUND(6002, "AI模型不存在"),
    AI_EMBEDDING_ERROR(6003, "文本向量化失败"),
    AI_CHAT_ERROR(6004, "AI对话失败"),

    // RAG 相关错误 7xx
    DOCUMENT_UPLOAD_ERROR(7001, "文档上传失败"),
    DOCUMENT_PARSE_ERROR(7002, "文档解析失败"),
    VECTOR_STORE_ERROR(7003, "向量存储失败"),
    VECTOR_SEARCH_ERROR(7004, "向量搜索失败"),

    // 文件相关错误 8xx
    FILE_NOT_FOUND(8001, "文件不存在"),
    FILE_TYPE_ERROR(8002, "文件类型不支持"),
    FILE_SIZE_ERROR(8003, "文件大小超出限制"),
    FILE_UPLOAD_ERROR(8004, "文件上传失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

package com.example.Ece.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 涓氬姟寮傚父绫? */
@Getter
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 閿欒鐮?     */
    private final Integer code;
    
    /**
     * 閿欒娑堟伅
     */
    private final String message;
    
    /**
     * HTTP鐘舵€佺爜
     */
    private final HttpStatus httpStatus;
    
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
        this.message = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    public BusinessException(Integer code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
    
    /**
     * 蹇€熷垱寤轰笟鍔″紓甯?     */
    public static BusinessException of(String message) {
        return new BusinessException(message);
    }
    
    public static BusinessException of(ResultCode resultCode) {
        return new BusinessException(resultCode);
    }
    
    public static BusinessException of(ResultCode resultCode, String message) {
        return new BusinessException(resultCode, message);
    }
}


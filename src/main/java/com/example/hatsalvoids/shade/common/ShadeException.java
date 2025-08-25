package com.example.hatsalvoids.shade.common;

import com.example.hatsalvoids.global.error.core.BaseException;

public class ShadeException extends BaseException {
    public ShadeException(ShadeErrorCode errorCode) {
        super(errorCode);
    }
    public ShadeException(ShadeErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
} 
package com.sku_sku.backend.exception;

public class S3PresignedException extends RuntimeException{
    public S3PresignedException(String message){
        super(message);
    }
}

package com.sku_sku.backend.enums;

import java.util.Arrays;

public enum AllowedFileType {
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png"),
    GIF("gif", "image/gif"),
    WEBP("webp", "image/webp"),

    PDF("pdf", "application/pdf"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    TXT("txt", "text/plain"),

    ZIP("zip", "application/zip"),
    RAR("rar", "application/vnd.rar"),
    SEVEN_Z("7z", "application/x-7z-compressed");

    private final String extension;
    private final String mimeType;

    AllowedFileType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    // 확장자 또는 MIME 타입 기준 검사
    public static boolean isAllowedExtension(String ext) {
        return Arrays.stream(values()).anyMatch(type -> type.extension.equalsIgnoreCase(ext));
    }

    public static boolean isAllowedMimeType(String mime) {
        return Arrays.stream(values()).anyMatch(type -> type.mimeType.equalsIgnoreCase(mime));
    }

    public static boolean isAllowed(String ext, String mime) {
        return Arrays.stream(values()).anyMatch(type ->
                type.extension.equalsIgnoreCase(ext) && type.mimeType.equalsIgnoreCase(mime));
    }
}
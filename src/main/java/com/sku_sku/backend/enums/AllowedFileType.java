package com.sku_sku.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonCreator
    public static AllowedFileType from(String input) {
        if (input == null) return null;

        return Arrays.stream(values())
                .filter(type ->
                        type.name().equalsIgnoreCase(input) ||
                                type.extension.equalsIgnoreCase(input) ||
                                type.mimeType.equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("허용되지 않은 파일 타입: " + input));
    }

    @JsonValue
    public String toValue() {
        return name();
    }

    public static boolean isAllowedMimeType(String mime) {
        return Arrays.stream(values())
                .anyMatch(type -> type.mimeType.equalsIgnoreCase(mime));
    }

    public static boolean isAllowedExtension(String ext) {
        return Arrays.stream(values())
                .anyMatch(type -> type.extension.equalsIgnoreCase(ext));
    }

    public static boolean isAllowed(String ext, String mime) {
        return Arrays.stream(values())
                .anyMatch(type -> type.extension.equalsIgnoreCase(ext)
                        && type.mimeType.equalsIgnoreCase(mime));
    }
}

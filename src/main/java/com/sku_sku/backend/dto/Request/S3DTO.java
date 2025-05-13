package com.sku_sku.backend.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class S3DTO {

    @Data
    public static class PresignedUrlRequest {
        @Schema(description = "파일 이름", example = "lecture1.pdf")
        private String fileName;

        @Schema(description = "MIME 타입", example = "application/pdf")
        private String mimeType;
    }
}

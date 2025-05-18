package com.sku_sku.backend.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public class S3DTO {

    @Data
    public static class PresignedUrlRequest {
        @Schema(description = "파일 이름", example = "lecture1.pdf")
        private String fileName;

        @Schema(description = "MIME 타입", example = "application/pdf")
        private String mimeType;
    }

    @Data
    @Builder
    public static class PresignedUrlResponse {
        @Schema(description = "Presigned PUT URL")
        private String uploadUrl;

        @Schema(description = "CDN으로 접근할 URL")
        private String cdnUrl;

        @Schema(description = "업로드된 파일의 키")
        private String key;
    }
}

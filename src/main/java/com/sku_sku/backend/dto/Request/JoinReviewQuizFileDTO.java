package com.sku_sku.backend.dto.Request;

import com.sku_sku.backend.enums.AllowedFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class JoinReviewQuizFileDTO {

    @Data
    public static class JoinReviewQuizFileField{
        @Schema(description = "퀴즈 첨부 파일 이름", example = "Spring.pdf")
        private String fileName;
        @Schema(description = "퀴즈 첨부 파일 유형", example = "PDF")
        private AllowedFileType fileType;
        @Schema(description = "퀴즈 첨부 파일 사이즈", example = "65362")
        private Long fileSize;
        @Schema(description = "퀴즈 첨부 파일 CDN URL", example = "https://~~~")
        private String fileUrl;
        @Schema(description = "퀴즈 첨부 파일 저장된 경로", example = "uploads/{uuid}.png")
        private String fileKey;
    }
}

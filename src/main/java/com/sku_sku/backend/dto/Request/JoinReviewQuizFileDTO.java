package com.sku_sku.backend.dto.Request;

import com.sku_sku.backend.enums.AllowedFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class JoinReviewQuizFileDTO {

    @Data
    public static class JoinReviewQuizFileField{
        @Schema(description = "퀴즈 첨부 자료 CDN URL", example = "")
        private String fileUrl;
        @Schema(description = "퀴즈 첨부 자료 이름", example = "Spring.pdf")
        private String fileName;
        @Schema(description = "퀴즈 첨부 자료 유형", example = "application/pdf")
        private AllowedFileType fileType;
        @Schema(description = "퀴즈 첨부 자료 사이즈", example = "65362")
        private Long fileSize;
    }
}

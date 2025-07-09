package com.sku_sku.backend.dto.Request;

import com.sku_sku.backend.enums.AllowedFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

public class JoinSubmitAssignmentFileDTO {

    @Data
    @AllArgsConstructor
    public static class submitAssignmentFileDTO {
        @Schema(description = "제출한 과제 파일 이름", example = "Spring.pdf")
        private String fileName;
        @Schema(description = "제출한 과제 파일 유형", example = "PDF")
        private AllowedFileType fileType;
        @Schema(description = "제출한 과제 파일 사이즈", example = "65362")
        private Long fileSize;
        @Schema(description = "제출한 과제 파일 CDN URL", example = "https://~~~")
        private String fileUrl;
        @Schema(description = "제출한 과제 파일 저장된 경로", example = "uploads/{uuid}.png")
        private String fileKey;
    }

    @Data
    public static class UpdateSubmitAssignmentFileDTO {
        @Schema(description = "강의자료 이름", example = "Spring.pdf")
        private String fileName;
        @Schema(description = "강의자료 유형", example = "PDF")
        private AllowedFileType fileType;
        @Schema(description = "강의자료 사이즈", example = "65362")
        private Long fileSize;
        @Schema(description = "강의자료 CDN URL", example = "https://~~~")
        private String fileUrl;
        @Schema(description = "프로젝트 사진 저장된 경로", example = "uploads/{uuid}.png")
        private String fileKey;
        @Schema(description = "프로젝트 사진 유지 정보", example = "KEEP or DELETE or NEW")
        private AllowedFileType status;
    }
}

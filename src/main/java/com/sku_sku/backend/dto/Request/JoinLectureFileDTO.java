package com.sku_sku.backend.dto.Request;

import com.sku_sku.backend.enums.AllowedFileType;
import com.sku_sku.backend.enums.FileStatusType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public class JoinLectureFileDTO {

    @Data
    public static class LectureFileDTO {
        @Schema(description = "강의자료 이름", example = "Spring.pdf")
        private String fileName;
        @Schema(description = "강의자료 유형", example = "application/pdf")
        private AllowedFileType fileType;
        @Schema(description = "강의자료 사이즈", example = "65362")
        private Long fileSize;
        @Schema(description = "강의자료 CDN URL", example = "https://~~~")
        private String fileUrl;
        @Schema(description = "프로젝트 사진 저장된 경로", example = "uploads/{uuid}.png")
        private String fileKey;
    }

    @Data
    public static class UpdateLectureFileDTO {
        @Schema(description = "강의자료 이름", example = "Spring.pdf")
        private String fileName;
        @Schema(description = "강의자료 유형", example = "application/pdf")
        private AllowedFileType fileType;
        @Schema(description = "강의자료 사이즈", example = "65362")
        private Long fileSize;
        @Schema(description = "강의자료 CDN URL", example = "https://~~~")
        private String fileUrl;
        @Schema(description = "프로젝트 사진 저장된 경로", example = "uploads/{uuid}.png")
        private String fileKey;
        @Schema(description = "프로젝트 사진 유지 정보", example = "KEEP or DELETE or NEW")
        private FileStatusType status;
    }
}

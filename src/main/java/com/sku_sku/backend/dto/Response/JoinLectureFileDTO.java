package com.sku_sku.backend.dto.Response;

import com.sku_sku.backend.enums.AllowedFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public class JoinLectureFileDTO {

    @Data
    @Builder
    public static class LectureFileDTOWithoutFileKey {
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
    }
}

package com.sku_sku.backend.dto.Request;

import com.sku_sku.backend.domain.lecture.JoinLectureFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class JoinLectureFilesDTO {

    @Data
    public static class LectureFileDTO {
        @Schema(description = "강의자료 CDN URL", example = "")
        private String fileUrl;
        @Schema(description = "강의자료 이름", example = "Spring.pdf")
        private String fileName;
        @Schema(description = "강의자료 유형", example = "application/pdf")
        private String fileType;
        @Schema(description = "강의자료 사이즈", example = "65362")
        private Long fileSize;
        @Schema(description = "프로젝트 사진 저장된 경로", example = "uploads/uuid.png")
        private String fileKey;
    }

    @Data
    public static class CreateJoinLectureFilesRequest {
        @Schema(description = "강의 안내물 id", example = "1")
        private Long lectureId;
        @Schema(description = "강의자료 CDN URL", example = "")
        private String fileUrl;
        @Schema(description = "강의자료 이름", example = "Spring.pdf")
        private String fileName;
        @Schema(description = "강의자료 유형", example = "application/pdf")
        private String fileType;
        @Schema(description = "강의자료 사이즈", example = "65362")
        private Long fileSize;
        @Schema(description = "프로젝트 사진 저장된 경로", example = "uploads/uuid.png")
        private String fileKey;

        public CreateJoinLectureFilesRequest(JoinLectureFile joinLectureFile) {
            this.lectureId = joinLectureFile.getId();
            this.fileUrl = joinLectureFile.getFileUrl();
            this.fileName = joinLectureFile.getFileName();
            this.fileType = joinLectureFile.getFileType();
            this.fileSize = joinLectureFile.getFileSize();
            this.fileKey = joinLectureFile.getFilekey();
        }
    }
}

package com.sku_sku.backend.dto.Request;

import com.sku_sku.backend.enums.TrackType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class LectureDTO {
    @Data
    public static class createLectureRequest {
        @Schema(description = "트랙", example = "BACKEND or FRONTEND or PM_DESIGN")
        private TrackType trackType;
        @Schema(description = "강의 안내물 제목", example = "백엔드 3주차")
        private String title;
        @Schema(description = "강의 안내물 파일", example = "파일 넣으셔")
        private List<MultipartFile> files;
    }

    @Data
    public static class updateLectureRequest {
        @Schema(description = "강의 안내물 id", example = "1")
        private Long id;
        @Schema(description = "트랙", example = "BACKEND or FRONTEND or PM_DESIGN")
        private TrackType trackType;
        @Schema(description = "강의 안내물 제목", example = "백엔드 3주차")
        private String title;
        @Schema(description = "강의자료", example = "파일 넣으셔")
        private List<MultipartFile> files;
    }
}

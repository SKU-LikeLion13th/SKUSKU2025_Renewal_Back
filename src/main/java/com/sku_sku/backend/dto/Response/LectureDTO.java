package com.sku_sku.backend.dto.Response;

import com.sku_sku.backend.enums.TrackType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class LectureDTO {

    @Data
    @Builder
    public static class ResponseLecture {
        @Schema(description = "강의 안내물 id", example = "1")
        private Long id;
        @Schema(description = "트랙", example = "BACKEND or FRONTEND or DESIGN")
        private TrackType trackType;
        @Schema(description = "강의 안내물 제목", example = "백엔드 3주차")
        private String title;
        @Schema(description = "강의 안내물 내용", example = "오늘은 JPA에 대해서 배워요~")
        private String content;
        @Schema(description = "강의 안내물 작성자", example = "한민규")
        private String writer;
        @Schema(description = "강의 안내물 작성 시간", example = "YYYY-MM-DD")
        private LocalDateTime createDateTime;
        @Schema(description = "강의자료", example = "")
        private List<JoinLectureFileDTO.LectureFileDTOWithoutFileKey> joinLectureFiles;
    }

    @Data
    @AllArgsConstructor
    public static class ResponseLectureIncludeFileKey {
        @Schema(description = "강의 안내물 id", example = "1")
        private Long id;
        @Schema(description = "트랙", example = "BACKEND or FRONTEND or DESIGN")
        private TrackType trackType;
        @Schema(description = "강의 안내물 제목", example = "백엔드 3주차")
        private String title;
        @Schema(description = "강의 안내물 내용", example = "오늘은 JPA에 대해서 배워요~")
        private String content;
        @Schema(description = "강의 안내물 작성자", example = "한민규")
        private String writer;
        @Schema(description = "강의 안내물 작성 시간", example = "YYYY-MM-DD")
        private LocalDateTime createDateTime;
        @Schema(description = "프로젝트 사진 저장된 경로", example = "uploads/{uuid}.png")
        private String fileKey;
    }
}

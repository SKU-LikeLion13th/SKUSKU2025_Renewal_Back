package com.sku_sku.backend.dto.Response;

import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.dto.Request.JoinLectureFilesDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class LectureDTO {

    @Data
    @AllArgsConstructor
    public static class ResponseLecture {
        @Schema(description = "강의 안내물 id", example = "1")
        private Long id;
        @Schema(description = "트랙", example = "BACKEND or FRONTEND or PM_DESIGN")
        private TrackType trackType;
        @Schema(description = "강의 안내물 제목", example = "백엔드 3주차")
        private String title;
        @Schema(description = "강의 안내물 작성자", example = "한민규")
        private String writer;
        @Schema(description = "강의 안내물 작성 시간", example = "YYYY-MM-DD")
        private LocalDateTime createDate;
        @Schema(description = "강의자료", example = "")
        private List<JoinLectureFilesDTO.CreateJoinLectureFilesRequest> joinLectureFiles;
    }

    @Data
    @AllArgsConstructor
    public static class ResponseLectureWithoutFiles {
        @Schema(description = "강의 안내물 id", example = "1")
        private Long id;
        @Schema(description = "강의 안내물 제목", example = "백엔드 3주차")
        private String title;
        @Schema(description = "강의 안내물 작성자", example = "한민규")
        private String writer;
        @Schema(description = "강의 안내물 작성 시간", example = "YYYY-MM-DD")
        private LocalDateTime createDate;
    }
}

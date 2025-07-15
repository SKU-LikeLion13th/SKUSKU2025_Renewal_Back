package com.sku_sku.backend.dto.Request;

import com.sku_sku.backend.enums.PassNonePass;
import com.sku_sku.backend.enums.QuizType;
import com.sku_sku.backend.enums.TrackType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

public class AssignmentDTO {


    @Data
    public static class UploadAssignment{
        @NonNull
        @Schema(description = "과제 제목", example = "1주차 과제")
        private String title;
        @Schema(description = "과제 설명", example = "스프링 의존성에 대해 정리해주세요")
        private String description;
        @Schema(description = "과제 타입", example = "ESSAY_QUESTION")
        private QuizType quizType;
        @Schema(description = "트랙 타입", example = "BACKEND")
        private TrackType trackType;
        @Schema(description = "파일", example = "파일 넣어")
        private List<JoinAssignmentFileDTO.AssignmentFileDTO> files;
    }

    @Data
    public static class CheckSubmittedAssignment{
        @Schema(description = "제출된 과제 id", example = "1")
        private Long submitAssignmentId;
        @Schema(description = "피드백", example = "이런 부분 수정 요망")
        private String feedback;
        @Schema(description = "운영진 확인", example = "PASS")
        private PassNonePass passNonePass;
    }

    @Data
    public static class UpdateAssignment{
        @Schema(description = "업로드 된 과제 id", example = "1")
        private Long assignmentId;
        @Schema(description = "과제 제목", example = "1주차 과제")
        private String title;
        @Schema(description = "트랙 타입", example = "BACKEND")
        private TrackType trackType;
        @Schema(description = "과제 설명", example = "스프링 의존성에 대해 정리해주세요")
        private String description;
        @Schema(description = "과제 타입", example = "ESSAY_QUESTION")
        private QuizType quizType;
        @Schema(description = "파일", example = "파일 넣어")
        private List<JoinAssignmentFileDTO.UpdateAssignmentFileDTO> files;
    }


}

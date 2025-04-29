package com.sku_sku.backend.dto.Response;

import com.sku_sku.backend.enums.PassNonePass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class AssignmentDTO {
    @Data
    public static class AssignmentRes{
        @Schema(description = "과제 id", example = "1")
        private Long assignmentId;
        @Schema(description = "과제 제목", example = "1주차 과제")
        private String title;
        @Schema(description = "제출 여부", example = "False")
        private Boolean isSubmit;
        @Schema(description = "과제 설명", example = "스프링 의존성에 대해 정리해주세요")
        private String description;
        @Schema(description = "운영진 확인", example = "PASS")
        private PassNonePass adminCheck;
    }

    @Data
    public static class SubmittedAssignmentLion{
        @Schema(description = "아기사자 이름", example = "홍민갱")
        private String lionName;
        @Schema(description = "제출된 과제 Id", example = "1")
        private Long submitAssignmentId;
        @Schema(description = "운영진 확인", example = "PASS")
        private PassNonePass passNonePass;
    }

    @Data
    public static class FeedbackDetailRes{
        @Schema(description = "과제 제목", example = "1주차 과제")
        private String title;
        @Schema(description = "과제 설명", example = "과제 안내 드려요 스프링 의존성~")
        private String description;
        @Schema(description = "피드백", example = "이 부분은 살짝 부족한거같네요")
        private String feedback;
    }

    @Data
    public static class AssignmentDetail{
        @Schema(description = "과제 설명", example = "과제 안내 드려요 스프링 의존성~")
        private String description;
    }

}

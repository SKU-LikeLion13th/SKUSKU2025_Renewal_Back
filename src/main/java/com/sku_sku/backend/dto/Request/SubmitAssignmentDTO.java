package com.sku_sku.backend.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

public class SubmitAssignmentDTO {

    @Data
    public static class SubmitAssignment{
        @Schema(description = "과제 id", example = "1")
        private Long assignmentId;
        @Schema(description = "과제 작성 내용", example = "주관식 답변")
        private String content;
        @Schema(description = "파일", example = "[스프링 의존성.java, 캡쳐.jpg]")
        private List<JoinSubmitAssignmentFileDTO.submitAssignmentFileDTO> files;
    }

    @Data
    public static class UpdateSubmitAssignment{
        @Schema(description = "제출된 과제 id", example = "1")
        private Long submitAssignmentId;
        @Schema(description = "과제 작성 내용", example = "주관식 답변")
        private String content;
        @Schema(description = "파일", example = "[스프링 의존성.java, 캡쳐.jpg]")
        private List<JoinSubmitAssignmentFileDTO.UpdateSubmitAssignmentFileDTO> files;
    }

}

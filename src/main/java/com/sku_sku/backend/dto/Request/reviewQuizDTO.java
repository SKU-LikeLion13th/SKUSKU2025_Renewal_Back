package com.sku_sku.backend.dto.Request;

import com.sku_sku.backend.enums.QuizType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
public class reviewQuizDTO {
    @Schema(description = "문제 타입(객관식, 주관식)", example = "MULTIPLE_CHOICE / ESSAY_QUESTION")
    private QuizType quizType;
    @Schema(description = "문제 내용", example = "스프링의 계층구조는?")
    private String content;
    @Schema(description = "보기 리스트(문자열 리스트)", example = """
                        [
                        "controller-service-repository",\
                        "model-view-controller",\
                        "model-template-view",\
                        "controller-service-model",\
                        "view-service-interface"\
                        ]""")
    private List<String> answerChoiceList;
    @Schema(description = "정답", example = "controller-service-repository")
    private String answer;
    @Schema(description = "운영진이 추가로 올리는 파일(없으면 null)", example = "계층구조.png")
    private List<MultipartFile> files;
    @Schema(description = "해설", example = "스프링의 계층구조는 controller-service-repository로 구성되어있습니다.")
    private String explanation;
}

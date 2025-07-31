package com.sku_sku.backend.dto.Request;

import com.google.type.DateTime;
import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.enums.QuizType;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.enums.UpdateQuizStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewQuizDTO {

    @Data
    public static class AddQuizRequest {
        @Schema(description = "복습퀴즈 메인 제목", example="1주차 복습퀴즈")
        private String title;
        @Schema(description = "트랙-복습퀴즈", example = "BACKEND")
        private TrackType trackType;
        @Schema(description = "복습퀴즈 문제들")
        private List<reviewQuizDTO> reviewQuizDTOList;
    }

    @Data
    public static class UpdateQuizRequest {
        @Schema(description = "복습퀴즈 메인 제목", example="1주차 복습퀴즈")
        private String title;
        @Schema(description = "트랙-복습퀴즈", example = "BACKEND")
        private TrackType trackType;
        @Schema(description = "복습퀴즈 문제들")
        private List<ShowReviewQuizDetails> showReviewQuizDTOList;
    }

    @Data
    public static class reviewQuizDTO {
        @Schema(description = "문제 타입(객관식, 주관식)", example = "MULTIPLE_CHOICE / ESSAY_QUESTION")
        private QuizType quizType;
        @Schema(description = "문제 내용", example = "스프링의 계층구조는?")
        private String content;
        @Schema(description = "보기 리스트(문자열 리스트)", example = """
                        (객관식이 아니면 빈 리스트)
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
        @Schema(description = "운영진이 추가로 올리는 파일(없으면 빈리스트)", example = "계층구조.png")
        private List<JoinReviewQuizFileDTO.JoinReviewQuizFileField> files;
        @Schema(description = "해설", example = "스프링의 계층구조는 controller-service-repository로 구성되어있습니다.")
        private String explanation;
    }

    @Data
    public static class ShowReviewQuizDetails {
        @Schema(description = "문제 Id", example = "1")
        private Long id;
        @Schema(description = "문제 타입(객관식, 주관식)", example = "MULTIPLE_CHOICE / ESSAY_QUESTION")
        private QuizType quizType;
        @Schema(description = "문제 내용", example = "스프링의 계층구조는?")
        private String content;
        @Schema(description = "보기 리스트(문자열 리스트)", example = """
                        (객관식이 아니면 빈 리스트)
                        [
                        "controller-service-repository",\
                        "model-view-controller",\
                        "model-template-view",\
                        "controller-service-model",\
                        "view-service-interface"\
                        ]""")
        private List<String> answerChoiceList;
        @Schema(description = "운영진이 추가로 올리는 파일(없으면 빈리스트)", example = "계층구조.png")
        private List<JoinReviewQuizFileDTO.JoinReviewQuizFileField> files;
        @Schema(description = "정답", example = "controller-service-repository")
        private String answer;
        @Schema(description = "해설", example = "스프링의 계층구조는 controller-service-repository로 구성되어있습니다.")
        private String explanation;
        @Schema(description = "해설")
        private String response;
    }


    @Data
    public static class SolveAnswerList {
        List<QuizAnswerList> quizAnswerList;
        Integer score;  //맞은 개수
        Integer total; //총 개수
    }

    @Data
    public static class QuizAnswerList{
        Long quizId;
        String answer;
        String explanation;
    }

    @Data
    public static class SolveRequest{
        Long reviewWeekId;
        List<QuizResponse> quizResponseList;
    }

    @Data
    public static class QuizResponse{
        Long quizId;
        String quizAnswer;

    }

    @Data
    public static class reviewQuizEditDTO {
        @Schema(description = "기존 문제 ID (신규 등록일 경우 null)", example = "402")
        private Long reviewQuizId;

        @Schema(description = "처리 상태 (CREATE / UPDATE / DELETE)", example = "UPDATE")
        private UpdateQuizStatus status;

        @Schema(description = "문제 타입(객관식, 주관식)", example = "MULTIPLE_CHOICE / ESSAY_QUESTION")
        private QuizType quizType;

        @Schema(description = "문제 내용", example = "스프링의 계층구조는?")
        private String content;

        @Schema(description = "보기 리스트(객관식일 경우 필수)", example = """
        [
          "controller-service-repository",
          "model-view-controller",
          "model-template-view"
        ]""")
        private List<String> answerChoiceList;

        @Schema(description = "정답", example = "controller-service-repository")
        private String answer;

        @Schema(description = "첨부 파일 리스트", example = "계층구조.png")
        private List<JoinReviewQuizFileDTO.JoinReviewQuizFileField> files;

        @Schema(description = "해설", example = "controller-service-repository 구조입니다.")
        private String explanation;
    }

    @Data
    public static class EditQuizRequest {
        @Schema(description = "복습퀴즈 메인 제목", example="10주차 복습퀴즈")
        private String title;

        @Schema(description = "트랙 타입", example="FRONTEND")
        private TrackType trackType;

        @Schema(description = "문제 리스트 (status 포함)", required = true)
        private List<reviewQuizEditDTO> reviewQuizDTOList;
    }

    @Data
    public static class GetLionReviewQuiz {
        @Schema(description = "퀴즈 제목", example="10주차 복습퀴즈")
        private String title;
        @Schema(description = "아기사자목록")
        private List<LionQuizList> lionQuizList;
    }

    @Data
    public static class LionQuizList {
        @Schema(description = "아기사자 이름")
        private String lionName;
        @Schema(description = "푼 횟수")
        private int count;
        @Schema(description = "맞은 개수")
        private Integer score;  //맞은 개수
        @Schema(description = "총 개수")
        private Integer total; //총 개수
        @Schema(description = "최근 푼 날짜")
        private LocalDateTime updateDate;
    }

}

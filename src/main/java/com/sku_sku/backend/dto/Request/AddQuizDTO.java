package com.sku_sku.backend.dto.Request;

import com.sku_sku.backend.enums.TrackType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

public class AddQuizDTO {
/*
{
	"title":"1주차 복습퀴즈",
	"reviewQuizList":[
		{
			//"questionNum":1,
			"quizType": "MULTIPLE_CHOICE",
			"content":"스프링의 계층구조는?",
			"AnswerChoiceList":[
				"controller-service-repository",
				"model-view-controller",
				"model-template-view",
				"controller-service-model",
				"view-service-interface"
				]
			"answer":"controller-service-repository",
			"file":null,
			"explanation":"스프링의 계층구조는 controller-service-repository로 구성되어있습니다.",
		},
*/
    @Data
    public static class AddQuizRequest {
        @Schema(description = "복습퀴즈 메인 제목", example="1주차 복습퀴즈")
        private String title;
        @Schema(description = "트랙-복습퀴즈", example = "BACKEND")
        private TrackType trackType;
        @Schema(description = "복습퀴즈 문제들")
        private List<reviewQuizDTO> reviewQuizDTOList;
    }
}

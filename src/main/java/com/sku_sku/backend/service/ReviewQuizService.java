package com.sku_sku.backend.service;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.domain.reviewquiz.*;
import com.sku_sku.backend.dto.Request.JoinReviewQuizFileDTO;
import com.sku_sku.backend.dto.Request.ReviewQuizDTO;
import com.sku_sku.backend.enums.AnswerStatus;
import com.sku_sku.backend.enums.QuizType;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sku_sku.backend.dto.Request.JoinReviewQuizFileDTO.*;
import static com.sku_sku.backend.dto.Request.ReviewQuizDTO.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewQuizService  {

    private final ReviewWeekRepository reviewWeekRepository;
    private final ReviewQuizRepository reviewQuizRepository;
    private final JoinReviewQuizFileRepository joinReviewQuizFileRepository;
    private final AnswerChoiceRepository answerChoiceRepository;
    private final LionService lionService;
    private final ReviewQuizResponseRepository reviewQuizResponseRepository;
    private final GeminiService geminiService;

    @Transactional
    public void addQuiz(AddQuizRequest req) {
        ReviewWeek reviewWeek = new ReviewWeek(req.getTrackType(), req.getTitle());
        reviewWeekRepository.save(reviewWeek);
        for (reviewQuizDTO reviewQuizDTO : req.getReviewQuizDTOList()) {
            ReviewQuiz reviewQuiz = new ReviewQuiz(
                    reviewWeek,
                    reviewQuizDTO.getContent(),
                    reviewQuizDTO.getExplanation(),
                    reviewQuizDTO.getAnswer(),
                    reviewQuizDTO.getQuizType());
            reviewQuizRepository.save(reviewQuiz);

            if (reviewQuizDTO.getFiles()!=null) {
                for (JoinReviewQuizFileField file : reviewQuizDTO.getFiles()) {
                    JoinReviewQuizFile joinReviewQuizFile = new JoinReviewQuizFile(
                            reviewQuiz,
                            file.getFileUrl(),
                            file.getFileName(),
                            file.getFileType(),
                            file.getFileSize());
                    joinReviewQuizFileRepository.save(joinReviewQuizFile);
                }
            }
            if (reviewQuizDTO.getQuizType()== QuizType.MULTIPLE_CHOICE){
                for(String StringAnswerChoice : reviewQuizDTO.getAnswerChoiceList()){
                    AnswerChoice answerChoice = new AnswerChoice(reviewQuiz, StringAnswerChoice);
                    answerChoiceRepository.save(answerChoice);
                }
            }
        }
    }

    @Transactional
    public SolveAnswerList solveReviewQuiz(String token, SolveRequest solveRequest) {
        Lion lion = lionService.tokenToLion(token);
        System.out.println("트랙타입: " + lion.getTrackType());
        System.out.println(solveRequest.getReviewWeekId());

        List<ReviewQuiz> reviewQuizzes = reviewQuizRepository.findByTrackTypeAndReviewWeek(lion.getTrackType(),solveRequest.getReviewWeekId());
        List<QuizResponse> userAnswers = solveRequest.getQuizResponseList();
        System.out.println(reviewQuizzes.size());

        SolveAnswerList solveAnswerList = new SolveAnswerList();
        solveAnswerList.setQuizAnswerList(new ArrayList<>());

        int correctCount = 0;
        int multipleChoiceTotal = 0;

        for (int i = 0; i < reviewQuizzes.size(); i++) {
            ReviewQuiz quiz = reviewQuizzes.get(i);
            QuizResponse lionAnswerDTO = userAnswers.get(i);

            ReviewQuizResponse existingResponse = reviewQuizResponseRepository.findReviewQuizResponseByLionAndReviewQuiz(lion, quiz);
            System.out.println(existingResponse);
            ReviewQuizResponse response;


            if(existingResponse==null){
                //새로 풀때
                //response = gradeReviewQuiz(lion, quiz, lionAnswerDTO.getQuizAnswer());
                response = new ReviewQuizResponse(lion, quiz, lionAnswerDTO.getQuizAnswer(), quiz.getQuizType());

                if (quiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
                    multipleChoiceTotal++;
                    System.out.println("정답: " + quiz.getAnswer());
                    System.out.println("사용자 답변: " + lionAnswerDTO.getQuizAnswer());
                    if (Objects.equals(quiz.getAnswer(), lionAnswerDTO.getQuizAnswer())) {
                        response.setAnswerStatus(AnswerStatus.TRUE);
                        correctCount++;
                        System.out.println(correctCount);
                    } else {
                        response.setAnswerStatus(AnswerStatus.FALSE);
                    }
                } else if (quiz.getQuizType() == QuizType.ESSAY_QUESTION) {
                    // 주관식 문제인 경우 Gemini API를 사용하여 평가
                    AnswerStatus answerStatus = evaluateEssayQuestion(quiz, lionAnswerDTO.getQuizAnswer());
                    response.setAnswerStatus(answerStatus);
                    
                    if (answerStatus == AnswerStatus.TRUE) {
                        correctCount++; // 정답으로 평가되었으면 점수 추가
                    }
                } else {
                    response.setAnswerStatus(AnswerStatus.EMPTY); // 다른 타입(파일 첨부 등)은 채점 안 함
                }
                reviewQuizResponseRepository.save(response);
            }

            else{//이미 푼 경우
                response = existingResponse;
                if (quiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
                    multipleChoiceTotal++;
                    if (response.getAnswerStatus() == AnswerStatus.TRUE) {
                        correctCount++;
                    }
                } else if (quiz.getQuizType() == QuizType.ESSAY_QUESTION) {
                    AnswerStatus answerStatus = evaluateEssayQuestion(quiz, lionAnswerDTO.getQuizAnswer());
                    
                    if (answerStatus == AnswerStatus.TRUE) {
                        correctCount++;
                    }
                }
                response.setUpdateDate(LocalDateTime.now());
                response.setCount(response.getCount() + 1);
            }
            //정답과 해설 세팅
            QuizAnswerList quizAnswerList = new QuizAnswerList();
            quizAnswerList.setQuizId(quiz.getId());
            quizAnswerList.setAnswer(quiz.getAnswer());
            quizAnswerList.setExplanation(quiz.getExplanation());

            solveAnswerList.getQuizAnswerList().add(quizAnswerList);

        }
        solveAnswerList.setScore(correctCount);
        solveAnswerList.setMultipleTotal(multipleChoiceTotal);
        return solveAnswerList;
    }

    /**
     * 주관식 문제를 Gemini API사용해서 평가하기.
     * @param quiz 평가할 문제
     * @param userAnswer 사용자 답변
     */
    private AnswerStatus evaluateEssayQuestion(ReviewQuiz quiz, String userAnswer) {
        try {
            // 문제의 내용(content)과 정답(answer)을 모두 Gemini에 전달
            boolean isCorrect = geminiService.evaluateEssayAnswer(quiz.getContent(), quiz.getAnswer(), userAnswer);
            if (isCorrect) {
                log.info("AI evaluated answer as correct for quiz ID: {}", quiz.getId());
                return AnswerStatus.TRUE;
            } else {
                log.info("AI evaluated answer as incorrect for quiz ID: {}", quiz.getId());
                return AnswerStatus.FALSE;
            }
        } catch (Exception e) {
            log.error("Error evaluating essay answer with Gemini API", e);
            return AnswerStatus.EMPTY; // API 오류 시 기본값으로 설정
        }
    }



//    //주차별 퀴즈 목록 조회
//    public List<ReviewWeekDTO.showReviewWeek> getReviewWeek(String token) {
//        Lion lion = lionService.tokenToLion(token.substring(7));//아기사자 찾기
//        List<ReviewWeek> reviewWeekList = reviewWeekRepository.findReviewWeekByTrackType(lion.getTrackType());//해당 트랙에 있는 복습퀴즈 조회
//
//
//    }

//    //복습 퀴즈 조회
//    public ReviewQuizDTO.ShowReviewQuizDetails getReviewQuiz(TrackType trackType, Long WeekId) {
//        reviewWeekRepository
//    }
}

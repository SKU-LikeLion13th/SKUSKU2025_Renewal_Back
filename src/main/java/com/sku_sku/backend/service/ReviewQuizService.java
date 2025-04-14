package com.sku_sku.backend.service;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.domain.reviewquiz.*;
import com.sku_sku.backend.dto.Request.ReviewQuizDTO;
import com.sku_sku.backend.enums.AnswerStatus;
import com.sku_sku.backend.enums.QuizType;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQuizService  {
    private final  ReviewWeekRepository reviewWeekRepository;
    private final  ReviewQuizRepository reviewQuizRepository;
    private final JoinReviewQuizFileRepository joinReviewQuizFileRepository;
    private final AnswerChoiceRepository answerChoiceRepository;
    private final LionService lionService;
    private final ReviewQuizResponseRepository reviewQuizResponseRepository;

    @Transactional
    public void addQuiz(String title, TrackType trackType, List<ReviewQuizDTO.reviewQuizDTO> quizList) throws IOException {
        ReviewWeek reviewWeek = new ReviewWeek(trackType,title);
        reviewWeekRepository.save(reviewWeek);
        for (ReviewQuizDTO.reviewQuizDTO reviewQuizDTO : quizList) {
            ReviewQuiz reviewQuiz = new ReviewQuiz(reviewWeek,reviewQuizDTO.getContent(),reviewQuizDTO.getExplanation(),reviewQuizDTO.getAnswer(),reviewQuizDTO.getQuizType());
            reviewQuizRepository.save(reviewQuiz);
            if (reviewQuizDTO.getFiles()!=null) {
                for (MultipartFile file : reviewQuizDTO.getFiles()) {
                    JoinReviewQuizFile joinReviewQuizFile = new JoinReviewQuizFile(reviewQuiz, file.getBytes());
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
    public ReviewQuizDTO.SolveAnswerList solveReviewQuiz(String token, ReviewQuizDTO.SolveRequest solveRequest) {
        Lion lion = lionService.tokenToLion(token);
        System.out.println("트랙타입: " + lion.getTrackType());
        System.out.println(solveRequest.getReviewWeekId());

        List<ReviewQuiz> reviewQuizzes = reviewQuizRepository.findByTrackTypeAndReviewWeek(lion.getTrackType(),solveRequest.getReviewWeekId());
        List<ReviewQuizDTO.QuizResponse> userAnswers = solveRequest.getQuizResponseList();
        System.out.println(reviewQuizzes.size());

        ReviewQuizDTO.SolveAnswerList solveAnswerList = new ReviewQuizDTO.SolveAnswerList();
        solveAnswerList.setQuizAnswerList(new ArrayList<>());

        int correctCount = 0;
        int multipleChoiceTotal = 0;

        for (int i = 0; i < reviewQuizzes.size(); i++) {
            ReviewQuiz quiz = reviewQuizzes.get(i);
            ReviewQuizDTO.QuizResponse lionAnswerDTO = userAnswers.get(i);

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
                } else {
                    response.setAnswerStatus(AnswerStatus.EMPTY); // 주관식은 채점 안 함
                }
                reviewQuizResponseRepository.save(response);
            }

            else{//이미 푼 경우
                response = existingResponse;
                if (quiz.getQuizType()==QuizType.MULTIPLE_CHOICE){
                    multipleChoiceTotal++;
                    if(response.getAnswerStatus()==AnswerStatus.TRUE){
                        correctCount++;
                    }
                }
                response.setUpdateDate(LocalDateTime.now());
                response.setCount(response.getCount() + 1);
            }
            //정답과 해설 세팅
            ReviewQuizDTO.QuizAnswerList quizAnswerList = new ReviewQuizDTO.QuizAnswerList();
            quizAnswerList.setQuizId(quiz.getId());
            quizAnswerList.setAnswer(quiz.getAnswer());
            quizAnswerList.setExplanation(quiz.getExplanation());

            solveAnswerList.getQuizAnswerList().add(quizAnswerList);

        }
        solveAnswerList.setScore(correctCount);
        solveAnswerList.setMultipleTotal(multipleChoiceTotal);
        return solveAnswerList;
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

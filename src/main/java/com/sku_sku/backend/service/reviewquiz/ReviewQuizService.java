package com.sku_sku.backend.service.reviewquiz;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.domain.reviewquiz.AnswerChoice;
import com.sku_sku.backend.domain.reviewquiz.ReviewQuiz;
import com.sku_sku.backend.domain.reviewquiz.ReviewQuizResponse;
import com.sku_sku.backend.domain.reviewquiz.ReviewWeek;
import com.sku_sku.backend.dto.Request.ReviewWeekDTO;
import com.sku_sku.backend.enums.AnswerStatus;
import com.sku_sku.backend.enums.QuizType;
import com.sku_sku.backend.repository.AnswerChoiceRepository;
import com.sku_sku.backend.repository.ReviewQuizRepository;
import com.sku_sku.backend.repository.ReviewQuizResponseRepository;
import com.sku_sku.backend.repository.ReviewWeekRepository;
import com.sku_sku.backend.service.GeminiService;
import com.sku_sku.backend.service.LionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sku_sku.backend.dto.Request.ReviewQuizDTO.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewQuizService  {

    private final ReviewWeekRepository reviewWeekRepository;
    private final ReviewQuizRepository reviewQuizRepository;
    private final JoinReviewQuizFileService joinReviewQuizFileService;
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

            if (reviewQuizDTO.getFiles()!= null) {
                joinReviewQuizFileService.createJoinReviewQuizFiles(reviewQuiz, reviewQuizDTO.getFiles());
            }

            if (reviewQuizDTO.getQuizType()== QuizType.MULTIPLE_CHOICE){
                for(String StringAnswerChoice : reviewQuizDTO.getAnswerChoiceList()){
                    AnswerChoice answerChoice = new AnswerChoice(reviewQuiz, StringAnswerChoice);
                    answerChoiceRepository.save(answerChoice);
                }
            }
        }
    }

//    @Transactional
//    public void addQuiz(AddQuizRequest req) {
//        ReviewWeek reviewWeek = new ReviewWeek(req.getTrackType(), req.getTitle());
//        reviewWeekRepository.save(reviewWeek);
//        for (reviewQuizDTO reviewQuizDTO : req.getReviewQuizDTOList()) {
//            ReviewQuiz reviewQuiz = new ReviewQuiz(
//                    reviewWeek,
//                    reviewQuizDTO.getContent(),
//                    reviewQuizDTO.getExplanation(),
//                    reviewQuizDTO.getAnswer(),
//                    reviewQuizDTO.getQuizType());
//            reviewQuizRepository.save(reviewQuiz);
//
//            if (reviewQuizDTO.getFiles()!=null) {
//                for (JoinReviewQuizFileField file : reviewQuizDTO.getFiles()) {
//                    JoinReviewQuizFile joinReviewQuizFile = new JoinReviewQuizFile(
//                            reviewQuiz,
//                            file.getFileUrl(),
//                            file.getFileName(),
//                            file.getFileType(),
//                            file.getFileSize());
//                    joinReviewQuizFileRepository.save(joinReviewQuizFile);
//                }
//            }
//            if (reviewQuizDTO.getQuizType()== QuizType.MULTIPLE_CHOICE){
//                for(String StringAnswerChoice : reviewQuizDTO.getAnswerChoiceList()){
//                    AnswerChoice answerChoice = new AnswerChoice(reviewQuiz, StringAnswerChoice);
//                    answerChoiceRepository.save(answerChoice);
//                }
//            }
//        }
//    }
    //이미 푼 퀴즈 새로 응답 저장 or 응답 업데이트
    private ReviewQuizResponse getOrCreateResponse(Lion lion, ReviewQuiz quiz, String userAnswer) {
        ReviewQuizResponse existingResponse = reviewQuizResponseRepository.findReviewQuizResponseByLionAndReviewQuiz(lion, quiz);

        if (existingResponse == null) {
            return new ReviewQuizResponse(lion, quiz, userAnswer, quiz.getQuizType());
        } else {
            existingResponse.setUpdateDate(LocalDateTime.now());
            existingResponse.setCount(existingResponse.getCount() + 1);
            return existingResponse;
        }
    }

    //객관식 채점
    private boolean gradeMultipleChoice(ReviewQuiz quiz, ReviewQuizResponse response, String lionAnswer) {
        if (Objects.equals(quiz.getAnswer(), lionAnswer)) {
            response.setAnswerStatus(AnswerStatus.TRUE);
            return true;
        } else {
            response.setAnswerStatus(AnswerStatus.FALSE);
            return false;
        }
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

    //정답/해설 리스트 생성
    private QuizAnswerList createQuizAnswerList(ReviewQuiz quiz) {
        QuizAnswerList quizAnswerList = new QuizAnswerList();
        quizAnswerList.setQuizId(quiz.getId());
        quizAnswerList.setAnswer(quiz.getAnswer());
        quizAnswerList.setExplanation(quiz.getExplanation());
        return quizAnswerList;
    }

    //정답 여부 반환
    private boolean evaluateAndMarkAnswer(ReviewQuiz quiz, ReviewQuizResponse response, String userAnswer) {
        if (quiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
            return gradeMultipleChoice(quiz, response, userAnswer); // 내부에서 answerStatus 설정까지 함
        } else if (quiz.getQuizType() == QuizType.ESSAY_QUESTION) {
            AnswerStatus status = evaluateEssayQuestion(quiz, userAnswer);
            response.setAnswerStatus(status);
            return status == AnswerStatus.TRUE;
        } else {
            response.setAnswerStatus(AnswerStatus.EMPTY);
            return false;
        }
    }

    //답 채점 및 저장
    @Transactional
    public SolveAnswerList solveReviewQuiz(String email, SolveRequest solveRequest) {
        Lion lion = lionService.findByEmail(email);

        List<ReviewQuiz> reviewQuizzes = reviewQuizRepository.findByTrackTypeAndReviewWeek(lion.getTrackType(),solveRequest.getReviewWeekId());
        List<QuizResponse> userAnswers = solveRequest.getQuizResponseList();

        SolveAnswerList solveAnswerList = new SolveAnswerList();
        solveAnswerList.setQuizAnswerList(new ArrayList<>());

        int correctCount = 0;
        int total = reviewQuizzes.size();

        for (int i = 0; i < reviewQuizzes.size(); i++) {
            ReviewQuiz quiz = reviewQuizzes.get(i);
            QuizResponse lionAnswerDTO = userAnswers.get(i);
            ReviewQuizResponse response = getOrCreateResponse(lion, quiz, lionAnswerDTO.getQuizAnswer());

            boolean isCorrect = evaluateAndMarkAnswer(quiz, response, lionAnswerDTO.getQuizAnswer());
            if (isCorrect) correctCount++;


            if (response.getId() == null) {
                reviewQuizResponseRepository.save(response);
            }

            solveAnswerList.getQuizAnswerList().add(createQuizAnswerList(quiz));
        }



//            ReviewQuizResponse existingResponse = reviewQuizResponseRepository.findReviewQuizResponseByLionAndReviewQuiz(lion, quiz);
//            System.out.println(existingResponse);
//            ReviewQuizResponse response;
//
//
//            if(existingResponse==null){
//                //새로 풀때
//                //response = gradeReviewQuiz(lion, quiz, lionAnswerDTO.getQuizAnswer());
//                response = new ReviewQuizResponse(lion, quiz, lionAnswerDTO.getQuizAnswer(), quiz.getQuizType());
//
//                if (quiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
//                    multipleChoiceTotal++;
//                    System.out.println("정답: " + quiz.getAnswer());
//                    System.out.println("사용자 답변: " + lionAnswerDTO.getQuizAnswer());
//                    if (Objects.equals(quiz.getAnswer(), lionAnswerDTO.getQuizAnswer())) {
//                        response.setAnswerStatus(AnswerStatus.TRUE);
//                        correctCount++;
//                        System.out.println(correctCount);
//                    } else {
//                        response.setAnswerStatus(AnswerStatus.FALSE);
//                    }
//                } else if (quiz.getQuizType() == QuizType.ESSAY_QUESTION) {
//                    // 주관식 문제인 경우 Gemini API를 사용하여 평가
//                    AnswerStatus answerStatus = evaluateEssayQuestion(quiz, lionAnswerDTO.getQuizAnswer());
//                    response.setAnswerStatus(answerStatus);
//
//                    if (answerStatus == AnswerStatus.TRUE) {
//                        correctCount++; // 정답으로 평가되었으면 점수 추가
//                    }
//                } else {
//                    response.setAnswerStatus(AnswerStatus.EMPTY); // 다른 타입(파일 첨부 등)은 채점 안 함
//                }
//                reviewQuizResponseRepository.save(response);
//            }
//
//            else{//이미 푼 경우
//                response = existingResponse;
//                if (quiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
//                    multipleChoiceTotal++;
//                    if (response.getAnswerStatus() == AnswerStatus.TRUE) {
//                        correctCount++;
//                    }
//                } else if (quiz.getQuizType() == QuizType.ESSAY_QUESTION) {
//                    AnswerStatus answerStatus = evaluateEssayQuestion(quiz, lionAnswerDTO.getQuizAnswer());
//
//                    if (answerStatus == AnswerStatus.TRUE) {
//                        correctCount++;
//                    }
//                }
//                response.setUpdateDate(LocalDateTime.now());
//                response.setCount(response.getCount() + 1);
//            }
            //정답과 해설 세팅

        solveAnswerList.setScore(correctCount);
        solveAnswerList.setMultipleTotal(total);
        return solveAnswerList;
    }




    //주차별 퀴즈 목록 조회
    public List<ReviewWeekDTO.showReviewWeek> getReviewWeek(String email) {
        Lion lion = lionService.findByEmail(email);//아기사자 찾기
        List<ReviewWeek> reviewWeekList = reviewWeekRepository.findReviewWeekByTrackType(lion.getTrackType());//해당 트랙에 있는 복습퀴즈 조회
        System.out.println(reviewWeekList);

        List<ReviewWeekDTO.showReviewWeek> reviewWeekDTOList = new ArrayList<>();

        for (ReviewWeek reviewWeek : reviewWeekList) {
            String IsSubmit="제출";
            int correctCount = 0;
            ReviewWeekDTO.showReviewWeek dto = new ReviewWeekDTO.showReviewWeek();
            dto.setReviewWeekId(reviewWeek.getId());
            dto.setTitle(reviewWeek.getTitle());
            List<ReviewQuiz> reviewQuizzes = reviewQuizRepository.findByTrackTypeAndReviewWeek(lion.getTrackType(),reviewWeek.getId());
            for(ReviewQuiz reviewQuiz : reviewQuizzes) {
                ReviewQuizResponse existingResponse = reviewQuizResponseRepository.findReviewQuizResponseByLionAndReviewQuiz(lion,reviewQuiz);
                if (existingResponse == null) {
                    IsSubmit = "미제출";
                    break;
                }
                if(existingResponse.getAnswerStatus()==AnswerStatus.TRUE) {
                    correctCount++;
                }
            }
            dto.setScore(correctCount);
            dto.setTotal(reviewQuizzes.size());
            dto.setIsSubmit(IsSubmit);
            reviewWeekDTOList.add(dto);
        }
        return reviewWeekDTOList;
    }

//    //복습 퀴즈 조회
//    public ReviewQuizDTO.ShowReviewQuizDetails getReviewQuiz(TrackType trackType, Long WeekId) {
//        reviewWeekRepository
//    }
}

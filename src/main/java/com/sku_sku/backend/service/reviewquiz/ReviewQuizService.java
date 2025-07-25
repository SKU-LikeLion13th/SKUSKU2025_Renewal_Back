package com.sku_sku.backend.service.reviewquiz;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.domain.reviewquiz.*;
import com.sku_sku.backend.dto.Request.JoinReviewQuizFileDTO;
import com.sku_sku.backend.dto.Request.ReviewQuizDTO;
import com.sku_sku.backend.dto.Request.ReviewWeekDTO;
import com.sku_sku.backend.enums.AnswerStatus;
import com.sku_sku.backend.enums.QuizType;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.repository.*;
import com.sku_sku.backend.service.GeminiService;
import com.sku_sku.backend.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final ReviewQuizResponseRepository reviewQuizResponseRepository;
    private final GeminiService geminiService;
    private final JoinReviewQuizFileRepository joinReviewQuizFileRepository;
    private final S3Service s3Service;

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
    public SolveAnswerList solveReviewQuiz(Lion lion, SolveRequest solveRequest) {
        ReviewWeek reviewWeek = reviewWeekRepository.findReviewWeekById(solveRequest.getReviewWeekId())
                .orElseThrow(()-> new InvalidIdException("reviewWeekId에 대한 ReviewWeek 없음"));

        List<ReviewQuiz> reviewQuizzes = reviewQuizRepository.findByReviewWeek(reviewWeek);
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

        solveAnswerList.setScore(correctCount);
        solveAnswerList.setTotal(total);
        return solveAnswerList;
    }




    //주차별 퀴즈 목록 조회
    public List<ReviewWeekDTO.showReviewWeek> getReviewWeek(Lion lion, TrackType trackType) {
        List<ReviewWeek> reviewWeekList = reviewWeekRepository.findReviewWeekByTrackType(trackType)
                .orElseThrow(()-> new InvalidIdException("트랙타입이 잘못됐습니다."));//해당 트랙에 있는 복습퀴즈 조회
        System.out.println(reviewWeekList);

        List<ReviewWeekDTO.showReviewWeek> reviewWeekDTOList = new ArrayList<>();

        for (ReviewWeek reviewWeek : reviewWeekList) {
            String IsSubmit="제출";
            int correctCount = 0;
            ReviewWeekDTO.showReviewWeek dto = new ReviewWeekDTO.showReviewWeek();
            dto.setReviewWeekId(reviewWeek.getId());
            dto.setTitle(reviewWeek.getTitle());
            List<ReviewQuiz> reviewQuizzes = reviewQuizRepository.findByReviewWeek(reviewWeek);
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

    //복습 퀴즈 상세 조회
    public List<ReviewQuizDTO.ShowReviewQuizDetails> getReviewQuiz(Long WeekId) {
        ReviewWeek reviewWeek = reviewWeekRepository.findReviewWeekById(WeekId).
                orElseThrow(()-> new InvalidIdException("해당 주차에 대한 복습퀴즈를 찾을 수 없습니다."));
        List<ReviewQuiz> reviewQuizzes =  reviewQuizRepository.findByReviewWeek(reviewWeek);

        return reviewQuizzes.stream()
                .map(reviewQuiz -> {
                    ShowReviewQuizDetails dto = new ShowReviewQuizDetails();
                    dto.setId(reviewQuiz.getId()); //문제 아이디
                    dto.setQuizType(reviewQuiz.getQuizType());//문제 타입
                    dto.setContent(reviewQuiz.getContent());//문제 내용
                    // 객관식일 때만 보기 리스트 설정
                    if (reviewQuiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
                        List<String> answerChoices = answerChoiceRepository.findByReviewQuiz(reviewQuiz).stream()
                                .map(AnswerChoice::getContent)
                                .collect(Collectors.toList());
                        dto.setAnswerChoiceList(answerChoices);
                    } else {
                        dto.setAnswerChoiceList(Collections.emptyList());
                    }
                    // 첨부 파일 DTO 매핑
                    List<JoinReviewQuizFileDTO.JoinReviewQuizFileField> fileDtos =
                            joinReviewQuizFileRepository.findByReviewQuiz(reviewQuiz).stream()
                                    .map(file -> {
                                        JoinReviewQuizFileDTO.JoinReviewQuizFileField fileDto =
                                                new JoinReviewQuizFileDTO.JoinReviewQuizFileField();
                                        fileDto.setFileName(file.getFileName());
                                        fileDto.setFileType(file.getFileType());
                                        fileDto.setFileSize(file.getFileSize());
                                        fileDto.setFileUrl(file.getFileUrl());
                                        fileDto.setFileKey(file.getFileKey());
                                        return fileDto;
                                    })
                                    .collect(Collectors.toList());


                    dto.setFiles(fileDtos);
                    dto.setAnswer(reviewQuiz.getAnswer());
                    dto.setExplanation(reviewQuiz.getExplanation());
                    return dto;
                }).toList();
    }

    //복습퀴즈 수정
    @Transactional
    public void updateQuiz(Long weekId,  AddQuizRequest addQuizRequest) {
        ReviewWeek reviewWeek = reviewWeekRepository.findReviewWeekById(weekId)
                .orElseThrow(()-> new InvalidIdException("해당 주차에 대한 복습퀴즈를 찾을 수 없습니다."));
        reviewWeek.update(addQuizRequest.getTrackType(), addQuizRequest.getTitle());
        reviewWeekRepository.save(reviewWeek);

        List<ReviewQuiz> reviewQuizList = reviewQuizRepository.findByReviewWeek(reviewWeek);
        if (reviewQuizList.size() != addQuizRequest.getReviewQuizDTOList().size()) {
            throw new IllegalArgumentException("기존 퀴즈 개수와 수정할 퀴즈 개수가 일치하지 않습니다.");
        }
        for (int i = 0; i < reviewQuizList.size(); i++) {
            ReviewQuiz quiz = reviewQuizList.get(i);
            reviewQuizDTO dto = addQuizRequest.getReviewQuizDTOList().get(i);

            quiz.setQuizType(dto.getQuizType());
            quiz.setContent(dto.getContent());
            quiz.setAnswer(dto.getAnswer());
            quiz.setExplanation(dto.getExplanation());

            // 객관식이면 기존 보기 삭제 후 새로 등록
            if (dto.getQuizType() == QuizType.MULTIPLE_CHOICE) {
                answerChoiceRepository.deleteByReviewQuiz(quiz); // 기존 보기 삭제
                for (String content : dto.getAnswerChoiceList()) {
                    answerChoiceRepository.save(new AnswerChoice(quiz, content));
                }
            }

            // 기존 파일 삭제 처리
            List<JoinReviewQuizFile> oldFiles = joinReviewQuizFileRepository.findByReviewQuiz(quiz);
            List<String> fileKeysToDelete = oldFiles.stream()
                    .map(JoinReviewQuizFile::getFileKey)
                    .collect(Collectors.toList());

            // S3에서 삭제
            if (!fileKeysToDelete.isEmpty()) {
                s3Service.deleteFiles(fileKeysToDelete);
            }

            // DB에서 삭제
            joinReviewQuizFileRepository.deleteAll(oldFiles);

            // 새 파일 저장 (프론트에서 받은 파일 메타데이터 기준)
            List<JoinReviewQuizFile> files = dto.getFiles().stream()
                    .map(fileDTO -> new JoinReviewQuizFile(
                            quiz,
                            fileDTO.getFileName(),
                            fileDTO.getFileType(),
                            fileDTO.getFileSize(),
                            fileDTO.getFileUrl(),
                            fileDTO.getFileKey()
                    ))
                    .collect(Collectors.toList());

            joinReviewQuizFileRepository.saveAll(files);

        }

    }


    @Transactional
    public void deleteQuiz(Long weekId){
        ReviewWeek reviewWeek = reviewWeekRepository.findById(weekId)
                .orElseThrow(()-> new InvalidIdException("해당 주차에 대한 복습퀴즈를 찾을 수 없습니다."));
        List<ReviewQuiz> reviewQuizList = reviewQuizRepository.findByReviewWeek(reviewWeek);
        for (ReviewQuiz reviewQuiz : reviewQuizList) {
            //해당 문제에 대한 파일 메타데이터 삭제
            List<JoinReviewQuizFile> joinReviewQuizFileList = joinReviewQuizFileRepository.findByReviewQuiz(reviewQuiz);

            if (!joinReviewQuizFileList.isEmpty()) {
                List<String> keys = joinReviewQuizFileList.stream().map(JoinReviewQuizFile::getFileKey).toList();
                s3Service.deleteFiles(keys);
                joinReviewQuizFileRepository.deleteAll(joinReviewQuizFileList);
            }
//            //해당 문제가 객관식일 경우 보기 삭제
//            if (reviewQuiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
//                List<AnswerChoice> answerChoiceList = answerChoiceRepository.findByReviewQuiz(reviewQuiz);
//                answerChoiceRepository.deleteAll(answerChoiceList);
//            }
//            //해당 문제에 대한 모든 응답 삭제
//            List<ReviewQuizResponse> reviewQuizResponseList = reviewQuizResponseRepository.findByReviewQuiz(reviewQuiz);
//            if(!reviewQuizResponseList.isEmpty()){
//                reviewQuizResponseRepository.deleteAll(reviewQuizResponseList);
//            }

        }
        reviewQuizRepository.deleteAll(reviewQuizList);
        reviewWeekRepository.delete(reviewWeek);
    }

}

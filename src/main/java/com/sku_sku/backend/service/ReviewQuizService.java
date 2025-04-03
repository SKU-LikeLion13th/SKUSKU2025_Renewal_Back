package com.sku_sku.backend.service;

import com.sku_sku.backend.domain.reviewquiz.AnswerChoice;
import com.sku_sku.backend.domain.reviewquiz.JoinReviewQuizFile;
import com.sku_sku.backend.domain.reviewquiz.ReviewQuiz;
import com.sku_sku.backend.domain.reviewquiz.ReviewWeek;
import com.sku_sku.backend.dto.Request.reviewQuizDTO;
import com.sku_sku.backend.enums.QuizType;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.repository.AnswerChoiceRepository;
import com.sku_sku.backend.repository.JoinReviewQuizFileRepository;
import com.sku_sku.backend.repository.ReviewQuizRepository;
import com.sku_sku.backend.repository.ReviewWeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewQuizService  {
    private final  ReviewWeekRepository reviewWeekRepository;
    private final  ReviewQuizRepository reviewQuizRepository;
    private final JoinReviewQuizFileRepository joinReviewQuizFileRepository;
    private final AnswerChoiceRepository answerChoiceRepository;

    public void addQuiz(String title, TrackType trackType, List<reviewQuizDTO> quizList) throws IOException {
        ReviewWeek reviewWeek = new ReviewWeek(trackType,title);
        reviewWeekRepository.save(reviewWeek);
        for (reviewQuizDTO reviewQuizDTO : quizList) {
            ReviewQuiz reviewQuiz = new ReviewQuiz(reviewWeek,reviewQuizDTO.getContent(),reviewQuizDTO.getExplanation(),reviewQuizDTO.getAnswer(),reviewQuizDTO.getQuizType());
            reviewQuizRepository.save(reviewQuiz);
            for(MultipartFile file : reviewQuizDTO.getFiles()) {
                JoinReviewQuizFile joinReviewQuizFile = new JoinReviewQuizFile(reviewQuiz, file.getBytes());
                joinReviewQuizFileRepository.save(joinReviewQuizFile);
            }
            if (reviewQuizDTO.getQuizType()== QuizType.MULTIPLE_CHOICE){
                for(String StringanswerChoice : reviewQuizDTO.getAnswerChoiceList()){
                    AnswerChoice answerChoice = new AnswerChoice(reviewQuiz, StringanswerChoice);
                    answerChoiceRepository.save(answerChoice);
                }
            }
        }
    }
}

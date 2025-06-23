package com.sku_sku.backend.service.reviewquiz;

import com.sku_sku.backend.domain.lecture.JoinLectureFile;
import com.sku_sku.backend.domain.lecture.Lecture;
import com.sku_sku.backend.domain.reviewquiz.JoinReviewQuizFile;
import com.sku_sku.backend.domain.reviewquiz.ReviewQuiz;
import com.sku_sku.backend.dto.Request.JoinLectureFileDTO;
import com.sku_sku.backend.dto.Request.JoinReviewQuizFileDTO;
import com.sku_sku.backend.repository.JoinReviewQuizFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinReviewQuizFileService {

    private final JoinReviewQuizFileRepository joinReviewQuizFileRepository;

    @Transactional
    public void createJoinReviewQuizFiles(ReviewQuiz reviewQuiz, List<JoinReviewQuizFileDTO.JoinReviewQuizFileField> files) {
        List<JoinReviewQuizFile> joinReviewQuizFileList = files.stream()
                .map(dto -> new JoinReviewQuizFile(
                        reviewQuiz,
                        dto.getFileName(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileUrl(),
                        dto.getFileKey()
                ))
                .toList();

        joinReviewQuizFileRepository.saveAll(joinReviewQuizFileList);
    }
}

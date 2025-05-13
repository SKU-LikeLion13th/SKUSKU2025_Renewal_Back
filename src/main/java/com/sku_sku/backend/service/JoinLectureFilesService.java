package com.sku_sku.backend.service;


import com.sku_sku.backend.domain.lecture.JoinLectureFile;
import com.sku_sku.backend.domain.lecture.Lecture;
import com.sku_sku.backend.dto.Request.JoinLectureFilesDTO;
import com.sku_sku.backend.repository.JoinLectureFilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinLectureFilesService {
    private final JoinLectureFilesRepository joinLectureFilesRepository;

    @Transactional
    public List<JoinLectureFile> createJoinLectureFiles(Lecture lecture, List<JoinLectureFilesDTO.LectureFileDTO> files) {
        List<JoinLectureFile> joinLectureFileList = files.stream()
                .map(dto -> new JoinLectureFile(
                        lecture,
                        dto.getFileName(),
                        dto.getFileUrl(),
                        dto.getFileType(),
                        dto.getFileSize()
                ))
                .toList();

        joinLectureFilesRepository.saveAll(joinLectureFileList);
        return joinLectureFileList;
    }



    @Transactional
    public void deleteByLecture(Lecture lecture) {
        joinLectureFilesRepository.deleteByLecture(lecture);
    }
}

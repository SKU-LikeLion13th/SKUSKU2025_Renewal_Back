package com.sku_sku.backend.service;


import com.sku_sku.backend.domain.lecture.JoinLectureFile;
import com.sku_sku.backend.domain.lecture.Lecture;
import com.sku_sku.backend.dto.Request.JoinLectureFilesDTO;
import com.sku_sku.backend.repository.JoinLectureFilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinLectureFilesService {
    private final JoinLectureFilesRepository joinLectureFilesRepository;

    @Transactional
    public void createJoinLectureFiles(Lecture lecture, List<JoinLectureFilesDTO.LectureFileDTO> files) {
        List<JoinLectureFile> joinLectureFileList = files.stream()
                .map(dto -> new JoinLectureFile(
                        lecture,
                        dto.getFileName(),
                        dto.getFileUrl(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileKey()
                ))
                .toList();

        joinLectureFilesRepository.saveAll(joinLectureFileList);
    }

    @Transactional
    public void updateJoinLectureFiles(Lecture lecture, List<JoinLectureFilesDTO.UpdateLectureFileDTO> files) {
        List<JoinLectureFile> joinLectureFileList = files.stream()
                .map(dto -> new JoinLectureFile(
                        lecture,
                        dto.getFileName(),
                        dto.getFileUrl(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileKey()
                ))
                .toList();

        joinLectureFilesRepository.saveAll(joinLectureFileList);
    }

    public void deleteFilesByKeyList(Lecture lecture, List<String> keysToDelete) {
        if (keysToDelete == null || keysToDelete.isEmpty()) return;
        joinLectureFilesRepository.deleteAllByLectureAndFileKeyIn(lecture, keysToDelete);
    }
}

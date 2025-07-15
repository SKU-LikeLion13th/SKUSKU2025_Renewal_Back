package com.sku_sku.backend.service.lecture;


import com.sku_sku.backend.domain.lecture.JoinLectureFile;
import com.sku_sku.backend.domain.lecture.Lecture;
import com.sku_sku.backend.dto.Request.JoinLectureFileDTO;
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
    public void createJoinLectureFiles(Lecture lecture, List<JoinLectureFileDTO.LectureFileDTO> files) {
        List<JoinLectureFile> joinLectureFileList = files.stream()
                .map(dto -> new JoinLectureFile(
                        lecture,
                        dto.getFileName(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileUrl(),
                        dto.getFileKey(),
                        false
                ))
                .toList();

        joinLectureFilesRepository.saveAll(joinLectureFileList);
    }

    @Transactional
    public void updateJoinLectureFiles(Lecture lecture, List<JoinLectureFileDTO.UpdateLectureFileDTO> files) {
        List<JoinLectureFile> newJoinLectureFileList = files.stream()
                .map(dto -> new JoinLectureFile(
                        lecture,
                        dto.getFileName(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileUrl(),
                        dto.getFileKey(),
                        true
                ))
                .toList();

        joinLectureFilesRepository.saveAll(newJoinLectureFileList);
    }

    public void deleteFilesByKeyList(Lecture lecture, List<String> keysToDelete) {
        if (keysToDelete == null || keysToDelete.isEmpty()) return;
        joinLectureFilesRepository.deleteAllByLectureAndFileKeyIn(lecture, keysToDelete);
    }
}

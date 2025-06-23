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
                        dto.getFileKey()
                ))
                .toList();

        joinLectureFilesRepository.saveAll(joinLectureFileList);
    }

    @Transactional
    public void updateJoinLectureFiles(Lecture lecture, List<JoinLectureFileDTO.UpdateLectureFileDTO> files) {
        List<JoinLectureFile> joinLectureFileList = files.stream()
                .map(dto -> {
                    JoinLectureFile joinLectureFile = new JoinLectureFile();
                    joinLectureFile.updateJoinLectureFile(
                            lecture,
                            dto.getFileName(),
                            dto.getFileType(),
                            dto.getFileSize(),
                            dto.getFileUrl(),
                            dto.getFileKey()
                    );
                    System.out.println("파일 타입: " + dto.getFileType());
                    System.out.println("파일 URL: " + dto.getFileUrl());
                    System.out.println("파일 키: " + dto.getFileKey());
                    return joinLectureFile;
                })
                .toList();

        joinLectureFilesRepository.saveAll(joinLectureFileList);
    }

    public void deleteFilesByKeyList(Lecture lecture, List<String> keysToDelete) {
        if (keysToDelete == null || keysToDelete.isEmpty()) return;
        joinLectureFilesRepository.deleteAllByLectureAndFileKeyIn(lecture, keysToDelete);
    }
}

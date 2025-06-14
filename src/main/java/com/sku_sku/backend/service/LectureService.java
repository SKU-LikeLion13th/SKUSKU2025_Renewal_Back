package com.sku_sku.backend.service;


import com.sku_sku.backend.domain.lecture.JoinLectureFile;
import com.sku_sku.backend.domain.lecture.Lecture;
import com.sku_sku.backend.dto.Response.JoinLectureFileDTO.LectureFileDTOWithoutFileKey;
import com.sku_sku.backend.dto.Request.LectureDTO;
import com.sku_sku.backend.dto.Response.LectureDTO.ResponseLectureWithoutFiles;
import com.sku_sku.backend.enums.FileStatusType;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.exception.EmptyLectureException;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.repository.JoinLectureFilesRepository;
import com.sku_sku.backend.repository.LectureRepository;
import com.sku_sku.backend.security.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sku_sku.backend.dto.Request.JoinLectureFileDTO.*;
import static com.sku_sku.backend.dto.Response.LectureDTO.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final JoinLectureFilesRepository joinLectureFilesRepository;
    private final JoinLectureFilesService joinLectureFilesService;
    private final JwtUtility jwtUtility;
    private final S3Service s3Service;

    @Transactional // 강의 안내물 생성 로직
    public void createLecture(HttpServletRequest header, LectureDTO.createLectureRequest req) {
        String writer = jwtUtility.getClaimsFromCookies(header).get("name", String.class);
        Lecture lecture = new Lecture(req.getTrackType(), req.getTitle(), req.getContent(), writer);
        lectureRepository.save(lecture);
        joinLectureFilesService.createJoinLectureFiles(lecture, req.getFiles());
    }

    @Transactional
    public void updateLecture(HttpServletRequest header, LectureDTO.updateLectureRequest req) {
        String newWriter = jwtUtility.getClaimsFromCookies(header).get("name", String.class);
        Lecture lecture = lectureRepository.findById(req.getId())
                .orElseThrow(() -> new InvalidIdException("lecture"));

        lecture.update(
                getOrDefault(req.getTrackType(), lecture.getTrack()),
                getOrDefault(req.getTitle(), lecture.getTitle()),
                getOrDefault(req.getContent(), lecture.getContent()),
                newWriter
        );
        System.out.println("파일 트랙타입: " + req.getTrackType());
        System.out.println("파일 타이틀: " + req.getTitle());
        System.out.println("파일 내용: " + req.getContent());
        System.out.println("파일 리스트: " + req.getFiles());
        List<UpdateLectureFileDTO> files = req.getFiles();
        if (files != null && !files.isEmpty()) {

            // 삭제 대상 파일 삭제
            List<String> keysToDelete = files.stream()
                    .filter(f -> f.getStatus() == FileStatusType.DELETE)
                    .map(UpdateLectureFileDTO::getFileKey)
                    .toList();
            s3Service.deleteFiles(keysToDelete);
            joinLectureFilesService.deleteFilesByKeyList(lecture, keysToDelete);

            // 새로 추가할 파일만 저장
            List<UpdateLectureFileDTO> newFiles = files.stream()
                    .filter(f -> f.getStatus() == FileStatusType.NEW)
                    .toList();
            joinLectureFilesService.updateJoinLectureFiles(lecture, newFiles);
        }
    }

    private <T> T getOrDefault(T newOne, T previousOne) {
        return newOne != null ? newOne : previousOne;
    }

    @Transactional // 강의 안내물 삭제 로직
    public void deleteLecture(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new InvalidIdException("lecture"));

        // 연관된 S3 파일 키 목록 조회
        List<String> fileKeys = joinLectureFilesRepository.findByLecture(lecture).stream()
                .map(JoinLectureFile::getFileKey)
                .filter(key -> key != null && !key.isBlank())
                .toList();

        // S3에서 파일 삭제
        if (!fileKeys.isEmpty()) {
            s3Service.deleteFiles(fileKeys);
        }

        lectureRepository.delete(lecture);
    }

    // 강의 안내물 조회 로직
    public ResponseLecture findLectureById(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new InvalidIdException("lecture"));

        List<LectureFileDTOWithoutFileKey> fileDTOs = joinLectureFilesRepository.findByLecture(lecture).stream()
                .map(file -> LectureFileDTOWithoutFileKey.builder()
                        .fileName(file.getFileName())
                        .fileType(file.getFileType())
                        .fileSize(file.getFileSize())
                        .fileUrl(file.getFileUrl())
                        .fileKey(file.getFileKey())
                        .build())
                .toList();

        return ResponseLecture.builder()
                .id(lecture.getId())
                .trackType(lecture.getTrack())
                .title(lecture.getTitle())
                .content(lecture.getContent())
                .writer(lecture.getWriter())
                .createDateTime(lecture.getCreateDateTime())
                .joinLectureFiles(fileDTOs)
                .build();
    }

    public List<ResponseLectureWithoutFiles> findAllLectureByTrack(TrackType trackType) {
        List<Lecture> lectures = lectureRepository.findByTrackOrderByCreateDateTimeDesc(trackType)
                .orElseThrow(EmptyLectureException::new);
        return lectures.stream()
                .map(this::convertToResponseLectureWithoutFilesDTO)
                .toList();
    }

    private ResponseLectureWithoutFiles convertToResponseLectureWithoutFilesDTO(Lecture lecture) {
        return new ResponseLectureWithoutFiles(
                lecture.getId(),
                lecture.getTrack(),
                lecture.getTitle(),
                lecture.getContent(),
                lecture.getWriter(),
                lecture.getCreateDateTime()
        );
    }
}

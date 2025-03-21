package com.sku_sku.backend.service;


import com.sku_sku.backend.domain.lecture.Lecture;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.dto.Request.JoinLectureFilesDTO;
import com.sku_sku.backend.dto.Request.LectureDTO;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LionService lionService;
    private final JoinLectureFilesService joinLectureFilesService;

    // @PostMapping("/admin/lecture/add")
    @Transactional // 강의 안내물 생성 로직
    public Lecture createLecture(String bearer, LectureDTO.createLectureRequest request) throws IOException {
        String writer = lionService.tokenToLionName(bearer.substring(7));
        Lecture lecture = new Lecture(request.getTrackType(), request.getTitle(), writer);
        lectureRepository.save(lecture);

        joinLectureFilesService.createJoinLectureFiles(lecture, request.getFiles());

        return lecture;
    }

    // @PutMapping("/admin/lecture/update")
    @Transactional // 강의 안내물 업데이트 로직
    public Lecture updateLecture(String bearer, LectureDTO.updateLectureRequest request) throws IOException {
        String newWriter = lionService.tokenToLionName(bearer.substring(7));
        Lecture lecture = lectureRepository.findById(request.getId())
                .orElseThrow(InvalidIdException::new);

        TrackType newTrack = (request.getTrackType() != null ? request.getTrackType() : lecture.getTrack());
        String newTitle = (request.getTitle() != null && !request.getTitle().isEmpty() ? request.getTitle() : lecture.getTitle());
        lecture.update(newTrack, newTitle, newWriter);

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            joinLectureFilesService.deleteByLecture(lecture);
            joinLectureFilesService.createJoinLectureFiles(lecture, request.getFiles());
        }

        return lecture;
    }

    @Transactional // 강의 안내물 조회 로직
    public com.sku_sku.backend.dto.Response.LectureDTO.ResponseLecture finaLectureById(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .map(lecture -> {
                    lectureRepository.save(lecture);
                    return new com.sku_sku.backend.dto.Response.LectureDTO.ResponseLecture(
                            lecture.getId(),
                            lecture.getTrack(),
                            lecture.getTitle(),
                            lecture.getWriter(),
                            lecture.getCreateDate(),
                            lecture.getJoinLectureFile().stream()
                                    .map(JoinLectureFilesDTO.CreateJoinLectureFilesRequest::new)
                                    .collect(Collectors.toCollection(ArrayList::new)));
                })
                .orElseThrow(InvalidIdException::new);
    }

    // @DeleteMapping("/admin/lecture/delete")
    @Transactional // 강의 안내물 삭제 로직
    public void deleteLecture(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(InvalidIdException::new);
        lectureRepository.delete(lecture);
    }

    public List<com.sku_sku.backend.dto.Response.LectureDTO.ResponseLectureWithoutFiles> findAllLectureByTrackOrderByIdDesc(TrackType trackType) {
        List<Lecture> lectures = lectureRepository.findByTrackOrderByIdDesc(trackType);
        return lectures.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<com.sku_sku.backend.dto.Response.LectureDTO.ResponseLectureWithoutFiles> findAllLectureByTrackOrderByCreateDateDesc(TrackType trackType) {
        List<Lecture> lectures = lectureRepository.findByTrackOrderByCreateDateDesc(trackType);
        return lectures.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private com.sku_sku.backend.dto.Response.LectureDTO.ResponseLectureWithoutFiles convertToDTO(Lecture lecture) {
        return new com.sku_sku.backend.dto.Response.LectureDTO.ResponseLectureWithoutFiles(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getWriter(),
                lecture.getCreateDate()
        );
    }
}

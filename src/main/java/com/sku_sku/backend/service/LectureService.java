package com.sku_sku.backend.service;


import com.sku_sku.backend.domain.lecture.Lecture;
import com.sku_sku.backend.dto.Request.JoinLectureFilesDTO.CreateJoinLectureFilesRequest;
import com.sku_sku.backend.dto.Request.LectureDTO;
import com.sku_sku.backend.dto.Response.LectureDTO.ResponseLectureWithoutFiles;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.exception.EmptyLectureException;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.repository.LectureRepository;
import com.sku_sku.backend.security.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sku_sku.backend.dto.Response.LectureDTO.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final JoinLectureFilesService joinLectureFilesService;
    private final JwtUtility jwtUtility;

    // @PostMapping("/admin/lecture/add")
    @Transactional // 강의 안내물 생성 로직
    public void createLecture(HttpServletRequest header, LectureDTO.createLectureRequest request) throws IOException {
        String writer = jwtUtility.getClaimsFromCookies(header).get("name", String.class);
        Lecture lecture = new Lecture(request.getTrackType(), request.getTitle(), request.getContent(), writer);
        lectureRepository.save(lecture);

        joinLectureFilesService.createJoinLectureFiles(lecture, request.getFiles());
    }

    // @PutMapping("/admin/lecture/update")
    @Transactional // 강의 안내물 업데이트 로직
    public void updateLecture(HttpServletRequest header, LectureDTO.updateLectureRequest request) throws IOException {
        String newWriter = jwtUtility.getClaimsFromCookies(header).get("name", String.class);
        Lecture lecture = lectureRepository.findById(request.getId())
                .orElseThrow(() -> new InvalidIdException("lecture"));

        TrackType newTrack = (request.getTrackType() != null ? request.getTrackType() : lecture.getTrack());
        String newTitle = (request.getTitle() != null && !request.getTitle().isEmpty() ? request.getTitle() : lecture.getTitle());
        String newContnet = (request.getContent() != null && !request.getContent().isEmpty() ? request.getContent() : lecture.getContent());
        lecture.update(newTrack, newTitle, newContnet, newWriter);

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            joinLectureFilesService.deleteByLecture(lecture);
            joinLectureFilesService.createJoinLectureFiles(lecture, request.getFiles());
        }
    }

    @Transactional // 강의 안내물 조회 로직
    public ResponseLecture finaLectureById(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .map(lecture -> {
                    lectureRepository.save(lecture);
                    return new ResponseLecture(
                            lecture.getId(),
                            lecture.getTrack(),
                            lecture.getTitle(),
                            lecture.getContent(),
                            lecture.getWriter(),
                            lecture.getCreateDate(),
                            lecture.getJoinLectureFile().stream()
                                    .map(CreateJoinLectureFilesRequest::new)
                                    .collect(Collectors.toCollection(ArrayList::new)));
                })
                .orElseThrow(() -> new InvalidIdException("lecture"));
    }

    // @DeleteMapping("/admin/lecture/delete")
    @Transactional // 강의 안내물 삭제 로직
    public void deleteLecture(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new InvalidIdException("lecture"));
        lectureRepository.delete(lecture);
    }

    public List<ResponseLectureWithoutFiles> findAllLectureByTrackOrderByIdDesc(TrackType trackType) {
        List<Lecture> lectures = lectureRepository.findByTrackOrderByIdDesc(trackType);
        return lectures.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<ResponseLectureWithoutFiles> findAllLectureByTrackOrderByCreateDateDesc(TrackType trackType) {
        List<Lecture> lectures = lectureRepository.findByTrackOrderByCreateDateDesc(trackType)
                .orElseThrow(EmptyLectureException::new);
        return lectures.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private ResponseLectureWithoutFiles convertToDTO(Lecture lecture) {
        return new ResponseLectureWithoutFiles(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getContent(),
                lecture.getWriter(),
                lecture.getCreateDate()
        );
    }
}

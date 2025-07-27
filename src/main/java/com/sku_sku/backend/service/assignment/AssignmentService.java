package com.sku_sku.backend.service.assignment;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.domain.assignment.*;
import com.sku_sku.backend.dto.Request.*;
import com.sku_sku.backend.dto.Response.AssignmentDTO.AssignmentDetail;
import com.sku_sku.backend.dto.Response.AssignmentDTO.AssignmentRes;
import com.sku_sku.backend.dto.Response.AssignmentDTO.FeedbackDetailRes;
import com.sku_sku.backend.dto.Response.AssignmentDTO.SubmittedAssignmentLion;
import com.sku_sku.backend.email.EmailService;
import com.sku_sku.backend.enums.FileStatusType;
import com.sku_sku.backend.enums.PassNonePass;
import com.sku_sku.backend.enums.RoleType;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.exception.InvalidAssignmentException;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.repository.*;
import com.sku_sku.backend.security.JwtUtility;
import com.sku_sku.backend.service.LionService;
import com.sku_sku.backend.service.S3Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sku_sku.backend.dto.Response.AssignmentDTO.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    private final SubmitAssignmentRepository submitAssignmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final JoinSubmitAssignmentFileRepository joinSubmitAssignmentFileRepository;
    private final JoinSubmitAssignmentFileService joinSubmitAssignmentFileService;
    private final FeedbackRepository feedbackRepository;
    private final EmailService emailService;
    private final JoinAssignmentFileService joinAssignmentFileService;
    private final JoinAssignmentFileRepository joinAssignmentFileRepository;
    private final S3Service s3Service;


    // 과제 업로드 (운영진)
    @Transactional
    public void uploadAssignment(AssignmentDTO.UploadAssignment request){

        if (request.getTrackType() == null || request.getDescription() == null || request.getQuizType() == null) {
            throw new IllegalArgumentException("필수값이 누락되었습니다.");
        }

        Assignment assignment=new Assignment(
                request.getTitle(),
                request.getTrackType(),
                request.getDescription(),
                request.getQuizType()
        );
        Assignment savedAssignment = assignmentRepository.save(assignment);
        joinAssignmentFileService.createJoinAssignmentFiles(savedAssignment, request.getFiles());

    }


    //업로드된 과제 삭제 (운영진)
    @Transactional
    public void deleteAssignment(Long assignmentId){
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(()->new InvalidIdException("해당 과제를 찾을 수 없음"));

        List<String> fileKey = joinAssignmentFileRepository.findByAssignment(assignment).stream()
                .map(JoinAssignmentFile::getFileKey)
                .filter(key -> key != null && !key.isBlank())
                .toList();

        if(!fileKey.isEmpty()){
            s3Service.deleteFiles(fileKey);
        }
        joinAssignmentFileRepository.deleteAllByAssignment(assignment);
        assignmentRepository.delete(assignment);
    }


    // 업로드된 과제 업데이트 (운영진)
    @Transactional
    public void updateAssignment(AssignmentDTO.UpdateAssignment req){
        Assignment assignment = assignmentRepository.findById(req.getAssignmentId())
                .orElseThrow(()-> new InvalidIdException("해당 과제가 없습니다."));

        assignment.updateAssignment(
                req.getTitle(),
                req.getTrackType(),
                req.getDescription(),
                req.getQuizType()
        );

        List<JoinAssignmentFileDTO.UpdateAssignmentFileDTO> files = req.getFiles();
        if(files != null && !files.isEmpty()){
            List<String> keyToDelete = files.stream()
                    .filter(f -> f.getStatus() == FileStatusType.DELETE)
                    .map(JoinAssignmentFileDTO.UpdateAssignmentFileDTO :: getFileKey)
                    .toList();
            s3Service.deleteFiles(keyToDelete);
            joinAssignmentFileService.deleteJoinAssignmentFiles(assignment, keyToDelete);

            List<JoinAssignmentFileDTO.UpdateAssignmentFileDTO> newFiles = files.stream()
                    .filter(f -> f.getStatus() == FileStatusType.NEW)
                    .toList();
            joinAssignmentFileService.updateJoinAssignmentFiles(assignment, newFiles);
        }
    }


    //과제를 제출한 아기사자 조회 (운영진)
    public List<SubmittedAssignmentLion> getSubmittedAssignmentLion(Long assignmentId){

        List<SubmitAssignment> submitAssignments= submitAssignmentRepository.findByAssignmentId(assignmentId);
        if(submitAssignments.isEmpty()) throw new InvalidAssignmentException("해당 과제에 대한 제출된 과제 없음");

        return submitAssignments.stream()
                .map(submitAssignment -> new SubmittedAssignmentLion(
                        submitAssignment.getLion().getName(),
                        submitAssignment.getId(),
                        submitAssignment.getPassNonePass()
                ))
                .collect(Collectors.toList());
    }


    //제출된 과제 운영진 채점 및 피드백 수정(운영진)
    @Transactional
    public void checkOrFeedbackSubmittedAssignment(AssignmentDTO.CheckSubmittedAssignment request){

        SubmitAssignment submitAssignment=submitAssignmentRepository.findById(request.getSubmitAssignmentId())
                .orElseThrow(()-> new InvalidIdException("해당 과제에 대한 제출된 과제를 찾을 수 없습니다."));

        submitAssignment.setPassNonePass(request.getPassNonePass());
        if ((request.getPassNonePass() == PassNonePass.NONE_PASS) &&
                (request.getFeedback() == null || request.getFeedback().trim().isEmpty())){
            throw new IllegalArgumentException("보류일 경우 피드백은 필수입니다.");
        }

        Optional<Feedback> existing=feedbackRepository.findBySubmitAssignmentId(request.getSubmitAssignmentId());

        Feedback feedback;

        if(existing.isPresent()){
            feedback=existing.get();
            feedback.updateFeedback(request.getFeedback());
        }else{
            feedback=new Feedback(submitAssignment, request.getFeedback());
        }

        feedbackRepository.save(feedback);

        Lion receiver = submitAssignment.getLion();
        String email=receiver.getEmail();

        String subject = "과제 채점 결과 알림";
        String content = "<h3>과제 채점 결과가 수정되었습니다.</h3>" +
                "<p>결과: "+request.getPassNonePass()+"</p>"+
                "<p>피드백: "+request.getFeedback()+"</p>";

        emailService.sendMail(email, subject, content);
    }


    //과제 채점 상세 페이지 (운영진) and 아기사자 본인이 제출한 과제 조회 (아기사자)
    public FeedbackDetailRes getFeedbackDetail(Long submitAssignmentId){

        SubmitAssignment submitAssignment=submitAssignmentRepository.findById(submitAssignmentId)
                .orElseThrow(()-> new InvalidIdException("제출된 과제가 없습니다."));

        Assignment assignment=submitAssignment.getAssignment();

        Optional<Feedback> feedback=feedbackRepository.findBySubmitAssignmentId(submitAssignmentId);

        List<JoinSubmitAssignmentFileDTO.submitAssignmentFileDTO> files = joinSubmitAssignmentFileRepository.findBySubmitAssignment(submitAssignment)
                .stream()
                .map(dto -> new JoinSubmitAssignmentFileDTO.submitAssignmentFileDTO(
                        dto.getFileName(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileUrl(),
                        dto.getFileKey()
                ))
                .toList();

        return new FeedbackDetailRes(
                assignment.getTitle(),
                assignment.getDescription(),
                submitAssignment.getContent(),
                feedback.map(Feedback::getContent).orElse(null),
                files
        );
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    // 과제 제출 (아기사자)
    @Transactional
    public void submitAssignment(Lion lion, SubmitAssignmentDTO.SubmitAssignment req){
        Assignment assignment = assignmentRepository.findById(req.getAssignmentId())
                .orElseThrow(() -> new InvalidIdException("해당 과제가 없습니다."));

        SubmitAssignment submitAssignment = new SubmitAssignment(assignment, lion, req.getContent());
        SubmitAssignment savedSubmitAssignment = submitAssignmentRepository.save(submitAssignment);
        joinSubmitAssignmentFileService.createJoinSubmitAssignmentFiles(savedSubmitAssignment, req.getFiles());
    }

    //제출된 과제 삭제(아기사자)
    @Transactional
    public void deleteSubmittedAssignment(Long submitAssignmentId){
        SubmitAssignment submitAssignment = submitAssignmentRepository.findById(submitAssignmentId)
                .orElseThrow(()->new InvalidIdException("제출된 과제가 없습니다."));

        List<String> filekey = joinSubmitAssignmentFileRepository
                .findBySubmitAssignment(submitAssignment)
                .stream()
                .map(JoinSubmitAssignmentFile :: getFileKey)
                .filter(key -> key != null && !key.isBlank())
                .toList();

        if(!filekey.isEmpty()){
            s3Service.deleteFiles(filekey);
        }
        joinSubmitAssignmentFileRepository.deleteAllBySubmitAssignment(submitAssignment);
        submitAssignmentRepository.delete(submitAssignment);
    }


    // 제출된 과제 업데이트(아기사자)
    @Transactional
    public void updateSubmitAssignment(Lion lion, SubmitAssignmentDTO.UpdateSubmitAssignment req){
        Assignment assignment = assignmentRepository.findById(req.getAssignmentId())
                .orElseThrow(() -> new InvalidIdException("해당 과제를 찾을 수 없습니다."));
        SubmitAssignment submitAssignment = submitAssignmentRepository.findByAssignmentAndLionId(assignment,lion.getId())
                .orElseThrow(()-> new EntityNotFoundException("제출된 과제가 없습니다."));

        submitAssignment.updateSubmitAssignment(req.getContent());

        List<JoinSubmitAssignmentFileDTO.UpdateSubmitAssignmentFileDTO> files = req.getFiles();
        if(files != null && !files.isEmpty()){
            List<String> keyToDelete = files.stream()
                    .filter(f -> f.getStatus() == FileStatusType.DELETE)
                    .map(JoinSubmitAssignmentFileDTO.UpdateSubmitAssignmentFileDTO :: getFileKey)
                    .toList();
            s3Service.deleteFiles(keyToDelete);
            joinSubmitAssignmentFileService.deleteJoinSubmitAssignmentFiles(submitAssignment, keyToDelete);

            List<JoinSubmitAssignmentFileDTO.UpdateSubmitAssignmentFileDTO> newFiles = files.stream()
                    .filter(f -> f.getStatus() == FileStatusType.NEW)
                    .toList();
            joinSubmitAssignmentFileService.updateJoinSubmitAssignmentFiles(submitAssignment, newFiles);
        }
    }

    //트랙별 모든 과제 조회 (아기사자)
    public List<AssignmentRes> getAssignment(Lion lion,TrackType trackType){
        List<Assignment> assignments=assignmentRepository.findByTrackType(trackType);
        if(assignments.isEmpty()) throw new InvalidAssignmentException("현재 트랙에 과제 없음");


        return assignments.stream()
                .map(assignment -> {
                    Optional<SubmitAssignment> submission = submitAssignmentRepository.findByAssignmentAndLionId(assignment, lion.getId());

                    PassNonePass status = submission
                            .map(SubmitAssignment::getPassNonePass)
                            .orElse(PassNonePass.UNREVIEWED);

                    return new AssignmentRes(
                            assignment.getId(),
                            assignment.getTitle(),
                            submission.isPresent(),
                            assignment.getDescription(),
                            status
                    );
                })
                .toList();
    }


    //과제 조회 상세페이지 (아기사자)
    public AssignmentDetail getAssignmentDetail(Long assignmentId){

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(()-> new InvalidAssignmentException("해당 과제를 찾을 수 없음"));

        List<JoinAssignmentFile> files = joinAssignmentFileRepository.findByAssignment(assignment);

        String description = assignment.getDescription();

        return new AssignmentDetail(
                description,
                files.stream()
                .map(dto -> new JoinAssignmentFileDTO.AssignmentFileDTO(
                        dto.getFileName(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileUrl(),
                        dto.getFileKey()
                ))
                .toList()
        );
    }

    public SubmitAssigmentRes getSubmitAssignment(Lion lion, Long assignmentId){
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new InvalidIdException("해당과제를 찾을 수 없음"));

        SubmitAssignment submitAssignment = submitAssignmentRepository.findByAssignmentAndLionId(assignment, lion.getId())
                .orElseThrow(() -> new EntityNotFoundException(lion.getName() + "의 제출물이 존재하지 않습니다."));

        String feedbackContent = feedbackRepository.findBySubmitAssignmentId(submitAssignment.getId())
                .map(Feedback :: getContent).orElse(null);

        List<JoinSubmitAssignmentFileDTO.submitAssignmentFileDTO> files = joinSubmitAssignmentFileRepository.findBySubmitAssignment(submitAssignment)
                .stream()
                .map(dto -> new JoinSubmitAssignmentFileDTO.submitAssignmentFileDTO(
                        dto.getFileName(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileUrl(),
                        dto.getFileKey()
                ))
                .toList();

        return new SubmitAssigmentRes(submitAssignment.getContent(), feedbackContent, files);
    }
}

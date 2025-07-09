package com.sku_sku.backend.service.assignment;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.domain.assignment.Assignment;
import com.sku_sku.backend.domain.assignment.Feedback;
import com.sku_sku.backend.domain.assignment.JoinSubmitAssignmentFile;
import com.sku_sku.backend.domain.assignment.SubmitAssignment;
import com.sku_sku.backend.dto.Request.AssignmentDTO;
import com.sku_sku.backend.dto.Response.AssignmentDTO.AssignmentDetail;
import com.sku_sku.backend.dto.Response.AssignmentDTO.AssignmentRes;
import com.sku_sku.backend.dto.Response.AssignmentDTO.FeedbackDetailRes;
import com.sku_sku.backend.dto.Response.AssignmentDTO.SubmittedAssignmentLion;
import com.sku_sku.backend.email.EmailService;
import com.sku_sku.backend.enums.PassNonePass;
import com.sku_sku.backend.enums.RoleType;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.exception.InvalidAssignmentException;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.repository.AssignmentRepository;
import com.sku_sku.backend.repository.FeedbackRepository;
import com.sku_sku.backend.repository.JoinSubmitAssignmentFileRepository;
import com.sku_sku.backend.repository.SubmitAssignmentRepository;
import com.sku_sku.backend.security.JwtUtility;
import com.sku_sku.backend.service.LionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final FeedbackRepository feedbackRepository;
    private final LionService lionService;
    private final JwtUtility jwtUtility;
    private final EmailService emailService;

    //과제 제출 // 고민 필요함
    @Transactional
    public void saveSubmittedAssignment(HttpServletRequest header, AssignmentDTO.SubmitAssignment req) throws IOException {
        String token = jwtUtility.extractTokenFromCookies(header);
        jwtUtility.validateJwt(token);
        Lion lion = lionService.tokenToLion(token);

        Assignment assignment = assignmentRepository.findById(req.getAssignmentId())
                .orElseThrow(()-> new InvalidAssignmentException("해당 과제가 없습니다."));

        Optional<SubmitAssignment> existing = submitAssignmentRepository.findByAssignmentAndLionId(assignment, lion.getId());

        SubmitAssignment submitAssignment;

        if(existing.isPresent()) {
            submitAssignment = existing.get();
            submitAssignment.updateSubmitAssignment(req.getContent());

            joinSubmitAssignmentFileRepository.deleteAllBySubmitAssignment(submitAssignment);
        } else {
            submitAssignment=new SubmitAssignment(assignment, lion, req.getContent());
        }

        submitAssignmentRepository.save(submitAssignment);

        if(req.getFiles() != null){
            List<JoinSubmitAssignmentFile> joinSubmitAssignmentFiles = req.getFiles().stream()
                    .map(dto -> new JoinSubmitAssignmentFile(
                            submitAssignment,
                            dto.getFileName(),
                            dto.getFileType(),
                            dto.getFileSize(),
                            dto.getFileUrl(),
                            dto.getFileKey()
                    ))
                    .toList();

            joinSubmitAssignmentFileRepository.saveAll(joinSubmitAssignmentFiles);
        }
    }

    // 과제 업로드
    @Transactional
    public void uploadAssignment(HttpServletRequest header, AssignmentDTO.UploadAssignment request) throws AccessDeniedException {
        String token= jwtUtility.extractTokenFromCookies(header);
        jwtUtility.validateJwt(token);
        Lion lion=lionService.tokenToLion(token);
        if(lion.getRoleType()!= RoleType.ADMIN_LION) throw new AccessDeniedException("관리자만 과제를 등록할 수 있습니다.");

        if (request.getTrackType() == null || request.getTitle() == null ||
                request.getDescription() == null || request.getQuizType() == null) {
            throw new IllegalArgumentException("필수값이 누락되었습니다.");
        }
        Assignment assignment=new Assignment(
                request.getTitle(),
                request.getTrackType(),
                request.getDescription(),
                request.getQuizType()
        );
        assignmentRepository.save(assignment);
    }

    //트랙별 모든 과제 조회
    public List<AssignmentRes> getAssignment(HttpServletRequest header, TrackType trackType){
        String token = jwtUtility.extractTokenFromCookies(header);
        jwtUtility.validateJwt(token);
        Long lionId=lionService.tokenToLion(token).getId();

        List<Assignment> assignments=assignmentRepository.findByTrackType(trackType);
        if(assignments.isEmpty()) throw new InvalidAssignmentException("현재 트랙에 과제 없음");


        return assignments.stream()
                .map(assignment -> {
                    AssignmentRes dto = new AssignmentRes();
                    dto.setAssignmentId(assignment.getId());
                    dto.setTitle(assignment.getTitle());
                    dto.setDescription(assignment.getDescription());

                    Optional<SubmitAssignment> submission= submitAssignmentRepository.findByAssignmentAndLionId(assignment, lionId);
                    dto.setIsSubmit(submission.isPresent());

                    PassNonePass status = submission
                            .map(SubmitAssignment::getPassNonePass)
                            .orElse(PassNonePass.UNREVIEWED);

                    dto.setAdminCheck(status);

                    return dto;

                })
                .collect(Collectors.toList());
    }

    //과제를 제출한 아기사자 조회
    public List<SubmittedAssignmentLion> getSubmittedAssignmentLion(HttpServletRequest header, Long assignmentId) throws AccessDeniedException{
        String token= jwtUtility.extractTokenFromCookies(header);
        jwtUtility.validateJwt(token);
        Lion lion=lionService.tokenToLion(token);
        if(lion.getRoleType()!= RoleType.ADMIN_LION) throw new AccessDeniedException("관리자만 조회 가능합니다.");


        List<SubmitAssignment> submitAssignments= submitAssignmentRepository.findByAssignmentId(assignmentId);
        if(submitAssignments.isEmpty()) throw new InvalidAssignmentException("해당 과제에 대한 제출된 과제 없음");

        return submitAssignments.stream()
                .map(submitAssignment -> {
                    SubmittedAssignmentLion dto = new SubmittedAssignmentLion();
                    dto.setLionName(submitAssignment.getLion().getName());
                    dto.setSubmitAssignmentId(submitAssignment.getId());
                    dto.setPassNonePass(submitAssignment.getPassNonePass());

                    return dto;
                })
                .collect(Collectors.toList());
    }


    //제출된 과제 운영진 채점 및 피드백 수정
    @Transactional
    public void checkOrFeedbackSubmittedAssignment(HttpServletRequest header, AssignmentDTO.CheckSubmittedAssignment request){
        String token= jwtUtility.extractTokenFromCookies(header);
        jwtUtility.validateJwt(token);
        Lion lion=lionService.tokenToLion(token);
        if(lion.getRoleType()!=RoleType.ADMIN_LION) throw new AccessDeniedException("관리자만 채점 및 피드백을 할 수 있습니다.");

        SubmitAssignment submitAssignment=submitAssignmentRepository.findById(request.getSubmitAssignmentId())
                .orElseThrow(()-> new InvalidIdException("해당 과제에 대한 제출된 과제를 찾을 수 없습니다."));

        submitAssignment.setPassNonePass(request.getPassNonePass());
        if (request.getPassNonePass() == PassNonePass.NONE_PASS&&
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


    //과제 채점 상세 페이지
    public FeedbackDetailRes getFeedbackDetail(HttpServletRequest header, Long submitAssignmentId){
        String token= jwtUtility.extractTokenFromCookies(header);
        jwtUtility.validateJwt(token);
        Lion lion=lionService.tokenToLion(token);
        if(lion.getRoleType()!=RoleType.ADMIN_LION) throw new AccessDeniedException("관리자만 채점 및 피드백을 할 수 있습니다.");

        SubmitAssignment submitAssignment=submitAssignmentRepository.findById(submitAssignmentId)
                .orElseThrow(()-> new InvalidIdException("제출된 과제가 없습니다."));

        Assignment assignment=submitAssignment.getAssignment();

        Optional<Feedback> feedback=feedbackRepository.findBySubmitAssignmentId(submitAssignmentId);

        FeedbackDetailRes dto = new FeedbackDetailRes();
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getDescription());
        dto.setFeedback(feedback.map(Feedback::getContent).orElse(null));

        return dto;
    }

    //과제 조회 상세페이지
    public AssignmentDetail getAssignmentDetail(HttpServletRequest header, Long assignmentId){
        String token= jwtUtility.extractTokenFromCookies(header);
        jwtUtility.validateJwt(token);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(()-> new InvalidAssignmentException("해당 과제를 찾을 수 없음"));

        String description = assignment.getDescription();
        AssignmentDetail dto = new AssignmentDetail();
        dto.setDescription(description);

        return dto;
    }
}

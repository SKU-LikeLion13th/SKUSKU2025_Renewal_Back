package com.sku_sku.backend.service;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.domain.assignment.Assignment;
import com.sku_sku.backend.domain.assignment.Feedback;
import com.sku_sku.backend.domain.assignment.JoinSubmitAssignmentFile;
import com.sku_sku.backend.domain.assignment.SubmitAssignment;
import com.sku_sku.backend.dto.Request.AssignmentDTO;
import com.sku_sku.backend.dto.Request.SubmitAssignmentDTO;
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
public class SubmitAssignmentService {

    private final SubmitAssignmentRepository submitAssignmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final JoinSubmitAssignmentFileRepository joinSubmitAssignmentFileRepository;
    private final LionService lionService;
    private final JwtUtility jwtUtility;

    //과제 제출 // 고민 필요함
    @Transactional
    public void submitAssignment(HttpServletRequest header, SubmitAssignmentDTO.SubmitAssignment req) {
        String token = jwtUtility.extractTokenFromCookies(header);
        Lion lion = lionService.tokenToLion(token);
        Assignment assignment = assignmentRepository.findById(req.getAssignmentId())
                .orElseThrow(()-> new InvalidAssignmentException("해당 과제가 없습니다."));
        SubmitAssignment submitAssignment = new SubmitAssignment(assignment, lion, req.getContent());
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

//    @Transactional
//    public void updateSubmittedAssignment(HttpServletRequest header, SubmitAssignmentRequest req) {
//        String token = jwtUtility.extractTokenFromCookies(header);
//        Lion lion = lionService.tokenToLion(token);
//    }
}

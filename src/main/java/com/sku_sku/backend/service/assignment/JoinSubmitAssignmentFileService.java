package com.sku_sku.backend.service.assignment;

import com.sku_sku.backend.domain.assignment.JoinSubmitAssignmentFile;
import com.sku_sku.backend.domain.assignment.SubmitAssignment;
import com.sku_sku.backend.dto.Request.JoinSubmitAssignmentFileDTO;
import com.sku_sku.backend.repository.JoinSubmitAssignmentFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinSubmitAssignmentFileService {

    private final JoinSubmitAssignmentFileRepository joinSubmitAssignmentFileRepository;

    @Transactional
    public void createJoinLectureFiles(SubmitAssignment submitAssignment, List<JoinSubmitAssignmentFileDTO.submitAssignmentFileDTO> files) {
        List<JoinSubmitAssignmentFile> joinSubmitAssignmentFiles = files.stream()
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

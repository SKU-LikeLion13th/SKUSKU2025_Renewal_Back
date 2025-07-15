package com.sku_sku.backend.service.assignment;

import com.sku_sku.backend.domain.assignment.Assignment;
import com.sku_sku.backend.domain.assignment.JoinAssignmentFile;
import com.sku_sku.backend.domain.assignment.JoinSubmitAssignmentFile;
import com.sku_sku.backend.domain.assignment.SubmitAssignment;
import com.sku_sku.backend.dto.Request.JoinSubmitAssignmentFileDTO;
import com.sku_sku.backend.dto.Request.SubmitAssignmentDTO;
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
    public void createJoinSubmitAssignmentFiles(SubmitAssignment submitAssignment, List<JoinSubmitAssignmentFileDTO.submitAssignmentFileDTO> files) {
        List<JoinSubmitAssignmentFile> joinSubmitAssignmentFiles = files.stream()
                .map(dto -> new JoinSubmitAssignmentFile(
                        submitAssignment,
                        dto.getFileName(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileUrl(),
                        dto.getFileKey(),
                        false
                ))
                .toList();

        joinSubmitAssignmentFileRepository.saveAll(joinSubmitAssignmentFiles);
    }

    @Transactional
    public void updateJoinSubmitAssignmentFiles(SubmitAssignment submitAssignment, List<JoinSubmitAssignmentFileDTO.UpdateSubmitAssignmentFileDTO> files){
        List<JoinSubmitAssignmentFile> newFiles = files.stream()
                .map(dto -> new JoinSubmitAssignmentFile(
                        submitAssignment,
                        dto.getFileName(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileUrl(),
                        dto.getFileKey(),
                        true
                ))
                .toList();

        joinSubmitAssignmentFileRepository.saveAll(newFiles);
    }

    @Transactional
    public void delteJoinSubmitAssignmentFilse(SubmitAssignment submitAssignment , List<String> keyToDelete){
        if(keyToDelete == null || keyToDelete.isEmpty()) throw new IllegalArgumentException("필수값 누락");
        joinSubmitAssignmentFileRepository.deleteAllBySubmitAssignmentAndFileKeyIn(submitAssignment, keyToDelete);
    }
}

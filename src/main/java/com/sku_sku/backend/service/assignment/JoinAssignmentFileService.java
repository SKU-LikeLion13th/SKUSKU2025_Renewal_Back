package com.sku_sku.backend.service.assignment;

import com.sku_sku.backend.domain.assignment.Assignment;
import com.sku_sku.backend.domain.assignment.JoinAssignmentFile;
import com.sku_sku.backend.dto.Request.JoinAssignmentFileDTO;
import com.sku_sku.backend.repository.JoinAssignmentFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinAssignmentFileService {
    private final JoinAssignmentFileRepository joinAssignmentFileRepository;

    @Transactional
    public void createJoinAssignmentFiles(Assignment assignment, List<JoinAssignmentFileDTO.AssignmentFileDTO> files){
        List<JoinAssignmentFile> joinAssignmentFiles = files.stream()
                .map(dto -> new JoinAssignmentFile(
                        assignment,
                        dto.getFileName(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileUrl(),
                        dto.getFileKey(),
                        false
                ))
                .toList();
        joinAssignmentFileRepository.saveAll(joinAssignmentFiles);
    }

    @Transactional
    public void updateJoinAssignmentFiles(Assignment assignment, List<JoinAssignmentFileDTO.UpdateAssignmentFileDTO> files){
        List<JoinAssignmentFile> newFiles = files.stream()
                .map(dto -> new JoinAssignmentFile(
                        assignment,
                        dto.getFileName(),
                        dto.getFileType(),
                        dto.getFileSize(),
                        dto.getFileUrl(),
                        dto.getFileKey(),
                        true
                ))
                .toList();
        joinAssignmentFileRepository.saveAll(newFiles);
    }

    @Transactional
    public void deleteJoinAssignmentFiles(Assignment assignment, List<String> keyToDelete){
        if (keyToDelete == null || keyToDelete.isEmpty()) throw new IllegalArgumentException("필수값 누락");
        joinAssignmentFileRepository.deleteAllByAssignmentAndFileKeyIn(assignment, keyToDelete);

    }


}

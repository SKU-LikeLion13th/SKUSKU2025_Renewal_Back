package com.sku_sku.backend.controller;

import com.sku_sku.backend.dto.Request.S3DTO;
import com.sku_sku.backend.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3PresignedService;

    @Operation(summary = "(민규) Presigned URL + CDN URL 요청", description = "body에 리스트로 파일 이름, 파일 MIME 타입 필요",
            responses = {@ApiResponse(responseCode = "200", description = "URL들 발급 성공"),
                    @ApiResponse(responseCode = "400", description = "허용되지 않은 MIME 타입입니다.")})
    @PostMapping("/s3/presigned-urls")
    public ResponseEntity<?> getPresignedUrls(@RequestBody List<S3DTO.PresignedUrlRequest> requests) {
        List<S3DTO.PresignedUrlResponse> response = requests.stream()
                .map(s3PresignedService::issuePresignedAndCdnUrl)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(summary = "(민규) FileKey로 삭제", description = "body에 리스트로 key 필요",
            responses = {@ApiResponse(responseCode = "204", description = "파일 삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "1.파일 삭제 중 오류가 발생했습니다.<br>2.파일 삭제 실패")})
    @DeleteMapping("/s3")
    public ResponseEntity<Void> deleteFile(@RequestBody List<String> keys) {
        s3PresignedService.deleteFiles(keys);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}


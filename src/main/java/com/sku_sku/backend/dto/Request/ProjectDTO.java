package com.sku_sku.backend.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

public class ProjectDTO {

    @Data
    public static class ProjectCreateRequest {
        @Schema(description = "프로젝트 기수", example = "11 or 12 or 13")
        private String classTh;
        @Schema(description = "프로젝트 제목", example = "스쿠스쿠")
        private String title;
        @Schema(description = "프로젝트 부제목", example = "LikeLion sku 공식페이지")
        private String subTitle;
        @Schema(description = "프로젝트 url", example = "https://sku-sku.com")
        private String url;
        @Schema(description = "프로젝트 사진 CDN URL", example = "")
        private String imageUrl;
    }

    @Data
    public static class ProjectUpdateRequest {
        @Schema(description = "프로젝트 아이디", example = "1")
        private Long id;
        @Schema(description = "프로젝트 기수", example = "11 or 12 or 13")
        private String classTh;
        @Schema(description = "프로젝트 제목", example = "스쿠스쿠")
        private String title;
        @Schema(description = "프로젝트 부제목", example = "LikeLion sku 공식페이지")
        private String subTitle;
        @Schema(description = "프로젝트 url", example = "https://sku-sku.com")
        private String url;
        @Nullable
        @Schema(description = "프로젝트 사진 CDN URL", example = "")
        private String imageUrl;
    }
}

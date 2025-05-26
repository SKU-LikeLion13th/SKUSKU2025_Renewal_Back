package com.sku_sku.backend.dto.Response;

import com.sku_sku.backend.enums.AllowedFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ProjectDTO {

    @Data
    @AllArgsConstructor
    public static class ProjectWithoutId {
        @Schema(description = "프로젝트 기수", example = "11 or 12 or 13")
        private String classTh;

        @Schema(description = "프로젝트 제목", example = "스쿠스쿠")
        private String title;

        @Schema(description = "프로젝트 부제목", example = "LikeLion sku 공식페이지")
        private String subTitle;

        @Schema(description = "프로젝트 url", example = "https://sku-sku.com")
        private String projectUrl;

        @Schema(description = "프로젝트 이미지 CDN URL", example = "https://~~~")
        private String imageUrl;

        @Schema(description = "프로젝트 이미지 저장된 경로", example = "uploads/{uuid}.png")
        private String imageKey;
    }

    @Data
    @AllArgsConstructor
    public static class ProjectRes {
        @Schema(description = "프로젝트 id", example = "1")
        private Long id;

        @Schema(description = "프로젝트 기수", example = "11 or 12 or 13")
        private String classTh;

        @Schema(description = "프로젝트 제목", example = "스쿠스쿠")
        private String title;

        @Schema(description = "프로젝트 부제목", example = "LikeLion sku 공식페이지")
        private String subTitle;

        @Schema(description = "프로젝트 url", example = "https://sku-sku.com")
        private String projectUrl;

        @Schema(description = "프로젝트 이미지 이름", example = "스쿠스쿠 로고")
        private String imageName;

        @Schema(description = "프로젝트 이미지 타입", example = "PNG")
        private AllowedFileType imageType;

        @Schema(description = "프로젝트 이미지 사이즈", example = "65362")
        private Long fileSize;

        @Schema(description = "프로젝트 이미지 CDN URL", example = "https://~~~")
        private String imageUrl;

        @Schema(description = "프로젝트 이미지 저장된 경로", example = "uploads/{uuid}.png")
        private String imageKey;
    }
}

package com.sku_sku.backend.dto.Response;

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
        private String url;

        @Schema(description = "프로젝트 사진 CDN URL", example = "")
        private String imageUrl;
    }

    @Data
    @AllArgsConstructor
    public static class ProjectAllField {
        @Schema(description = "프로젝트 id", example = "1")
        private Long id;

        @Schema(description = "프로젝트 기수", example = "11 or 12 or 13")
        private String classTh;

        @Schema(description = "프로젝트 제목", example = "스쿠스쿠")
        private String title;

        @Schema(description = "프로젝트 부제목", example = "LikeLion sku 공식페이지")
        private String subTitle;

        @Schema(description = "프로젝트 url", example = "https://sku-sku.com")
        private String url;

        @Schema(description = "프로젝트 사진", example = "")
        private String image;
    }
}

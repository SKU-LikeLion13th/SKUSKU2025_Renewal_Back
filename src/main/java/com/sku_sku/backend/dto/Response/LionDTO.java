package com.sku_sku.backend.dto.Response;

import com.sku_sku.backend.enums.RoleType;
import com.sku_sku.backend.enums.TrackType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

public class LionDTO {
// Response
    @Data
    @AllArgsConstructor
    public static class ResponseLionUpdate {
        @Schema(description = "이름", example = "한민규")
        private String name;

        @Schema(description = "이메일", example = "alswb0830@sungkyul.ac.kr")
        private String email;

        @Schema(description = "트랙", example = "BACKEND or FRONTEND or DESIGN")
        private TrackType trackType;

        @Schema(description = "권한", example = "ADMIN_LION or BABY_LION")
        private RoleType roleType;
    }
}

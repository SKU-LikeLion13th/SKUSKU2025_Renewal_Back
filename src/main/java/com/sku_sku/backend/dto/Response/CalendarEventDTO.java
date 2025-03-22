package com.sku_sku.backend.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

public class CalendarEventDTO {

    @Data
    @AllArgsConstructor
    public static class AllCalendarEvent {
        @Schema(description = "캘린더 일정 id(pk)", example = "1")
        private Long id;
        @Schema(description = "캘린더 일정 제목", example = "중앙 해커톤")
        private String title;
        @Schema(description = "일정 시작 시점", example = "YYYY-MM-DD")
        private LocalDate startDate;
        @Schema(description = "일정 종료 시점", example = "YYYY-MM-DD")
        private LocalDate endDate;
        @Schema(description = "일정 표시 색상", example = "#265EBF")
        private String color;
    }
}

package com.sku_sku.backend.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

public class CalendarScheduleDTO {

    @Data
    public static class AddCalendarEvent {
        @Schema(description = "캘린더 일정 제목", example = "중앙 해커톤")
        private String title;
        @Schema(description = "일정 시작 시점", example = "YYYY-MM-DD")
        private LocalDate startDate;
        @Schema(description = "일정 종료 시점", example = "YYYY-MM-DD")
        private LocalDate endDate;
        @Schema(description = "일정 표시 색상", example = "#265EBF")
        private String color;
    }

    @Data
    public static class UpdateCalendarEvent {
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

    @Data
    public static class OnlyIdCalendarEvent {
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

    @Data
    public static class YearAndMonth {
        @Schema(description = "캘린더 일정 연도", example = "2025")
        private int year;
        @Schema(description = "캘린더 일정 월", example = "3")
        private int month;
    }
}

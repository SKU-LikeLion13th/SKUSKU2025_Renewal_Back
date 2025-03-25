package com.sku_sku.backend.dto.Response;

import com.sku_sku.backend.domain.CalendarSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class CalendarScheduleDTO {

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

    public record MonthlySchedulesResponse(
            @Schema(description = "캘린더에 보여줄 시작 날짜", example = "2025-02-23")
            LocalDate from,
            @Schema(description = "캘린더에 보여줄 종료 날짜", example = "2025-04-05")
            LocalDate to,
            @Schema(description = "해당 범위에 포함된 일정 목록")
            List<CalendarSchedule> calendarSchedule
    ) {}
}

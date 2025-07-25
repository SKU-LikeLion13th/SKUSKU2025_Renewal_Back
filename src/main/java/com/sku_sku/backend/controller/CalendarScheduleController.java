package com.sku_sku.backend.controller;

import com.sku_sku.backend.service.CalendarScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sku_sku.backend.dto.Request.CalendarScheduleDTO.YearAndMonth;
import static com.sku_sku.backend.dto.Response.CalendarScheduleDTO.MonthlySchedulesResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "아기사자 기능: 캘린더 일정 관련")
public class CalendarScheduleController {

    private final CalendarScheduleService calendarScheduleService;

    @Operation(summary = "(민규) 캘린더 일정 달별로 조회", description = "쿼리 파라미터로 CalendarSchedule의 year, month 필요",
            responses = @ApiResponse(responseCode = "200", description = "일정 조회 성공"))
    @GetMapping("/schedules")
    public ResponseEntity<MonthlySchedulesResponse> findMonthlySchedules(YearAndMonth req) {
        return ResponseEntity.status(HttpStatus.OK).body(calendarScheduleService.findMonthlySchedules(req));
    }
}

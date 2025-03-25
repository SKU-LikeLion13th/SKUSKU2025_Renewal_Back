package com.sku_sku.backend.controller;

import com.sku_sku.backend.service.CalendarScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import static com.sku_sku.backend.dto.Request.CalendarScheduleDTO.YearAndMonth;
import static com.sku_sku.backend.dto.Response.CalendarScheduleDTO.MonthlySchedulesResponse;

@RestController
@RequiredArgsConstructor
public class CalendarScheduleController {

    private final CalendarScheduleService calendarScheduleService;

    @Operation(summary = "(민규) CalendarSchedule 하나 삭제", description = "Headers에 Bearer token 필요, 쿼리 파라미터로 CalendarSchedule의 year, month 필요",
            responses = {@ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "calendarScheduleId로 조회한 결과 없음")})
    @GetMapping("/schedules")
    public ResponseEntity<MonthlySchedulesResponse> findMonthlySchedules(@ModelAttribute YearAndMonth req) {
        return ResponseEntity.status(HttpStatus.OK).body(calendarScheduleService.findMonthlySchedules(req));
    }
}

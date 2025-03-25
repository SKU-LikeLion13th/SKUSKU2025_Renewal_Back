package com.sku_sku.backend.controller.admin;

import com.sku_sku.backend.domain.CalendarSchedule;
import com.sku_sku.backend.service.CalendarScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sku_sku.backend.dto.Request.CalendarScheduleDTO.AddCalendarEvent;
import static com.sku_sku.backend.dto.Request.CalendarScheduleDTO.UpdateCalendarEvent;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/schedule")
@Tag(name = "관리자 기능: 캘린더 일정 관련")
public class CalendarScheduleAdminController {

    private final CalendarScheduleService calendarScheduleService;

    @Operation(summary = "(민규) CalendarSchedule 추가", description = "Headers에 Bearer token 필요, body에 json로 CalendarSchedule의 title, startDate, eventDate, color 필요",
            responses = {@ApiResponse(responseCode = "201", description = "생성 성공")})
    @PostMapping("/add")
    public ResponseEntity<CalendarSchedule> addCalendarEvent(@RequestBody AddCalendarEvent req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarScheduleService.addCalendarEvent(req));
    }

    @Operation(summary = "(민규) CalendarSchedule 수정", description = "Headers에 Bearer token 필요, body에 json로 CalendarSchedule의 id, title, startDate, eventDate, color 필요",
            responses = {@ApiResponse(responseCode = "201", description = "수정 성공"),
                    @ApiResponse(responseCode = "404", description = "calendarScheduleId로 조회한 결과 없음")})
    @PutMapping("/update")
    public ResponseEntity<CalendarSchedule> updateCalendarEvent(@RequestBody UpdateCalendarEvent req) {
        return ResponseEntity.status(HttpStatus.OK).body(calendarScheduleService.updateCalendarEvent(req));
    }

    @Operation(summary = "(민규) CalendarSchedule 하나 삭제", description = "Headers에 Bearer token 필요, 경로변수에 json로 CalendarSchedule의 id 필요",
            responses = {@ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "calendarScheduleId로 조회한 결과 없음")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendarEvent(@PathVariable("id") Long calendarEventId) {
        calendarScheduleService.deleteCalendarEvent(calendarEventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

package com.sku_sku.backend.controller.admin;

import com.sku_sku.backend.domain.CalendarEvent;
import com.sku_sku.backend.dto.Request.CalendarEventDTO;
import com.sku_sku.backend.service.CalendarEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sku_sku.backend.dto.Request.CalendarEventDTO.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/calendarevent")
public class CalendarEventController {

    private CalendarEventService calendarEventService;

    @Operation(summary = "(민규) CalendarEvent 추가", description = "Headers에 Bearer token 필요, body에 json로 CalendarEvent의 title, startDate, eventDate, color 필요",
            responses = {@ApiResponse(responseCode = "201", description = "생성 성공")})
    @PostMapping("/add")
    public ResponseEntity<CalendarEvent> addCalendarEvent(@RequestBody AddCalendarEvent req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarEventService.addCalendarEvent(req));
    }

    @Operation(summary = "(민규) CalendarEvent 수정", description = "Headers에 Bearer token 필요, body에 json로 CalendarEvent의 id, title, startDate, eventDate, color 필요",
            responses = {@ApiResponse(responseCode = "201", description = "수정 성공"),
                    @ApiResponse(responseCode = "404", description = "calendarEventId로 조회한 결과 없음")})
    @PutMapping("/update")
    public ResponseEntity<CalendarEvent> updateCalendarEvent(@RequestBody UpdateCalendarEvent req) {
        return ResponseEntity.status(HttpStatus.OK).body(calendarEventService.updateCalendarEvent(req));
    }

    @Operation(summary = "(민규) CalendarEvent 하나 삭제", description = "Headers에 Bearer token 필요, body에 json로 CalendarEvent의 id 필요",
            responses = {@ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "calendarEventId로 조회한 결과 없음")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendarEvent(@PathVariable("id") Long calendarEventId) {
        calendarEventService.deleteCalendarEvent(calendarEventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

package com.sku_sku.backend.service;

import com.sku_sku.backend.domain.CalendarSchedule;
import com.sku_sku.backend.dto.Response.CalendarScheduleDTO.MonthlySchedulesResponse;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.repository.CalendarScheduleRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.sku_sku.backend.dto.Request.CalendarScheduleDTO.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarScheduleService {

    private final CalendarScheduleRepository calendarScheduleRepository;

    @Transactional
    public void addCalendarEvent(AddCalendarEvent req) {
        CalendarSchedule calendarSchedule = new CalendarSchedule(req.getTitle(), req.getStartDate(), req.getEndDate(), req.getColor());
        calendarScheduleRepository.save(calendarSchedule);
    }

    @Transactional
    public void  updateCalendarEvent(UpdateCalendarEvent req) {
        CalendarSchedule calendarSchedule = calendarScheduleRepository.findById(req.getId())
                .orElseThrow(() -> new InvalidIdException("calendarEvent"));

        String newTitle = ((req.getTitle() != null && !req.getTitle().isEmpty()) ? req.getTitle() : calendarSchedule.getTitle());
        LocalDate newStartDate = (req.getStartDate() != null ? req.getStartDate() : calendarSchedule.getStartDate());
        LocalDate newEndDate = (req.getEndDate() != null ? req.getEndDate() : calendarSchedule.getEndDate());
        String newColor = ((req.getColor() != null && !req.getColor().isEmpty()) ? req.getColor() : calendarSchedule.getColor());
        calendarSchedule.update(newTitle, newStartDate, newEndDate, newColor);
    }

    @Transactional
    public void deleteCalendarEvent(Long calendarEventId) {
        CalendarSchedule calendarSchedule = calendarScheduleRepository.findById(calendarEventId)
                .orElseThrow(() -> new InvalidIdException("calendarEvent"));

        calendarScheduleRepository.delete(calendarSchedule);
    }

    public MonthlySchedulesResponse findMonthlySchedules(YearAndMonth req) {
        CalendarRange calendarRange = findCalendarRangeForMonth(req);

        List<CalendarSchedule> calendarSchedules = calendarScheduleRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(calendarRange.to(), calendarRange.from());

        return new MonthlySchedulesResponse(calendarRange.from(), calendarRange.to(), calendarSchedules);
    }

    public CalendarRange findCalendarRangeForMonth(YearAndMonth req) {
        LocalDate firstOfMonth = LocalDate.of(req.getYear(), req.getMonth(), 1); // 해당 연월의 1일을 구함
        LocalDate start = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)); // firstOfMonth에서 그 주의 일요일까지 앞으로 이동 // 예) 3/1(토) -> 2/24(일)

        LocalDate lastOfMonth = firstOfMonth.with(TemporalAdjusters.lastDayOfMonth()); // 해당 연월의 마지막 날을 구함
        LocalDate end = lastOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)); // 마지막 날을 기준으로 그 주의 토요일까지 앞 or 뒤로 이동 // 예) 3/31(월) -> 4/5(토)

        return new CalendarRange(start, end);
    }

    public record CalendarRange(
            @Schema(description = "캘린더에 보여줄 시작 날짜", example = "2025-02-23")
            LocalDate from,
            @Schema(description = "캘린더에 보여줄 종료 날짜", example = "2025-04-05")
            LocalDate to
    ) {}

}

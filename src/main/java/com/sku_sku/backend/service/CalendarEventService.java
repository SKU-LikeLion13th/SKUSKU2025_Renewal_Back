package com.sku_sku.backend.service;

import com.sku_sku.backend.domain.CalendarEvent;
import com.sku_sku.backend.dto.Request.CalendarEventDTO;
import com.sku_sku.backend.dto.Response.CalendarEventDTO.AllCalendarEvent;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.sku_sku.backend.dto.Request.CalendarEventDTO.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarEventService {

    private CalendarEventRepository calendarEventRepository;

    @Transactional
    public CalendarEvent addCalendarEvent(AddCalendarEvent req) {
        CalendarEvent calendarEvent = new CalendarEvent(req.getTitle(), req.getStartDate(), req.getEndDate(), req.getColor());
        calendarEventRepository.save(calendarEvent);
        return calendarEvent;
    }

    @Transactional
    public CalendarEvent updateCalendarEvent(UpdateCalendarEvent req) {
        CalendarEvent calendarEvent = calendarEventRepository.findById(req.getCalendarEventId())
                .orElseThrow(() -> new InvalidIdException("calendarEvent"));

        String newTitle = ((req.getTitle() != null && !req.getTitle().isEmpty()) ? req.getTitle() : calendarEvent.getTitle());
        LocalDate newStartDate = (req.getStartDate() != null ? req.getStartDate() : calendarEvent.getStartDate());
        LocalDate newEndDate = (req.getEndDate() != null ? req.getEndDate() : calendarEvent.getEndDate());
        String newColor = ((req.getColor() != null && !req.getColor().isEmpty()) ? req.getColor() : calendarEvent.getColor());
        calendarEvent.update(newTitle, newStartDate, newEndDate, newColor);

        return calendarEvent;
    }

    @Transactional
    public void deleteCalendarEvent(Long calendarEventId) {
        CalendarEvent calendarEvent = calendarEventRepository.findById(calendarEventId)
                .orElseThrow(() -> new InvalidIdException("calendarEvent"));

        calendarEventRepository.delete(calendarEvent);
    }


}

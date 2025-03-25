package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.CalendarSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarScheduleRepository extends JpaRepository<CalendarSchedule, Long> {

    List<CalendarSchedule> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate end, LocalDate start); // startDate <= endDate AND endDate >= startDate

}

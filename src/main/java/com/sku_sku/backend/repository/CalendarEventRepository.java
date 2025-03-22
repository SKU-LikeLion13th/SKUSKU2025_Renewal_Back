package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.CalendarEvent;
import com.sku_sku.backend.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {


}

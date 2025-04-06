package com.sku_sku.backend.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

public class ReviewWeekDTO {
    @Data
    @AllArgsConstructor
    public static class showReviewWeek{
        Long ReviewWeekId;
        String title;
        String IsSubmit;
        Integer score;
        Integer total;
    }
}

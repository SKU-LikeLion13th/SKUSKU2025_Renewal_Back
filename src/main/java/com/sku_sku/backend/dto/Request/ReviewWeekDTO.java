package com.sku_sku.backend.dto.Request;

import lombok.Data;

public class ReviewWeekDTO {
    @Data
    public static class showReviewWeek{
        Long ReviewWeekId;
        String title;
        String IsSubmit;
        Integer score;
        Integer total;
    }
}

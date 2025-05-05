package com.s206.health.nutrition.foodset.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodSetRequest {

    private String name;

    private List<FoodItem> foods;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodItem {

        private Integer foodId;

        private Float quantity;
    }
}

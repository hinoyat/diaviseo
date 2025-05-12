package com.s206.user.physical.dto.response;

import com.s206.user.physical.entity.UserPhysicalInfo;
import com.s206.user.user.enums.Goal;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Builder
public class UserPhysicalInfoResponse {

    // 기록된 값
    private BigDecimal height;
    private BigDecimal weight;
    private LocalDate birthday;
    private Goal goal;
    private LocalDate date;

    // 계산된 값
    private int age;
    private int bmr;
    private int tdee;
    private int recommendedIntake;
    private int recommendedExercise;

    public static UserPhysicalInfoResponse toDto(UserPhysicalInfo info, String gender) {
        int age = Period.between(info.getBirthday(), LocalDate.now()).getYears();
        double height = info.getHeight().doubleValue();
        double weight = info.getWeight().doubleValue();

        int bmr = (int) (
                10 * weight +
                        6.25 * height -
                        5 * age +
                        ("M".equals(gender) ? 5 : -161)
        );

        int tdee = (int) Math.round(bmr * 1.375);

        int recommendedIntake;
        int recommendedExercise;

        switch (info.getGoal()) {
            case WEIGHT_LOSS -> {
                recommendedIntake = tdee - 400;
                recommendedExercise = (int) (tdee * 0.15) + 150;
            }
            case WEIGHT_GAIN -> {
                recommendedIntake = tdee + 250;
                recommendedExercise = (int) (tdee * 0.15) - 70;
            }
            default -> {
                recommendedIntake = tdee;
                recommendedExercise = (int) (tdee * 0.15);
            }
        }

        return UserPhysicalInfoResponse.builder()
                .height(info.getHeight())
                .weight(info.getWeight())
                .birthday(info.getBirthday())
                .goal(info.getGoal())
                .date(info.getDate())
                .age(age)
                .bmr(bmr)
                .tdee(tdee)
                .recommendedIntake(recommendedIntake)
                .recommendedExercise(recommendedExercise)
                .build();
    }
}

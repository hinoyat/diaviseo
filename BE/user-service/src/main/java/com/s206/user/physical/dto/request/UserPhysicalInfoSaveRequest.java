package com.s206.user.physical.dto.request;

import com.s206.user.user.enums.Goal;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class UserPhysicalInfoSaveRequest {

    @NotNull
    private BigDecimal height;

    @NotNull
    private BigDecimal weight;

    @NotNull
    private LocalDate birthday;

    @NotNull
    private Goal goal;

    @NotNull
    private LocalDate date;
}

package com.pavikumbhar.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class OperatingSystemDTO {

    private Long id;

    @NotNull(message = "Name cannot be null")
    private String name;

    private String version;

    private String kernel;

    private LocalDateTime releaseDate;

    //@NumericValue(message = "Value must be a numeric integer")
    @NotNull(message = "usages cannot be null")
    private Integer usages;

    private boolean active;

}

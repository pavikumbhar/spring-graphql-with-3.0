package com.pavikumbhar.dto;

import com.pavikumbhar.annotation.NumericValue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatingSystemDTO {


    private Long id;

    @NotNull(message = "Name cannot be null")
    private String name;

    private String version;

    private String kernel;

    private LocalDateTime releaseDate;


    @NumericValue(message = "Value must be a numeric integer")
    private Integer usages;

    private boolean active;

}

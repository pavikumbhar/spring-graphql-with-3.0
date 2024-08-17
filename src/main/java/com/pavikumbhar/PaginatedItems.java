package com.pavikumbhar;

import com.pavikumbhar.entity.OperatingSystem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedItems {
    private List<OperatingSystem> items;
    private String nextCursor;
    private int totalElements;

}

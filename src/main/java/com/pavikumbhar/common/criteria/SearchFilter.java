package com.pavikumbhar.common.criteria;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchFilter {
    private String field;
    private Object value;
    private List<Object> values;
    private QueryOperator operator;
    private LogicalOperator logicalOperator;

}

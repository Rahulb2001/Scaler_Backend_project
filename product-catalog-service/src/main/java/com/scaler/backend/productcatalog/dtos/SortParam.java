package com.scaler.backend.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortParam {
    private String field;
    private SortOrder order;
}

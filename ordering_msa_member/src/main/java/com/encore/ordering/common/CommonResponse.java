package com.encore.ordering.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class CommonResponse {
    private HttpStatus httpStatus;
    private String message;
    private Object result;
}

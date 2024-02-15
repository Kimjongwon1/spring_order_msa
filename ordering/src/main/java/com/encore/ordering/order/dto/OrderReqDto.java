package com.encore.ordering.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderReqDto {
        private Long itemId;
        private int count;

}
//예시데이터
/*
"orderReqITemDtos":{
"itemIds" : {1,2},"counts" : {2,3}
}
        {

        }
 */

package com.encore.ordering.order.controller;

import com.encore.ordering.common.CommonResponse;
import com.encore.ordering.order.domain.Ordering;
import com.encore.ordering.order.dto.OrderReqDto;
import com.encore.ordering.order.dto.OrderResDto;
import com.encore.ordering.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<CommonResponse> orderCreate(@RequestBody List<OrderReqDto> orderReqDtos){
        Ordering ordering = orderService.create(orderReqDtos);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED,"order successes",ordering.getId()),HttpStatus.CREATED);
    }
//    @DeleteMapping("/{id}/cancel")
//    public ResponseEntity<CommonResponse> orderCancle(@PathVariable Long id){
//        Ordering ordering = orderService.cancel(id);
//        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK
//                ,"cancle Success"
//                ,ordering.getId()),HttpStatus.OK);
//    }
    @GetMapping("/orders")
    public List<OrderResDto> orderResDtoList(){
        return orderService.findAll();
    }
}

package com.encore.ordering.order.service;

import com.encore.ordering.order.domain.OrderStatus;
import com.encore.ordering.order.domain.Ordering;
import com.encore.ordering.order.dto.OrderReqDto;
import com.encore.ordering.order.dto.OrderResDto;
import com.encore.ordering.order.repository.OrderRepository;
import com.encore.ordering.order.domain.OrderItem;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Ordering create(List<OrderReqDto> orderReqDtos) {
//       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//       String email = authentication.getName();
//       Member member =  memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("not found email"));
//        Ordering ordering = Ordering.builder()
//                .member(member)
//                .build();
////        ordring객체 생성될때 orderingitem객체도 함께 생성, cascading
//        for(OrderReqDto orderReqItemDto :orderReqDtos){
//            Item item = itemRepository.findById(orderReqItemDto.getItemId()).orElseThrow(()->new EntityNotFoundException("아이템이 없습니다."));
//            OrderItem orderItem = OrderItem.builder()
//                    .item(item)
//                    .quantity(orderReqItemDto.getCount())
//                    .ordering(ordering)
//                    .build();
//            ordering.getOrderItems().add(orderItem);
//            if(item.getStockQuantity() - orderReqItemDto.getCount() < 0){
//                throw new IllegalArgumentException("재고 부족");
//            }
//            orderItem.getItem().updateStockQuantity(item.getStockQuantity() - orderReqItemDto.getCount());
//        }
//
//        return orderRepository.save(ordering);
        return null;
    }
//    public Ordering cancel(Long id) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        Ordering ordering = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not found order"));
//        if (!ordering.getMember().getEmail().equals(email) && !authentication.getAuthorities().contains((new SimpleGrantedAuthority("ROLE_ADMIN")))) {
//            throw new AccessDeniedException("권한이 없습니다.");
//        }
//   if(ordering.getOrderStatus() == OrderStatus.CANCELED){
//       throw new IllegalArgumentException("이미 취소된 주문");
//   }
//        ordering.cancleOrderStatus();
//   for (OrderItem orderItem : ordering.getOrderItems()){
//       orderItem.getItem().updateStockQuantity(orderItem.getItem().getStockQuantity() + orderItem.getQuantity());
//   }
//        return ordering;
//    }

    public List<OrderResDto> findAll() {
            List<Ordering> orderings = orderRepository.findAll();
            return orderings.stream().map(OrderResDto::toDto).collect(Collectors.toList());
    }
    public List<OrderResDto> findMyOrders(Long memberId){

        List<Ordering> orderings = orderRepository.findByMemberId(memberId);
        return orderings.stream().map(o->OrderResDto.toDto(o)).collect(Collectors.toList());
    }

    public List<OrderResDto> findByMember(Long id) {
        List<Ordering> orderings = orderRepository.findByMemberId(id);;
        return orderings.stream().map(o->OrderResDto.toDto(o)).collect(Collectors.toList());
    }
}

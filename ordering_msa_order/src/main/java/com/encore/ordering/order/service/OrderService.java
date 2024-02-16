package com.encore.ordering.order.service;

import com.encore.ordering.common.CommonResponse;
import com.encore.ordering.order.dto.*;
import com.encore.ordering.order.domain.OrderItem;
import com.encore.ordering.order.domain.Ordering;
import com.encore.ordering.order.repository.OrderRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private  final RestTemplate restTemplate;
    private final String MEMBER_API = "http://member-service/";
    private final String ITEM_API = "http://item-service/";

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public Ordering create(List<OrderReqDto> orderReqDtos, String email) {
        String url = MEMBER_API + "member/findbyemail?email="+email;
        MemberDto member = restTemplate.getForObject(url, MemberDto.class);
        System.out.println(member);
        Ordering ordering = Ordering.builder()
                .memberId(Objects.requireNonNull(member).getId())
                .build();
        List<ItemQuantityUpdateDto> itemQuantityUpdateDtos = new ArrayList<>();
//        ordring객체 생성될때 orderingitem객체도 함께 생성, cascading
        for(OrderReqDto orderReqItemDto :orderReqDtos){
            OrderItem orderItem = OrderItem.builder()
                    .itemId(orderReqItemDto.getItemId())
                    .quantity(orderReqItemDto.getCount())
                    .ordering(ordering)
                    .build();
            ordering.getOrderItems().add(orderItem);
            String Itemurl = ITEM_API + "item/findById/"+orderReqItemDto.getItemId();
           ResponseEntity<ItemDto> itemDtoResponseEntity = restTemplate.getForEntity(Itemurl, ItemDto.class);
            System.out.println(member);
            if(itemDtoResponseEntity.getBody().getStockQuantity() - orderReqItemDto.getCount() < 0){
                throw new IllegalArgumentException("재고 부족");
            }
            int newQuantity = itemDtoResponseEntity.getBody().getStockQuantity() - orderReqItemDto.getCount();
            ItemQuantityUpdateDto updateDto = new ItemQuantityUpdateDto();
            updateDto.setId(orderReqItemDto.getItemId());
            updateDto.setStockQuantity(newQuantity);
            itemQuantityUpdateDtos.add(updateDto);
        }
        Ordering ordering1 = orderRepository.save(ordering);
//        orderRepository.save를 먼저 함으로써 위 코드에서 에러 발생시 item서비스 호출 하지 않으므로,
//        트랜잭션 문제 발생 x
        String ItemPatchurl = ITEM_API + "item/updatequantity";
        HttpEntity<List<ItemQuantityUpdateDto>> entity = new HttpEntity<>(itemQuantityUpdateDtos);
        ResponseEntity<CommonResponse> response = restTemplate.exchange(ItemPatchurl, HttpMethod.POST, entity, CommonResponse.class);
//      만약에 위 updateQuantity이후에 추가적인 로직이 존재할경우에 트랜잭션이슈는 여전히 발생가능함
//        해결책으로 에러 발생할 가능성이 있는 코드 전체를 try catch로 예외 처리 이후에 catch에서 updateRollbackQuantity 호출
        System.out.println(response.getBody().getMessage());
        return ordering1;
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

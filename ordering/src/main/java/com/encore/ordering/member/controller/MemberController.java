package com.encore.ordering.member.controller;

import com.encore.ordering.common.CommonResponse;
import com.encore.ordering.member.domain.Member;
import com.encore.ordering.member.dto.LoginReqDto;
import com.encore.ordering.member.dto.MemberCreateReqDto;
import com.encore.ordering.member.dto.MemberResponseDto;
import com.encore.ordering.member.repository.MemberRepository;
import com.encore.ordering.member.service.MemberService;
import com.encore.ordering.order.dto.OrderResDto;
import com.encore.ordering.order.service.OrderService;
import com.encore.ordering.securities.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    private final OrderService orderService;

    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, MemberRepository memberRepository, OrderService orderService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.orderService = orderService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/create")
    public ResponseEntity<CommonResponse> memberCreate(@Valid @RequestBody MemberCreateReqDto memberCreateReqDto){
        String email = memberCreateReqDto.getEmail();
               if(memberRepository.findByEmail(email).isPresent()){
                   throw new IllegalArgumentException("중복이메일입니다.");
               }
            Member member = memberService.create(memberCreateReqDto);
            return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED,"memberSuccesfuly created", member.getId()),HttpStatus.CREATED);
    }
    @GetMapping("/myInfo")
    public MemberResponseDto findMyInfo(){
       return memberService.findMyInfo();
    }

//    @GetMapping("/{id}/orders")
//    @GetMapping("myinfo")
    @PostMapping("/doLogin")
    public ResponseEntity<CommonResponse> memberLogin(@Valid @RequestBody LoginReqDto loginReqDto){
           Member member = memberService.login(loginReqDto);
//           토큰생성
           String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
           Map<String, Object> member_info = new HashMap<>();
           member_info.put("id",member.getId());
           member_info.put("token",jwtToken);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK,"memberSuccesfuly Login",member_info),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/members")
    public List<MemberResponseDto> members() {
        return memberService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/orders")
    public List<OrderResDto> findmemberorder(@PathVariable Long id){
        return orderService.findByMember(id);
    }

//
   // @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    @GetMapping("/myorders")
    public List<OrderResDto> myorder(){
        return orderService.findMyOrders();
    }
}

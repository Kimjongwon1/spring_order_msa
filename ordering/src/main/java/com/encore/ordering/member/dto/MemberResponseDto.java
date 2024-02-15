package com.encore.ordering.member.dto;

import com.encore.ordering.member.domain.Address;
import com.encore.ordering.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {

    private Long id;
    private String name;
    private String email;
    private String city;
    private String street;
    private String zipcode;
    private int orderCount;

    public static MemberResponseDto toMemberResponseDto(Member member){
        MemberResponseDtoBuilder memberResponseDtoBuilder = MemberResponseDto.builder();
        memberResponseDtoBuilder.name(member.getName());
        memberResponseDtoBuilder.email(member.getEmail());
        memberResponseDtoBuilder.id(member.getId());
        memberResponseDtoBuilder.orderCount(member.getOrderings().size());
        Address address = member.getAddres();
        if (address != null){
            memberResponseDtoBuilder.city(address.getCity());
            memberResponseDtoBuilder.street(address.getStreet());
            memberResponseDtoBuilder.zipcode(address.getZipcode());
        }
        return memberResponseDtoBuilder.build();
    }

}

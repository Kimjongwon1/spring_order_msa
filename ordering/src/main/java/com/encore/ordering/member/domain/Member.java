package com.encore.ordering.member.domain;
import com.encore.ordering.member.dto.MemberCreateReqDto;
import com.encore.ordering.order.domain.Ordering;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false , unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Embedded
    private Address addres;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member")
    private List<Ordering> orderings;
    @CreationTimestamp
    private LocalDateTime createdTime;

    @UpdateTimestamp
    private LocalDateTime updatedTime;


public static Member toEntity(MemberCreateReqDto memberCreateReqDto){
    Address address =new Address(memberCreateReqDto.getCity()
            ,memberCreateReqDto.getStreet()
            ,memberCreateReqDto.getZipcode());

    Member member = Member.builder()
            .name(memberCreateReqDto.getName())
            .email(memberCreateReqDto.getEmail())
            .password(memberCreateReqDto.getPassword())
            .addres(address)
            .role(Role.USER)
            .build();
    return member;
}


}

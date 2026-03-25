package org.example.springbatchstudy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    /**
     *  파트너 회사명
     */
    @Column(nullable = false, length = 100)
    private String partnerCorpName;

    /**
    * 결제 상태, 취소, 부분 취소
    * */
    @Column(nullable = false)
    private String status;

}

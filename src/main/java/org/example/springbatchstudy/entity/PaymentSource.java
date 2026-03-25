package org.example.springbatchstudy.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_source")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PaymentSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
    *  파트너 회사명
    */
    @Column(nullable = false, length = 100)
    private String partnerCorpName;

    /**
     *  파트너 사업자 번호
     */
    @Column(nullable = false, length = 100, name = "partner_business_registration_number")
    private String partnerBusinessRegistrationNumber;

    @Column(nullable = false)
    private BigDecimal originalAmount;

    @Column(nullable = false)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private BigDecimal finalAmount;

    @Column(nullable = false)
    private LocalDate paymentDate;
}

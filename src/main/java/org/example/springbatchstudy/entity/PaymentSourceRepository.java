package org.example.springbatchstudy.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentSourceRepository extends JpaRepository<PaymentSource,Long> {
}

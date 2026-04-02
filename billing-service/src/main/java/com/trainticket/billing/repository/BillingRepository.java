package com.trainticket.billing.repository;

import com.trainticket.billing.entity.BillingRecord;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRepository extends JpaRepository<BillingRecord, UUID> {
}

package com.example.stream.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deposit_details")
public class DepositDetails {
    
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "dbo_id")
    private UUID dboId;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "create_date")
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(name = "update_date")
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @Column(name="status")
    private Status status;

    @Column(name = "deposit_sum")
    private BigDecimal depositSum;

    @Column(name = "repayment_account_code")
    private String repaymentAccountCode;

    @Column(name = "deposit_code")
    private UUID depositCode;

    @Column(name = "deposit_term_id")
    private UUID depositTermId;

    @Column(name = "deposit_currency")
    private String depositCurrency;

    @Column(name = "deposit_rate")
    private BigDecimal depositRate;

    @Column(name = "deposit_capitalization")
    private boolean depositCapitalization;

    @Column(name = "deposit_auto_prolongation")
    private boolean depositAutoProlongation;

    @Column(name = "min_sum")
    private BigDecimal minSum;

    @Column(name = "max_sum")
    private BigDecimal maxSum;

    @Column(name = "add_min_sum")
    private BigDecimal addMinSum;

    @Column(name = "partial_withdrawal")
    private boolean partialWithdrawal;

    @Column(name = "early_withdrawal_rate")
    private BigDecimal earlyWithdrawalRate;

    @Column(name = "deposit_end_rate")
    private LocalDateTime depositEndRate;

    @Column(name = "deposit_replenishment")
    private boolean depositReplenishment;

    @Column(name = "deposit_interest_freq")
    private int depositInterestFreq;

    @Column(name = "deposit_name")
    private String depositName;

    @Column(name = "remind_after_mm")
    private int remindAfterMM;

    @Column(name = "status_reminder")
    private int statusReminder; 
}

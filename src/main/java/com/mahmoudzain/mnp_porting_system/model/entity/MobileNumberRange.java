package com.mahmoudzain.mnp_porting_system.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mobile_number_ranges")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MobileNumberRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prefix")
    private String prefix;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private Operator operator;
}
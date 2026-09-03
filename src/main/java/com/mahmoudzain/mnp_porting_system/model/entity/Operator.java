package com.mahmoudzain.mnp_porting_system.model.entity;

import com.mahmoudzain.mnp_porting_system.model.enums.Organization;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "operators")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization")
    private Organization organization;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

}

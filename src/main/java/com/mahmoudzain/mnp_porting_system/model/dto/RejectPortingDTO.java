package com.mahmoudzain.mnp_porting_system.model.dto;

import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RejectPortingDTO {
    @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
    private String reason;
}
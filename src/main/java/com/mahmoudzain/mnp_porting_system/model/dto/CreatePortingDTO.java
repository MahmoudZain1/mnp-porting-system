package com.mahmoudzain.mnp_porting_system.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePortingDTO {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^01[012][0-9]{8}$", message = "Phone number must be a valid Egyptian mobile number")
    private String phoneNumber;
}

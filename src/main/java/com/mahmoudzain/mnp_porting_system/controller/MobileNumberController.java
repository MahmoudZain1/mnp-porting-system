package com.mahmoudzain.mnp_porting_system.controller;

import com.mahmoudzain.mnp_porting_system.model.enums.Organization;
import com.mahmoudzain.mnp_porting_system.model.vto.MobileNumberStatusVTO;
import com.mahmoudzain.mnp_porting_system.service.api.MobileNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile-numbers")
public class MobileNumberController {

    private final MobileNumberService mobileNumberService;


    @GetMapping("/{phoneNumber}/status")
    public ResponseEntity<MobileNumberStatusVTO> getMobileNumberStatus(@PathVariable("phoneNumber") String phoneNumber,
            @RequestHeader("organization") String organization) {

        MobileNumberStatusVTO response = mobileNumberService.getMobileNumberStatus(phoneNumber,
                Organization.valueOf(organization.toUpperCase()));
        return ResponseEntity.ok(response);
    }

}

package com.mahmoudzain.mnp_porting_system.controller;

import com.mahmoudzain.mnp_porting_system.model.dto.CreatePortingDTO;
import com.mahmoudzain.mnp_porting_system.model.dto.RejectPortingDTO;
import com.mahmoudzain.mnp_porting_system.model.enums.Organization;
import com.mahmoudzain.mnp_porting_system.model.vto.PortingRequestVTO;
import com.mahmoudzain.mnp_porting_system.service.api.PortingRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/porting-requests")
public class PortingRequestController {

    private final PortingRequestService portingRequestService;

    @PostMapping
    public ResponseEntity<PortingRequestVTO> createPortingRequest(
            @Valid @RequestBody CreatePortingDTO request , @RequestHeader("organization")String organization){
        PortingRequestVTO response = portingRequestService.createPortingRequest(request , Organization.valueOf(organization.toUpperCase()));
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<PortingRequestVTO>> getPortingRequests(@RequestHeader("organization") String organization){
        List<PortingRequestVTO> response = portingRequestService.getPortingRequests(
                Organization.valueOf(organization.toUpperCase())
        );
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<PortingRequestVTO> getPortingRequestById(@PathVariable("requestId") Long requestId,
            @RequestHeader("organization") String organization) {
        PortingRequestVTO response = portingRequestService.getPortingRequestById(
                requestId, Organization.valueOf(organization.toUpperCase())
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<PortingRequestVTO> acceptPortingRequest(@PathVariable("requestId") Long requestId,
            @RequestHeader("organization") String organization) {
        PortingRequestVTO response = portingRequestService.acceptPortingRequest(
                requestId, Organization.valueOf(organization.toUpperCase())
        );
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{requestId}/reject")
    public ResponseEntity<PortingRequestVTO> rejectPortingRequest(@PathVariable("requestId") Long requestId,
            @Valid @RequestBody(required = false) RejectPortingDTO rejectPortingDTO, @RequestHeader("organization") String organization) {


        PortingRequestVTO response = portingRequestService.rejectPortingRequest(
                requestId, rejectPortingDTO, Organization.valueOf(organization.toUpperCase()));

        return ResponseEntity.ok(response);

    }

}

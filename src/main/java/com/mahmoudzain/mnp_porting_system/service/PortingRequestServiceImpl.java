package com.mahmoudzain.mnp_porting_system.service;

import com.mahmoudzain.mnp_porting_system.exception.BusinessException;
import com.mahmoudzain.mnp_porting_system.exception.MnpErrors;
import com.mahmoudzain.mnp_porting_system.model.dto.CreatePortingDTO;
import com.mahmoudzain.mnp_porting_system.model.dto.RejectPortingDTO;
import com.mahmoudzain.mnp_porting_system.model.entity.MobileNumberRange;
import com.mahmoudzain.mnp_porting_system.model.entity.Operator;
import com.mahmoudzain.mnp_porting_system.model.entity.PortingRequest;
import com.mahmoudzain.mnp_porting_system.model.enums.Organization;
import com.mahmoudzain.mnp_porting_system.model.enums.PortingRequestStatus;
import com.mahmoudzain.mnp_porting_system.model.vto.MobileNumberStatusVTO;
import com.mahmoudzain.mnp_porting_system.model.vto.PortingRequestVTO;
import com.mahmoudzain.mnp_porting_system.repository.MobileNumberRangeRepository;
import com.mahmoudzain.mnp_porting_system.repository.OperatorRepository;
import com.mahmoudzain.mnp_porting_system.repository.PortingRequestRepository;
import com.mahmoudzain.mnp_porting_system.service.api.PortingRequestService;
import com.mahmoudzain.mnp_porting_system.service.mapper.PortingRequestMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PortingRequestServiceImpl implements PortingRequestService {

    private static final Pattern EGYPTIAN_MOBILE_NUMBER_PATTERN =
            Pattern.compile("^01[012][0-9]{8}$");
    private final OperatorRepository operatorRepository;
    private final MobileNumberRangeRepository mobileNumberRangeRepository;
    private final PortingRequestRepository portingRequestRepository;
    private final PortingRequestMapper portingRequestMapper;


    @Override
    @Transactional
    public PortingRequestVTO createPortingRequest(CreatePortingDTO request, Organization organization) {
       String phoneNumber = validatePhoneNumber(request.getPhoneNumber());
       if(organization == null) {
           throw new BusinessException(MnpErrors.INVALID_ORGANIZATION);
       }

       String prefix = phoneNumber.substring(0, 3);
       MobileNumberRange numberRange = mobileNumberRangeRepository.findByPrefix(prefix)
                .orElseThrow(() -> new BusinessException(
                        MnpErrors.PHONE_NUMBER_RANGE_NOT_FOUND, phoneNumber));


        Operator donor = numberRange.getOperator();

        Operator recipient = operatorRepository.findByOrganization(organization)
                .orElseThrow(() -> new BusinessException(
                        MnpErrors.INVALID_ORGANIZATION));


        if (donor.getOrganization() == recipient.getOrganization()) {
            throw new BusinessException(MnpErrors.SAME_DONOR_AND_RECIPIENT);
        }

        boolean pendingRequestExists = portingRequestRepository.existsByPhoneNumberAndStatus(phoneNumber, PortingRequestStatus.PENDING);
        if(pendingRequestExists){
            throw new BusinessException(MnpErrors.PENDING_REQUEST_EXISTS, phoneNumber);
        }

        LocalDateTime now = LocalDateTime.now();

        PortingRequest portingRequest = portingRequestMapper.toEntity(request);
        portingRequest.setDonor(donor);
        portingRequest.setRecipient(recipient);
        portingRequest.setStatus(PortingRequestStatus.PENDING);
        portingRequest.setCreatedAt(now);
        portingRequest.setUpdatedAt(now);

        PortingRequest savedRequest = portingRequestRepository.save(portingRequest);

        return portingRequestMapper.toVTO(savedRequest);
    }

    @Override
    public List<PortingRequestVTO> getPortingRequests(Organization organization) {
        Operator operator = operatorRepository.findByOrganization(organization)
                .orElseThrow(() -> new BusinessException(MnpErrors.INVALID_ORGANIZATION));
        List<PortingRequest> requests = portingRequestRepository.findVisibleRequests(operator, PortingRequestStatus.ACCEPTED);
        return portingRequestMapper.toVTOList(requests);
    }

    @Override
    public PortingRequestVTO getPortingRequestById(Long requestId, Organization organization) {
        PortingRequest request = portingRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(MnpErrors.PORTING_REQUEST_NOT_FOUND, requestId));

        Operator operator = operatorRepository.findByOrganization(organization)
                .orElseThrow(() -> new BusinessException(MnpErrors.INVALID_ORGANIZATION));

        boolean isDonor = request.getDonor().getOrganization() == organization;
        boolean isRecipient = request.getRecipient().getOrganization() == organization;
        boolean isAccepted = request.getStatus() == PortingRequestStatus.ACCEPTED;
        if (!isDonor && !isRecipient && !isAccepted) {
            throw new BusinessException(MnpErrors.FORBIDDEN_REQUEST_ACCESS);
        }
        return portingRequestMapper.toVTO(request);
    }

    @Override
    @Transactional
    public PortingRequestVTO acceptPortingRequest(Long requestId, Organization organization) {
       PortingRequest request = portingRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(MnpErrors.PORTING_REQUEST_NOT_FOUND, requestId));

        Operator operator = operatorRepository.findByOrganization(organization)
                .orElseThrow(() -> new BusinessException(MnpErrors.INVALID_ORGANIZATION));

        if (request.getDonor().getOrganization() != organization) {
            throw new BusinessException(MnpErrors.ONLY_DONOR_CAN_RESPOND);
        }

        if (request.getStatus() != PortingRequestStatus.PENDING) {
            throw new BusinessException(MnpErrors.INVALID_REQUEST_STATUS, request.getStatus());
        }

        request.setStatus(PortingRequestStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());

        PortingRequest savedRequest = portingRequestRepository.save(request);
        return portingRequestMapper.toVTO(savedRequest);
    }

    @Override
    @Transactional
    public PortingRequestVTO rejectPortingRequest(Long requestId, RejectPortingDTO rejectDto, Organization organization) {

        PortingRequest request = portingRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(MnpErrors.PORTING_REQUEST_NOT_FOUND, requestId));

        Operator operator = operatorRepository.findByOrganization(organization)
                .orElseThrow(() -> new BusinessException(MnpErrors.INVALID_ORGANIZATION));

        if (request.getDonor().getOrganization() != organization) {
            throw new BusinessException(MnpErrors.ONLY_DONOR_CAN_RESPOND);
        }

        if (request.getStatus() != PortingRequestStatus.PENDING) {
            throw new BusinessException(MnpErrors.INVALID_REQUEST_STATUS, request.getStatus());
        }

        request.setStatus(PortingRequestStatus.REJECTED);
        if (rejectDto != null && rejectDto.getReason() != null) {
            request.setRejectionReason(rejectDto.getReason());
        }
        request.setUpdatedAt(LocalDateTime.now());

        PortingRequest savedRequest = portingRequestRepository.save(request);
        return portingRequestMapper.toVTO(savedRequest);
    }


    @Override
    @Transactional
    public int cancelExpiredRequests() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(2);

        List<PortingRequest> expiredRequests = portingRequestRepository
                .findByStatusAndCreatedAtBefore(PortingRequestStatus.PENDING, cutoffTime);

        if (expiredRequests.isEmpty()) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        for (PortingRequest request : expiredRequests) {
            request.setStatus(PortingRequestStatus.CANCELLED);
            request.setUpdatedAt(now);
        }

        portingRequestRepository.saveAll(expiredRequests);
        return expiredRequests.size();

    }


    private String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !EGYPTIAN_MOBILE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new BusinessException(MnpErrors.INVALID_PHONE_NUMBER);
        }
        return phoneNumber;
    }
}
package com.mahmoudzain.mnp_porting_system.service;

import com.mahmoudzain.mnp_porting_system.exception.BusinessException;
import com.mahmoudzain.mnp_porting_system.exception.MnpErrors;
import com.mahmoudzain.mnp_porting_system.model.entity.MobileNumberRange;
import com.mahmoudzain.mnp_porting_system.model.entity.Operator;
import com.mahmoudzain.mnp_porting_system.model.entity.PortingRequest;
import com.mahmoudzain.mnp_porting_system.model.enums.Organization;
import com.mahmoudzain.mnp_porting_system.model.enums.PortingRequestStatus;
import com.mahmoudzain.mnp_porting_system.model.vto.MobileNumberStatusVTO;
import com.mahmoudzain.mnp_porting_system.repository.MobileNumberRangeRepository;
import com.mahmoudzain.mnp_porting_system.repository.OperatorRepository;
import com.mahmoudzain.mnp_porting_system.repository.PortingRequestRepository;
import com.mahmoudzain.mnp_porting_system.service.api.MobileNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MobileNumberServiceImpl implements MobileNumberService {

    private static final Pattern EGYPTIAN_MOBILE_NUMBER_PATTERN =
            Pattern.compile("^01[012][0-9]{8}$");
    private final OperatorRepository operatorRepository;
    private final MobileNumberRangeRepository mobileNumberRangeRepository;
    private final PortingRequestRepository portingRequestRepository;



    @Override
    public MobileNumberStatusVTO getMobileNumberStatus(String phoneNumber, Organization organization) {
        String validatedPhoneNumber = validatePhoneNumber(phoneNumber);
        operatorRepository.findByOrganization(organization)
                .orElseThrow(() -> new BusinessException(MnpErrors.INVALID_ORGANIZATION));

        String prefix = validatedPhoneNumber.substring(0, 3);

        MobileNumberRange range = mobileNumberRangeRepository.findByPrefix(prefix)
                .orElseThrow(() -> new BusinessException(MnpErrors.PHONE_NUMBER_RANGE_NOT_FOUND, phoneNumber));
        Operator originalOperator = range.getOperator();


        Optional<PortingRequest> latestRequest = portingRequestRepository
                .findLatestRequestByPhoneNumber(validatedPhoneNumber);

        boolean isAccepted = latestRequest.isPresent()
                && latestRequest.get().getStatus() == PortingRequestStatus.ACCEPTED;

        boolean ported = isAccepted;
        String currentOperator = isAccepted
                ? latestRequest.get().getRecipient().getOrganization().getCode()
                : originalOperator.getOrganization().getCode();
        String activeRequestStatus = latestRequest.map(req -> req.getStatus().name()).orElse(null);
        Long lastPortingRequestId = latestRequest.map(PortingRequest::getId).orElse(null);

        return MobileNumberStatusVTO.builder()
                .phoneNumber(validatedPhoneNumber)
                .currentOperator(currentOperator)
                .ported(ported)
                .activeRequestStatus(activeRequestStatus)
                .lastPortingRequestId(lastPortingRequestId)
                .build();
    }


    private String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !EGYPTIAN_MOBILE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new BusinessException(MnpErrors.INVALID_PHONE_NUMBER);
        }
        return phoneNumber;
    }
}

package com.mahmoudzain.mnp_porting_system.service;

import com.mahmoudzain.mnp_porting_system.common.TestDataFactory;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mobile Number Service Unit Tests")
public class MobileNumberServiceImplTest {

    @Mock private OperatorRepository operatorRepository;
    @Mock private MobileNumberRangeRepository mobileNumberRangeRepository;
    @Mock private PortingRequestRepository portingRequestRepository;

    @InjectMocks
    private MobileNumberServiceImpl mobileNumberService;


    @Test
    void getMobileNumberStatus_shouldReturnPorted_whenAcceptedRequestExists(){
        String phoneNumber = TestDataFactory.VODAFONE_NUMBER;
        Operator originalOperator = TestDataFactory.createVodafoneOperator();
        MobileNumberRange range = TestDataFactory.createVodafoneRange();
        PortingRequest acceptedRequest = TestDataFactory.createPendingPortingRequest();
        acceptedRequest.setStatus(PortingRequestStatus.ACCEPTED);

        when(operatorRepository.findByOrganization(Organization.VODAFONE)).thenReturn(Optional.of(originalOperator));
        when(mobileNumberRangeRepository.findByPrefix("010")).thenReturn(Optional.of(range));
        when(portingRequestRepository.findLatestRequestByPhoneNumber(phoneNumber)).thenReturn(Optional.of(acceptedRequest));

        MobileNumberStatusVTO response = mobileNumberService.getMobileNumberStatus(phoneNumber, Organization.VODAFONE);


        assertThat(response).isNotNull();
        assertThat(response.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(response.isPorted()).isTrue();
        assertThat(response.getCurrentOperator()).isEqualTo("orange");

        assertThat(response.getActiveRequestStatus()).isEqualTo("ACCEPTED");

    }

    @Test
    void getMobileNumberStatus_shouldReturnOriginalOperator_whenNoAcceptedRequestExists(){

        String phoneNumber = TestDataFactory.VODAFONE_NUMBER;
        Operator originalOperator = TestDataFactory.createVodafoneOperator();
        MobileNumberRange range = TestDataFactory.createVodafoneRange();

        when(operatorRepository.findByOrganization(Organization.VODAFONE)).thenReturn(Optional.of(originalOperator));
        when(mobileNumberRangeRepository.findByPrefix("010")).thenReturn(Optional.of(range));
        when(portingRequestRepository.findLatestRequestByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        MobileNumberStatusVTO response = mobileNumberService.getMobileNumberStatus(phoneNumber, Organization.VODAFONE);

        assertThat(response).isNotNull();
        assertThat(response.isPorted()).isFalse();
        assertThat(response.getCurrentOperator()).isEqualTo("vodafone");
        assertThat(response.getActiveRequestStatus()).isNull();
    }

    @Test
    void getMobileNumberStatus_shouldThrowException_whenPhoneNumberIsInvalid(){
        assertThatThrownBy(() -> mobileNumberService.getMobileNumberStatus(TestDataFactory.INVALID_NUMBER, Organization.VODAFONE))
                .isInstanceOf(BusinessException.class)
                .extracting("error")
                .isEqualTo(MnpErrors.INVALID_PHONE_NUMBER);
        verifyNoInteractions(mobileNumberRangeRepository);
        verifyNoInteractions(portingRequestRepository);
    }


}

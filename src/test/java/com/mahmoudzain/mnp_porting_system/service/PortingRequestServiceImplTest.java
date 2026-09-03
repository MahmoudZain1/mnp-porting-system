package com.mahmoudzain.mnp_porting_system.service;

import com.mahmoudzain.mnp_porting_system.common.TestDataFactory;
import com.mahmoudzain.mnp_porting_system.exception.BusinessException;
import com.mahmoudzain.mnp_porting_system.exception.MnpErrors;
import com.mahmoudzain.mnp_porting_system.model.dto.CreatePortingDTO;
import com.mahmoudzain.mnp_porting_system.model.dto.RejectPortingDTO;
import com.mahmoudzain.mnp_porting_system.model.entity.MobileNumberRange;
import com.mahmoudzain.mnp_porting_system.model.entity.Operator;
import com.mahmoudzain.mnp_porting_system.model.entity.PortingRequest;
import com.mahmoudzain.mnp_porting_system.model.enums.Organization;
import com.mahmoudzain.mnp_porting_system.model.enums.PortingRequestStatus;
import com.mahmoudzain.mnp_porting_system.model.vto.PortingRequestVTO;
import com.mahmoudzain.mnp_porting_system.repository.MobileNumberRangeRepository;
import com.mahmoudzain.mnp_porting_system.repository.OperatorRepository;
import com.mahmoudzain.mnp_porting_system.repository.PortingRequestRepository;
import com.mahmoudzain.mnp_porting_system.service.mapper.PortingRequestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Porting Request Service Unit Tests")
public class PortingRequestServiceImplTest {

    @Mock private OperatorRepository operatorRepository;
    @Mock private MobileNumberRangeRepository mobileNumberRangeRepository;
    @Mock private PortingRequestRepository portingRequestRepository;
    @Mock private PortingRequestMapper portingRequestMapper;

    @InjectMocks private PortingRequestServiceImpl portingRequestService;

    private ArgumentCaptor<PortingRequest> portingRequestCaptor;

    @BeforeEach
    void setUp() {
        portingRequestCaptor = ArgumentCaptor.forClass(PortingRequest.class);
    }



    @Test
    void createPortingRequest_shouldSucceed_withValidData(){
        CreatePortingDTO request = TestDataFactory.createSampleCreateDTO();
        Operator donor = TestDataFactory.createVodafoneOperator();
        Operator recipient = TestDataFactory.createOrangeOperator();
        MobileNumberRange range = TestDataFactory.createVodafoneRange();
        PortingRequest savedEntity = TestDataFactory.createPendingPortingRequest();
        PortingRequestVTO expectedVTO = TestDataFactory.createSamplePortingRequestVTO();

        when(mobileNumberRangeRepository.findByPrefix("010")).thenReturn(Optional.of(range));
        when(operatorRepository.findByOrganization(Organization.ORANGE)).thenReturn(Optional.of(recipient));
        when(portingRequestRepository.existsByPhoneNumberAndStatus(request.getPhoneNumber(), PortingRequestStatus.PENDING))
                .thenReturn(false);
        when(portingRequestMapper.toEntity(request)).thenReturn(new PortingRequest());
        when(portingRequestRepository.save(any(PortingRequest.class))).thenReturn(savedEntity);
        when(portingRequestMapper.toVTO(savedEntity)).thenReturn(expectedVTO);


        PortingRequestVTO response = portingRequestService.createPortingRequest(request, Organization.ORANGE);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getStatus()).isEqualTo("PENDING");
        verify(portingRequestRepository, times(1)).save(portingRequestCaptor.capture());

    }

     @Test
    void createPortingRequest_shouldThrowException_whenPendingRequestExists(){
        CreatePortingDTO request = TestDataFactory.createSampleCreateDTO();
        Operator donor = TestDataFactory.createVodafoneOperator();
        Operator recipient = TestDataFactory.createOrangeOperator();
        MobileNumberRange range = TestDataFactory.createVodafoneRange();

         when(mobileNumberRangeRepository.findByPrefix("010")).thenReturn(Optional.of(range));
         when(operatorRepository.findByOrganization(Organization.ORANGE)).thenReturn(Optional.of(recipient));
         when(portingRequestRepository.existsByPhoneNumberAndStatus(request.getPhoneNumber(), PortingRequestStatus.PENDING))
                 .thenReturn(true);

         assertThatThrownBy(() -> portingRequestService.createPortingRequest(request, Organization.ORANGE))
                 .isInstanceOf(BusinessException.class)
                 .extracting("error")
                 .isEqualTo(MnpErrors.PENDING_REQUEST_EXISTS);

         verify(portingRequestRepository, never()).save(any(PortingRequest.class));

     }

     @Test
     void createPortingRequest_shouldThrowException_whenDonorAndRecipientAreSame(){
         CreatePortingDTO request = TestDataFactory.createSampleCreateDTO();
         Operator vodafoneDonor = TestDataFactory.createVodafoneOperator();
         Operator vodafoneRecipient = TestDataFactory.createVodafoneOperator();
         MobileNumberRange range = TestDataFactory.createVodafoneRange();

         when(mobileNumberRangeRepository.findByPrefix("010")).thenReturn(Optional.of(range));
         when(operatorRepository.findByOrganization(Organization.VODAFONE)).thenReturn(Optional.of(vodafoneRecipient));

         assertThatThrownBy(() -> portingRequestService.createPortingRequest(request, Organization.VODAFONE))
                 .isInstanceOf(BusinessException.class)
                 .extracting("error")
                 .isEqualTo(MnpErrors.SAME_DONOR_AND_RECIPIENT);

         verify(portingRequestRepository, never()).save(any(PortingRequest.class));
     }


     @Test
     void createPortingRequest_shouldThrowException_whenPhoneNumberIsInvalid(){
         CreatePortingDTO request = new CreatePortingDTO(TestDataFactory.INVALID_NUMBER);
         assertThatThrownBy(() -> portingRequestService.createPortingRequest(request, Organization.ORANGE))
                 .isInstanceOf(BusinessException.class)
                 .extracting("error")
                 .isEqualTo(MnpErrors.INVALID_PHONE_NUMBER);

         verifyNoInteractions(mobileNumberRangeRepository);
         verifyNoInteractions(operatorRepository);
         verifyNoInteractions(portingRequestRepository);
     }

     @Test
     void acceptPortingRequest_shouldSucceed_whenDonorAcceptsPendingRequest(){
        Long requestId = 100L;
        PortingRequest request =  TestDataFactory.createPendingPortingRequest();
        Operator donorOperator = TestDataFactory.createVodafoneOperator();
        PortingRequest acceptedEntity =  TestDataFactory.createPendingPortingRequest();
        acceptedEntity.setStatus(PortingRequestStatus.ACCEPTED);
        PortingRequestVTO expectedVTO = TestDataFactory.createSamplePortingRequestVTO();
        expectedVTO.setStatus("ACCEPTED");

        when(portingRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(portingRequestRepository.save(any(PortingRequest.class))).thenReturn(acceptedEntity);
        when(operatorRepository.findByOrganization(Organization.VODAFONE)).thenReturn(Optional.of(donorOperator));
        when(portingRequestMapper.toVTO(acceptedEntity)).thenReturn(expectedVTO);


        PortingRequestVTO response = portingRequestService.acceptPortingRequest(requestId, Organization.VODAFONE);

         assertThat(response).isNotNull();
         assertThat(response.getStatus()).isEqualTo("ACCEPTED");

         verify(portingRequestRepository, times(1)).save(portingRequestCaptor.capture());

         assertThat(portingRequestCaptor.getValue().getStatus()).isEqualTo(PortingRequestStatus.ACCEPTED);

     }

     @Test
     void acceptPortingRequest_shouldThrowException_whenOperatorIsNotDonor(){
         Long requestId = 100L;
         PortingRequest pendingRequest = TestDataFactory.createPendingPortingRequest();
         Operator orangeOperator = TestDataFactory.createOrangeOperator();

         when(portingRequestRepository.findById(requestId)).thenReturn(Optional.of(pendingRequest));
         when(operatorRepository.findByOrganization(Organization.ORANGE)).thenReturn(Optional.of(orangeOperator));

         assertThatThrownBy(() -> portingRequestService.acceptPortingRequest(requestId, Organization.ORANGE))
                 .isInstanceOf(BusinessException.class)
                 .extracting("error")
                 .isEqualTo(MnpErrors.ONLY_DONOR_CAN_RESPOND);
         verify(portingRequestRepository, never()).save(any(PortingRequest.class));
     }

     @Test
     void acceptPortingRequest_shouldThrowException_whenStatusIsNotPending(){
         Long requestId = 100L;
         PortingRequest alreadyAcceptedRequest = TestDataFactory.createPendingPortingRequest();
         alreadyAcceptedRequest.setStatus(PortingRequestStatus.ACCEPTED);
         Operator donorOperator = TestDataFactory.createVodafoneOperator();

         when(portingRequestRepository.findById(requestId)).thenReturn(Optional.of(alreadyAcceptedRequest));
         when(operatorRepository.findByOrganization(Organization.VODAFONE)).thenReturn(Optional.of(donorOperator));

         assertThatThrownBy(() -> portingRequestService.acceptPortingRequest(requestId, Organization.VODAFONE))
                 .isInstanceOf(BusinessException.class)
                 .extracting("error")
                 .isEqualTo(MnpErrors.INVALID_REQUEST_STATUS);
         verify(portingRequestRepository, never()).save(any(PortingRequest.class));
     }

     @Test
     void rejectPortingRequest_shouldSucceed_whenDonorRejectsWithReason(){
         Long requestId = 100L;
         RejectPortingDTO rejectDto = TestDataFactory.createSampleRejectDTO();
         PortingRequest pendingRequest = TestDataFactory.createPendingPortingRequest();

         Operator donorOperator = TestDataFactory.createVodafoneOperator();
         PortingRequest rejectedEntity = TestDataFactory.createPendingPortingRequest();
         rejectedEntity.setStatus(PortingRequestStatus.REJECTED);
         rejectedEntity.setRejectionReason(rejectDto.getReason());
         PortingRequestVTO expectedVTO = TestDataFactory.createSamplePortingRequestVTO();
         expectedVTO.setStatus("REJECTED");
         expectedVTO.setRejectionReason(rejectDto.getReason());

         when(portingRequestRepository.findById(requestId)).thenReturn(Optional.of(pendingRequest));
         when(operatorRepository.findByOrganization(Organization.VODAFONE)).thenReturn(Optional.of(donorOperator));
         when(portingRequestRepository.save(any(PortingRequest.class))).thenReturn(rejectedEntity);
         when(portingRequestMapper.toVTO(rejectedEntity)).thenReturn(expectedVTO);

         PortingRequestVTO response = portingRequestService.rejectPortingRequest(requestId, rejectDto, Organization.VODAFONE);

         assertThat(response).isNotNull();
         assertThat(response.getStatus()).isEqualTo("REJECTED");

         verify(portingRequestRepository, times(1)).save(portingRequestCaptor.capture());
         PortingRequest captured = portingRequestCaptor.getValue();
         assertThat(captured.getStatus()).isEqualTo(PortingRequestStatus.REJECTED);
         assertThat(captured.getRejectionReason()).isEqualTo(rejectDto.getReason());
     }

     @Test
     void rejectPortingRequest_shouldThrowException_whenOperatorIsNotDonor(){
         Long requestId = 100L;
         RejectPortingDTO rejectDto = TestDataFactory.createSampleRejectDTO();
         PortingRequest pendingRequest = TestDataFactory.createPendingPortingRequest();
         Operator orangeOperator = TestDataFactory.createOrangeOperator();

         when(portingRequestRepository.findById(requestId)).thenReturn(Optional.of(pendingRequest));
         when(operatorRepository.findByOrganization(Organization.ORANGE)).thenReturn(Optional.of(orangeOperator));

         assertThatThrownBy(() -> portingRequestService.rejectPortingRequest(requestId, rejectDto, Organization.ORANGE))
                 .isInstanceOf(BusinessException.class)
                 .extracting("error")
                 .isEqualTo(MnpErrors.ONLY_DONOR_CAN_RESPOND);
         verify(portingRequestRepository, never()).save(any(PortingRequest.class));
     }


     @Test
     void  getPortingRequestById_shouldReturnRequest_whenOperatorIsAuthorized(){
         Long requestId = 100L;
         PortingRequest request = TestDataFactory.createPendingPortingRequest();
         Operator donor = TestDataFactory.createVodafoneOperator();

         PortingRequestVTO expectedVTO = TestDataFactory.createSamplePortingRequestVTO();
         when(portingRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
         when(operatorRepository.findByOrganization(Organization.VODAFONE)).thenReturn(Optional.of(donor));
         when(portingRequestMapper.toVTO(request)).thenReturn(expectedVTO);

         PortingRequestVTO result = portingRequestService.getPortingRequestById(requestId, Organization.VODAFONE);
         assertThat(result).isNotNull();
         assertThat(result.getId()).isEqualTo(requestId);
     }

    @Test
    void getPortingRequestById_shouldThrowForbidden_whenThirdPartyViewsPendingRequest(){
        Long requestId = 100L;
        PortingRequest pendingRequest = TestDataFactory.createPendingPortingRequest();
        Operator etisalat = TestDataFactory.createEtisalatOperator();

        when(portingRequestRepository.findById(requestId)).thenReturn(Optional.of(pendingRequest));
        when(operatorRepository.findByOrganization(Organization.ETISALAT)).thenReturn(Optional.of(etisalat));

        assertThatThrownBy(() -> portingRequestService.getPortingRequestById(requestId, Organization.ETISALAT))
                .isInstanceOf(BusinessException.class)
                .extracting("error")
                .isEqualTo(MnpErrors.FORBIDDEN_REQUEST_ACCESS);
    }


    @Test
    void cancelExpiredRequests_shouldCancelRequests_whenExpiredRequestsExist(){
        PortingRequest expiredRequest = TestDataFactory.createPendingPortingRequest();
        List<PortingRequest> expiredList = List.of(expiredRequest);

        when(portingRequestRepository.findByStatusAndCreatedAtBefore(eq(PortingRequestStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(expiredList);

        int cancelledCount = portingRequestService.cancelExpiredRequests();
        assertThat(cancelledCount).isEqualTo(1);
        assertThat(expiredRequest.getStatus()).isEqualTo(PortingRequestStatus.CANCELLED);
        verify(portingRequestRepository, times(1)).saveAll(expiredList);
    }

}

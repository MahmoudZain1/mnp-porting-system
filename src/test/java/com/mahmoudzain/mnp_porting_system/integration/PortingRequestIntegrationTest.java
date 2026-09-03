package com.mahmoudzain.mnp_porting_system.integration;

import com.mahmoudzain.mnp_porting_system.common.TestDataFactory;
import com.mahmoudzain.mnp_porting_system.model.dto.CreatePortingDTO;
import com.mahmoudzain.mnp_porting_system.model.vto.MobileNumberStatusVTO;
import com.mahmoudzain.mnp_porting_system.model.vto.PortingRequestVTO;
import com.mahmoudzain.mnp_porting_system.repository.PortingRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Porting Request API Integration Tests")
public class PortingRequestIntegrationTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private PortingRequestRepository portingRequestRepository;

    @BeforeEach
    void setup(){
        String baseUrl = "http://localhost:" + port;
        restClient = RestClient.create(baseUrl);
        portingRequestRepository.deleteAll();
    }

    @Test
    void createPortingRequest_shouldSucceed_withVaildData(){

        CreatePortingDTO portingDTO = TestDataFactory.createSampleCreateDTO();

        ResponseEntity<PortingRequestVTO> response = restClient.post()
                .uri("/porting-requests")
                .header("organization", "orange")
                .contentType(MediaType.APPLICATION_JSON)
                .body(portingDTO)
                .retrieve()
                .toEntity(PortingRequestVTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


        PortingRequestVTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getPhoneNumber()).isEqualTo(portingDTO.getPhoneNumber());
        assertThat(body.getDonor()).isEqualTo("vodafone");
        assertThat(body.getRecipient()).isEqualTo("orange");
        assertThat(body.getStatus()).isEqualTo("PENDING");


        assertThat(portingRequestRepository.count()).isEqualTo(1);

    }

    @Test
    void createPortingRequest_shouldFail_whenDonorAndRecipientAreSame(){
        CreatePortingDTO portingDTO = TestDataFactory.createSampleCreateDTO();

        assertThatThrownBy(() -> restClient.post()
                .uri("/porting-requests")
                .header("organization", "vodafone")
                .contentType(MediaType.APPLICATION_JSON)
                .body(portingDTO)
                .retrieve()
                .toBodilessEntity())
                .isInstanceOf(RestClientResponseException.class)
                .satisfies(ex -> {
                   RestClientResponseException e = (RestClientResponseException) ex;
                   assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        });
        assertThat(portingRequestRepository.count()).isZero();


    }


    @Test
    @DisplayName("End-to-End: Create request ->" +
                 "Non-donor fails to accept ->  " +
                 "Donor accepts -> " +
                 "Verify number status is PORTED")
    void fullPortingLifecycle_shouldSucceed_endToEnd(){

        CreatePortingDTO createDTO = TestDataFactory.createSampleCreateDTO();

        PortingRequestVTO createdRequest = restClient.post()
                .uri("/porting-requests")
                .header("organization", "orange")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createDTO)
                .retrieve()
                .body(PortingRequestVTO.class);
        assertThat(createdRequest).isNotNull();
        Long requestId = createdRequest.getId();

        assertThatThrownBy(() -> restClient.post()
                .uri("/porting-requests/" + requestId + "/accept")
                .header("organization", "orange")
                .retrieve()
                .toBodilessEntity())
                .isInstanceOf(RestClientResponseException.class)
                .satisfies(ex -> {
                    RestClientResponseException e = (RestClientResponseException) ex;
                    assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                });

        ResponseEntity<PortingRequestVTO> acceptResponse = restClient.post()
                .uri("/porting-requests/" + requestId + "/accept")
                .header("organization", "vodafone")
                .retrieve()
                .toEntity(PortingRequestVTO.class);
        assertThat(acceptResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(acceptResponse.getBody()).isNotNull();
        assertThat(acceptResponse.getBody().getStatus()).isEqualTo("ACCEPTED");

        MobileNumberStatusVTO numberStatus = restClient.get()
                .uri("/mobile-numbers/" + createDTO.getPhoneNumber() + "/status")
                .header("organization", "vodafone")
                .retrieve()
                .body(MobileNumberStatusVTO.class);
        assertThat(numberStatus).isNotNull();
        assertThat(numberStatus.isPorted()).isTrue();
        assertThat(numberStatus.getCurrentOperator()).isEqualTo("orange");
        assertThat(numberStatus.getActiveRequestStatus()).isEqualTo("ACCEPTED");

    }


}

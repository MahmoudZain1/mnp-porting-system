package com.mahmoudzain.mnp_porting_system.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MnpErrors {

    INVALID_PHONE_NUMBER(4001, HttpStatus.BAD_REQUEST, "The phone number '{0}' is invalid."),
    PHONE_NUMBER_RANGE_NOT_FOUND(4002, HttpStatus.BAD_REQUEST, "No operator is configured for phone number '{0}'."),
    PENDING_REQUEST_EXISTS(4003, HttpStatus.CONFLICT, "A pending porting request already exists for phone number '{0}'."),
    INVALID_ORGANIZATION(4004, HttpStatus.UNAUTHORIZED, "The organization header is missing or invalid."),
    SAME_DONOR_AND_RECIPIENT(4005,HttpStatus.BAD_REQUEST,"The donor and recipient operators cannot be the same."),
    PORTING_REQUEST_NOT_FOUND(4006, HttpStatus.NOT_FOUND, "The porting request with id '{0}' was not found."),
    FORBIDDEN_REQUEST_ACCESS(4007, HttpStatus.FORBIDDEN, "You are not authorized to view this porting request."),
    ONLY_DONOR_CAN_RESPOND(4008, HttpStatus.FORBIDDEN, "Only the donor operator can accept or reject this request."),
    INVALID_REQUEST_STATUS(4009, HttpStatus.CONFLICT, "Cannot modify porting request with status ''{0}''. Only PENDING requests can be accepted or rejected.");
    private final int code;
    private final HttpStatus httpStatus;
    private final String messageTemplate;
}

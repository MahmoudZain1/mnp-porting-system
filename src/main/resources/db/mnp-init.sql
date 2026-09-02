
CREATE TABLE IF NOT EXISTS operators (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organization VARCHAR(20) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS mobile_number_ranges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prefix VARCHAR(3) NOT NULL,
    operator_id BIGINT NOT NULL,
    CONSTRAINT fk_mobile_number_ranges_operator
       FOREIGN KEY (operator_id) REFERENCES operators(id)
);


CREATE TABLE IF NOT EXISTS porting_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    donor_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    rejection_reason VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_porting_requests_donor
       FOREIGN KEY (donor_id) REFERENCES operators(id),
    CONSTRAINT fk_porting_requests_recipient
        FOREIGN KEY (recipient_id) REFERENCES operators(id)
);

CREATE INDEX idx_mobile_number_ranges_prefix
    ON mobile_number_ranges (prefix);

CREATE INDEX idx_porting_requests_phone_status
    ON porting_requests (phone_number, status);

CREATE INDEX idx_porting_requests_status_created_at
    ON porting_requests (status, created_at);

INSERT INTO operators (organization, display_name, active)
SELECT 'VODAFONE', 'Vodafone', TRUE
    WHERE NOT EXISTS (
    SELECT 1 FROM operators WHERE organization = 'VODAFONE'
);

INSERT INTO operators (organization, display_name, active)
SELECT 'ORANGE', 'Orange', TRUE
    WHERE NOT EXISTS (
    SELECT 1 FROM operators WHERE organization = 'ORANGE'
);

INSERT INTO operators (organization, display_name, active)
SELECT 'ETISALAT', 'Etisalat', TRUE
    WHERE NOT EXISTS (
    SELECT 1 FROM operators WHERE organization = 'ETISALAT'
);

INSERT INTO mobile_number_ranges (prefix, operator_id)
SELECT '010', id FROM operators
WHERE organization = 'VODAFONE'
  AND NOT EXISTS (
    SELECT 1 FROM mobile_number_ranges WHERE prefix = '010'
);

INSERT INTO mobile_number_ranges (prefix, operator_id)
SELECT '011', id FROM operators
WHERE organization = 'ETISALAT'
  AND NOT EXISTS (
    SELECT 1 FROM mobile_number_ranges WHERE prefix = '011'
);

INSERT INTO mobile_number_ranges (prefix, operator_id)
SELECT '012', id FROM operators
WHERE organization = 'ORANGE'
  AND NOT EXISTS (
    SELECT 1 FROM mobile_number_ranges WHERE prefix = '012'
);
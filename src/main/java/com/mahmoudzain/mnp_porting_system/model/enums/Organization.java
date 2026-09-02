package com.mahmoudzain.mnp_porting_system.model.enums;

public enum Organization {
    VODAFONE("vodafone"),
    ORANGE("orange"),
    ETISALAT("etisalat");

  private final   String code;

    Organization(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

package com.example.securify.domain;

import java.util.HashMap;

public class DomainInfo {
    private static DomainInfo DOMAIN_INFO = null;
    private HashMap<String, HashMap<String, String>> domainInfo = new HashMap<>();
    public static final String DOMAIN_NAME = "DomainName";
    public static final String REGISTRAR_DOMAIN_ID = "RegistrarDomainID";
    public static final String REGISTRAR_NAME = "RegistrarName";
    public static final String REGISTRAR_EXPIRY_DATE = "RegistrarExpiryDate";
    public static final String DOMAIN_TIMESTAMP = "DomainTimeStamp";

    public static DomainInfo getInstance()
    {
        if (DOMAIN_INFO == null) {
            DOMAIN_INFO = new DomainInfo();
        }

       return DOMAIN_INFO;
    }

    private DomainInfo() {}

    public void addDomain(String name, HashMap<String, String> info) {
        domainInfo.put(name, info);
    }

    public HashMap<String, String> getInfo(String name) {
        return domainInfo.get(name);
    }

}

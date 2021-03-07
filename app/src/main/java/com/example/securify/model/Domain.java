package com.example.securify.model;

public class Domain {

    private String domainID;

    private String proxyID;

    private String domainName;

    private enum listType {
        Whitelist,
        Blacklist,
        Safe,
        Malicious,
        Undefined
    };

    private int num_of_accesses;

    public String getDomainID() {
        return domainID;
    }

    public String getProxyID() {
        return proxyID;
    }

    public String getDomainName() {
        return domainName;
    }

    public int getNum_of_accesses() {
        return num_of_accesses;
    }
}

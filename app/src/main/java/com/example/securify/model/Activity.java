package com.example.securify.model;

import java.util.Date;

public class Activity {

    private String activityID;

    private String domainID;

    private String domainName;

    private String proxyID;

    private Date timestamp;

    private enum status {
        Whitelist,
        Blacklist,
        Safe,
        Malicious,
        Undefined
    }

    public String getActivityID() {
        return activityID;
    }

    public String getDomainID() {
        return domainID;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getProxyID() {
        return proxyID;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

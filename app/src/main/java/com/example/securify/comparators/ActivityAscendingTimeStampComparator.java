package com.example.securify.comparators;

import com.example.securify.domain.DomainInfo;

import java.util.Comparator;

public class ActivityAscendingTimeStampComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        String firstTimeStamp = DomainInfo.getInstance().getInfo(o1).get(DomainInfo.DOMAIN_TIMESTAMP);
        String secondTimeStamp = DomainInfo.getInstance().getInfo(o2).get(DomainInfo.DOMAIN_TIMESTAMP);
        return firstTimeStamp.compareTo(secondTimeStamp);
    }
}

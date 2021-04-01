package com.example.securify.comparators;

import com.example.securify.domain.DomainLists;

import java.util.Comparator;

public class ActivityDescendingListComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        boolean isFirstWhiteList = DomainLists.getInstance().whiteListContains(o1);
        boolean isSecondWhiteList = DomainLists.getInstance().whiteListContains(o2);

        return -Boolean.compare(isFirstWhiteList, isSecondWhiteList);
    }
}

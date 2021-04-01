package com.example.securify.comparators;

import com.example.securify.domain.TopDomainsInfo;
import com.example.securify.ui.volley.VolleySingleton;

import java.util.Comparator;

public class StatisticsDescendingListComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        boolean isFirstWhiteList = TopDomainsInfo.getInstance().getInfo(o1).get(VolleySingleton.listType).equals(VolleySingleton.Whitelist);
        boolean isSecondWhiteList = TopDomainsInfo.getInstance().getInfo(o2).get(VolleySingleton.listType).equals(VolleySingleton.Whitelist);

        return Boolean.compare(isSecondWhiteList, isFirstWhiteList);
    }
}

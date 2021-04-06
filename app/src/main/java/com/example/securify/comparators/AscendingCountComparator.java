package com.example.securify.comparators;

import com.example.securify.domain.TopDomainsInfo;
import com.example.securify.ui.volley.VolleySingleton;

import java.util.Comparator;

public class AscendingCountComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        int i1 = Integer.parseInt(TopDomainsInfo.getInstance().getInfo(o1).get(VolleySingleton.num_of_accesses));
        int i2 = Integer.parseInt(TopDomainsInfo.getInstance().getInfo(o2).get(VolleySingleton.num_of_accesses));
        return Integer.compare(i1, i2);
    }
}

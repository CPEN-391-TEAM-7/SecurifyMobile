package com.example.securify.comparators;

import com.example.securify.domain.TopDomainsInfo;
import com.example.securify.ui.volley.VolleySingleton;

import java.util.Comparator;

/**
 * Sorts top domains by descending number of accesses in StatisticsFragment
 */
public class DescendingCountComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        int i1 = Integer.parseInt(TopDomainsInfo.getInstance().getInfo(o1).get(VolleySingleton.num_of_accesses));
        int i2 = Integer.parseInt(TopDomainsInfo.getInstance().getInfo(o2).get(VolleySingleton.num_of_accesses));
        return Integer.compare(i2, i1);
    }
}

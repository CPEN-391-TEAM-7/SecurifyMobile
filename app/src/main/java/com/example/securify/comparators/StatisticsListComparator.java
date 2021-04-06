package com.example.securify.comparators;

import android.util.Log;

import com.example.securify.domain.DomainLists;
import com.example.securify.domain.TopDomainsInfo;
import com.example.securify.ui.volley.VolleySingleton;

import java.util.Comparator;
import java.util.HashMap;

public class StatisticsListComparator implements Comparator<String> {
    HashMap<String, Integer> priorityList;

    private static HashMap<String, Integer> prioritiesWhiteList = new HashMap<>();
    private static HashMap<String, Integer> prioritiesBlackList = new HashMap<>();
    private static HashMap<String, Integer> prioritiesUndefined = new HashMap<>();

    public final static int priorityWhiteList = 0;
    public final static int priorityBlackList = 1;
    public final static int priorityUndefined = 2;

    static {
        prioritiesWhiteList.put(VolleySingleton.Undefined, 3);
        prioritiesWhiteList.put(VolleySingleton.Blacklist, 2);
        prioritiesWhiteList.put(VolleySingleton.Whitelist, 1);

        prioritiesBlackList.put(VolleySingleton.Undefined, 3);
        prioritiesBlackList.put(VolleySingleton.Whitelist, 2);
        prioritiesBlackList.put(VolleySingleton.Blacklist, 1);

        prioritiesUndefined.put(VolleySingleton.Blacklist, 3);
        prioritiesUndefined.put(VolleySingleton.Whitelist, 2);
        prioritiesUndefined.put(VolleySingleton.Undefined, 1);
    }


    public StatisticsListComparator(int priorityList) {

        switch (priorityList) {
            case 0:
                this.priorityList = prioritiesWhiteList;
                break;
            case 1:
                this.priorityList = prioritiesBlackList;
                break;
            case 2:
                this.priorityList = prioritiesUndefined;
                break;
        }
    }

    private int getPriority(String domainName)  {
        if (TopDomainsInfo.getInstance().getInfo(domainName).get(VolleySingleton.listType).equals(VolleySingleton.Whitelist)) {
            Log.d("TEST", domainName + " = " + "whitelist");
            return priorityList.get(VolleySingleton.Whitelist);
        }

        if (TopDomainsInfo.getInstance().getInfo(domainName).get(VolleySingleton.listType).equals(VolleySingleton.Blacklist)) {
            Log.d("TEST", domainName + " = " + "blacklist");
            return priorityList.get(VolleySingleton.Blacklist);
        }

        Log.d("TEST", domainName + " = " + "undefined");
        return priorityList.get(VolleySingleton.Undefined);
    }
    @Override
    public int compare(String o1, String o2) {
        int priority1 = getPriority(o1);
        int priority2 = getPriority(o2);
        return Integer.compare(priority1, priority2);
    }
}

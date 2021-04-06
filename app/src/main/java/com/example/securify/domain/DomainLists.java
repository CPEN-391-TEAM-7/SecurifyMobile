package com.example.securify.domain;

import java.util.ArrayList;

/**
 * Singleton class that stores names of domains and their associated lists
 */
public class DomainLists {
    private ArrayList<String> blackList = new ArrayList<>();
    private ArrayList<String> whiteList = new ArrayList<>();
    private ArrayList<String> activityDomainsList = new ArrayList<>();
    private static DomainLists DOMAIN_LISTS = null;

    public static DomainLists getInstance()
    {
        if(DOMAIN_LISTS == null) {
            DOMAIN_LISTS = new DomainLists();
        }

        return DOMAIN_LISTS;
    }
    private DomainLists() {}

    public ArrayList<String> getBlackList() {return blackList;}
    public ArrayList<String> getWhiteList() {return whiteList;}
    public ArrayList<String> getActivityDomainsList() {return activityDomainsList;}

    public void setBlackList(ArrayList<String> blackList) {
        this.blackList = blackList;
    }
    public void setWhiteList(ArrayList<String> whiteList) {
        this.whiteList = whiteList;
    }

    public void addToBlackList(String domain) {
        this.blackList.add(domain);
    }
    public void addToWhiteList(String domain) {
        this.whiteList.add(domain);
    }

    public void removeFromBlackList(String domain) {
        this.blackList.remove(domain);
    }
    public void removeFromWhiteList(String domain) {
        this.whiteList.remove(domain);
    }

    public boolean blackListContains(String domain) {return this.blackList.contains(domain);}
    public boolean whiteListContains(String domain) {return this.whiteList.contains(domain);}

}

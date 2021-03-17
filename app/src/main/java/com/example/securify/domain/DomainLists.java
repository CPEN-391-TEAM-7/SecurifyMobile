package com.example.securify.domain;

import java.util.ArrayList;

public class DomainLists {
    private ArrayList<String> blackList = new ArrayList<>();
    private ArrayList<String> whiteList = new ArrayList<>();
    private ArrayList<String> allDomainsList = new ArrayList<>();
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
    public ArrayList<String> getAllDomainsList() {return allDomainsList;}

    public void setBlackList(ArrayList<String> blackList) {
        allDomainsList.removeAll(this.blackList);
        this.blackList = blackList;
        this.allDomainsList.addAll(blackList);
    }
    public void setWhiteList(ArrayList<String> whiteList) {
        allDomainsList.removeAll(this.whiteList);
        this.whiteList = whiteList;
        this.allDomainsList.addAll(whiteList);
    }

    public void addToBlackList(String domain) {
        this.blackList.add(domain);
        this.allDomainsList.add(domain);
    }
    public void addToWhiteList(String domain) {
        this.whiteList.add(domain);
        this.allDomainsList.add(domain);
    }

    public void removeFromBlackList(String domain) {
        this.blackList.remove(domain);
        this.allDomainsList.remove(domain);
    }
    public void removeFromWhiteList(String domain) {
        this.whiteList.remove(domain);
        this.allDomainsList.remove(domain);
    }

    public boolean blackListContains(String domain) {return this.blackList.contains(domain);}
    public boolean whiteListContains(String domain) {return this.whiteList.contains(domain);}

}

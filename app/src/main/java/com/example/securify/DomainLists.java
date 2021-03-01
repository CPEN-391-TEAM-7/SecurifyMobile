package com.example.securify;

import java.util.ArrayList;

public class DomainLists {
    private ArrayList<String> blackList = null;
    private ArrayList<String> whiteList = null;
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

    public void setBlackList(ArrayList<String> blackList) {this.blackList = blackList;}
    public void setWhiteList(ArrayList<String> whiteList) {this.whiteList = whiteList;}

    public void addToBlackList(String domain) {this.blackList.add(domain);}
    public void addToWhiteList(String domain) {this.whiteList.add(domain);}

    public void removeFromBlackList(String domain) {this.blackList.remove(domain);}
    public void removeFromWhiteList(String domain) {this.whiteList.remove(domain);}

}

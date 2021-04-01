package com.example.securify.domain;

import com.example.securify.ui.volley.VolleySingleton;

import java.util.HashMap;

public class TopDomainsInfo {
    private static TopDomainsInfo TOP_DOMAINS_INFO = null;
    private HashMap<String, HashMap<String, String>> domainInfo = new HashMap<>();


    public static TopDomainsInfo getInstance()
    {
        if (TOP_DOMAINS_INFO == null) {
            TOP_DOMAINS_INFO = new TopDomainsInfo();
        }

        return TOP_DOMAINS_INFO;
    }

    private TopDomainsInfo() {}

    public void addDomain(String name, HashMap<String, String> info) {
        domainInfo.put(name, info);
    }

    public HashMap<String, String> getInfo(String name) {
        return domainInfo.get(name);
    }

    public boolean contains(String name) {return domainInfo.containsKey(name);}
}

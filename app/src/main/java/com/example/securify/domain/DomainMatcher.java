package com.example.securify.domain;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses WhoIs response for domain information
 */
public class DomainMatcher {
    private static Pattern pattern;

    private static final String WHOIS_SERVER_PATTERN = "whois:\\s(.*)";
    private static final String REGISTRAR_DOMAIN_ID_PATTERN = "Registry Domain ID:\\s*(.*)";
    private static final String REGISTRAR_NAME_PATTERN = "Registrar:\\s*(.*)";
    private static final String REGISTRAR_EXPIRY_DATE_PATTERN = "Registry Expiry Date:\\s*(.*)";

    public static final String WHOIS_SERVER = "WHOIS_SERVER";
    public static final String REGISTRAR_DOMAIN_ID = "REGISTRAR_DOMAIN_ID";
    public static final String REGISTRAR_NAME  = "WREGISTRAR_NAME";
    public static final String REGISTRAR_EXPIRY_DATE= "REGISTRAR_EXPIRY_DATE";

    public static String getMatch(String whoIsInfo, String match) {
        String result = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        switch (match) {
            case WHOIS_SERVER:
                pattern = Pattern.compile(WHOIS_SERVER_PATTERN);
                break;
            case REGISTRAR_DOMAIN_ID:
                pattern = Pattern.compile(REGISTRAR_DOMAIN_ID_PATTERN);
                break;
            case REGISTRAR_NAME:
                pattern = Pattern.compile(REGISTRAR_NAME_PATTERN);
                break;
            case REGISTRAR_EXPIRY_DATE:
                pattern = Pattern.compile(REGISTRAR_EXPIRY_DATE_PATTERN);
                break;
        }

        Matcher matcher = pattern.matcher(whoIsInfo);

        while (matcher.find()) {
            result = matcher.group(1);
        }

        return result;
    }




}

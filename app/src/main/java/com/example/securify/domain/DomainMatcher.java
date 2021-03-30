package com.example.securify.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomainMatcher {
    private static Pattern pattern;
    private static Matcher matcher;

    private static final String WHOIS_SERVER_PATTERN = "whois:\\s(.*)";
    private static final String REGISTRAR_DOMAIN_ID_PATTERN = "Registry Domain ID:\\s*(.*)";
    private static final String REGISTRAR_NAME_PATTERN = "Registrar:\\s*(.*)";
    private static final String REGISTRAR_EXPIRY_DATE_PATTERN = "Registry Expiry Date:\\s*(.*)";

    public static final String WHOIS_SERVER = "WHOIS_SERVER";
    public static final String REGISTRAR_DOMAIN_ID = "REGISTRAR_DOMAIN_ID";
    public static final String REGISTRAR_NAME  = "WREGISTRAR_NAME";
    public static final String REGISTRAR_EXPIRY_DATE= "REGISTRAR_EXPIRY_DATE";

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    public static String getMatch(String whoIsInfo, String match) {
        String result = "";

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

        matcher = pattern.matcher(whoIsInfo);

        while (matcher.find()) {
            result = matcher.group(1);
        }

        if (match.equals(REGISTRAR_EXPIRY_DATE)) {
            if (result != null) {
                try {
                    result = String.valueOf(simpleDateFormat.parse(result));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }




}

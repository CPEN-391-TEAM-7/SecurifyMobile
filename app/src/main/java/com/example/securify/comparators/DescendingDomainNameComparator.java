package com.example.securify.comparators;

import java.util.Comparator;

/**
 * Sorts domains based on descending alphabetical order
 */
public class DescendingDomainNameComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return -o1.compareTo(o2);
    }
}

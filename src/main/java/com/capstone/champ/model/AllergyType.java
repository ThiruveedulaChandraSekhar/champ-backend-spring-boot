package com.capstone.champ.model;

import java.util.Locale;

public enum AllergyType {
    DRUG,
    FOOD,
    ENVIRONMENTAL,
    SEASONAL,
    OTHER,
    NONE;

    public static AllergyType resolve(String title, AllergyType requestedType) {
        if (title != null && title.trim().toLowerCase(Locale.ROOT).equals("no known allergy")) {
            return NONE;
        }
        return requestedType == null ? OTHER : requestedType;
    }
}
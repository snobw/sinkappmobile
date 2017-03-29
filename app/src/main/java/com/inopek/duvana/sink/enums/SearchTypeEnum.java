package com.inopek.duvana.sink.enums;

public enum SearchTypeEnum {

    LOCAL("Datos locales"),
    DISTANT("Datos en base");

    private final String label;

    private SearchTypeEnum(String label) {
        this.label = label;
    }

    public static SearchTypeEnum getSinkTypeEnum(String label) {
        if (LOCAL.getLabel().equals(label)) {
            return SearchTypeEnum.LOCAL;
        } else if (DISTANT.getLabel().equals(label)) {
            return SearchTypeEnum.DISTANT;
        }
        throw new IllegalArgumentException(String.format("La valeur %s n'est pas definie", label));
    }

    public String getLabel() {
        return label;
    }

}

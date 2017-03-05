package com.inopek.duvana.sink.enums;

public enum ProfileEnum {

    BEGIN("Etapa 1"),
    END("Etapa 2");

    private final String label;

    ProfileEnum(String label) {
        this.label = label;
    }

    public static ProfileEnum getProfileEnum(String label) {
        if (BEGIN.getLabel().equals(label)) {
            return ProfileEnum.BEGIN;
        } else if (END.getLabel().equals(label)) {
            return ProfileEnum.END;
        }
        throw new IllegalArgumentException(String.format("La valeur %s n'est pas definie", label));
    }

    public String getLabel() {
        return label;
    }

}

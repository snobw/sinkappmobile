package com.inopek.duvana.sink.enums;

public enum SinkStatutEnum {

    GOOD(1L, "Bueno"),
    BAD(2L, "Malo"),
    MODERATE(3L, "Regular");

    private final Long id;
    private final String label;

    private SinkStatutEnum(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public static SinkStatutEnum getSinkStatutEnumByName(String label) {
        if (GOOD.getLabel().equals(label)) {
            return SinkStatutEnum.GOOD;
        } else if (BAD.getLabel().equals(label)) {
            return SinkStatutEnum.BAD;
        } else if (MODERATE.getLabel().equals(label)) {
            return SinkStatutEnum.MODERATE;
        }
        throw new IllegalArgumentException(String.format("La valeur %s n'est pas definie", label));
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}

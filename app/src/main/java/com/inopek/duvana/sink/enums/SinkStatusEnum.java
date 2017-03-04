package com.inopek.duvana.sink.enums;

public enum SinkStatusEnum {

    GOOD(1L, "Bueno"),
    BAD(2L, "Malo"),
    MODERATE(3L, "Regular");

    private final Long id;
    private final String label;

    private SinkStatusEnum(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public static SinkStatusEnum getSinkStatutEnumByName(String label) {
        if (GOOD.getLabel().equals(label)) {
            return SinkStatusEnum.GOOD;
        } else if (BAD.getLabel().equals(label)) {
            return SinkStatusEnum.BAD;
        } else if (MODERATE.getLabel().equals(label)) {
            return SinkStatusEnum.MODERATE;
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

package com.inopek.duvana.sink.enums;

public enum SinkDiameterEnum {

    EIGHT(1L, "8\""),
    TEN(2L, "10\""),
    TWELVE(3L, "12\"");

    private final Long id;
    private final String label;

    private SinkDiameterEnum(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public static SinkDiameterEnum getSinkDiameterEnum(String label) {
        if (EIGHT.getLabel().equals(label)) {
            return SinkDiameterEnum.EIGHT;
        } else if (TEN.getLabel().equals(label)) {
            return SinkDiameterEnum.TEN;
        } else if (TWELVE.getLabel().equals(label)) {
            return SinkDiameterEnum.TWELVE;
        }
        throw new IllegalArgumentException(String.format("La valeur %s n'est pas definie", label));
    }

    public static SinkDiameterEnum getSinkDiameterEnumById(Long id) {
        if (EIGHT.getId().equals(id)) {
            return SinkDiameterEnum.EIGHT;
        } else if (TEN.getId().equals(id)) {
            return SinkDiameterEnum.TEN;
        } else if (TWELVE.getId().equals(id)) {
            return SinkDiameterEnum.TWELVE;
        }
        throw new IllegalArgumentException(String.format("La valeur %s n'est pas definie", id));
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}

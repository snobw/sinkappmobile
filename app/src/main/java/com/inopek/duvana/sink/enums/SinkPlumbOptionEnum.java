package com.inopek.duvana.sink.enums;

public enum SinkPlumbOptionEnum {

    YES(1L, "Si"),
    NO(2L, "No");

    private final Long id;
    private final String label;

    SinkPlumbOptionEnum(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public static SinkPlumbOptionEnum getSinkPlumbEnum(String label) {
        if (YES.getLabel().equals(label)) {
            return SinkPlumbOptionEnum.YES;
        } else if (NO.getLabel().equals(label)) {
            return SinkPlumbOptionEnum.NO;
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

package com.inopek.duvana.sink.enums;

public enum SinkPlumbEnum {

    YES(1L, "Si"),
    NO(2L, "No");

    private final Long id;
    private final String label;

    private SinkPlumbEnum(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public static SinkPlumbEnum getSinkPlumbEnum(String label) {
        if (YES.getLabel().equals(label)) {
            return SinkPlumbEnum.YES;
        } else if (NO.getLabel().equals(label)) {
            return SinkPlumbEnum.NO;
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

package com.inopek.duvana.sink.enums;

public enum SinkTypeEnum {

    LATERAL(1L, "Lateral"),
    COVENTIONAL(2L, "Convencional"),
    TRANSVERSAL(2L, "Transversal");

    private final Long id;
    private final String label;

    private SinkTypeEnum(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public static SinkTypeEnum getSinkTypeEnum(String label) {
        if (LATERAL.getLabel().equals(label)) {
            return SinkTypeEnum.LATERAL;
        } else if (COVENTIONAL.getLabel().equals(label)) {
            return SinkTypeEnum.COVENTIONAL;
        } else if (TRANSVERSAL.getLabel().equals(label)) {
            return SinkTypeEnum.TRANSVERSAL;
        }
        throw new IllegalArgumentException(String.format("La valeur %s n'est pas definie", label));
    }

    public static SinkTypeEnum getSinkTypeEnumById(Long id) {
        if (LATERAL.getId().equals(id)) {
            return SinkTypeEnum.LATERAL;
        } else if (COVENTIONAL.getId().equals(id)) {
            return SinkTypeEnum.COVENTIONAL;
        } else if (TRANSVERSAL.getId().equals(id)) {
            return SinkTypeEnum.TRANSVERSAL;
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

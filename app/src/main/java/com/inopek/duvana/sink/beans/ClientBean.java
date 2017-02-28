package com.inopek.duvana.sink.beans;

import java.io.Serializable;

public class ClientBean implements Serializable {

    private Long id;
    private final String name;

    public ClientBean(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

}

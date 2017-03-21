package ru.atott.mapper.introspection;

import java.util.Collections;
import java.util.List;

public class GenericType {

    private String type;

    private List<GenericType> arguments = Collections.emptyList();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<GenericType> getArguments() {
        return arguments;
    }

    public void setArguments(List<GenericType> arguments) {
        this.arguments = arguments;
    }
}

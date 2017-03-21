package ru.atott.mapper.dump;

import java.util.Optional;

public class OptionalDump {

    private Optional<String> id;

    private Optional<String> unexistedField;

    public Optional<String> getId() {
        return id;
    }

    public void setId(Optional<String> id) {
        this.id = id;
    }

    public Optional<String> getUnexistedField() {
        return unexistedField;
    }

    public void setUnexistedField(Optional<String> unexistedField) {
        this.unexistedField = unexistedField;
    }
}

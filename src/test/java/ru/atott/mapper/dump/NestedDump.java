package ru.atott.mapper.dump;

import java.util.Optional;

public class NestedDump {

    private SimpleDump simpleDump;

    private String id;

    private Optional<SimpleDump> simpleDumpOptional;

    public SimpleDump getSimpleDump() {
        return simpleDump;
    }

    public void setSimpleDump(SimpleDump simpleDump) {
        this.simpleDump = simpleDump;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Optional<SimpleDump> getSimpleDumpOptional() {
        return simpleDumpOptional;
    }

    public void setSimpleDumpOptional(Optional<SimpleDump> simpleDumpOptional) {
        this.simpleDumpOptional = simpleDumpOptional;
    }
}

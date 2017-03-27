package ru.atott.mapper.dump;

public class SimpleDump {

    private String id = "defaultId";

    private long longField = 16;

    public long getLongField() {
        return longField;
    }

    public void setLongField(long longField) {
        this.longField = longField;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SimpleDump() { }

    public SimpleDump(String id) {
        this.id = id;
    }
}

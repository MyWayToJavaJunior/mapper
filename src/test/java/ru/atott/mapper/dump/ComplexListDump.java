package ru.atott.mapper.dump;

import java.util.List;
import java.util.Optional;

public class ComplexListDump {

    private List<SimpleDump> list;

    private Optional<List<SimpleDump>> optionalList1;

    private Optional<List<SimpleDump>> optionalList2;

    public List<SimpleDump> getList() {
        return list;
    }

    public void setList(List<SimpleDump> list) {
        this.list = list;
    }

    public ComplexListDump() { }

    public ComplexListDump(List<SimpleDump> list) {
        this.list = list;
    }

    public Optional<List<SimpleDump>> getOptionalList1() {
        return optionalList1;
    }

    public void setOptionalList1(Optional<List<SimpleDump>> optionalList1) {
        this.optionalList1 = optionalList1;
    }

    public Optional<List<SimpleDump>> getOptionalList2() {
        return optionalList2;
    }

    public void setOptionalList2(Optional<List<SimpleDump>> optionalList2) {
        this.optionalList2 = optionalList2;
    }
}

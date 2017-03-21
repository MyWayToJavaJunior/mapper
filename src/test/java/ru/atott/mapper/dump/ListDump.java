package ru.atott.mapper.dump;

import java.util.List;
import java.util.Optional;

public class ListDump {

    private List<String> list1;

    private Optional<List<String>> list2;

    public List<String> getList1() {
        return list1;
    }

    public void setList1(List<String> list1) {
        this.list1 = list1;
    }

    public Optional<List<String>> getList2() {
        return list2;
    }

    public void setList2(Optional<List<String>> list2) {
        this.list2 = list2;
    }
}

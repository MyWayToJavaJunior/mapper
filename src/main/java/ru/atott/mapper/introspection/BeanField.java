package ru.atott.mapper.introspection;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

public class BeanField {

    private String fieldName;

    private CtMethod getter;

    private CtMethod setter;

    private CtClass effectiveType;

    private boolean optional;

    private boolean list;

    private CtField field;

    public CtMethod getGetter() {
        return getter;
    }

    public void setGetter(CtMethod getter) {
        this.getter = getter;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public CtMethod getSetter() {
        return setter;
    }

    public void setSetter(CtMethod setter) {
        this.setter = setter;
    }

    public CtClass getEffectiveType() {
        return effectiveType;
    }

    public void setEffectiveType(CtClass effectiveType) {
        this.effectiveType = effectiveType;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public CtField getField() {
        return field;
    }

    public void setField(CtField field) {
        this.field = field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanField beanField = (BeanField) o;

        if (optional != beanField.optional) return false;
        if (list != beanField.list) return false;
        if (fieldName != null ? !fieldName.equals(beanField.fieldName) : beanField.fieldName != null) return false;
        if (getter != null ? !getter.equals(beanField.getter) : beanField.getter != null) return false;
        if (setter != null ? !setter.equals(beanField.setter) : beanField.setter != null) return false;
        return effectiveType != null ? effectiveType.equals(beanField.effectiveType) : beanField.effectiveType == null;
    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (getter != null ? getter.hashCode() : 0);
        result = 31 * result + (setter != null ? setter.hashCode() : 0);
        result = 31 * result + (effectiveType != null ? effectiveType.hashCode() : 0);
        result = 31 * result + (optional ? 1 : 0);
        result = 31 * result + (list ? 1 : 0);
        return result;
    }
}

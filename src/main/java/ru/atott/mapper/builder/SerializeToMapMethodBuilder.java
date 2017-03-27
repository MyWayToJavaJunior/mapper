package ru.atott.mapper.builder;

import javassist.*;
import ru.atott.mapper.convertion.CustomValueConverter;
import ru.atott.mapper.convertion.ValueConverter;
import ru.atott.mapper.convertion.ValueProducer;
import ru.atott.mapper.introspection.BeanField;
import ru.atott.mapper.introspection.BeanIntrospection;
import ru.atott.mapper.introspection.IntrospectionUtils;

import java.util.*;

public class SerializeToMapMethodBuilder {

    private Class tClass;

    private CtClass ctClass;

    private ClassPool classPool;

    private Set<BeanField> valueProducers = new HashSet<>();

    private ValueConverter valueConverter;

    public SerializeToMapMethodBuilder settClass(Class tClass) {
        this.tClass = tClass;
        return this;
    }

    public SerializeToMapMethodBuilder setCtClass(CtClass ctClass) {
        this.ctClass = ctClass;
        return this;
    }

    public SerializeToMapMethodBuilder setClassPool(ClassPool classPool) {
        this.classPool = classPool;
        return this;
    }

    public SerializeToMapMethodBuilder addValueProducer(BeanField beanField) {
        this.valueProducers.add(beanField);
        return this;
    }

    public SerializeToMapMethodBuilder addValueProducers(Collection<BeanField> beanFields) {
        this.valueProducers.addAll(beanFields);
        return this;
    }

    public SerializeToMapMethodBuilder setValueConverter(ValueConverter valueConverter) {
        this.valueConverter = valueConverter;
        return this;
    }

    public CtMethod build() throws CannotCompileException, NotFoundException {
        this.tClass = Objects.requireNonNull(tClass);
        this.ctClass = Objects.requireNonNull(ctClass);
        this.classPool = Objects.requireNonNull(classPool);
        this.valueConverter = Objects.requireNonNull(this.valueConverter);

        StringBuilder body = new StringBuilder();
        body.append("public java.util.Map serializeToMap(Object source, Object context) { ");
        body.append(tClass.getCanonicalName()).append(" objectSource = (").append(tClass.getCanonicalName()).append(") source;");
        body.append("java.util.Map result = new java.util.HashMap();");

        BeanIntrospection beanIntrospection = new BeanIntrospection(classPool, tClass);
        beanIntrospection.getBeanFields().forEach(beanField -> {
            String effectiveTypeName = beanField.getEffectiveType().getName();
            String getterName = beanField.getGetter().getName();
            String fieldName = beanField.getFieldName();
            boolean optional = beanField.isOptional();
            boolean list = beanField.isList();

            body.append("{");
            body.append("String key = \"").append(fieldName).append("\";");
            if (optional) {
                body.append("Object mapValue = null;");
                body.append("if (objectSource.").append(getterName).append("() != null) {");
                body.append("mapValue = objectSource.").append(getterName).append("().orElse(null);");
                body.append("}");
            } else {
                body.append("Object mapValue = objectSource.").append(getterName).append("();");
            }

            if (valueProducers.contains(beanField)) {
                String valueProducerFieldName = IntrospectionUtils.getValueProducerFieldName(beanField);
                body.append("key = ").append(valueProducerFieldName).append(".getToMapFieldName(key);");

                if (!list) {
                    body.append("if (").append(valueProducerFieldName).append(".isCustomSerialization()) {");
                    body.append("mapValue = ").append(valueProducerFieldName).append(".serializeToMapValue(mapValue, context);");
                    body.append("}");
                }
            }

            if (list) {
                body.append("if (mapValue != null) {");
                body.append("java.util.List listMapVaue = new java.util.ArrayList();");
                body.append("for (int i = 0; i < ((java.util.List) mapValue).size(); i++) {");
                body.append("Object item = ((java.util.List) mapValue).get(i);");
                if (!isUnmodifiableSourceValue(effectiveTypeName)) {
                    body.append("item = vc.convertToMap(item, ").append(effectiveTypeName).append(".class);");
                }
                body.append("listMapVaue.add(item);");
                body.append("}");
                body.append("result.put(key, listMapVaue);");
                body.append("}");
            } else {
                if (isUnmodifiableSourceValue(effectiveTypeName)) {
                    body.append("result.put(key, mapValue);");
                } else {
                    body.append("result.put(key, vc.convertToMap(mapValue, ").append(effectiveTypeName).append(".class));");
                }
            }

            body.append("}");
        });

        body.append("return result;");
        body.append("}");

        return CtNewMethod.make(body.toString(), this.ctClass);
    }

    private String wrapOptional(String source, boolean optional) {
        if (!optional) {
            return source;
        } else {
            return "java.util.Optional.ofNullable(" + source + ")";
        }
    }

    private boolean isUnmodifiableSourceValue(String effectiveTypeName) {
        if (effectiveTypeName.equals("java.lang.String")) {
            return true;
        }

        if (effectiveTypeName.equals("java.lang.Byte")) {
            return true;
        }

        if (effectiveTypeName.equals("java.lang.Short")) {
            return true;
        }

        if (effectiveTypeName.equals("java.lang.Integer")) {
            return true;
        }

        if (effectiveTypeName.equals("java.lang.Long")) {
            return true;
        }

        if (effectiveTypeName.equals("java.lang.Float")) {
            return true;
        }

        if (effectiveTypeName.equals("java.lang.Double")) {
            return true;
        }

        if (effectiveTypeName.equals("java.lang.Character")) {
            return true;
        }

        if (effectiveTypeName.equals("java.lang.Boolean")) {
            return true;
        }

        if (effectiveTypeName.equals("byte")) {
            return true;
        }

        if (effectiveTypeName.equals("short")) {
            return true;
        }

        if (effectiveTypeName.equals("int")) {
            return true;
        }

        if (effectiveTypeName.equals("long")) {
            return true;
        }

        if (effectiveTypeName.equals("float")) {
            return true;
        }

        if (effectiveTypeName.equals("double")) {
            return true;
        }

        if (effectiveTypeName.equals("boolean")) {
            return true;
        }

        if (effectiveTypeName.equals("char")) {
            return true;
        }

        if (effectiveTypeName.equals("java.util.Map")) {
            return true;
        }

        if (effectiveTypeName.equals("java.util.List")) {
            return true;
        }

        /*try {
            Class effectiveClass = Class.forName(effectiveTypeName);
            CustomValueConverter customValueConverter = this.valueConverter.getCustomValueConverter(effectiveClass);
            if (customValueConverter != null) {
                return "(" + effectiveTypeName + ") vc.getCustomValueConverter(" + effectiveTypeName + ".class).convertToObject(" + source + ", context)";
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }*/


        // Смапать сложный объект.
        return false;
    }
}

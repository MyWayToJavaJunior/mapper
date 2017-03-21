package ru.atott.mapper.builder;

import javassist.*;
import ru.atott.mapper.convertion.CustomValueConverter;
import ru.atott.mapper.convertion.ValueConverter;
import ru.atott.mapper.introspection.BeanField;
import ru.atott.mapper.introspection.BeanIntrospection;
import ru.atott.mapper.introspection.IntrospectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SerializeToObjectMethodBuilder {

    private Class tClass;

    private CtClass ctClass;

    private ClassPool classPool;

    private Set<BeanField> valueProducers = new HashSet<>();

    private ValueConverter valueConverter;

    public SerializeToObjectMethodBuilder settClass(Class tClass) {
        this.tClass = tClass;
        return this;
    }

    public SerializeToObjectMethodBuilder setCtClass(CtClass ctClass) {
        this.ctClass = ctClass;
        return this;
    }

    public SerializeToObjectMethodBuilder setClassPool(ClassPool classPool) {
        this.classPool = classPool;
        return this;
    }

    public SerializeToObjectMethodBuilder addValueProducer(BeanField beanField) {
        this.valueProducers.add(beanField);
        return this;
    }

    public SerializeToObjectMethodBuilder addValueProducers(Collection<BeanField> beanFields) {
        this.valueProducers.addAll(beanFields);
        return this;
    }

    public SerializeToObjectMethodBuilder setValueConverter(ValueConverter valueConverter) {
        this.valueConverter = valueConverter;
        return this;
    }

    public CtMethod build() throws CannotCompileException, NotFoundException {
        this.tClass = Objects.requireNonNull(tClass);
        this.ctClass = Objects.requireNonNull(ctClass);
        this.classPool = Objects.requireNonNull(classPool);
        this.valueConverter = Objects.requireNonNull(this.valueConverter);

        StringBuilder body = new StringBuilder();
        body.append("public Object serializeToObject(java.util.Map source, Object context) { ");
        body.append("source = java.util.Objects.requireNonNull(source);");
        body.append(tClass.getCanonicalName()).append(" result = new ").append(tClass.getCanonicalName()).append("();");

        BeanIntrospection beanIntrospection = new BeanIntrospection(classPool, tClass);
        beanIntrospection.getBeanFields().forEach(beanField -> {
            String effectiveTypeName = beanField.getEffectiveType().getName();
            String setterName = beanField.getSetter().getName();
            String fieldName = beanField.getFieldName();
            boolean optional = beanField.isOptional();
            boolean list = beanField.isList();

            if (list) {
                body.append("{");
                body.append("java.util.List sourceValue = (java.util.List) source.get(\"").append(fieldName).append("\");");
                if (valueProducers.contains(beanField)) {
                    String valueProducerFieldName = IntrospectionUtils.getValueProducerFieldName(beanField);
                    body.append("sourceValue = ").append(valueProducerFieldName).append(".prepareToObjectSourceValue(sourceValue, context);");
                }
                body.append("java.util.List objectValue = null;");

                if (valueProducers.contains(beanField)) {
                    String valueProducerFieldName = IntrospectionUtils.getValueProducerFieldName(beanField);
                    body.append("if (").append(valueProducerFieldName).append(".isCustomSerializationToObject()) {");
                    body.append("objectValue = (java.util.List)").append(valueProducerFieldName).append(".serializeToObject(sourceValue, context);");
                    body.append("} else {");
                    body.append("objectValue = new java.util.LinkedList();");
                    body.append("if (sourceValue == null) { sourceValue = java.util.Collections.emptyList(); }");
                    body.append("for (int i = 0; i < sourceValue.size(); i++) {");
                    body.append("objectValue.add(").append(convertEffectiveValue("sourceValue.get(i)", effectiveTypeName)).append(");");
                    body.append("}");
                    body.append("}");
                } else {
                    body.append("objectValue = new java.util.LinkedList();");
                    body.append("if (sourceValue == null) { sourceValue = java.util.Collections.emptyList(); }");
                    body.append("for (int i = 0; i < sourceValue.size(); i++) {");
                    body.append("objectValue.add(").append(convertEffectiveValue("sourceValue.get(i)", effectiveTypeName)).append(");");
                    body.append("}");
                }
                body.append("result.").append(setterName).append("(").append(wrapOptional("objectValue", optional)).append(");");
                body.append("}");
                return;
            }

            body.append("{");
            body.append("Object sourceValue = source.get(\"").append(fieldName).append("\");");
            body.append(effectiveTypeName).append(" objectValue;");
            if (valueProducers.contains(beanField)) {
                String valueProducerFieldName = IntrospectionUtils.getValueProducerFieldName(beanField);
                body.append("sourceValue = ").append(valueProducerFieldName).append(".prepareToObjectSourceValue(sourceValue, context);");
                body.append("if (").append(valueProducerFieldName).append(".isCustomSerializationToObject()) {");
                body.append("objectValue = (").append(effectiveTypeName).append(")").append(valueProducerFieldName).append(".serializeToObject(sourceValue, context);");
                body.append("} else {");
                body.append("objectValue = ").append(convertEffectiveValue("sourceValue", effectiveTypeName)).append(";");
                body.append("}");
            } else {
                body.append("objectValue = ").append(convertEffectiveValue("sourceValue", effectiveTypeName)).append(";");
            }
            body.append("result.").append(setterName).append("(").append(wrapOptional("objectValue", optional)).append(");");
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

    private String convertEffectiveValue(String source, String effectiveTypeName) {
        if (effectiveTypeName.equals("java.lang.String")) {
            return "(java.lang.String) vc.convertToString(" + source + ")";
        }

        if (effectiveTypeName.equals("java.lang.Byte")) {
            return "(java.lang.Byte) vc.convertToByteObject(" + source + ")";
        }

        if (effectiveTypeName.equals("java.lang.Short")) {
            return "(java.lang.Short) vc.convertToShortObject(" + source + ")";
        }

        if (effectiveTypeName.equals("java.lang.Integer")) {
            return "(java.lang.Integer) vc.convertToInteger(" + source + ")";
        }

        if (effectiveTypeName.equals("java.lang.Long")) {
            return "(java.lang.Long) vc.convertToLongObject(" + source + ")";
        }

        if (effectiveTypeName.equals("java.lang.Float")) {
            return "(java.lang.Float) vc.convertToFloatObject(" + source + ")";
        }

        if (effectiveTypeName.equals("java.lang.Double")) {
            return "(java.lang.Double) vc.convertToDoubleObject(" + source + ")";
        }

        if (effectiveTypeName.equals("java.lang.Character")) {
            return "(java.lang.Character) vc.convertToCharacter(" + source + ")";
        }

        if (effectiveTypeName.equals("java.lang.Boolean")) {
            return "(java.lang.Boolean) vc.convertToBooleanObject(" + source + ")";
        }

        if (effectiveTypeName.equals("byte")) {
            return "vc.convertToByte(" + source + ")";
        }

        if (effectiveTypeName.equals("short")) {
            return "vc.convertToShort(" + source + ")";
        }

        if (effectiveTypeName.equals("int")) {
            return "vc.convertToInt(" + source + ")";
        }

        if (effectiveTypeName.equals("long")) {
            return "vc.convertToLong(" + source + ")";
        }

        if (effectiveTypeName.equals("float")) {
            return "vc.convertToFloat(" + source + ")";
        }

        if (effectiveTypeName.equals("double")) {
            return "vc.convertToDouble(" + source + ")";
        }

        if (effectiveTypeName.equals("boolean")) {
            return "vc.convertToBoolean(" + source + ")";
        }

        if (effectiveTypeName.equals("char")) {
            return "vc.convertToChar(" + source + ")";
        }

        if (effectiveTypeName.equals("java.util.Map")) {
            return "(" + effectiveTypeName + ") vc.convertToMap(" + source + ")";
        }

        try {
            Class effectiveClass = Class.forName(effectiveTypeName);
            CustomValueConverter customValueConverter = this.valueConverter.getCustomValueConverter(effectiveClass);
            if (customValueConverter != null) {
                return "(" + effectiveTypeName + ") vc.getCustomValueConverter(" + effectiveTypeName + ".class).convertToObject(" + source + ", context)";
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        // Смапать сложный объект.
        return "(" + effectiveTypeName + ") vc.convertToObject(" + source + ", " + effectiveTypeName + ".class)";
    }
}

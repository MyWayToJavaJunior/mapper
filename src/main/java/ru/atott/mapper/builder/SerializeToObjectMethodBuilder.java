package ru.atott.mapper.builder;

import javassist.*;
import ru.atott.mapper.introspection.BeanField;
import ru.atott.mapper.introspection.BeanIntrospection;
import ru.atott.mapper.introspection.IntrospectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class SerializeToObjectMethodBuilder {

    private Class tClass;

    private CtClass ctClass;

    private ClassPool classPool;

    private Set<BeanField> valueProducers = new HashSet<>();

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

    public CtMethod build() throws CannotCompileException, NotFoundException {
        this.tClass = Objects.requireNonNull(tClass);
        this.ctClass = Objects.requireNonNull(ctClass);
        this.classPool = Objects.requireNonNull(classPool);

        StringBuilder body = new StringBuilder();
        body.append("public Object serializeToObject(java.util.Map source) { ");
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
                    body.append("sourceValue = ").append(valueProducerFieldName).append(".prepareToObjectSourceValue(sourceValue);");
                }
                body.append("java.util.List objectValue = new java.util.LinkedList();");
                body.append("if (sourceValue == null) { sourceValue = java.util.Collections.emptyList(); }");
                body.append("for (int i = 0; i < sourceValue.size(); i++) {");
                body.append("objectValue.add(").append(convertEffectiveValue("sourceValue.get(i)", effectiveTypeName, false)).append(");");
                body.append("}");
                if (!optional) {
                    body.append("result.").append(setterName).append("(objectValue);");
                } else {
                    body.append("result.").append(setterName).append("(java.util.Optional.ofNullable(objectValue));");
                }
                body.append("}");
                return;
            }

            body.append("{");
            body.append("Object sourceValue = source.get(\"").append(fieldName).append("\");");
            if (valueProducers.contains(beanField)) {
                String valueProducerFieldName = IntrospectionUtils.getValueProducerFieldName(beanField);
                body.append("sourceValue = ").append(valueProducerFieldName).append(".prepareToObjectSourceValue(sourceValue);");
                body.append("result.").append(setterName).append("(").append(convertEffectiveValue("sourceValue", effectiveTypeName, optional)).append(");");
            } else {
                body.append("result.").append(setterName).append("(").append(convertEffectiveValue("sourceValue", effectiveTypeName, optional)).append(");");
            }
            body.append("}");
        });

        body.append("return result;");
        body.append("}");

        return CtNewMethod.make(body.toString(), this.ctClass);
    }

    private String convertEffectiveValue(String source, String effectiveTypeName, boolean optional) {
        if (effectiveTypeName.equals("java.lang.String")) {
            if (!optional) {
                return "vc.convertToString(" + source + ")";
            } else {
                return "java.util.Optional.ofNullable(vc.convertToString(" + source + "))";
            }
        }

        if (effectiveTypeName.equals("java.lang.Byte")) {
            if (!optional) {
                return "vc.convertToByteObject(" + source + ")";
            } else {
                return "java.util.Optional.ofNullable(vc.convertToByteObject(" + source + "))";
            }
        }

        if (effectiveTypeName.equals("java.lang.Short")) {
            if (!optional) {
                return "vc.convertToShortObject(" + source + ")";
            } else {
                return "java.util.Optional.ofNullable(vc.convertToShortObject(" + source + "))";
            }
        }

        if (effectiveTypeName.equals("java.lang.Integer")) {
            if (!optional) {
                return "vc.convertToInteger(" + source + ")";
            } else {
                return "java.util.Optional.ofNullable(vc.convertToInteger(" + source + "))";
            }
        }

        if (effectiveTypeName.equals("java.lang.Long")) {
            if (!optional) {
                return "vc.convertToLongObject(" + source + ")";
            } else {
                return "java.util.Optional.ofNullable(vc.convertToLongObject(" + source + "))";
            }
        }

        if (effectiveTypeName.equals("java.lang.Float")) {
            if (!optional) {
                return "vc.convertToFloatObject(" + source + ")";
            } else {
                return "java.util.Optional.ofNullable(vc.convertToFloatObject(" + source + "))";
            }
        }

        if (effectiveTypeName.equals("java.lang.Double")) {
            if (!optional) {
                return "vc.convertToDoubleObject(" + source + ")";
            } else {
                return "java.util.Optional.ofNullable(vc.convertToDoubleObject(" + source + "))";
            }
        }

        if (effectiveTypeName.equals("java.lang.Character")) {
            if (!optional) {
                return "vc.convertToCharacter(" + source + ")";
            } else {
                return "java.util.Optional.ofNullable(vc.convertToCharacter(" + source + "))";
            }
        }

        if (effectiveTypeName.equals("java.lang.Boolean")) {
            if (!optional) {
                return "vc.convertToBooleanObject(" + source + ")";
            } else {
                return "java.util.Optional.ofNullable(vc.convertToBooleanObject(" + source + "))";
            }
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

        // Смапать сложный объект.
        if (!optional) {
            return "(" + effectiveTypeName + ") vc.convertToObject(" + source + ", " + effectiveTypeName + ".class)";
        } else {
            return "java.util.Optional.ofNullable(vc.convertToObject(" + source + ", " + effectiveTypeName + ".class))";
        }
    }
}

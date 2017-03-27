package ru.atott.mapper.introspection;

import javassist.CtField;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.SignatureAttribute;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public final class IntrospectionUtils {

    private IntrospectionUtils() { }

    public static String getValueProducerFieldName(BeanField field) {
        field = Objects.requireNonNull(field);

        return field.getFieldName() + "_valueProducer";
    }

    public static String getSerializerCtClassName(Class tClass) {
        tClass = Objects.requireNonNull(tClass);

        return tClass.getSimpleName() + "_CtSerializer";
    }

    public static String getGetterName(CtField field) {
        field = Objects.requireNonNull(field);

        try {
            if (field.getType().getName().equals("boolean")) {
                return "is" + StringUtils.capitalize(field.getName());
            } else {
                return "get" + StringUtils.capitalize(field.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String getSetterName(CtField field) {
        field = Objects.requireNonNull(field);

        try {
            return "set" + StringUtils.capitalize(field.getName());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static GenericType parseGenericTypeSignature(String signature) throws BadBytecode, NoSuchFieldException, IllegalAccessException {
        SignatureAttribute.Type saSignature = SignatureAttribute.toTypeSignature(signature);

        if (saSignature instanceof SignatureAttribute.ClassType) {
            return parseTypeSignature((SignatureAttribute.ClassType) saSignature);
        }

        if (saSignature instanceof SignatureAttribute.BaseType) {
            GenericType genericType = new GenericType();
            genericType.setType(saSignature.jvmTypeName());
            return genericType;
        }

        throw new RuntimeException("Неподдерживаемый тип.");
    }

    private static GenericType parseTypeSignature(SignatureAttribute.ClassType signature) throws NoSuchFieldException, IllegalAccessException {
        GenericType result = new GenericType();

        Field nameField = SignatureAttribute.ClassType.class.getDeclaredField("name");
        nameField.setAccessible(true);

        if (signature instanceof SignatureAttribute.NestedClassType) {
            result.setType(signature.jvmTypeName());
        } else {
            result.setType((String) nameField.get(signature));
        }

        Field argumentsField = SignatureAttribute.ClassType.class.getDeclaredField("arguments");
        argumentsField.setAccessible(true);
        Object[] arguments = (Object[]) argumentsField.get(signature);
        if (arguments != null) {
            result.setArguments(new ArrayList<>());

            for (Object argument: arguments) {
                if (argument instanceof SignatureAttribute.ClassType) {
                    result.getArguments().add(parseTypeSignature((SignatureAttribute.ClassType) argument));
                    continue;
                }

                if (argument instanceof SignatureAttribute.TypeArgument) {
                    SignatureAttribute.TypeArgument typeArgument = (SignatureAttribute.TypeArgument) argument;
                    result.getArguments().add(parseTypeSignature(typeArgument));
                    continue;
                }

                throw new RuntimeException("Неподдерживаемый тип.");
            }
        }

        return result;
    }

    private static GenericType parseTypeSignature(SignatureAttribute.TypeArgument signature) throws NoSuchFieldException, IllegalAccessException {
        Field argField = signature.getClass().getDeclaredField("arg");
        argField.setAccessible(true);
        Object argument = argField.get(signature);

        if (argument instanceof SignatureAttribute.ClassType) {
            return parseTypeSignature((SignatureAttribute.ClassType) argument);
        }

        GenericType result = new GenericType();
        result.setType(signature.getType().jvmTypeName());
        return result;
    }
}

package ru.atott.mapper.introspection;

import javassist.*;
import javassist.bytecode.Descriptor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.atott.mapper.introspection.IntrospectionUtils.parseGenericTypeSignature;

public class BeanIntrospection {

    private ClassPool classPool;

    private Class tClass;

    public BeanIntrospection(ClassPool classPool, Class tClass) {
        this.classPool = Objects.requireNonNull(classPool);
        this.tClass = Objects.requireNonNull(tClass);
    }

    public List<BeanField> getBeanFields() throws NotFoundException {
        CtClass ctClass = classPool.get(tClass.getName());

        return Arrays.stream(ctClass.getDeclaredFields())
                .filter(ctField -> !Modifier.isStatic(ctField.getModifiers()))
                .map(ctField -> {
                    try {
                        String getterName = IntrospectionUtils.getGetterName(ctField);
                        String setterName = IntrospectionUtils.getSetterName(ctField);

                        BeanField beanField = new BeanField();
                        beanField.setFieldName(ctField.getName());
                        beanField.setGetter(ctClass.getDeclaredMethod(getterName, new CtClass[0]));
                        beanField.setSetter(ctClass.getDeclaredMethod(setterName, new CtClass[] { ctField.getType() }));
                        beanField.setField(ctField);

                        GenericType effectiveGenericType = ctField.getGenericSignature() != null
                                ? parseGenericTypeSignature(ctField.getGenericSignature())
                                : parseGenericTypeSignature(ctField.getSignature());

                        if (effectiveGenericType.getType().equals(Optional.class.getName())) {
                            beanField.setOptional(true);

                            effectiveGenericType = effectiveGenericType.getArguments().get(0);
                        }

                        if (effectiveGenericType.getType().equals(List.class.getName())) {
                            beanField.setList(true);

                            effectiveGenericType = effectiveGenericType.getArguments().get(0);
                        }

                        CtClass effectiveType = classPool.get(effectiveGenericType.getType());
                        beanField.setEffectiveType(effectiveType);
                        return beanField;
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    public void assertIsBean() throws NotFoundException, CannotCompileException {
        // TODO: Проверить, что бин не генерик

        CtClass ctClass = classPool.get(tClass.getName());

        if (ctClass.getSuperclass() != null) {
            CtClass ctSuperclass = ctClass.getSuperclass();

            if (!ctClass.isEnum() && !ctSuperclass.getName().equals(Object.class.getName())) {
                throw new NotBeanException("Бин " + tClass.getName() + " не может быть наследником другого класса");
            }
        }

        if (ctClass.getInterfaces() != null && ctClass.getInterfaces().length != 0) {
            throw new NotBeanException("Бин " + tClass.getName() + " не может имплементировать какие-либо интерфейсы");
        }

        if (!Modifier.isPublic(ctClass.getModifiers())) {
            throw new NotBeanException("Бин " + tClass.getName() + " не public");
        }

        if (Modifier.isInterface(ctClass.getModifiers())) {
            throw new NotBeanException("Бин " + tClass.getName() + " не класс (а interface)");
        }

        if (Modifier.isAbstract(ctClass.getModifiers())) {
            throw new NotBeanException("Бин " + tClass.getName() + " не может быть абстрактным классом");
        }

        String defaultConstructorDescriptor = Descriptor.ofConstructor(new CtClass[0]);
        try {
            CtConstructor constructor = ctClass.getConstructor(defaultConstructorDescriptor);

            if (!ctClass.isEnum() && !Modifier.isPublic(constructor.getModifiers())) {
                throw new NotBeanException("Бин " + tClass.getName() + " не имеет public конструктора без параметров");
            }
        } catch (NotFoundException e) {
            if (!ctClass.isEnum()) {
                throw new NotBeanException("Бин " + tClass.getName() + " не имеет конструктора без параметров");
            }
        }

        for (CtField field : Arrays.asList(ctClass.getDeclaredFields())) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            if (Modifier.isFinal(field.getModifiers())) {
                throw new NotBeanException("Бин " + tClass.getName() + " имеет final свойство " + field.getName());
            }

            String getterName = IntrospectionUtils.getGetterName(field);
            try {
                CtMethod getter = ctClass.getDeclaredMethod(getterName, new CtClass[0]);

                if (!Modifier.isPublic(getter.getModifiers())
                        || Modifier.isAbstract(getter.getModifiers())) {
                    throw new NotBeanException("Бин " + tClass.getName() + " свойство "
                            + field.getName() + " getter " + getterName + " должен быть public и не abstract");
                }

                if (!getter.getReturnType().equals(field.getType())) {
                    throw new NotBeanException("Бин " + tClass.getName() + " свойство "
                            + field.getName() + " результирующий тип getter-а не совпадает с типом поля");
                }
            } catch (NotFoundException e) {
                throw new NotBeanException("Бин " + tClass.getName() + " свойство "
                        + field.getName() + " не найден getter " + getterName);
            }

            String setterName = IntrospectionUtils.getSetterName(field);
            try {
                CtMethod setter = ctClass.getDeclaredMethod(setterName, new CtClass[] { field.getType() });

                if (!Modifier.isPublic(setter.getModifiers())
                        || Modifier.isAbstract(setter.getModifiers())) {
                    throw new NotBeanException("Бин " + tClass.getName() + " свойство "
                            + field.getName() + " setter " + getterName + " должен быть public и не abstract");
                }
            } catch (NotFoundException e) {
                throw new NotBeanException("Бин " + tClass.getName() + " свойство "
                        + field.getName() + " не найден setter " + setterName);
            }

            // TODO: проверить сложные типы рекурсивно

            // TODO: Лист по интерфейсу и генерик только один.
        }
    }
}

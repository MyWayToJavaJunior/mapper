package ru.atott.mapper.builder;

import javassist.*;
import javassist.bytecode.SignatureAttribute;
import ru.atott.mapper.Mapper;
import ru.atott.mapper.convertion.ValueConverter;
import ru.atott.mapper.convertion.ValueProducer;
import ru.atott.mapper.introspection.BeanField;
import ru.atott.mapper.introspection.IntrospectionUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MapperClassBuilder {

    private ClassPool classPool;

    private Class tClass;

    private Set<BeanField> valueProducers = new HashSet<>();

    private ValueConverter valueConverter;

    public MapperClassBuilder settClass(Class tClass) {
        this.tClass = tClass;
        return this;
    }

    public MapperClassBuilder setClassPool(ClassPool classPool) {
        this.classPool = classPool;
        return this;
    }

    public MapperClassBuilder addValueProducer(BeanField field) {
        valueProducers.add(field);
        return this;
    }

    public MapperClassBuilder setValueConverter(ValueConverter valueConverter) {
        this.valueConverter = valueConverter;
        return this;
    }

    public CtClass build() throws NotFoundException, CannotCompileException {
        tClass = Objects.requireNonNull(tClass);
        classPool = Objects.requireNonNull(classPool);
        valueConverter = Objects.requireNonNull(valueConverter);

        CtClass ctClass = classPool.makeClass(IntrospectionUtils.getSerializerCtClassName(tClass));
        ctClass.setInterfaces(new CtClass[] { classPool.get(Mapper.class.getName()) });
        SignatureAttribute.ClassSignature classSignature = new SignatureAttribute.ClassSignature(null, null,
                new SignatureAttribute.ClassType[] {
                        new SignatureAttribute.ClassType(Mapper.class.getName(),
                                new SignatureAttribute.TypeArgument[] { new SignatureAttribute.TypeArgument(new SignatureAttribute.ClassType(tClass.getName()))})
                });
        ctClass.setGenericSignature(classSignature.encode());

        CtClass ctVc = classPool.get(ValueConverter.class.getName());
        CtField vcField = new CtField(ctVc, "vc", ctClass);
        vcField.setModifiers(Modifier.setPublic(vcField.getModifiers()));
        ctClass.addField(vcField);

        CtClass ctVp = classPool.get(ValueProducer.class.getName());
        valueProducers.forEach(beanField -> {
            try {
                CtField vpField = new CtField(ctVp, IntrospectionUtils.getValueProducerFieldName(beanField), ctClass);
                vpField.setModifiers(Modifier.setPublic(vpField.getModifiers()));
                ctClass.addField(vpField);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });

        ctClass.addMethod(
                new NewInstanceMethodBuilder()
                        .settClass(tClass)
                        .setCtClass(ctClass)
                        .build());

        ctClass.addMethod(
                new SerializeToObjectMethodBuilder()
                        .settClass(tClass)
                        .setCtClass(ctClass)
                        .setClassPool(classPool)
                        .addValueProducers(valueProducers)
                        .setValueConverter(valueConverter)
                        .build());

        ctClass.addMethod(
                new SerializeToMapMethodBuilder()
                        .settClass(tClass)
                        .setCtClass(ctClass)
                        .setClassPool(classPool)
                        .addValueProducers(valueProducers)
                        .setValueConverter(valueConverter)
                        .build());

        return ctClass;
    }
}

package ru.atott.mapper.builder;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.util.Objects;

public class NewInstanceMethodBuilder {

    private Class tClass;

    private CtClass ctClass;

    public NewInstanceMethodBuilder settClass(Class tClass) {
        this.tClass = tClass;
        return this;
    }

    public NewInstanceMethodBuilder setCtClass(CtClass ctClass) {
        this.ctClass = ctClass;
        return this;
    }

    public CtMethod build() throws CannotCompileException {
        this.tClass = Objects.requireNonNull(tClass);
        this.ctClass = Objects.requireNonNull(ctClass);

        StringBuilder body = new StringBuilder();
        body.append("public Object newInstance() { ");
        body.append("return new ").append(tClass.getCanonicalName()).append("();");
        body.append("}");
        return CtNewMethod.make(body.toString(), this.ctClass);
    }
}

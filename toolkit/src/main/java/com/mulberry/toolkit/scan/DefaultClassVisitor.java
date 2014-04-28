/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.toolkit.scan;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mulberry.toolkit.base.Consumer;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/28/14
 *         Time: 7:19 PM
 */
@NotThreadSafe
public class DefaultClassVisitor extends ClassVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClassVisitor.class);

    private final Predicate<ClassInfo> predicate;
    private final Consumer<String> consumer;
    private volatile ClassInfo classInfo;

    public DefaultClassVisitor(Predicate<ClassInfo> predicate, Consumer<String> consumer) {
        super(Opcodes.ASM5);
        this.predicate = predicate;
        this.consumer = consumer;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        classInfo = new ClassInfo(access, name, signature, superName, interfaces);
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (classInfo != null) {
            classInfo.add(desc);
        }
        return null;
    }


    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        // If the name of the class that was visited is equal to the name of this visited inner class then
        // this access field needs to be used for checking the scope of the inner class
        if (classInfo != null && classInfo.name.equals(name)) {
            classInfo.setInnerClass(true);
            classInfo.setAccess(classInfo.access | access);
        }
        super.visitInnerClass(name, outerName, innerName, access);
    }

    public void visitEnd() {
        if (predicate.apply(classInfo)) {
            String className = classInfo.name.replace("/", ".");
            LOGGER.debug("Found class {}.", className);
            consumer.accept(className);
        }
    }

    public static class ClassInfo {
        private final String name;
        private final String signature;
        private final String superName;
        private final Set<String> interfaces;
        private final Set<String> annotations = Sets.newConcurrentHashSet();

        private boolean innerClass;
        private int access;

        public ClassInfo(int access, String name, String signature, String superName, String... interfaces) {
            this(name, signature, superName, interfaces);
            this.access = access;
        }

        public ClassInfo(String name, String signature, String superName, String... interfaces) {
            this.name = name;
            this.signature = signature;
            this.superName = superName;
            this.interfaces = new ImmutableSet.Builder<String>().add(interfaces).build();
        }

        public boolean add(String annotation) {
            return annotations.add(annotation);
        }

        public boolean isInnerClass() {
            return innerClass;
        }

        public void setInnerClass(boolean innerClass) {
            this.innerClass = innerClass;
        }

        public void setAccess(int access) {
            this.access = access;
        }

        public int getAccess() {
            return access;
        }

        public String getName() {
            return name;
        }

        public String getSuperName() {
            return superName;
        }

        public Set<String> getInterfaces() {
            return interfaces;
        }

        public String getSignature() {
            return signature;
        }

        public Set<String> getAnnotations() {
            return annotations;
        }

        public boolean isScoped(){
            if (innerClass) {
                return (access & Opcodes.ACC_PUBLIC) != 0 && (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
            } else {
                return (access & Opcodes.ACC_PUBLIC) != 0;
            }
        }
    }
}

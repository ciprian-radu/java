package com.structurizr.analysis;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Some utility methods for working with types.
 */
public class TypeUtils {

    /**
     * Finds the visibility of a given type.
     *
     * @param typeName  the fully qualified type name
     * @return          a TypeVisibility object representing the visibility (e.g. public, package, etc)
     */
    public static TypeVisibility getVisibility(String typeName) throws Exception {
        try {
            Class<?> type = ClassLoader.getSystemClassLoader().loadClass(typeName);
            int modifiers = type.getModifiers();
            if (Modifier.isPrivate(modifiers)) {
                return TypeVisibility.PRIVATE;
            } else if (Modifier.isPublic(modifiers)) {
                return TypeVisibility.PUBLIC;
            } else if (Modifier.isProtected(modifiers)) {
                return TypeVisibility.PROTECTED;
            } else {
                return TypeVisibility.PACKAGE;
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("The specified type could not be found.");
        }
    }

    /**
     * Finds the category of a given type.
     *
     * @param typeName  the fully qualified type name
     * @return          a TypeCategory object representing the category (e.g. class, interface, enum, etc)
     */
    public static TypeCategory getCategory(String typeName) throws Exception {
        try {
            Class<?> type = ClassLoader.getSystemClassLoader().loadClass(typeName);
            if (type.isInterface()) {
                return TypeCategory.INTERFACE;
            } else if (type.isEnum()) {
                return TypeCategory.ENUM;
            } else {
                if (Modifier.isAbstract(type.getModifiers())) {
                    return TypeCategory.ABSTRACT_CLASS;
                } else{
                    return TypeCategory.CLASS;
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("The specified type could not be found.");
        }
    }

    /**
     * Finds the set of types that are annotated with the specified annotation.
     *
     * @param annotation        the Annotation to find
     * @return                  a Set of Class objects, or an empty set of none could be found
     */
    public static Set<Class<?>> findTypesAnnotatedWith(Class<? extends Annotation> annotation, Set<Class<?>> types) {
        if (annotation == null) {
            throw new IllegalArgumentException("An annotation type must be specified.");
        }

        return types.stream().filter(c -> c.isAnnotationPresent(annotation)).collect(Collectors.toSet());
    }

    /**
     * Finds the first implementation of the given interface.
     *
     * @param interfaceType     a Class object representing the interface type
     * @param types             the set of Class objects to search through
     * @return  the first concrete implementation class of the given interface,
     *          or null if one can't be found
     */
    public static Class findFirstImplementationOfInterface(Class interfaceType, Set<Class<?>> types) {
        if (interfaceType == null) {
            throw new IllegalArgumentException("An interface type must be provided.");
        } else if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException("The interface type must represent an interface.");
        }

        if (types == null) {
            throw new IllegalArgumentException("The set of types to search through must be provided.");
        }

        for (Class<?> type : types) {
            boolean isInterface = type.isInterface();
            boolean isAbstract = Modifier.isAbstract(type.getModifiers());
            boolean isAssignable = interfaceType.isAssignableFrom(type);
            if (!isInterface && !isAbstract && isAssignable) {
                return type;
            }
        }

        return null;
    }

}
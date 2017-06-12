package com.tmarsteel.jcli.util.formatting.table.builder;

import java.lang.reflect.Method;

/**
 * Converts the name of a getter method to a string that can be used as a column heading.
 */
public interface GetterMethodToHeadingConverter
{
    /**
     * Converts the name of a getter method to a string that can be used as a column heading.
     * @param method The method to convert; must not return void and must not accept arguments. The given {@link Method}s
     *               {@link Method#name} must start with {@code "get"}; the given method must not declare thrown
     *               exceptions.
     * @return The heading to show for the column obtained from the given getter
     */
    String toHeading(Method method);

    /**
     * Asserts that the given method is a valid argument for {@link #toHeading}
     * @throws IllegalArgumentException If the given method is not suitable
     */
    default void rejectUnsupported(Method method) throws AssertionError {
        // non-void return value
        if (method.getReturnType().equals(Void.class)) {
            throw new IllegalArgumentException(method.getDeclaringClass().getName() + "#" + method.getName() + " does not appear to be a conventional getter: returns void");
        }

        // no declared exceptions
        if (method.getAnnotatedExceptionTypes().length > 0) {
            throw new IllegalArgumentException(method.getDeclaringClass().getName() + "#" + method.getName() + " does not appear to be a conventional getter: declares exceptions");
        }

        // no parameters
        if (method.getParameterCount() != 0) {
            throw new IllegalArgumentException(method.getDeclaringClass().getName() + "#" + method.getName() + " does not appear to be a conventional getter: requires parameters");
        }

        // must start with get and continue uppercase
        if (!method.getName().matches("^get([A-Z|0-9]).*")) {
            throw new IllegalArgumentException(method.getDeclaringClass().getName() + "#" + method.getName() + " does not appear to be a conventional getter: does not start with get");
        }
    }
}

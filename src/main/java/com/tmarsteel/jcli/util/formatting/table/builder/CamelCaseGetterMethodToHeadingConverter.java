package com.tmarsteel.jcli.util.formatting.table.builder;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link GetterMethodToHeadingConverter}; assumes camel case getters.
 */
public class CamelCaseGetterMethodToHeadingConverter implements GetterMethodToHeadingConverter
{
    private static final Pattern captitalWordPattern = Pattern.compile("([A-Z|0-9][a-z]*)");

    private static CamelCaseGetterMethodToHeadingConverter singleton;

    public static CamelCaseGetterMethodToHeadingConverter getInstance() {
        // this is not thread safe... but having multiple instances of this thing doesn't really hurt, does it?

        if (singleton == null) {
            singleton = new CamelCaseGetterMethodToHeadingConverter();
        }

        return singleton;
    }

    private CamelCaseGetterMethodToHeadingConverter() {}

    @Override
    public String toHeading(Method method)
    {
        rejectUnsupported(method);

        String camelCasePropertyName = method.getName().substring(3);

        return captitalWordPattern.matcher(camelCasePropertyName).replaceAll("$1 ").trim();
    }
}

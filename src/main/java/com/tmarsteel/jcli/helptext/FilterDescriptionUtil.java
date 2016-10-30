package com.tmarsteel.jcli.helptext;

import com.tmarsteel.jcli.filter.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains default {@link FilterDescriptor} methods for the default filters.
 */
public abstract class FilterDescriptionUtil
{
    /**
     * Returns a list of constraint explanations for instances of {@link IntegerFilter}.
     * See {@link FilterDescriptor#describe(Filter)} for the detailed contract of this method.
     * @throws UnsupportedOperationException If the given filter is not an instance of {@link IntegerFilter}
     */
    public static List<String> describeInteger(Filter oFilter)
    {
        if (!(oFilter instanceof IntegerFilter))
        {
            throw new UnsupportedOperationException("This method supports only objects of type " + IntegerFilter.class.getName());
        }

        IntegerFilter filter = (IntegerFilter) oFilter;

        ArrayList<String> list = new ArrayList<>(3);
        list.add("must be an integer number");

        if (filter.getRadix() != 10)
        {
            list.add("must be specified in base " + filter.getRadix());
        }

        list.add(
            "must be between " + Long.toString(filter.getMinValue(), filter.getRadix()) +
                " and " + Long.toString(filter.getMaxValue(), filter.getRadix()) + " inclusive"
        );

        return list;
    }

    /**
     * Returns a list of constraint explanations for instances of {@link BigIntegerFilter}.
     * See {@link FilterDescriptor#describe(Filter)} for the detailed contract of this method.
     * @throws UnsupportedOperationException If the given filter is not an instance of {@link BigIntegerFilter}
     */
    public static List<String> describeBigInteger(Filter oFilter)
    {
        if (!(oFilter instanceof BigIntegerFilter))
        {
            throw new UnsupportedOperationException("This method supports only objects of type " + BigIntegerFilter.class.getName());
        }

        BigIntegerFilter filter = (BigIntegerFilter) oFilter;

        ArrayList<String> list = new ArrayList<>(4);

        list.add("must be an integer number");

        if (filter.getRadix() != 10)
        {
            list.add("must be specified in base " + filter.getRadix());
        }

        if (filter.getMinValue() != null)
        {
            list.add("must be equal to or greater than " + filter.getMinValue().toString(filter.getRadix()));
        }

        if (filter.getMaxValue() != null)
        {
            list.add("must be equal to or less than " + filter.getMaxValue().toString(filter.getRadix()));
        }

        return list;
    }

    /**
     * Returns a list of constraint explanations for instances of {@link DecimalFilter}.
     * See {@link FilterDescriptor#describe(Filter)} for the detailed contract of this method.
     * @throws UnsupportedOperationException If the given filter is not an instance of {@link DecimalFilter}
     */
    public static List<String> describeDecimal(Filter oFilter)
    {
        if (!(oFilter instanceof DecimalFilter))
        {
            throw new UnsupportedOperationException("This method supports only objects of type " + DecimalFilter.class.getName());
        }

        DecimalFilter filter = (DecimalFilter) oFilter;

        ArrayList<String> list = new ArrayList<>(2);
        list.add("must be a number");

        list.add(
            "must be between " + Double.toString(filter.getMinValue()) +
                " and " + Double.toString(filter.getMaxValue()) + " inclusive"
        );

        return list;
    }

    /**
     * Returns a list of constraint explanations for instances of {@link BigDecimalFilter}.
     * See {@link FilterDescriptor#describe(Filter)} for the detailed contract of this method.
     * @throws UnsupportedOperationException If the given filter is not an instance of {@link BigDecimalFilter}
     */
    public static List<String> describeBigDecimal(Filter oFilter)
    {
        if (!(oFilter instanceof BigDecimalFilter))
        {
            throw new UnsupportedOperationException("This method supports only objects of type " + BigDecimalFilter.class.getName());
        }

        BigDecimalFilter filter = (BigDecimalFilter) oFilter;

        ArrayList<String> list = new ArrayList<>(3);
        list.add("must be a number");

        if (filter.getMinValue() != null)
        {
            list.add("must be equal to or greater than " + filter.getMinValue().toString());
        }

        if (filter.getMaxValue() != null)
        {
            list.add("must be equal to or less than " + filter.getMaxValue().toString());
        }

        return list;
    }

    /**
     * Returns a list of constraint explanations for instances of {@link SetFilter}.
     * See {@link FilterDescriptor#describe(Filter)} for the detailed contract of this method.
     * @throws UnsupportedOperationException If the given filter is not an instance of {@link SetFilter}
     */
    public static List<String> describeSet(Filter oFilter)
    {
        if (!(oFilter instanceof SetFilter))
        {
            throw new UnsupportedOperationException("This method supports only objects of type " + SetFilter.class.getName());
        }

        SetFilter filter = (SetFilter) oFilter;

        ArrayList<String> list = new ArrayList<>(1 + filter.options().size());

        list.add(
            "must be one of the following options (case " +
                (filter.isCaseSensitive()? "" : "in") + "sensitive):"
        );
        list.addAll(filter.options());

        return list;
    }

    /**
     * Returns a list of constraint explanations for instances of {@link RegexFilter}.
     * See {@link FilterDescriptor#describe(Filter)} for the detailed contract of this method.
     * @throws UnsupportedOperationException If the given filter is not an instance of {@link RegexFilter}
     */
    public static List<String> describeRegex(Filter oFilter) {
        if (!(oFilter instanceof RegexFilter))
        {
            throw new UnsupportedOperationException("This method supports only objects of type " + RegexFilter.class.getName());
        }

        RegexFilter filter = (RegexFilter) oFilter;

        ArrayList<String> list = new ArrayList<>(2);

        list.add("must match this regular expression: " + filter.getPattern());

        if (filter.getReturnGroup() != 0) {
            list.add("group " + filter.getReturnGroup() + " is relevant");
        }

        return list;
    }

    /**
     * Returns a list of constraint explanations for instances of {@link MetaRegexFilter}.
     * See {@link FilterDescriptor#describe(Filter)} for the detailed contract of this method.
     * @throws UnsupportedOperationException If the given filter is not an instance of {@link MetaRegexFilter}
     */
    public static List<String> describeMetaRegex(Filter oFilter) {
        if (!(oFilter instanceof MetaRegexFilter))
        {
            throw new UnsupportedOperationException("This method supports only objects of type " + MetaRegexFilter.class.getName());
        }

        ArrayList<String> list = new ArrayList<>(1);
        list.add("must be a valid regular expression");
        return list;
    }

    /**
     * Returns a list of constraint explanations for instances of {@link FileFilter} and {@link PathFilter}.
     * See {@link FilterDescriptor#describe(Filter)} for the detailed contract of this method.
     * @throws UnsupportedOperationException If the given filter is not an instance of either {@link FileFilter} or {@link PathFilter}.
     */
    public static List<String> describeFile(Filter oFilter) {
        if (!(oFilter instanceof FileFilter) && !(oFilter instanceof PathFilter))
        {
            throw new UnsupportedOperationException("This method supports only objects of type " +
                FileFilter.class.getName() + " or " + PathFilter.class.getName());
        }

        FileFilter filter = oFilter instanceof FileFilter? (FileFilter) oFilter : ((PathFilter) oFilter).getDelegate();

        ArrayList<String> list = new ArrayList<>(6);

        String fileType = ""; // is also used for other constraints
        switch (filter.getFileType()) {
            case IRRELEVANT: fileType += "file or directory"; break;
            case FILE:       fileType += "file"; break;
            case DIRECTORY:  fileType += "directory"; break;
        }

        // type
        list.add("must point to a " + fileType);

        // existence
        switch (filter.getExistenceState()) {
            case MUST_EXIST: list.add("the " + fileType + " must exist"); break;
            case MUST_NOT_EXIST: list.add("the " + fileType + " must not exist"); break;
        }

        // permissions
        if (filter.getPermissions() != FileFilter.PERMISSION.IRRELEVANT) {
            if (filter.getPermissions().readR) {
                list.add("the " + fileType + " must be readable");
            }
            if (filter.getPermissions().writeR) {
                list.add("the " + fileType + " must be writable");
            }
            if (filter.getPermissions().execR) {
                switch (filter.getFileType()) {
                    case IRRELEVANT: list.add("the " + fileType + " must be executable / listable"); break;
                    case FILE:       list.add("the " + fileType + " must be executable"); break;
                    case DIRECTORY:  list.add("the " + fileType + " must be listable"); break;
                }
            }
        }

        if (filter.getExtension() != null) {
            list.add("the " + fileType + " name must end with " + filter.getExtension());
        }

        return list;
    }
}

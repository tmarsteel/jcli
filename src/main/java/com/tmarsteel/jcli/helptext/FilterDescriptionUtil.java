package com.tmarsteel.jcli.helptext;

import com.tmarsteel.jcli.filter.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains default {@link FilterDescriptor} methods for the default filters.
 */
public abstract class FilterDescriptionUtil
{
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
}

package com.tmarsteel.jcli.util.formatting.table.builder;

import com.tmarsteel.jcli.util.formatting.table.TextTable;

import java.util.Collection;

/**
 * Groups {@link TextTableBuilder}s that can take a {@link Collection}<? extends ObjectType> and spit out a nice
 * {@link TextTable}.
 */
public abstract class ObjectTextTableBuilder<ObjectType> extends TextTableBuilder<ObjectTextTableBuilder<ObjectType>>
{
    public abstract TextTable build(Collection<? extends ObjectType> items);
}

package com.tmarsteel.jcli.util.formatting.table.builder;

import com.tmarsteel.jcli.util.formatting.Renderable;
import com.tmarsteel.jcli.util.formatting.table.TextTable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Builds {@link TextTable}s based on the getter method of a given type
 */
class GetterBasedTextTableBuilder<ObjectType>
    extends ObjectTextTableBuilder<ObjectType>
{
    private final Class<ObjectType> typeClass;
    private final GetterMethodToHeadingConverter nameConverter;

    private final ArrayList<Method> gettersOfType;

    GetterBasedTextTableBuilder(Class<ObjectType> typeClass, GetterMethodToHeadingConverter nameConverter) {
        this.typeClass = typeClass;
        this.nameConverter = nameConverter;

        // fills the getters of the type
        gettersOfType = new ArrayList<>(typeClass.getMethods().length / 2);

        Method[] methods = typeClass.getMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));

        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers())) continue;
            if (method.getDeclaringClass().equals(Object.class)) continue;
            try {
                nameConverter.rejectUnsupported(method);
            }
            catch (IllegalArgumentException ex) {
                // method rejected => not supported
                continue;
            }

            gettersOfType.add(method);
        }
    }

    public List<Method> getRecognizedMethods() {
        return Collections.unmodifiableList(this.gettersOfType);
    }

    @Override
    public TextTable build() {
        TextTable table = super.build();

        if (showHeadings)
        {
            // set the headings
            String[] headings = getRecognizedMethods().stream().map(nameConverter::toHeading).toArray(String[]::new);
            table.setHeadings(headings);
        }
        else
        {
            table.setHeadings((List<Renderable>) null);
        }

        return table;
    }

    @Override
    public TextTable build(Collection<? extends ObjectType> items) {
        TextTable table = build();

        // wrap all the returned data into a Renderable that lazily obtains the multiline text strategy from the
        // underlying table and renders the data
        List<List<Renderable>> renderableData = items
            .stream()
            .map(item -> {
                return gettersOfType
                    .stream()
                    .map(getter -> (Renderable) (maxWidth, lineSeparator) -> {
                        try
                        {
                            Object cellContent = getter.invoke(item);
                            String cellContentAsString;
                            if (cellContent instanceof Renderable) {
                                cellContentAsString = ((Renderable) cellContent).render(maxWidth, lineSeparator);
                            }
                            else
                            {
                                cellContentAsString = table.getMultilineTextStrategy().wrap(cellContent.toString(), maxWidth, lineSeparator);
                            }

                            return cellContentAsString;
                        }
                        catch (IllegalAccessException | InvocationTargetException ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .collect(toList());
            })
            .collect(toList());

        table.rows().addAll(renderableData);

        return table;
    }
}

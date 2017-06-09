package com.tmarsteel.jcli.util.formatting;

public final class FormattingUtils
{
    private FormattingUtils() {}

    /**
     * Appends space characters ({@code ' '}) to the end of the given string to make the string of the given target
     * length. Returns that string. If the given string is as long or longer than the target length the given string
     * is returned without any modification.
     * @return The padded string, e.g. for inputs "abc" and 6 returns "abc   "
     */
    public static String padRight(String string, int targetLength) {
        int stringLength = string.length();
        if (stringLength >= targetLength) {
            return string;
        }

        char[] target = new char[targetLength];

        System.arraycopy(string.toCharArray(), 0, target, 0, stringLength);

        for (int paddingIndex = stringLength;paddingIndex < targetLength;paddingIndex++) {
            target[paddingIndex] = ' ';
        }

        return new String(target);
    }

    /**
     * Prepends space characters ({@code ' '}) to the start of the given string to make the string of the given target
     * length. Returns that string. If the given string is as long or longer than the target length the given string
     * is returned without any modification.
     * @return The padded string, e.g. for inputs "abc" and 6 returns "   abc"
     */
    public static String padLeft(String toBePadded, int nSpaces)
    {
        return (new String(new char[nSpaces]).replace('\0', ' ')) + toBePadded;
    }

    /**
     * Returns a {@link Renderable} that right-aligns the given string when rendering. Does not support wrapping the
     * text because that is a responsibility of {@link com.tmarsteel.jcli.util.formatting.multiline.MultilineTextStrategy}.
     */
    public static Renderable padLeftRenderable(String content) {
        return (maxWidth, lineSeparator) -> {
            if (content.length() > maxWidth) {
                throw new OutOfRenderingSpaceException("Wrapping + right align not supported yet.");
            }
            return padLeft(content, maxWidth);
        };
    }

    public static <T> String join(T[] items, String glue) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0;i < items.length;i++) {
            stringBuilder.append(items[i]);
            if (i < items.length - 1) {
                stringBuilder.append(glue);
            }
        }

        return stringBuilder.toString();
    }
}

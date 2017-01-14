package com.tmarsteel.jcli.util.formatting.multiline;

/**
 * A multiline strategy that splits the given text by words (spaces).
 */
public final class WordsplitMultilineStrategy implements MultilineTextStrategy
{
    private static WordsplitMultilineStrategy singletonInstance;

    public static MultilineTextStrategy getInstance() {
        // this is not thread safe. But having multiple instances of this class does not really hurt anyone.
        if (singletonInstance == null) {
            singletonInstance = new WordsplitMultilineStrategy();
        }

        return singletonInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String wrap(String inputString, int maxWidth, char lineSeparator)
    {
        char[] input = inputString.toCharArray();

        StringBuilder finalOut = new StringBuilder(input.length + 15);

        StringBuilder currentLineBuilder = new StringBuilder(maxWidth);
        int lastWSSeenAtLinePos = -1; // position of the most recent whitespace in the current line
        char current;

        for (int inputPos = 0; inputPos < input.length; inputPos++)
        {
            current = input[inputPos];
            if (current == lineSeparator)
            {
                finalOut.append(currentLineBuilder);
                finalOut.append(lineSeparator);
                if (currentLineBuilder.length() != 0) currentLineBuilder = new StringBuilder(maxWidth);
                continue;
            }
            else if (Character.isWhitespace(current))
            {
                lastWSSeenAtLinePos = currentLineBuilder.length();
                if (currentLineBuilder.length() == maxWidth)
                {
                    // luckily there is a space right where the line limit is ... :O
                    finalOut.append(currentLineBuilder);
                    finalOut.append(lineSeparator);
                    currentLineBuilder = new StringBuilder(maxWidth);
                    lastWSSeenAtLinePos = -1;
                    continue;
                }
                else
                {
                    // do not output leading whitespace onto a new line
                    if (currentLineBuilder.length() != 0)
                    {
                        currentLineBuilder.append(current);
                        continue;
                    }
                }
            }

            if (currentLineBuilder.length() == maxWidth)
            {
                // we just hit the line limit mid-word
                // try to break at the most recent whitespace

                if (lastWSSeenAtLinePos == -1)
                {
                    // there is no whitespace in the line - break mid-word
                    finalOut.append(currentLineBuilder);
                    finalOut.append(lineSeparator);
                    currentLineBuilder = new StringBuilder(maxWidth);
                    currentLineBuilder.append(current);
                    continue;
                }
                else
                {
                    String afterBreak = currentLineBuilder.substring(lastWSSeenAtLinePos + 1);
                    finalOut.append(currentLineBuilder.substring(0, lastWSSeenAtLinePos));
                    finalOut.append(lineSeparator);
                    currentLineBuilder = new StringBuilder(maxWidth);
                    currentLineBuilder.append(afterBreak);
                    currentLineBuilder.append(current);
                    lastWSSeenAtLinePos = -1;
                    continue;
                }
            }

            currentLineBuilder.append(current);
        }

        finalOut.append(currentLineBuilder);
        return finalOut.toString().trim();
    }
}

package fr.cm.scorexpress.ihm.editor;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

public class ToolTipFormatter {
    private ToolTipFormatter() {
    }

    public static Collection<String> lineFormater(final String text, final StringBuilder builder, final int maxLineSize) {
        final Collection<String> lines = new ArrayList<String>();
        if (text.length() > maxLineSize) {
            int length = 0;
            boolean first1 = true;
            final Collection<String> slipped = newArrayList();
            for (final String split : text.split("]")) {
                boolean first = true;
                for (final String split2 : ((first1 ? "" : "]") + split).split(",")) {
                    slipped.add((first ? "" : ",") + split2);
                    first = false;
                }
                first1 = false;
            }
            for (final String split : slipped) {
                length += split.length();
                if (length < maxLineSize) {
                    builder.append(split);
                } else {
                    lines.add(builder.toString());
                    builder.setLength(0);
                    builder.append(split);
                }
                length = builder.length();
            }
            if (length > 0) {
                lines.add(builder.toString());
            }

        } else {
            lines.add(text);
        }
        return lines;
    }
}

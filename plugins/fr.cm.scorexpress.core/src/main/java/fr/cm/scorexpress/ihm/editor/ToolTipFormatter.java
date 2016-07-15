package fr.cm.scorexpress.ihm.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

public class ToolTipFormatter {
    private ToolTipFormatter() {
    }

    public static Collection<String> lineFormater(final String text, final StringBuilder builder, final int maxLineSize) {
        final Collection<String> lines = new ArrayList<String>();
        if (text.length() > maxLineSize) {
            int length = 0;
            final Collection<String> slipped = newArrayList();
            final Iterator<String> iter2 = asList(text.split("]")).iterator();
            while (iter2.hasNext() ) {
                final String split = iter2.next();
                final List<String> listSplit = asList((split + "]" + (iter2.hasNext()?"":"]")).split(","));
                final Iterator<String> iter = listSplit.iterator();
                while (iter.hasNext()) {
                    final String split2 =  iter.next();
                    slipped.add(split2 + (iter.hasNext() ? ",": ""));
                }
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

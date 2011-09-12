package com.pica.sonarplugins.ruby.parsers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;

public class CommentCountParser {
    public static int countLinesOfComment(File file) {
        int numComments = 0;
        LineIterator iterator = null;
        try {
            iterator = FileUtils.lineIterator(file);

            while (iterator.hasNext()) {
                String line = iterator.nextLine();
                if (StringUtils.startsWith(line.trim(), "#")) {
                    numComments++;
                }
            }
        } catch (IOException e) {
            //TODO: Log ALL the errors!
        } finally {
            LineIterator.closeQuietly(iterator);
        }

        return numComments;
    }
}

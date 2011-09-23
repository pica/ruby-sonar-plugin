package com.pica.sonarplugins.ruby.parsers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class CommentCountParser {

    private static final Logger LOG = LoggerFactory.getLogger(CommentCountParser.class);

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
            LOG.error("Error determining comment count for file " + file, e);
        } finally {
            LineIterator.closeQuietly(iterator);
        }

        return numComments;
    }
}

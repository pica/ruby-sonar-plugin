package com.pica.sonarplugins.ruby.core;

import org.sonar.api.resources.AbstractLanguage;

public class Ruby extends AbstractLanguage {

    public static final String KEY = "ruby";
    public static Ruby INSTANCE = new Ruby();

    static final String[] RUBY_KEYWORDS_ARRAY = new String[]{
            "alias", "and", "BEGIN", "begin", "break", "case", "class", "def", "defined?",
            "do", "else", "elsif", "END", "end", "ensure", "false", "for", "if", "in", "module",
            "next", "nil", "not", "or", "redo", "rescue", "retry", "return", "self", "super",
            "then", "true", "undef", "unless", "until", "when", "while", "yield"
    };

    static final String[] RUBY_RESERVED_VARIABLES_ARRAY = new String[]{
            "__FILE__", "__LINE__"
    };

    public Ruby() {
        super(KEY, "Ruby");
    }

    public String[] getFileSuffixes() {
        return new String[]{"rb"};
    }
}

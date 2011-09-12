package com.pica.sonarplugins.ruby.core;

import org.sonar.api.resources.AbstractLanguage;

public class Ruby extends AbstractLanguage {

    public static final String KEY = "ruby";
    public static Ruby INSTANCE = new Ruby();

    public Ruby() {
        super(KEY, "Ruby");
    }

    public String[] getFileSuffixes() {
        return new String[]{"rb"};
    }
}

package com.pica.sonarplugins.ruby;

import com.pica.sonarplugins.ruby.core.Ruby;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;

import java.util.Arrays;
import java.util.List;

@Properties({})

public class RubyPlugin implements Plugin {

    public String getKey() {
        return Ruby.KEY;
    }

    public String getName() {
        return "Ruby";
    }

    public String getDescription() {
        return "Analysis of Ruby projects";
    }

    public List getExtensions() {
        return Arrays.asList(
                Ruby.class,
                RubySensor.class
        );
    }

}

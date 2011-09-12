package com.pica.sonarplugins.ruby.core;

import org.sonar.squid.recognizer.CodeRecognizer;

public class RubyRecognizer extends CodeRecognizer {

  public RubyRecognizer() {
    super(1.0, new RubyFootPrint());
  }
}

package com.pica.sonarplugins.ruby.core;

import org.sonar.squid.recognizer.Detector;
import org.sonar.squid.recognizer.LanguageFootprint;

import java.util.Collections;
import java.util.Set;

public class RubyFootPrint implements LanguageFootprint {

  public Set<Detector> getDetectors() {
    return Collections.emptySet();
  }
}

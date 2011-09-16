package com.pica.sonarplugins.ruby.core;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.WildcardPattern;

import java.io.File;
import java.util.List;

public class RubyFile extends Resource<RubyPackage> {

  private String filename;
  private String longName;
  private String packageKey;
  private boolean unitTest = false;
  private RubyPackage parent = null;

  /**
   * SONARPLUGINS-687: For backward compatibility
   */
  public RubyFile(String key) {
    this(key, false);
  }

  public RubyFile(String key, boolean unitTest) {
    super();
    String realKey = StringUtils.trim(key);
    this.unitTest = unitTest;

    if (realKey.contains(".")) {
      this.filename = StringUtils.substringAfterLast(realKey, ".");
      this.packageKey = StringUtils.substringBeforeLast(realKey, ".");
      this.longName = realKey;

    } else {
      this.filename = realKey;
      this.longName = realKey;
      this.packageKey = RubyPackage.DEFAULT_PACKAGE_NAME;
      realKey = new StringBuilder().append(RubyPackage.DEFAULT_PACKAGE_NAME).append(".").append(realKey).toString();
    }
    setKey(realKey);
  }

  public RubyFile(String packageKey, String className, boolean unitTest) {
    super();

    this.filename = className.trim();
    String key;
    if (StringUtils.isBlank(packageKey)) {
      this.packageKey = RubyPackage.DEFAULT_PACKAGE_NAME;
      this.longName = this.filename;
      key = new StringBuilder().append(this.packageKey).append(".").append(this.filename).toString();
    } else {
      this.packageKey = packageKey.trim();
      key = new StringBuilder().append(this.packageKey).append(".").append(this.filename).toString();
      this.longName = key;
    }
    setKey(key);
    this.unitTest = unitTest;
  }

  public RubyPackage getParent() {
    if (parent == null) {
      parent = new RubyPackage(packageKey);
    }
    return parent;
  }

  public String getDescription() {
    return null;
  }

  public Language getLanguage() {
    return Ruby.INSTANCE;
  }

  public String getName() {
    return filename;
  }

  public String getLongName() {
    return longName;
  }

  public String getScope() {
    return Resource.SCOPE_ENTITY;
  }

  public String getQualifier() {
    return unitTest ? Resource.QUALIFIER_UNIT_TEST_CLASS : Resource.QUALIFIER_CLASS;
  }

  public boolean isUnitTest() {
    return unitTest;
  }

  public boolean matchFilePattern(String antPattern) {
    String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
    WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, ".");
    return matcher.match(getKey());
  }

  /**
   * SONARPLUGINS-687: For backward compatibility
   */
  public static RubyFile fromIOFile(File file, List<File> sourceDirs) {
    return fromIOFile(file, sourceDirs, false);
  }

  /**
   * Creates a {@link RubyFile} from a file in the source directories.
   *
   * @param unitTest whether it is a unit test file or a source file
   * @return the {@link RubyFile} created if exists, null otherwise
   */
  public static RubyFile fromIOFile(File file, List<File> sourceDirs, boolean unitTest) {
    if (file == null) {
      return null;
    }
    String relativePath = DefaultProjectFileSystem.getRelativePath(file, sourceDirs);
    if (relativePath != null) {
      String pacname = null;
      String classname = relativePath;

      if (relativePath.indexOf('/') >= 0) {
        pacname = StringUtils.substringBeforeLast(relativePath, "/");
        pacname = StringUtils.replace(pacname, "/", ".");
        classname = StringUtils.substringAfterLast(relativePath, "/");
      }
      classname = StringUtils.substringBeforeLast(classname, ".");
      return new RubyFile(pacname, classname, unitTest);
    }
    return null;
  }

  /**
   * Shortcut to {@link #fromIOFile(File, List, boolean)} with an absolute path.
   */
  public static RubyFile fromAbsolutePath(String path, List<File> sourceDirs, boolean unitTest) {
    if (path == null) {
      return null;
    }
    return fromIOFile(new File(path), sourceDirs, unitTest);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("key", getKey())
        .append("package", packageKey)
        .append("longName", longName)
        .append("unitTest", unitTest)
        .toString();
  }
}

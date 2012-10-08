package com.pica.sonarplugins.ruby.core;

import static com.pica.sonarplugins.ruby.core.Ruby.INSTANCE;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;

@Phase(name = Phase.Name.PRE)
public class RubySourceImporter extends AbstractSourceImporter {

    private static final Logger LOG = LoggerFactory.getLogger(RubySourceImporter.class);
    private Project project;

    public RubySourceImporter(Project project) {
        super(Ruby.INSTANCE);
        this.project = project;
    }

    @Override
    public void analyse(Project project, SensorContext context) {
        try {
            LOG.info("Importing files from project " + project.getName());
            doAnalyse(project, context);
        } catch (IOException e) {
            throw new SonarException("Parsing source files ended abnormally", e);
        }
    }

    @Override
    protected RubyFile createResource(File file, List<File> sourceDirs, boolean unitTest) {
        return file != null ? RubyFile.fromIOFile(file, sourceDirs, unitTest) : null;
    }

    protected void doAnalyse(Project project, SensorContext context) throws IOException {
        // Importing source files
        ProjectFileSystem fileSystem = project.getFileSystem();
        Charset sourceCharset = fileSystem.getSourceCharset();

        List<File> sourceDirs = fileSystem.getSourceDirs();
        LOG.info("Got {} source dirs", sourceDirs.size());
        List<File> sourceFiles = fileSystem.getSourceFiles(INSTANCE);
        LOG.info("Got {} source files", sourceFiles.size());
        parseDirs(context, sourceFiles, sourceDirs, false, sourceCharset);
        for (File directory : sourceDirs) {
            LOG.info(directory.getName());
        }

        // Importing tests files
        List<File> testDirs = fileSystem.getTestDirs();
        LOG.info("Got {} test dirs", testDirs.size());
        List<File> testFiles = fileSystem.getTestFiles(INSTANCE);
        LOG.info("Got {} test files", testFiles.size());
        parseDirs(context, testFiles, testDirs, true, sourceCharset);
        // Display source dirs and tests directories if info level is enabled.
        for (File directory : testDirs) {
            LOG.info(directory.getName());
        }
    }

    @Override
    protected void parseDirs(SensorContext context, List<File> files, List<File> sourceDirs, boolean unitTest, Charset sourcesEncoding) {
        for (File file : files) {
            Resource resource = createResource(file, sourceDirs, unitTest);
            if (resource != null) {
                try {
                    LOG.debug("Indexing resource: " + resource.getName());
                    context.index(resource);
                    if (isEnabled(project)) {
                        String source = FileUtils.readFileToString(file, sourcesEncoding.name());
                        LOG.debug("Saving source from: " + file.getPath());
                        context.saveSource(resource, source);
                    }
                } catch (Exception e) {
                    throw new SonarException("Unable to read and import the source file : '" + file.getAbsolutePath() + "' with the charset : '"
                            + sourcesEncoding.name() + "'.", e);
                }
            }
        }
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("getLanguage()", getLanguage());
        builder.append("getClass()", getClass());
        return builder.toString();
    }

}

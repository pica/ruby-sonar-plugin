package com.pica.sonarplugins.ruby;

import com.pica.sonarplugins.ruby.core.Ruby;
import com.pica.sonarplugins.ruby.core.RubyFile;
import com.pica.sonarplugins.ruby.core.RubyPackage;
import com.pica.sonarplugins.ruby.core.RubyRecognizer;
import com.pica.sonarplugins.ruby.parsers.CommentCountParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.SonarException;
import org.sonar.squid.measures.Metric;
import org.sonar.squid.text.Source;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public class RubySensor implements Sensor {

    private Ruby ruby;

    public RubySensor(Ruby ruby) {
        this.ruby = ruby;
    }

    public boolean shouldExecuteOnProject(Project project) {
        return project.getLanguage().equals(Ruby.INSTANCE);
    }

    public void analyse(Project project, SensorContext context) {
        computeBaseMetrics(context, project);
    }

    protected void computeBaseMetrics(SensorContext sensorContext, Project project) {
        Reader reader = null;
        ProjectFileSystem fileSystem = project.getFileSystem();

        Set<RubyPackage> packageList = new HashSet<RubyPackage>();
        for (File rubyFile : fileSystem.getSourceFiles(ruby)) {
            try {
                reader = new StringReader(FileUtils.readFileToString(rubyFile, fileSystem.getSourceCharset().name()));
                RubyFile resource = RubyFile.fromIOFile(rubyFile, fileSystem.getSourceDirs());
                Source source = new Source(reader, new RubyRecognizer());
                packageList.add(new RubyPackage(resource.getParent().getKey()));

                sensorContext.saveMeasure(resource, CoreMetrics.LINES, (double) source.getMeasure(Metric.LINES));
                sensorContext.saveMeasure(resource, CoreMetrics.NCLOC, (double) source.getMeasure(Metric.LINES_OF_CODE));
                int numCommentLines = CommentCountParser.countLinesOfComment(rubyFile);
                sensorContext.saveMeasure(resource, CoreMetrics.COMMENT_LINES, (double) numCommentLines);
                sensorContext.saveMeasure(resource, CoreMetrics.FILES, 1.0);
                sensorContext.saveMeasure(resource, CoreMetrics.CLASSES, 1.0);
            } catch (Exception e) {
                throw new SonarException("Error computing base metrics for project.", e);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
        for (RubyPackage pack : packageList) {
            sensorContext.saveMeasure(pack, CoreMetrics.PACKAGES, 1.0);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

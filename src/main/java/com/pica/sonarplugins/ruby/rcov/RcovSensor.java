package com.pica.sonarplugins.ruby.rcov;

import com.pica.sonarplugins.ruby.core.RubyFile;
import hudson.plugins.rubyMetrics.rcov.RcovParser;
import hudson.plugins.rubyMetrics.rcov.model.RcovResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

import java.io.File;
import java.io.IOException;

public class RcovSensor implements Sensor {

    private static final Logger LOG = LoggerFactory.getLogger(RcovSensor.class);

    public boolean shouldExecuteOnProject(Project project) {
        return true;
    }

    public void analyse(Project project, SensorContext context) {
        //TODO: Configure rcov location
        File reportFile = new File("coverage/rcov/");
        try {
            parseReport(project.getFileSystem(), reportFile, context);
        } catch (IOException e) {
            LOG.error("Error analysing project.", e);
        }
    }

    protected void parseReport(ProjectFileSystem fileSystem, File rootFile, final SensorContext context) throws IOException {
        //Coverage will be assigned to the "root" ruby file
        RubyFile rootRubyFile = RubyFile.fromIOFile(fileSystem.getBasedir(), fileSystem.getSourceDirs());

        RcovParser parser = new RcovParser(rootFile);
        //TODO: Dynamic coverage file locations
        File coverageFile = new File("coverage/rcov/index.html");
        RcovResult result = parser.parse(coverageFile);

        context.saveMeasure(rootRubyFile, CoreMetrics.COVERAGE, result.getCodeCoverageFloat().doubleValue());
    }

    @Override
    public String toString() {
        return "Ruby RcovSensor";
    }
}

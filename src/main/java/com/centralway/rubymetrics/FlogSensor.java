/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centralway.rubymetrics;

import com.pica.sonarplugins.ruby.core.RubyFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.util.Map;


/**
 *
 * @author martin.naumann
 */
public class FlogSensor implements Sensor {

    private static final Logger LOG = LoggerFactory.getLogger(FlogSensor.class);

    public boolean shouldExecuteOnProject(Project project) {
        return true;
    }

    public void analyse(Project project, SensorContext context) {
        try {
            processReport(project, context);
        } catch(IOException e) {
            LOG.error("Flog report could not be parsed properly!");
            LOG.error(e.getMessage());
        }
    }
    
    private void processReport(Project project, SensorContext context) throws IOException {
        LineIterator lineIter = FileUtils.lineIterator(new java.io.File("flog.txt"));
                        
        context.saveMeasure(project, CoreMetrics.FUNCTION_COMPLEXITY, getAverageScorePerMethod(lineIter));
    }

    private double getAverageScorePerMethod(LineIterator lineIter) {
        lineIter.nextLine(); // Skip the total score
        Double averagePerMethod = Double.parseDouble(lineIter.nextLine().split(":")[0]);
        lineIter.nextLine(); //There is an empty line that we wanna skip.    
        return averagePerMethod;
    }    
        
    @Override
    public String toString() {
        return "Ruby Flog report sensor";
    }    
}

package org.javaparser.trace_code_plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @goal trace-code
 * @phase process-sources
 */
public class TraceCodePluginMojo extends AbstractMojo {
    /**
     * Location for the source files with added trace lines.
     *
     * @required
     */
    private File sourceDirectory;

    /**
     * Location for the source files with added trace lines.
     *
     * @parameter property="project.build.directory"
     * @required
     */
    private File destinationDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        Log log = getLog();
        log.error("Writing output to " + destinationDirectory);

        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
        }

        final File touch = new File(destinationDirectory, "touch.txt");

        try (FileWriter w = new FileWriter(touch)) {
            w.write("touch.txt");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        }
    }
}

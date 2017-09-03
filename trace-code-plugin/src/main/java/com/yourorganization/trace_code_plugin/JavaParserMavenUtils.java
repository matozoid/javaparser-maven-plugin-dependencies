package com.yourorganization.trace_code_plugin;

import org.apache.maven.plugin.logging.Log;

/**
 * Things to make JavaParser and Maven interact better
 */
public class JavaParserMavenUtils {
    public static void makeJavaParserLogToMavenOutput(Log log) {
        com.github.javaparser.utils.Log.setAdapter(new com.github.javaparser.utils.Log.Adapter() {
            @Override
            public void info(String message) {
                log.info(message);
            }

            @Override
            public void trace(String message) {
                log.debug(message);
            }

            @Override
            public void error(Throwable throwable, String f) {
                log.error(f, throwable);
            }
        });
    }
}

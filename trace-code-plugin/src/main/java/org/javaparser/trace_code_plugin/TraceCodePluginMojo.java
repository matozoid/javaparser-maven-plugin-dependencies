package org.javaparser.trace_code_plugin;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

import static com.github.javaparser.utils.CodeGenerationUtils.f;

/**
 * This plugin is a sample for building your own plugins. It takes a directory of source code and adds a trace line to
 * each method.
 */
@Mojo(name = "trace-code", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class TraceCodePluginMojo extends AbstractMojo {
    /**
     * Location for the source files with added trace lines.
     */
    @Parameter(required = true)
    private File sourceDirectory;

    /**
     * Location where the modified source files should be saved.
     */
    @Parameter(required = true, defaultValue = "${project.build.directory}/generated-sources/trace-code")
    private File destinationDirectory;

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        JavaParserMavenUtils.makeJavaParserLogToMavenOutput(getLog());
        addTraceCodeToAllFiles(sourceDirectory, destinationDirectory);
    }

    private void addTraceCodeToAllFiles(File sourceDirectory, File destinationDirectory) throws MojoExecutionException {
        final SourceRoot source = new SourceRoot(sourceDirectory.toPath());
        try {
            for (ParseResult<CompilationUnit> parseResult : source.tryToParse()) {
                // Only deal with files without parse errors
                if (parseResult.isSuccessful()) {
                    parseResult.getResult().ifPresent(cu -> {
                        // Make the plugin a little noisier
                        cu.getStorage().ifPresent(storage -> Log.info(f("Processing %s...", storage.getFileName())));
                        // Do the actual logic
                        addTraceCodeToCompilationUnit(cu);
                    });
                }
            }
            // Everything parsed with sourceroot is kept in a cache so that it can be saved to disk in 1 line:
            source.saveAll(destinationDirectory.toPath());
            // Tell Maven where we put the generated code so it will be added to the jar.
            project.addCompileSourceRoot(destinationDirectory.getPath());
        } catch (IOException e) {
            throw new MojoExecutionException("Error reading from source directory", e);
        }
    }

    private void addTraceCodeToCompilationUnit(CompilationUnit compilationUnit) {
        // Use a visitor to easily find methods
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration n, Void arg) {
                // Take the method body if it exists, and insert the trace statement. 
                n.getBody().ifPresent(b -> b.getStatements().add(0, traceLine(n.getNameAsString())));
                // In case there are methods somewhere inside this method, visit children:
                super.visit(n, arg);
            }

            private Statement traceLine(String methodName) {
                /* Creates a small AST of the code in the string.
                "f" is a shortcut to String.format.
                 */
                return JavaParser.parseStatement(f("System.out.println(\"Entering %s\");", methodName));
            }
        }, null);
    }
}

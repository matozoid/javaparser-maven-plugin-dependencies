package org.javaparser.trace_code_plugin;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;

import static com.github.javaparser.utils.CodeGenerationUtils.f;

/**
 * This plugin is a sample for building your own plugins. It takes a directory of source code and adds a trace line to
 * each method.
 *
 * @goal trace-code
 * @phase process-sources
 */
public class TraceCodePluginMojo extends AbstractMojo {
    /**
     * Location for the source files with added trace lines.
     *
     * @parameter
     * @required
     */
    private File sourceDirectory;

    /**
     * Location for the source files with added trace lines.
     *
     * @parameter default-value="${project.build.directory}/generated-sources/trace-code"
     * @required
     */
    private File destinationDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        Log log = getLog();
        addTraceCode(log, sourceDirectory, destinationDirectory);
    }

    private void addTraceCode(Log log, File sourceDirectory, File destinationDirectory) throws MojoExecutionException {
        log.error("Adding tracing info to source from " + sourceDirectory + " and storing in " + destinationDirectory);

        SourceRoot source = new SourceRoot(sourceDirectory.toPath());
        try {
            for (ParseResult<CompilationUnit> parseResult : source.tryToParse()) {
                if (parseResult.isSuccessful()) {
                    addTracing(parseResult.getResult().get());
                }
            }
            source.saveAll(destinationDirectory.toPath());
        } catch (IOException e) {
            throw new MojoExecutionException("Error reading from source directory", e);
        }

        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
        }
    }

    private void addTracing(CompilationUnit compilationUnit) {
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration n, Void arg) {
                n.getBody().ifPresent(b -> b.getStatements().add(0, traceLine(n.getNameAsString())));
                super.visit(n, arg);
            }

            private Statement traceLine(String methodName) {
                return JavaParser.parseStatement(f("System.out.println(\"Entering %s\");", methodName));
            }
        }, null);
    }
}

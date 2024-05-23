package org.beeherd.openrewrite;

import org.openrewrite.*;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.*;

import java.util.Comparator;

/**
 * Adds an annotation to every class/interface.
 * TODO cleanup/doc/etc..
 */
public class AddAnnotation extends Recipe {

    @Option(
            displayName = "Add annotation to all classes.",
            description = "Add annotation to all classes.",
            example = "Deprecation"
    )
    private String annotationName = "Deprecation"; // TODO fix hack, options not working..

    AddAnnotation() {}

    public AddAnnotation(String annotationName) {
        this.annotationName = annotationName;
    }

    @Override
    public String getDisplayName() {
        return "Add an annotation";
    }

    @Deprecated
    @Override
    public String getDescription() {
        return "Adds an annotation.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<>() {
            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext ctx) {
                final J.ClassDeclaration c = super.visitClassDeclaration(classDecl, ctx);

                final boolean isAlreadyDeprecated =
                        c.getLeadingAnnotations().stream()
                                .map(ann -> ann.getSimpleName())
                                .anyMatch(annotationName::equals);

                if (isAlreadyDeprecated) {
                    return c;
                }

                return JavaTemplate.builder("@" + annotationName)
                        .build()
                        .apply(
                                updateCursor(c),
                                c.getCoordinates().addAnnotation(Comparator.comparing(J.Annotation::getSimpleName))
                        );
            }

            @Override
            public J.CompilationUnit visitCompilationUnit(J.CompilationUnit origCu, ExecutionContext executionContext) {
                final J.CompilationUnit cu = super.visitCompilationUnit(origCu, executionContext);

                return cu;
            }
        };


    }
}

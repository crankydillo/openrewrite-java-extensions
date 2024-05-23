package org.beeherd.openrewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class AddAnnotationTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new AddAnnotation("Deprecated"));
    }

    @Test
    void addAnnotationToSimpleClass() {
        rewriteRun(
            java(
                """
                    package com.yourorg;

                    class FooBar {}
                """,
                """
                    package com.yourorg;

                    @Deprecated
                    class FooBar {}
                """
            )
        );
    }

    @Test
    void addAnnotationToInterface() {
        rewriteRun(
                java(
                        """
                            package com.yourorg;
        
                            interface FooBar {}
                        """,
                        """
                            package com.yourorg;
        
                            @Deprecated
                            interface FooBar {}
                        """
                )
        );
    }

    @Test
    void doNotDuplicateAnnotation() {
        rewriteRun(
                java(
                        """
                            package com.yourorg;
        
                            @Deprecated
                            class FooBar {}
                        """
                )
        );
    }

    @Test
    void addAnnotationToNestedClasses() {
        // TODO figure out why it's adding space...
        rewriteRun(
                java(
                        """
                            package com.yourorg;
        
                            class FooBar {
                                class Inner {}
                                static class StaticInner {}
                                interface HiHo {}
                            }
                        """,
                        """
                            package com.yourorg;
        
                            @Deprecated
                            class FooBar {
                                @Deprecated
                                class Inner {}
                                
                                @Deprecated
                                static class StaticInner {}
                                
                                @Deprecated
                                interface HiHo {}
                            }
                        """
                )
        );
    }

}

package net.thenextlvl.protect.area;

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.*;

/**
 * An annotation used to annotate elements that must match against a valid {@link Area} name pattern.
 */
@Documented
@Pattern(NamePattern.PATTERN)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
public @interface NamePattern {
    @Language("RegExp")
    String PATTERN = "^@?[a-zA-Z0-9_-]+$";

    @Documented
    @Pattern(Global.PATTERN)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
    @interface Global {
        @Language("RegExp")
        String PATTERN = "^@[a-zA-Z0-9_-]+$";
    }

    @Documented
    @Pattern(Regionized.PATTERN)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
    @interface Regionized {
        @Language("RegExp")
        String PATTERN = "^[a-zA-Z0-9_-]+$";
    }
}

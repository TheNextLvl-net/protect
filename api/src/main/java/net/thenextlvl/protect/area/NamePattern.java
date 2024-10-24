package net.thenextlvl.protect.area;

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.*;

/**
 * An annotation used to specify that a variable, method, parameter, or field must conform to a specific name pattern.
 * <p>
 * The pattern enforced by this annotation is defined by the {@link NamePattern#PATTERN} constant,
 * which is a regular expression that ensures the value starts optionally with an "@"
 * and followed by alphanumeric characters, underscores, or hyphens.
 * <p>
 * This annotation can be applied to fields, local variables, methods,
 * and parameters to ensure that their values match the expected pattern.
 */
@Documented
@Pattern(NamePattern.PATTERN)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
public @interface NamePattern {
    @Language("RegExp")
    String PATTERN = "^@?[a-zA-Z0-9_-]+$";

    /**
     * An annotation used to specify that a variable, method, parameter, or field must conform to the Global pattern.
     * <p>
     * The pattern enforced by this annotation is defined by the {@link Global#PATTERN} constant,
     * which is a regular expression that ensures the value starts with an "@"
     * followed by alphanumeric characters, underscores, or hyphens.
     * <p>
     * This annotation can be applied to fields, local variables, methods, and parameters to ensure that their values
     * match the expected Global pattern.
     */
    @Documented
    @Pattern(Global.PATTERN)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
    @interface Global {
        @Language("RegExp")
        String PATTERN = "^@[a-zA-Z0-9_-]+$";
    }

    /**
     * An annotation used to specify that a variable, method,
     * parameter, or field must conform to the Regionized pattern.
     * <p>
     * The pattern enforced by this annotation is defined by the {@link Regionized#PATTERN} constant,
     * which is a regular expression that ensures the value contains only alphanumeric characters, underscores, or hyphens.
     * <p>
     * This annotation can be applied to fields, local variables, methods, and parameters to ensure that their values
     * match the expected Regionized pattern.
     */
    @Documented
    @Pattern(Regionized.PATTERN)
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
    @interface Regionized {
        @Language("RegExp")
        String PATTERN = "^[a-zA-Z0-9_-]+$";
    }
}

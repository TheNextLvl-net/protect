package net.thenextlvl.protect.area;

import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.*;

/**
 * An annotation used to annotate elements that must match against a valid {@link Area} name pattern.
 */
@Documented
@Pattern("^@?[a-zA-Z0-9_-]+$")
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
public @interface NamePattern {
}

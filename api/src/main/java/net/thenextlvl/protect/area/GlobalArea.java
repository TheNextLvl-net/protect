package net.thenextlvl.protect.area;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;

/**
 * The GlobalArea interface represents the area of an entire world.
 */
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public interface GlobalArea extends Area {

    @Override
    @NamePattern.Global
    String getName();
}

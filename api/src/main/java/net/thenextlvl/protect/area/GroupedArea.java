package net.thenextlvl.protect.area;

import net.thenextlvl.protect.region.GroupedRegion;
import org.jetbrains.annotations.NotNull;

/**
 * GroupedArea is an interface that represents an area bound to many regions.
 */
public interface GroupedArea extends RegionizedArea<@NotNull GroupedRegion> {
}

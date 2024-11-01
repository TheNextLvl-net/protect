package net.thenextlvl.protect.area;

import net.thenextlvl.protect.region.GroupedRegion;
import org.jspecify.annotations.NullMarked;

/**
 * GroupedArea is an interface that represents an area bound to many regions.
 */
@NullMarked
public interface GroupedArea extends RegionizedArea<GroupedRegion> {
}

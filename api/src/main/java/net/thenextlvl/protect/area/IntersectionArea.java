package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.RegionIntersection;
import core.annotation.TypesAreNotNullByDefault;

/**
 * IntersectionArea is an interface that represents an area bound to a RegionIntersection.
 */
@TypesAreNotNullByDefault
public interface IntersectionArea extends RegionizedArea<RegionIntersection> {
}

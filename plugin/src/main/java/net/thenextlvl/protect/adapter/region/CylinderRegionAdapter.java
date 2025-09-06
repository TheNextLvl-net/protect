package net.thenextlvl.protect.adapter.region;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.CylinderRegion;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CylinderRegionAdapter implements TagAdapter<CylinderRegion> {
    @Override
    public CylinderRegion deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var center = context.deserialize(compound.get("center"), BlockVector3.class);
        var radius = context.deserialize(compound.get("radius"), Vector2.class);
        var max = compound.get("max").getAsInt();
        var min = compound.get("min").getAsInt();
        return new CylinderRegion(center, radius, min, max);
    }

    @Override
    public Tag serialize(CylinderRegion vector, TagSerializationContext context) {
        var tag = CompoundTag.empty();
        tag.add("center", context.serialize(vector.getCenter().toBlockPoint()));
        tag.add("radius", context.serialize(vector.getRadius()));
        tag.add("max", vector.getMaximumY());
        tag.add("min", vector.getMinimumY());
        return tag;
    }
}

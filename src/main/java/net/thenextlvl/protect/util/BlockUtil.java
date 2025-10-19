package net.thenextlvl.protect.util;

import org.bukkit.Material;

public final class BlockUtil {
    @SuppressWarnings("deprecation")
    public static boolean isInteractable(Material type, Material item) {
        return switch (item) {
            case BRUSH -> switch (type) {
                case SUSPICIOUS_GRAVEL, SUSPICIOUS_SAND -> true;
                default -> false;
            };
            case SHEARS, GLASS_BOTTLE -> switch (type) {
                case BEEHIVE, BEE_NEST -> true;
                default -> false;
            };
            default -> false;
        } || type.isInteractable();
    }
}

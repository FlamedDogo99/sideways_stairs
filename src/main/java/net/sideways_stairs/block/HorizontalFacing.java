package net.sideways_stairs.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.IStringSerializable;

public enum HorizontalFacing implements IStringSerializable {
    NE("ne"), SE("se"), SW("sw"), NW("nw");

    private final String name;

    HorizontalFacing(String name) {
        this.name = name;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public String getSerializedName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

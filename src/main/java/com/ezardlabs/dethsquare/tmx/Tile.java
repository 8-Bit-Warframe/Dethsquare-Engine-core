package com.ezardlabs.dethsquare.tmx;

/**
 * Created by Benjamin on 2016-04-09.
 */
public class Tile {// Bits on the far end of the 32-bit global tile ID are used for tile flags
    private static final int FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
    private static final int FLIPPED_VERTICALLY_FLAG   = 0x40000000;
    private static final int FLIPPED_DIAGONALLY_FLAG   = 0x20000000;

    private long gid;
    private boolean flipH;
    private boolean flipV;
    private boolean flipD;

    public Tile(int gid) {
        this.flipH = (gid & FLIPPED_HORIZONTALLY_FLAG) != 0;
        this.flipV = (gid & FLIPPED_VERTICALLY_FLAG) != 0;
        this.flipD = (gid & FLIPPED_DIAGONALLY_FLAG) != 0;
        this.gid = gid &
            (~(FLIPPED_HORIZONTALLY_FLAG |
            FLIPPED_VERTICALLY_FLAG |
            FLIPPED_DIAGONALLY_FLAG));
    }

    public long getGid() {
        return this.gid;
    }

    public boolean isFlippedHorizontal() {
        return this.flipH;
    }

    public boolean isFlippedVertical() {
        return this.flipV;
    }

    public boolean isFlippedDiagonal() {
        return this.flipD;
    }
}

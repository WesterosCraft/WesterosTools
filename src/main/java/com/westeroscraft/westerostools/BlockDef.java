package com.westeroscraft.westerostools;

import com.google.gson.annotations.SerializedName;

/*
 * A single block definition associated with an id and variant
 */
public class BlockDef {
  public String id;
  public Variant variant;

	public static enum Variant {
    @SerializedName("solid")
		SOLID,
    @SerializedName("stairs")
    STAIRS,
    @SerializedName("slab")
    SLAB,
    @SerializedName("wall")
    WALL,
		@SerializedName("fence")
		FENCE,
		@SerializedName("hopper")
		HOPPER,
		@SerializedName("tip")
		TIP,
		@SerializedName("carpet")
		CARPET,
		@SerializedName("fence_gate")
		FENCE_GATE,
		@SerializedName("half_door")
		HALF_DOOR,
		@SerializedName("cover")
		COVER,
		@SerializedName("hollow_hopper")
		HOLLOW_HOPPER,
		@SerializedName("log")
		LOG,
		@SerializedName("directional")
		DIRECTIONAL,
		@SerializedName("layer")
		LAYER,
		@SerializedName("pane")
		PANE,
		@SerializedName("sand")
		SAND,
		@SerializedName("path")
		PATH,
		@SerializedName("window_frame")
		WINDOW_FRAME,
		@SerializedName("window_frame_mullion")
		WINDOW_FRAME_MULLION,
		@SerializedName("arrow_slit")
		ARROW_SLIT,
		@SerializedName("arrow_slit_window")
		ARROW_SLIT_WINDOW,
		@SerializedName("arrow_slit_ornate")
		ARROW_SLIT_ORNATE;
	}

  @Override
  public boolean equals(Object o) {
    if (o instanceof BlockDef) {
      BlockDef sd = (BlockDef) o;
      return sd.id.equals(this.id) && (sd.variant == this.variant);
    }
    return false;
  }

  @Override
  public String toString() {
		return this.id;
  }

  public Variant getVariant() {
    return this.variant;
  }
}
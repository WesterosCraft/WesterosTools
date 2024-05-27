package com.westeroscraft.westerostools;

import java.util.List;
import java.util.ArrayList;

/*
 * A named set of blocks.
 */
public class BlockSet {
  public String id;
  public String altname;
  public List<BlockDef> blocks;

  public BlockSet(String n, String alt, List<BlockDef> bl) {
    this.id = n;
    this.altname = (alt == null) ? null : alt;
    this.blocks = (bl == null) ? (new ArrayList<BlockDef>()) : bl;
  }
}
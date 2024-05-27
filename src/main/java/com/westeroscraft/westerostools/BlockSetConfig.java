package com.westeroscraft.westerostools;

import java.util.List;
import java.util.ArrayList;

// Top level container for blocksets.json (populated using GSON)
public class BlockSetConfig {
  public List<BlockSet> blocksets;

  public BlockSetConfig() {
    this.blocksets = new ArrayList<BlockSet>();
  }
}

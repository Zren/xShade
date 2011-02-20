package com.shade.bukkit.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class SetBlockTask implements Runnable {
	Block block;
	Material material;
	Byte data;
	
	public SetBlockTask(Block block, Material material) {
		this.block = block;
		this.material = material;
		this.data = null;
	}
	
	public SetBlockTask(Block block, Material material, byte data) {
		this.block = block;
		this.material = material;
		this.data = data;
	}
	
	@Override
	public void run() {
		block.setType(material);
		if (data != null)
			block.setData(data);
		//System.out.println("[Portal] " + block.toString());
	}

}

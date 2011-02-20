package ca.xshade.bukkit.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class SetBlocksTask implements Runnable {
	List<Block> blocks;
	Material material;
	Byte data;
	
	public SetBlocksTask(List<Block> blocks, Material material) {
		this.blocks = blocks;
		this.material = material;
		this.data = null;
	}
	
	public SetBlocksTask(List<Block> blocks, Material material, byte data) {
		this.blocks = blocks;
		this.material = material;
		this.data = data;
	}
	
	@Override
	public void run() {
		for (Block block : blocks) {
			block.setType(material);
			if (data != null)
				block.setData(data);
			//System.out.println("[Portal] " + block.toString());
		}
	}

}

package com.shade.bukkit.xshade;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.shade.bukkit.util.Direction;

public class Portal {
	private Block base;
	private Direction rotation;
	private Portal link;
	private List<Material> oldMaterials;
	private Location exit;
	
	public Portal() {
		this.rotation = Direction.UNKOWN;
		this.base = null;
		this.exit = null;
		this.oldMaterials = null;
		this.link = null;
	}
	
	public Portal(Block base) {
		this.base = base;
		this.rotation = Direction.UNKOWN;
		this.exit = null;
		this.oldMaterials = null;
		this.link = null;
	}
	
	public Portal(Block base, Direction rotation) {
		this.base = base;
		this.rotation = rotation;
		this.exit = null;
		this.oldMaterials = null;
		this.link = null;
	}
	
	public Portal(Block base, Direction rotation, List<Material> oldMaterials, Location exit, Portal link) {
		this.base = base;
		this.rotation = rotation;
		this.oldMaterials = oldMaterials;
		this.link = link;
		this.exit = exit;
	}

	public void setBase(Block base) {
		this.base = base;
	}

	public Block getBase() {
		return base;
	}
	
	public boolean hasBase() {
		return base != null;
	}

	public void setRotation(Direction rotation) {
		this.rotation = rotation;
	}

	public Direction getRotation() {
		return rotation;
	}
	
	public Portal newInstance() {
		return new Portal(base, rotation, oldMaterials, exit, link);
	}

	public void setLink(Portal portal) {
		this.link = portal;
	}
	
	public Portal getLink() {
		return link;
	}

	public void setOldMaterials(List<Material> oldMaterials) {
		this.oldMaterials = oldMaterials;
	}

	public List<Material> getOldMaterials() {
		return oldMaterials;
	}
	
	public Location getExit() {
		return exit;
	}
	
	public void setExit(Location loc) {
		this.exit = loc;
		if (getRotation() == Direction.N)
			exit.setX(exit.getX() - 1);
		else if (getRotation() == Direction.S)
			exit.setX(exit.getX() + 1);
		else if (getRotation() == Direction.E)
			exit.setX(exit.getZ() - 1);
		else if (getRotation() == Direction.W)
			exit.setX(exit.getZ() + 1);
		
	}
}

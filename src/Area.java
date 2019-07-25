import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Area {
	public enum Terrain { SEA, OCEAN, ARCTIC, TUNDRA, FOREST, FERTILE, MOUNTAIN, JUNGLE, STEPPE, DESERT };
	public enum Extra { NORMAL, WHEAT, OIL, ELEPHANT };
	public enum Border { NORMAL, RIVER, STRAIT, CHANNEL6, CHANNEL7 };
	private String name;
	private Terrain terrain;
	private Extra extra;
	private Region region;
	private Map<Area, Border> neighbors = new HashMap<>();
	
	private Empire empire;
	
	public final int x;
	public final int y;
	
	public Area(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
	public void setExtra(Extra extra) {
		this.extra = extra;
	}
	
	public Extra getExtra() {
		return extra;
	}
	
	public void setRegion(Region region) {
		this.region = region;
	}
	
	public Region getRegion() {
		return region;
	}
	
	public Map<Area, Border> getNeighbors() {
		return neighbors;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public void addNeighbor(Area area, Border border) {
		if (area == this) {
			return;
		}
		neighbors.put(area, border);
		area.neighbors.put(this, border);
	}
	
	public void removeNeighbor(Area area) {
		if (area == this) {
			return;
		}
		neighbors.remove(area);
		area.neighbors.remove(this);
	}
	
	public boolean isSea() {
		return terrain == Terrain.SEA || terrain == Terrain.OCEAN;
	}
}


public class Region {
	private final String name;
	private final Region parent;
	public static final String[] regionNames = new String[] {
		"North America",
		"South America",
		"Europe",
		"Asia",
		"Australasia",
		"Africa",
		"Italy",
		"India",
		"China",
		"South East Asia",
		"North East Asia",
		"No Region"
	};
	private static final Region[] regions = createRegions();
	
	public Region(String name) {
		this(name, null);
	}
	
	public Region(String name, Region parent) {
		this.name = name;
		this.parent = parent;
	}
	
	public String getName() {
		return name;
	}
	
	public Region getParent() {
		return parent;
	}
	
	public boolean locatedIn(String region) {
		if (name.equals(region)) 
		{
			return true;
		}
		if (parent != null) {
			return parent.locatedIn(region);
		}
		return false;
	}
	
	public static Region getRegion(String name) {
		for (Region region : regions) {
			if (region.getName().equals(name)) {
				return region;
			}
		}
		return null;
	}
	
	public static Region[] createRegions() {
		final Region[] regions = new Region[12];
		regions[0] = new Region(regionNames[0]);
		regions[1] = new Region(regionNames[1]);
		regions[2] = new Region(regionNames[2]);
		regions[3] = new Region(regionNames[3]);
		regions[4] = new Region(regionNames[4]);
		regions[5] = new Region(regionNames[5]);
		regions[6] = new Region(regionNames[6], regions[2]);
		regions[7] = new Region(regionNames[7], regions[3]);
		regions[8] = new Region(regionNames[8], regions[3]);
		regions[9] = new Region(regionNames[9], regions[3]);
		regions[10] = new Region(regionNames[10], regions[3]);
		regions[11] = new Region(regionNames[11]);
		return regions;
	}
}

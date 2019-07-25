import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class Empire {
	
	private static final Font FONT = new Font("Arial", Font.BOLD, 12);
	public enum GloryType { ALL, SEA, LAND, SEA_COUNT, LAND_COUNT };
	private Map<Area, Integer> areas = new HashMap<>();
	private final String name;
	
	public Empire(String name, Area location, int power) {
		this.name = name;
		areas.put(location, power);
	}
	
	public void addArea(Area area, int count) {
		if (!areas.containsKey(area)) {
			areas.put(area, count);
		} else {
			System.err.println(area.getName() + " is already owned");
		}
	}
	
	public void modifyArea(Area area, int delta) {
		final Integer count = areas.get(area);
		if (count != null) {
			final int newCount = count + delta;
			if (newCount > 0) {
				areas.put(area, newCount);
			} else if (newCount == 0) {
				areas.remove(area);
			} else {
				System.err.println(area.getName() + " count cannot be negative");
			}
		}
	}
	
	public void removeArea(Area area) {
		areas.remove(area);
	}
	
	public int getUnitCount() {
		return areas.values().stream().mapToInt(Integer::intValue).sum();
	}
	
	public int getAreaCount(Region region, GloryType type) {
		int count = 0;
		for (Map.Entry<Area, Integer> e : areas.entrySet()) {
			int delta = 1;
			if (type == GloryType.LAND_COUNT || type == GloryType.SEA_COUNT) {
				delta = e.getValue();
			}
			final Area area = e.getKey();
			if (area.isSea()) {
				if (type == GloryType.LAND || type == GloryType.LAND_COUNT) {
					continue;
				}
				if (region == null) {
					count += delta;
					continue;
				}
				final Set<Region> regions = new HashSet<>();
				for (Area neighbor : area.getNeighbors().keySet()) {
					if (neighbor.isSea()) {
						continue;
					}
					regions.add(neighbor.getRegion());
				}
				for (Region landRegion : regions) {
					if (landRegion.locatedIn(region.getName())) {
						count += delta;
						break;
					}
				}
			} else {
				if (type == GloryType.SEA || type == GloryType.SEA_COUNT) {
					continue;
				}
				if (region == null || area.getRegion().locatedIn(region.getName())) {
					count += delta;
				}
			}
		}
		return count;
	}
	
	public void paint(Graphics g) {
		Color old = g.getColor();
		Font oldFont = g.getFont();
		g.setFont(FONT);
		final FontMetrics metrics = g.getFontMetrics();
		for (Map.Entry<Area, Integer> e : areas.entrySet()) {
			final Area area = e.getKey();
			final int x = area.x;
			final int y = area.y;
			final int count = e.getValue();
			final String countStr = Integer.toString(count);
			final int h = metrics.getHeight();
			final int w = metrics.stringWidth(countStr);
			g.setColor(Color.BLUE);
			g.fillRect(x - 12, y - 8, 24, 16);
			g.setColor(Color.WHITE);
			g.drawString(countStr, x - w / 2, y + 4);
		}
		g.setColor(old);
		g.setFont(oldFont);
	}
	
	public static void resolveCombat(Area area, Empire attacker, Map<Area, Empire> owners) {
		if (area == null || attacker == null) {
			return;
		}
		final Empire defender = owners.get(area);
		if (defender == null) {
			owners.put(area, attacker);
			return;
		}
		final Integer count1 = attacker.areas.get(area);
		final Integer count2 = defender.areas.get(area);
		if (count1 == null || count2 == null) {
			return;
		}
		if (count1 <= 0 || count2 <= 0) {
			return;
		}
		if (count1 > count2) {
			attacker.areas.put(area, count1 - count2 + 1);
			defender.areas.put(area, 0);
			owners.put(area, attacker);
		} else if (count1 < count2) {
			attacker.areas.put(area, 0);
			defender.areas.put(area, count2 - count1 + 1);
		} else {
			attacker.areas.put(area, 0);
			defender.areas.put(area, 0);
			owners.remove(defender);
		}
	}
	
	public boolean isEliminated() {
		return areas.isEmpty();
	}
}

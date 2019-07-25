import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;


public class Game extends JPanel {
	
	private final BufferedImage bg = ImageCache.getImage("7ages.jpg");
	private final Collection<Area> areas = Editor.loadAreas(new File(System.getProperty("user.home") + "/7ages.dat"));
	private final Map<Area, Empire> owners = new HashMap<>();
	private final List<Empire> empires = new ArrayList<>();
	
	public Game() {
		setPreferredSize(new Dimension(bg.getWidth(), bg.getHeight()));
		/*
		for (Empire.GloryType type : Empire.GloryType.values()) {
			System.err.println(type);
			for (String regionName : Region.regionNames) {
				Region region = Region.getRegion(regionName);
				System.err.println(empire.getAreaCount(region, type) + " areas in region " + region.getName());
			}
			System.err.println(empire.getAreaCount(null, type) + " areas in world");
		}*/
		startEmpire("Egyptians", "Egypt", 15);
	}
	
	public void startEmpire(String name, String location, int power) {
		final Optional<Area> start = areas.stream().filter(area -> area.getName().equals(location)).findAny();
		if (!start.isPresent()) {
			throw new RuntimeException("Unknown area for " + name + ": " + location);
		}
		final Area startArea = start.get();
		final Empire newEmpire = new Empire(name, startArea, power);
		empires.add(newEmpire);
		Empire.resolveCombat(startArea, newEmpire, owners);
		checkEliminations();
	}
	
	public void checkEliminations() {
		final Iterator<Empire> it = empires.iterator();
		while (it.hasNext()) {
			Empire empire = it.next();
			if (empire.isEliminated()) {
				it.remove();
			}
		}
	}
	
	public void score() {
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(bg, 0, 0, null);
		for (Empire empire : empires) {
			empire.paint(g);
		}
	}
	
	public static void main(String[] args) {
		final JFrame f = new JFrame();
        final Game g = new Game();
        JScrollPane scrollPane = new JScrollPane(g);
        f.setContentPane(scrollPane);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.pack();
        f.setLocation(0, 0);
        f.setVisible(true);
	}
}

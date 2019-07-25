import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

@SuppressWarnings("serial")
public class Editor extends JPanel {
    private BufferedImage backgroundImage = ImageCache.getImage("7ages.jpg");
    private final List<Area> areas = new ArrayList<>();
    public static final int DIAMETER = 20;
    private Area fromArea;
    private Region previousRegion;
    private Area.Terrain previousTerrain;
    
    public Editor() {
    	setPreferredSize(new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight()));
    	addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Area area = getArea(e.getX(), e.getY(), DIAMETER);
                	final JTextField nameField = new JTextField();
                	final JComboBox<Area.Terrain> terrainField = new JComboBox<>(Area.Terrain.values());
                	final JComboBox<Area.Extra> extraField = new JComboBox<>(Area.Extra.values());
                	final JComboBox<String> regionField = new JComboBox<>(Region.regionNames);
                	final boolean createNew = area == null;
                	if (area == null) {
                		area = new Area(e.getX(), e.getY());
                		if (previousTerrain != null) {
                			terrainField.setSelectedItem(previousTerrain);
                		}
                		if (previousRegion != null) {
                			regionField.setSelectedItem(previousRegion.getName());
                		}
                	} else {
                    	nameField.setText(area.getName());
                    	terrainField.setSelectedItem(area.getTerrain());
                    	extraField.setSelectedItem(area.getExtra());
                    	if (area.getRegion() != null) {
                    		regionField.setSelectedItem(area.getRegion().getName());
                    	}
                    }
                   	JPanel panel = new JPanel(new GridLayout(0, 1));
                   	panel.add(new JLabel("Area Name:"));
                   	panel.add(nameField);
                   	panel.add(terrainField);
                   	panel.add(extraField);
                   	panel.add(regionField);
                   	if (createNew) {
                   		nameField.addAncestorListener(new AncestorListener() {
							@Override
							public void ancestorAdded(AncestorEvent e) {
								SwingUtilities.invokeLater(() -> {
									e.getComponent().requestFocusInWindow();
									e.getComponent().removeAncestorListener(this);
								});
							}
							@Override
							public void ancestorMoved(AncestorEvent e) {
							}
							@Override
							public void ancestorRemoved(AncestorEvent e) {
							}
                   		});
                   	}
                   	int result = JOptionPane.showConfirmDialog(null, panel, createNew ? "Create New Area" : "Edit Area",
                   			JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                   	final String name = nameField.getText();
                   	if (name != null && !name.isEmpty() && result == JOptionPane.OK_OPTION) {
                   		area.setName(nameField.getText());
                   		final Area.Terrain terrain = (Area.Terrain) terrainField.getSelectedItem(); 
                   		area.setTerrain(terrain);
                   		area.setExtra((Area.Extra) extraField.getSelectedItem());
                   		final Region region = Region.getRegion((String) regionField.getSelectedItem());
                   		area.setRegion(region);
                   		if (createNew) {
                    		areas.add(area);
                    		previousRegion = region;
                    		previousTerrain = terrain;
                   		}
                    }	
                    repaint();
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                	final Area area = getArea(e.getX(), e.getY(), DIAMETER / 2 + 1);
                	if (area != null) {
                		if (fromArea == null) {
                			fromArea = area;
                		} else if (fromArea == area) {
                			fromArea = null;
                		} else {
                        	final JComboBox<Area.Border> borderField = new JComboBox<>(Area.Border.values());
                           	JPanel panel = new JPanel(new GridLayout(0, 1));
                           	panel.add(borderField);
                			int result = JOptionPane.showConfirmDialog(null, panel, "Select Border Type",
                           			JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                			if (result == JOptionPane.OK_OPTION) {
                				fromArea.addNeighbor(area, (Area.Border) borderField.getSelectedItem());
                				fromArea = null;
                				repaint();
                			}
                		}
                	}
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                	final Area area = getArea(e.getX(), e.getY(), DIAMETER / 2 + 1);
                	if (area != null) {
                		areas.remove(area);
                		for (Area otherArea : areas) {
                			otherArea.removeNeighbor(area);
                		}
                	}
                	repaint();
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
    
    @Override
    public void paintComponent(Graphics g) {
	    if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, null);
        }
	    for (Area area : areas) {
	    	drawOval((Graphics2D) g, area.x, area.y, DIAMETER, DIAMETER, true, false, Color.BLACK, 2);
	    	for (Map.Entry<Area, Area.Border> e : area.getNeighbors().entrySet()) {
	    		final boolean water1 = area.getTerrain() == Area.Terrain.OCEAN || area.getTerrain() == Area.Terrain.SEA;
	    		final boolean water2 = e.getKey().getTerrain() == Area.Terrain.OCEAN || e.getKey().getTerrain() == Area.Terrain.SEA;
	    		if (water1 && water2) continue;
	    		if (!water1 && !water2) continue;
	    		Color old = g.getColor();
	    		if (e.getValue() == Area.Border.NORMAL) {
	    			g.setColor(Color.BLACK);
	    		} else {
	    			g.setColor(Color.BLUE);
	    		}
	    		g.drawLine(area.x, area.y, e.getKey().x, e.getKey().y);
	    		g.setColor(old);
	    	}
	    }
    }
    
    public Area getArea(int x, int y, int threshold) {
        for (Area area : areas) {
            if (Math.hypot(x - area.x, y - area.y) < threshold) {
                return area;
            }
        }
        return null;
    }
    
    private void save() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        final int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileChooser.getSelectedFile();
            try (final PrintWriter writer = new PrintWriter(selectedFile, "UTF-8")) {
                final Map<Area, Integer> idMap = new HashMap<>();
                for (int i = 0; i < areas.size(); i++) {
                    final Area area = areas.get(i);
                    idMap.put(area, i);
                    writer.print(i);
                    writer.print(",");
                    writer.print(area.x);
                    writer.print(",");
                    writer.print(area.y);
                    writer.print(",");
                    writer.print(area.getName());
                    writer.print(",");
                    writer.print(area.getTerrain().ordinal());
                    writer.print(",");
                    writer.print(area.getExtra().ordinal());
                    writer.print(",");
                    writer.println(area.getRegion().getName());
                }
                writer.println();
                for (Area area : areas) {
                	for (Map.Entry<Area, Area.Border> e : area.getNeighbors().entrySet()) {
                        writer.print(idMap.get(area));
                        writer.print(",");
                        writer.print(idMap.get(e.getKey()));
                        writer.print(",");
                        writer.println(e.getValue().ordinal());
                	}
                }
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void load() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        final int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileChooser.getSelectedFile();
            final Collection<Area> loadedNodes = loadAreas(selectedFile);
            if (loadedNodes != null) {
                areas.clear();
                areas.addAll(loadedNodes);
                repaint();
            }
        }
    }
    
    public static Collection<Area> loadAreas(final File selectedFile) {
        try (final BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            // File format begins with nodes, then a single empty line and then edges.
            final Map<Integer, Area> idMap = new HashMap<>();
            boolean edges = false;
            String line;
            while ((line = br.readLine()) != null) {
                final String[] parts = line.split(",");
                if (parts.length == 1 && "".equals(parts[0])) {
                    edges = true;
                } else if (edges) {
                    final int fromId = Integer.parseInt(parts[0]);
                    final int toId = Integer.parseInt(parts[1]);
                    final int type = Integer.parseInt(parts[2]);
                    idMap.get(fromId).addNeighbor(idMap.get(toId), Area.Border.values()[type]);
                } else {
                    final int id = Integer.parseInt(parts[0]);
                    final int x = Integer.parseInt(parts[1]);
                    final int y = Integer.parseInt(parts[2]);
                    final String name = parts[3];
                    final int terrain = Integer.parseInt(parts[4]);
                    final int extra = Integer.parseInt(parts[5]);
                    final String region;
                    if (parts.length < 7) {
                    	region = "No Region";
                    } else {
                    	region = parts[6];
                    }
                    final Area area = new Area(x, y);
                    area.setName(name);
                    area.setTerrain(Area.Terrain.values()[terrain]);
                    area.setExtra(Area.Extra.values()[extra]);
                    area.setRegion(Region.getRegion(region));
                    idMap.put(id, area);
                }
            }
            // The file format was appraently good.
            return idMap.values();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void drawOval(final Graphics2D g,
            final int x,
            final int y,
            final int width,
            final int height,
            final boolean centered,
            final boolean filled,
            final Color color,
            final int lineThickness) {

    	// Store before changing.
    	final Stroke tmpS = g.getStroke();
    	final Color tmpC = g.getColor();

    	g.setColor(color);
    	g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

    	final int x2 = centered ? x - (width / 2) : x;
    	final int y2 = centered ? y - (height / 2) : y;

    	if (filled) g.fillOval(x2, y2, width, height);
    	else g.drawOval(x2, y2, width, height);

    	// Set values to previous when done.
    	g.setColor(tmpC);
    	g.setStroke(tmpS);
    }
    
	public static void main(final String[] args) {
        final JFrame f = new JFrame();
        final Editor p = new Editor();
        f.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                final char c = e.getKeyChar();
                switch (c) {
                case '1':

                    break;
                case '2':

                    break;
                case '3':

                    break;
                case '4':

                    break;
                case '5':
                    break;
                case 'a':

                    break;
                case 's':
                	p.save();
                    break;
                case 'l':
                	p.load();
                    break;
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        JScrollPane scrollPane = new JScrollPane(p);
        f.setContentPane(scrollPane);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.pack();
        f.setLocation(0, 0);
        f.setVisible(true);
        
    }
}

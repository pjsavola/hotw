
public class Border {
	public enum Type { RIVER, STRAIT, CHANNEL6, CHANNEL7 };
	
	private Area a;
	private Area b;
	private Type type;

	public Border(Area a, Area b) {
		this(a, b, null);
	}
	
	public Border(Area a, Area b, Type type) {
		this.a = a;
		this.b = b;
		this.type = type;
	}
}

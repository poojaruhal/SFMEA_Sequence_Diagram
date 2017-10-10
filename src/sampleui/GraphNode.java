package sampleui;

public class GraphNode {
	String blockName;
	String label;
	String color;
	String style;
	String shape;
	int x;
	int y;
	boolean isConditional;
	boolean isLoop;
	public GraphNode(String blockName, String label, boolean isConditional, boolean isLoop) {
		super();
		this.blockName = label ;
		this.label = blockName;
		this.isConditional = isConditional;
		this.isLoop = isLoop;
	}
	public GraphNode(GraphNode bb) {
		// TODO Auto-generated constructor stub
		this.blockName		= bb.blockName;
		this.label			= bb.label;
		this.color			= bb.color;
		this.style			= bb.style;
		this.shape			= bb.shape;
		this.x				= bb.x;
		this.y				= bb.y;
		this.isConditional	= bb.isConditional;
		this.isLoop			= bb.isLoop;
	}
	
}

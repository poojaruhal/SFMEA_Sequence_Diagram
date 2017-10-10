package sampleui;

public class EdgeNode {
	GraphNode start;
	GraphNode end;
	String label;
	int x1;
	int y1;
	int x2;
	int y2;
	public EdgeNode(GraphNode start, GraphNode end, String label) {
		super();
		this.start = start;
		this.end = end;
		this.label = label;
	}
}

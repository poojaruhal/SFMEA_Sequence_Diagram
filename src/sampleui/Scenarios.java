package sampleui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scenarios {

	LinkedHashMap<GraphNode,Integer> graphNodeMap = new LinkedHashMap<GraphNode,Integer>();
	ArrayList<EdgeNode> graphEdges = new ArrayList<EdgeNode>();
	HashMap<GraphNode,ArrayList<GraphNode>> graph = new HashMap<GraphNode,ArrayList<GraphNode>>();
	HashMap<GraphNode,Boolean> isVisited = new HashMap<GraphNode,Boolean>();
	HashMap<GraphNode,Boolean> isVisitedAgain = new HashMap<GraphNode,Boolean>();
	ArrayList<GraphNode> localPath = new ArrayList<GraphNode>();//This list is used during finding all paths.
	GraphNode start,end;// Reference to START and END node.
	ArrayList<ArrayList<GraphNode>> shortPaths = new ArrayList<ArrayList<GraphNode>>();
	CopyOnWriteArrayList<ArrayList<GraphNode>> fullPaths = new CopyOnWriteArrayList<ArrayList<GraphNode>>();
	ArrayList<ArrayList<GraphNode>> loopPaths = new ArrayList<ArrayList<GraphNode>>();
	
	/**
	 * Following constructor is used to prepare different data-structures. 
	 * i.e Graph represented as HashMap and isVisited is also HashMap.
	 * @param graphNodeMap contains information about nodes.
	 * @param graphEdges contains information about edges.
	 */
	
	public Scenarios(LinkedHashMap<GraphNode,Integer> graphNodeMap,ArrayList<EdgeNode> graphEdges)
	{
		this.graphEdges = graphEdges;
		Iterator<EdgeNode> it = graphEdges.iterator();
		
		while(it.hasNext())
		{
			EdgeNode edge = it.next();
			System.out.println("call"+edge.start.label);
			/*if(edge.label.contains("BACK"))//Not considering BACK EDGE.
			{
				continue;
			}*/
			
			if(graph.get(edge.start) == null)
			{
				if(edge.start.blockName.equals("START"))
				{
					this.start = edge.start;
					System.out.println("START DETECTED");
				}
				else if(edge.start.blockName.equals("END"))
				{
					this.end = edge.start;
					System.out.println("END DETECTED");
				}
				ArrayList<GraphNode> adjcentNodes = new ArrayList<GraphNode>();
				adjcentNodes.add(edge.end);
				graph.put(edge.start,adjcentNodes);
				
			}
			else
			{
				ArrayList<GraphNode> adjcentNodes = graph.get(edge.start);
				adjcentNodes.add(edge.end);
				graph.put(edge.start,adjcentNodes);
			}
		}
		
		
		for(Map.Entry<GraphNode,Integer> one : graphNodeMap.entrySet())
		{
			isVisited.put(one.getKey(), false);//initially all are unvisited so false.
		}
		
		
	}
	
	/**
	 * This method is call Util function with START and END graph Node.	
	 */
	
	public void generateAllScenarios()
	{
		generateAllScenariosUtil(this.start,this.end);
	}
	
	/**
	 * Find the path between START and END node.End will always will be "END" node.
	 * @param start
	 * @param end
	 */
	public void generateAllScenariosUtil(GraphNode start,GraphNode end)
	{
		isVisited.put(start, true);
		localPath.add(start);
		System.out.println(start.label);
		if(start.blockName.equals("END")) 
		{
			displayAndWriteInFile();
		}
		else
		{
			// Recur for all the vertices adjacent to current vertex
			Iterator<GraphNode> it = graph.get(start).iterator();
			while (it.hasNext()) {
				GraphNode graphNode = (GraphNode) it.next();
				if(!isVisited.get(graphNode))
				{
					generateAllScenariosUtil(graphNode,end);
				}
				else
				{
					addShortPath(graphNode);
					System.out.println("HURREYYY!!!!LOOP DETECTED SHORT PATH ADDED!!!");
				}
			}			
		}
		
		localPath.remove(start);
		isVisited.put(start, false);
	}
	
	public void displayAndWriteInFile()
	{
		ArrayList<GraphNode> path = new ArrayList<GraphNode>();
		Iterator<GraphNode> it = localPath.iterator();
		while (it.hasNext()) {
			GraphNode graphNode = (GraphNode) it.next();
			path.add(graphNode);
			System.out.print(graphNode.blockName+"-");
			// Logic to write in file
		}
	
		fullPaths.add(path);
		
		System.out.println();
		System.out.println("SHORT LENGTH:"+shortPaths.size()+"FULL LENGTH:"+fullPaths.size());
	}
	
	
	/**
	 * This method add short path in arrayList 
	 * @param lastNode indicate start node of loop.
	 */
	public void addShortPath(GraphNode lastNode)
	{
		ArrayList<GraphNode> path = new ArrayList<GraphNode>();
		Iterator<GraphNode> it = localPath.iterator();
		while (it.hasNext()) {
			GraphNode graphNode = (GraphNode) it.next();
			path.add(graphNode);
			System.out.print(graphNode.blockName+"-");
			// Logic to write in file
		}
		path.add(lastNode);
		System.out.println("loop end"+lastNode.blockName);
		shortPaths.add(path);
	}
	
	/**
	 * This method generate all paths from short paths and long paths.
	 */
	public void generateLoopPaths()
	{
		System.out.println("SOHTEST SIZE:"+shortPaths.size());
		for(int i=shortPaths.size()-1;i>=0;i--)
		{
			ArrayList<GraphNode> loopPath = shortPaths.get(i);
			
			System.out.println("start...");
			for(GraphNode node:loopPath)
			{
				System.out.print(node.blockName+" ");
			}
			System.out.println("end...");
			ArrayList<ArrayList<GraphNode>> localPaths = new ArrayList<ArrayList<GraphNode>>();
			for(ArrayList<GraphNode> fullPath: fullPaths)
			{
				Iterator<GraphNode> it = fullPath.iterator();
				while (it.hasNext()) {
					
					GraphNode graphNode = (GraphNode) it.next();
					if(loopPath.get(loopPath.size()-1) == graphNode)
					{
						ArrayList<GraphNode> fullLoopPath = new ArrayList<GraphNode>();
						Iterator<GraphNode> itLoop = loopPath.iterator();
						while (itLoop.hasNext()) {
						
							fullLoopPath.add(itLoop.next());
						}
						
						while (it.hasNext()) {
							fullLoopPath.add(it.next());
						}
						localPaths.add(fullLoopPath);
						fullPaths.add(fullLoopPath);
						System.out.println("FULL LOOP PATHS"+localPaths.size());
						System.out.println("ALL PATHS"+fullPaths);
					}
				}
			}
			
			for(ArrayList<GraphNode> path: localPaths)
			{
				System.out.println("LOCAL PATHS:::");
				for(GraphNode node:path)
				{
					System.out.print(node.blockName+" ");
				}
				fullPaths.add(new ArrayList(path));
				System.out.println();
			}
		}
	}
	
	public void displayPaths()
	{
		for(ArrayList<GraphNode> loopPath: loopPaths)
		{
			Iterator<GraphNode> it = loopPath.iterator();
			while (it.hasNext()) {
				System.out.print(it.next().label+" ");
			}
			System.out.println();
		}
		
	}
	
	public String getEdgeComment(GraphNode u, GraphNode v){
		if(u!=null && v!=null){
			for(EdgeNode e: graphEdges){
				if(e.start.blockName.equals(u.blockName) && e.end.blockName.equals(v.blockName)){
					//System.out.println("Matched Edge: comment: "+e.label);
					return e.label;
				}
			}
		}
		if(u!=null && u.isConditional)	return "FALSE";
		return "";
	}
	
	public String getScenarios()
	{
		String allScenarios = "";
		int index = 1;
//		for(ArrayList<GraphNode> loopPath: loopPaths)
//		{
//			String scenario = "Scenario loop"+(index++)+"\n";
//			Iterator<GraphNode> it = loopPath.iterator();
//			while (it.hasNext()) {
//				scenario +=  it.next().blockName+"\n";
//				//System.out.print(it.next().label+" ");
//			}
//			
//			allScenarios += scenario +"\n";
//			System.out.println(scenario);
//		}
//		
		Set<String> uniqFullPaths = new HashSet<String>();
		for(ArrayList<GraphNode> loopPath: fullPaths)
		{
			//String scenario = "Scenario "+(index++)+"\n";
			String scenario = "";
			Iterator<GraphNode> it = loopPath.iterator();
			GraphNode cur=null,prev=null;
			while (it.hasNext()) {
				cur = it.next();
				String temp = cur.blockName;
				String cmt = getEdgeComment(prev,cur);
				if(!cmt.equals("") && (cmt.equals("TRUE") || cmt.equals("FALSE")))
					temp=" ("+cmt+")\n"+temp;
				else if(prev!=null && prev.isConditional)
					temp = " (FALSE)\n"+temp;
				else
					temp="\n"+temp;
				if(!temp.contains("JOIN"))
					scenario += temp;
				else if(temp.contains("JOIN") && (cmt.equals("TRUE") || cmt.equals("FALSE")))
					scenario += " ("+cmt+")";
				prev = cur;
				//System.out.print(it.next().label+" ");
			}
			uniqFullPaths.add(scenario);
			//System.out.println(scenario);
		}
		 Iterator iterator = uniqFullPaths.iterator(); 
	      
		   // check values
		
		   while (iterator.hasNext()){
			   
			   String scenario = "Scenario "+(index++)+"\n";
			   scenario +=  iterator.next()+"\n";
			   allScenarios += scenario +"\n";
		   }
		   
		return allScenarios;
		
	}
}

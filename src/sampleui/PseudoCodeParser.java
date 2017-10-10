package sampleui;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

public class PseudoCodeParser {
	
	String dotfile = "";
	String pseudoCode = "";
	String nextToken = "";
	String nextCondition = "";
	String nextBasicBlock = "";
	int nextTokenIndex = -1;
	int currentDisplayLevel = 0;
	int currentIndex = 0;
	int nextIndex = 0;
	int blockNo = 1;
	int joinNo = 1;
	int horizontalDistance = 290;
	int verticalDistance = 170;
	double widthDiamond = 2.5;//1.75; 
	double widthRectangle = 3.7;
	double heightDiamond = 1.5;//1.0; 
	double heightRectangle = 1;
	double diamondXAdjustment = (widthDiamond*34.28);
	double diamondYAdjustment = (heightDiamond*40);
	double rectangleXAdjustment = (widthRectangle*34.28);
	double rectangleYAdjustment = (heightRectangle*40);
	boolean isElseIf = false;
	
	Integer maxLevel = Integer.MIN_VALUE;
	public LinkedHashMap<GraphNode, Integer> getGraphNodeMap() {
		return graphNodeMap;
	}

	public ArrayList<EdgeNode> getGraphEdges() {
		return graphEdges;
	}

	//boolean isEndIf = false;
	LinkedHashMap<GraphNode,Integer> graphNodeMap = new LinkedHashMap<GraphNode,Integer>();
	HashMap<Integer,String> colors = new HashMap<Integer,String>();
	ArrayList<EdgeNode> graphEdges = new ArrayList<EdgeNode>();
	ArrayList<GraphNode> joinNodes = new ArrayList<GraphNode>();
	ArrayList<GraphNode> exitNodes = new ArrayList<GraphNode>();
	Stack<GraphNode> loopStack = new Stack<GraphNode>();
	Stack<GraphNode> ifStack = new Stack<GraphNode>();
	//EdgeNode endIfEdge ;
	EdgeNode lastEdge = null;
	public PseudoCodeParser generateGraph(String pseudoCode){
		this.pseudoCode = pseudoCode;
		//create start and end graphnodes
		GraphNode start = new GraphNode(""+(blockNo++),"START",false,false);
		graphNodeMap.put(start, currentDisplayLevel);
		joinNodes.add(start);
		//populateWillGetEndIf(); - obsolete
		if(currentIndex<pseudoCode.length()){
			nextIndex = findNextToken(currentIndex);
			
			if(nextIndex!=currentIndex){
				//handle basic block
				handleBasicBlock(currentIndex,"");
			}
			else if(nextToken.equalsIgnoreCase("IF")){
				//handle if
				handleIf(currentIndex,"");
				
			}else if(nextToken.equalsIgnoreCase("LOOP")){
				//handle loop
				handleLoop(currentIndex,"");
			}			
		}
		currentDisplayLevel++;
		GraphNode end = new GraphNode(""+(blockNo++),"END",false,false);
		graphNodeMap.put(end, currentDisplayLevel);
		//do join all nodes with end node
		for(int i=0; i<joinNodes.size(); i++){
			if(joinNodes.get(i).isConditional)
				graphEdges.add(new EdgeNode(joinNodes.get(i),end,"FALSE"));
			else
				graphEdges.add(new EdgeNode(joinNodes.get(i),end,""));
		}
		for(int i=0; i<exitNodes.size(); i++){
			graphEdges.add(new EdgeNode(exitNodes.get(i),end,""));
		}
		return this;
	}
	
	//obsolete
	private void populateWillGetEndIf(){
		int tempIndex = findNextToken(currentIndex);
		while(currentIndex<pseudoCode.length()){
			currentIndex = tempIndex;
			tempIndex = findNextToken(currentIndex+nextToken.length());
		}
	}
	
	private int findNextToken(int index){
		int tokenNo = -1;
		int minIndex = pseudoCode.length();
		int tokenIndex;
		String[] listTokens = {"IF","ELSE","ENDIF","EXIT","LOOP","END LOOP"};
		for(int i = 0; i < listTokens.length; i++){
			tokenIndex = pseudoCode.indexOf(listTokens[i], index);
			if(tokenIndex<=minIndex && tokenIndex!=-1){
				minIndex = tokenIndex;
				tokenNo = i;
			}
		}
		if(tokenNo!=-1)
			nextToken = listTokens[tokenNo];
		else if(pseudoCode.substring(index).trim().equalsIgnoreCase(""))
			nextToken = "";
		else
			nextToken = "BB";
		System.out.println("nextToken:"+nextToken);
		return minIndex;
	}
	
	private void extractCondition(int index){
		if(pseudoCode.charAt(index)=='I'){
			nextCondition = pseudoCode.substring(pseudoCode.indexOf(' ', index)+1,pseudoCode.indexOf("THEN", index)-1).trim();
			System.out.println("nextCondition:"+nextCondition);
		}else{
			nextCondition = pseudoCode.substring(pseudoCode.indexOf(' ', index)+1,pseudoCode.indexOf("\n", index)).trim();
			System.out.println("nextCondition:"+nextCondition);
		}
	}
	
	private void extractBasicBlock(int start, int end){
			nextBasicBlock = pseudoCode.substring(start,end).trim().replace('\t', ' ');
			System.out.println("nextBasicBlock:"+nextBasicBlock);
	}
	
	private void handleIf(int index, String label){
		currentDisplayLevel++;
		//isEndIf = false;
		if(currentIndex<pseudoCode.length()){
			//take action 
			extractCondition(index);
			GraphNode cb = new GraphNode(""+(blockNo++),nextCondition,true,false);
			graphNodeMap.put(cb, currentDisplayLevel);
			if(!isElseIf)
				ifStack.push(cb);
			isElseIf = false;
			int pos=1;
			if(!joinNodes.isEmpty()){
				//need corrections here
				
				/*while(pos <= joinNodes.size() && !joinNodes.get(joinNodes.size()-pos).isConditional ){
					System.out.println("joinNode: "+ joinNodes.get(joinNodes.size()-pos).blockName +" at level:"+graphNodeMap.get(joinNodes.get(joinNodes.size()-pos)));
					System.out.println("POS:"+pos + " size:"+joinNodes.size());
					pos++;
				}
				if(pos>joinNodes.size())	pos = 1;*/
				graphEdges.add(new EdgeNode(joinNodes.get(joinNodes.size()-pos),cb,label));
				if(!label.equalsIgnoreCase("TRUE"))
					joinNodes.remove(joinNodes.size()-pos);
			}
			joinNodes.add(cb);
			currentIndex = index+3+nextCondition.length()+5;
			while(!Character.isLetter(pseudoCode.charAt(currentIndex)) && currentIndex<pseudoCode.length()-2){
				currentIndex++;
			}
			//handle next token
			nextIndex = findNextToken(currentIndex);
			if(!nextToken.equalsIgnoreCase("")){
				if(nextIndex!=currentIndex){
					//handle basic block
					handleBasicBlock(currentIndex,"TRUE");
				}else if(nextToken.equalsIgnoreCase("IF")){
					//handle if
					handleIf(currentIndex,"TRUE");
				}else if(nextToken.equalsIgnoreCase("LOOP")){
					//handle loop
					handleLoop(currentIndex,"TRUE");
				}else if(nextToken.equalsIgnoreCase("ELSE")){
					//handle else - error
					System.out.println("ERROR: ELSE detected just after IF. Statement(s) required in IF block.");
				}else if(nextToken.equalsIgnoreCase("ENDIF")){
					//handle endif - error
					System.out.println("ERROR: ENDIF detected just after IF. Statement(s) required in IF block.");
				}else if(nextToken.equalsIgnoreCase("EXIT")){
					//handle exit - error
					System.out.println("ERROR: EXIT detected just after IF. Statement(s) required in IF block.");
				}else if(nextToken.equalsIgnoreCase("END LOOP")){
					//handle exit - error
					System.out.println("ERROR: END LOOP detected just after IF. Statement(s) required in IF block.");
				}
			}
		}
	}
	
	private void handleElse(int index){
		currentDisplayLevel--;//currentDisplayLevel--;
		if(currentIndex<pseudoCode.length()){
			//take action 
			setIsElseIf(index);
			/*
			if(isEndIf){
				//handle else - remove last edge and resotre last join node and then handle else
				//EdgeNode e = graphEdges.get(graphEdges.size()-1);
				joinNodes.add(endIfEdge.start);
				graphEdges.remove(endIfEdge);
				isEndIf = false;
			}*/
			//find matching If block. If not found then leave it as it is.
			GraphNode matchingIf = ifStack.peek();
			if(joinNodes.contains(matchingIf)){
				joinNodes.remove(matchingIf);
				joinNodes.add(matchingIf);
			}
			currentIndex = index + 4;
			while(!Character.isLetter(pseudoCode.charAt(currentIndex)) && currentIndex<pseudoCode.length()-2){
				currentIndex++;
			}
			//handle next token
			nextIndex = findNextToken(currentIndex);
			if(!nextToken.equalsIgnoreCase("")){
				if(nextIndex!=currentIndex){
					//handle basic block
					//handleBasicBlockElse(currentIndex,"FALSE");
					handleBasicBlock(currentIndex,"FALSE");
				}else if(nextToken.equalsIgnoreCase("IF")){
					//handle if
					if(!joinNodes.contains(matchingIf)){
						//if matching if is not present then find previous if
						int pos = 1;
						while(pos <= joinNodes.size() && (!joinNodes.get(joinNodes.size()-pos).isConditional || joinNodes.get(joinNodes.size()-pos).isLoop)){
							System.out.println("joinNode: "+ joinNodes.get(joinNodes.size()-pos).blockName +" at level:"+graphNodeMap.get(joinNodes.get(joinNodes.size()-pos)));
							System.out.println("POS:"+pos + " size:"+joinNodes.size());
							pos++;
						}
						matchingIf = joinNodes.get(joinNodes.size()-pos);
						joinNodes.remove(matchingIf);
						joinNodes.add(matchingIf);
					}
					handleIf(currentIndex,"FALSE");
				}else if(nextToken.equalsIgnoreCase("LOOP")){
					//handle loop
					handleLoop(currentIndex,"FALSE");
				}else if(nextToken.equalsIgnoreCase("ELSE")){
					//handle else - error
					System.out.println("ERROR: ELSE detected just after ELSE.");
				}else if(nextToken.equalsIgnoreCase("ENDIF")){
					//handle endif - error
					System.out.println("ERROR: ENDIF detected just after ELSE.");
				}else if(nextToken.equalsIgnoreCase("EXIT")){
					//handle exit - error
					System.out.println("ERROR: EXIT detected just after ELSE.");
				}else if(nextToken.equalsIgnoreCase("END LOOP")){
					//handle exit - error
					System.out.println("ERROR: END LOOP detected just after ELSE.");
				}
			}
		}
	}
	
	private void setIsElseIf(int index) {
		// TODO Auto-generated method stub
		if(pseudoCode.indexOf("ELSE IF", index)==index)
			isElseIf = true;
		else
			isElseIf = false;
	}

	private void handleEndIf(int index){
		//currentDisplayLevel++;
		//isEndIf = true;
		if(currentIndex<pseudoCode.length()){
			//pending: take action 
			if(joinNodes.size()>1){
				GraphNode join = new GraphNode("JOIN"+joinNo,"JOIN"+(joinNo++),false,false);
				//graphNodeMap.put(join, currentDisplayLevel);
				//do join all nodes with this node
				GraphNode matchingIf = ifStack.pop();
				Stack<GraphNode> removeStack = new Stack<GraphNode>();
				//System.out.println("Matching IF: "+matchingIf.blockName+" at level:"+graphNodeMap.get(matchingIf));
				for(int i=joinNodes.size()-1; i>=0; i--){
					//System.out.println("joinNode: "+ joinNodes.get(i).blockName +" at level:"+graphNodeMap.get(joinNodes.get(i)));
					if(joinNodes.get(i).isConditional && graphNodeMap.get(joinNodes.get(i)) >= graphNodeMap.get(matchingIf)){
						graphEdges.add(new EdgeNode(joinNodes.get(i),join,"FALSE"));
						//joinNodes.remove(i);
						removeStack.push(joinNodes.get(i));
						//System.out.println("removed");
					}
					else if(graphNodeMap.get(joinNodes.get(i)) >= graphNodeMap.get(matchingIf)){
						graphEdges.add(new EdgeNode(joinNodes.get(i),join,""));
						//joinNodes.remove(i);
						removeStack.push(joinNodes.get(i));
						//System.out.println("removed");
					}
				}
				
				if(removeStack.size()==1){
					graphEdges.remove(graphEdges.size()-1);
					removeStack.clear();
				}
				else{
					graphNodeMap.put(join, currentDisplayLevel);
					while(!removeStack.isEmpty()){
						joinNodes.remove(removeStack.pop());
					}
					joinNodes.add(join);
				}
						
				//endIfEdge = graphEdges.get(graphEdges.size()-1);
				//joinNodes.clear();
				//joinNodes.add(join);
			}
			currentIndex = index + 5;
			while(!Character.isLetter(pseudoCode.charAt(currentIndex)) && currentIndex<pseudoCode.length()-2){
				currentIndex++;
			}
			//pending: handle next token
			nextIndex = findNextToken(currentIndex);
			if(!nextToken.equalsIgnoreCase("")){
				if(nextIndex!=currentIndex){
					//handle basic block  -- may have problem for FALSE label here...
					handleBasicBlock(currentIndex,"");
				}else if(nextToken.equalsIgnoreCase("IF")){
					//handle if
					handleIf(currentIndex,"");
				}else if(nextToken.equalsIgnoreCase("LOOP")){
					//handle loop
					handleLoop(currentIndex,"");
				}else if(nextToken.equalsIgnoreCase("ELSE")){
					//handle else 
					handleElse(currentIndex);
				}else if(nextToken.equalsIgnoreCase("ENDIF")){
					//handle endif
					handleEndIf(currentIndex);
				}else if(nextToken.equalsIgnoreCase("EXIT")){
					//handle exit - error
					System.out.println("ERROR: EXIT detected just after ENDIF.");
				}else if(nextToken.equalsIgnoreCase("END LOOP")){
					//handle exit
					//lastEdge = (graphEdges.get(graphEdges.size()-1));
					//System.out.println("LAST EDGE: "+lastEdge.start.blockName+" to "+ lastEdge.end.blockName);
					handleEndLoop(currentIndex);
				}
			}			
		}
	}
	
	private void handleExit(int index){
		//currentDisplayLevel++;
		currentDisplayLevel--;
		if(currentIndex<pseudoCode.length()){
			//take action 
			if(!joinNodes.isEmpty()){
				exitNodes.add(joinNodes.get(joinNodes.size()-1));
				//System.out.println("CONNECT TO END: "+exitNodes.get(0).blockName);
				joinNodes.remove(joinNodes.size()-1);
			}
			currentIndex = index+4;
			while(!Character.isLetter(pseudoCode.charAt(currentIndex)) && currentIndex<pseudoCode.length()-2){
				currentIndex++;
			}
			//handle next token
			nextIndex = findNextToken(currentIndex);
			if(!nextToken.equalsIgnoreCase("")){
				if(nextIndex!=currentIndex){
					//handle basic block - error
					System.out.println("ERROR: Statements after EXIT detected. Will never be executed.");
				}else if(nextToken.equalsIgnoreCase("IF")){
					//handle if - error
					System.out.println("ERROR: IF Statement after EXIT detected. Will never be executed.");
				}else if(nextToken.equalsIgnoreCase("LOOP")){
					//handle loop - error
					System.out.println("ERROR: LOOP Statement after EXIT detected. Will never be executed.");
				}else if(nextToken.equalsIgnoreCase("ELSE")){
					//handle else
					handleElse(currentIndex);
				}else if(nextToken.equalsIgnoreCase("ENDIF")){
					//handle endif
					handleEndIf(currentIndex);
				}else if(nextToken.equalsIgnoreCase("EXIT")){
					//handle exit - error
					System.out.println("ERROR: EXIT detected just after EXIT.");
				}else if(nextToken.equalsIgnoreCase("END LOOP")){
					//handle exit - error
					System.out.println("ERROR: END LOOP detected just after EXIT.");
				}
			}			
		}
	}
	
	private void handleLoop(int index, String label){
		currentDisplayLevel++;
		if(currentIndex<pseudoCode.length()){
			//take action 
			extractCondition(index);
			GraphNode cb = new GraphNode(""+(blockNo++),nextCondition,true,true);
			graphNodeMap.put(cb, currentDisplayLevel);
			loopStack.push(cb);
			if(!joinNodes.isEmpty()){
				graphEdges.add(new EdgeNode(joinNodes.get(joinNodes.size()-1),cb,label));
				if(!label.equalsIgnoreCase("TRUE"))
					joinNodes.remove(joinNodes.size()-1);
			}
			joinNodes.add(cb);
			currentIndex = index+4+nextCondition.length()+1;
			while(!Character.isLetter(pseudoCode.charAt(currentIndex)) && currentIndex<pseudoCode.length()-2){
				currentIndex++;
			}
			//handle next token
			nextIndex = findNextToken(currentIndex);
			if(!nextToken.equalsIgnoreCase("")){
				if(nextIndex!=currentIndex){
					//handle basic block
					handleBasicBlock(currentIndex,"TRUE");
				}else if(nextToken.equalsIgnoreCase("IF")){
					//handle if
					handleIf(currentIndex,"TRUE");
				}else if(nextToken.equalsIgnoreCase("LOOP")){
					//handle loop
					handleLoop(currentIndex,"TRUE");
				}else if(nextToken.equalsIgnoreCase("ELSE")){
					//handle else - error
					System.out.println("ERROR: ELSE detected just after LOOP.");
				}else if(nextToken.equalsIgnoreCase("ENDIF")){
					//handle endif - error
					System.out.println("ERROR: ENDIF detected just after LOOP.");
				}else if(nextToken.equalsIgnoreCase("EXIT")){
					//handle exit - error
					System.out.println("ERROR: EXIT detected just after LOOP.");
				}else if(nextToken.equalsIgnoreCase("END LOOP")){
					//handle exit - error
					System.out.println("ERROR: END LOOP detected just after LOOP.");
				}
			}
			
		}
	}
	
	private void handleEndLoop(int index){
		currentDisplayLevel--;
		GraphNode temp = null;
		if(currentIndex<pseudoCode.length()){
			//take action 
			if(!joinNodes.isEmpty() && !loopStack.empty()){
				temp = loopStack.pop();
				if(joinNodes.get(joinNodes.size()-1).isConditional)
					graphEdges.add(new EdgeNode(joinNodes.get(joinNodes.size()-1),temp,"BACKEDGE-FALSE"));
				else
					graphEdges.add(new EdgeNode(joinNodes.get(joinNodes.size()-1),temp,"BACKEDGE"));
			}
			joinNodes.remove(joinNodes.size()-1);
			
			/*
			if(lastEdge!=null){
				joinNodes.add(lastEdge.start);
				graphEdges.remove(lastEdge);
				lastEdge = null;
			}*/
			currentIndex = index + 8;
			while(!Character.isLetter(pseudoCode.charAt(currentIndex)) && currentIndex<pseudoCode.length()-2){
				currentIndex++;
			}
			//handle next token
			nextIndex = findNextToken(currentIndex);
			if(!nextToken.equalsIgnoreCase("")){
				if(nextIndex!=currentIndex){
					//handle basic block
					handleBasicBlock(currentIndex,"");
				}else if(nextToken.equalsIgnoreCase("IF")){
					//handle if
					handleIf(currentIndex,"FALSE");
				}else if(nextToken.equalsIgnoreCase("LOOP")){
					//handle loop
					handleLoop(currentIndex,"FALSE");
				}else if(nextToken.equalsIgnoreCase("ELSE")){
					//handle else
					handleElse(currentIndex);
				}else if(nextToken.equalsIgnoreCase("ENDIF")){
					//handle endif
					if(temp!=null)
						joinNodes.add(temp);
					handleEndIf(currentIndex);
				}else if(nextToken.equalsIgnoreCase("EXIT")){
					//handle exit
					handleExit(currentIndex);
				}else if(nextToken.equalsIgnoreCase("END LOOP")){
					//handle end loop
					handleEndLoop(currentIndex);
				}
			}
			
		}		
	}
	
	private void handleBasicBlockElse(int index, String label){
		currentDisplayLevel++;
		if(currentIndex<pseudoCode.length()){
			//take action 
			extractBasicBlock(index, nextIndex);
			GraphNode bb = new GraphNode(""+(blockNo++),nextBasicBlock,false,false);
			graphNodeMap.put(bb, currentDisplayLevel);
			if(!joinNodes.isEmpty()){
				graphEdges.add(new EdgeNode(joinNodes.get(joinNodes.size()-2),bb,label));
				if(!label.equalsIgnoreCase("TRUE")){
					joinNodes.remove(joinNodes.size()-2);
				}
			}
			joinNodes.add(bb);
			currentIndex = nextIndex;
			//handle next token
			//nextIndex = findNextToken(currentIndex);
			if(!nextToken.equalsIgnoreCase("")){
				if(nextToken.equalsIgnoreCase("IF")){
					//handle if
					handleIf(currentIndex,"");
				}else if(nextToken.equalsIgnoreCase("LOOP")){
					//handle loop
					handleLoop(currentIndex,"");
				}else if(nextToken.equalsIgnoreCase("ELSE")){
					//handle else
					handleElse(currentIndex);
				}else if(nextToken.equalsIgnoreCase("ENDIF")){
					//handle endif
					handleEndIf(currentIndex);
				}else if(nextToken.equalsIgnoreCase("EXIT")){
					//handle exit
					handleExit(currentIndex);
				}else if(nextToken.equalsIgnoreCase("END LOOP")){
					//handle end loop
					handleEndLoop(currentIndex);
				}
			}
			
		}
	}
	
	private void handleBasicBlock(int index, String label){
		currentDisplayLevel++;
		if(currentIndex<pseudoCode.length()){
			//take action 
			extractBasicBlock(index, nextIndex);
			
			//separate node for separate line
			String[] separateLine = nextBasicBlock.split("\n");
			GraphNode bb = new GraphNode(""+(blockNo++),separateLine[0],false,false);
			graphNodeMap.put(bb, currentDisplayLevel);
			if(!joinNodes.isEmpty()){
				if(label.equalsIgnoreCase("") && joinNodes.get(joinNodes.size()-1).isConditional)
					graphEdges.add(new EdgeNode(joinNodes.get(joinNodes.size()-1),bb,"FALSE"));
				else
					graphEdges.add(new EdgeNode(joinNodes.get(joinNodes.size()-1),bb,label));
				if(!label.equalsIgnoreCase("TRUE")){
					joinNodes.remove(joinNodes.size()-1);
				}
			}
			GraphNode temp = bb;
			//joinNodes.add(bb);
			for(int i=1; i<separateLine.length; i++){
				currentDisplayLevel++;
				bb = new GraphNode(""+(blockNo++),separateLine[i],false,false);
				graphEdges.add(new EdgeNode(temp,bb,""));
				temp = bb;
				graphNodeMap.put(bb, currentDisplayLevel);
			}
			//GraphNode bb = new GraphNode(""+(blockNo++),nextBasicBlock,false,false);
			
			//graphNodeMap.put(bb, currentDisplayLevel);
			/*if(!joinNodes.isEmpty()){
				if(label.equalsIgnoreCase("") && joinNodes.get(joinNodes.size()-1).isConditional)
					graphEdges.add(new EdgeNode(joinNodes.get(joinNodes.size()-1),bb,"FALSE"));
				else
					graphEdges.add(new EdgeNode(joinNodes.get(joinNodes.size()-1),bb,label));
				if(!label.equalsIgnoreCase("TRUE")){
					joinNodes.remove(joinNodes.size()-1);
				}
			}*/
			joinNodes.add(bb);
			currentIndex = nextIndex;
			//handle next token
			//nextIndex = findNextToken(currentIndex);
			if(!nextToken.equalsIgnoreCase("")){
				if(nextToken.equalsIgnoreCase("IF")){
					//handle if
					handleIf(currentIndex,"");
				}else if(nextToken.equalsIgnoreCase("LOOP")){
					//handle loop
					handleLoop(currentIndex,"");
				}else if(nextToken.equalsIgnoreCase("ELSE")){
					//handle else
					handleElse(currentIndex);
				}else if(nextToken.equalsIgnoreCase("ENDIF")){
					//handle endif
					handleEndIf(currentIndex);
				}else if(nextToken.equalsIgnoreCase("EXIT")){
					//handle exit
					handleExit(currentIndex);
				}else if(nextToken.equalsIgnoreCase("END LOOP")){
					//handle end loop
					handleEndLoop(currentIndex);
				}
			}
			
		}
	}
	
	//---------functions to generate dot file------------------------
	
	public String generateDot(String fileName, String dotfile){
		
		this.dotfile = dotfile;
		//set colors
              
		colors.put(1,"white");
		colors.put(2,"white");
		colors.put(3,"white");
		//colors.put(4,"yellow");
		//colors.put(5,"cyan");
		//colors.put(6,"brown");
		//colors.put(7,"orange");
		
		HashMap<Integer,ArrayList<GraphNode>> allNodeByLevel = new HashMap<Integer,ArrayList<GraphNode>>();
		
		//Converting LinkedHashMap to level wise nodes. i.e LEVEL -> ALL NODES AT THAT LEVEL.
		
		for(Map.Entry<GraphNode,Integer> one : graphNodeMap.entrySet())
		{
			if(allNodeByLevel.get(one.getValue()) == null)
			{
				ArrayList<GraphNode> list = new ArrayList<GraphNode>();
				list.add(one.getKey());
				allNodeByLevel.put(one.getValue(), list);
			}
			else
			{
				ArrayList<GraphNode> list = allNodeByLevel.get(one.getValue());
				list.add(one.getKey());
				allNodeByLevel.put(one.getValue(), list);
			}
		}
		
		maxLevel = allNodeByLevel.size();
		Integer level = 0;
		
		//First create Nodes
		try {	 
			File file = new File(dotfile);
			PrintWriter out = new PrintWriter(file);
			out.write("digraph "+fileName+"{\n");
			
			
		for(Map.Entry<Integer,ArrayList<GraphNode>> one : allNodeByLevel.entrySet())
		{
			level = (maxLevel - one.getKey() + 1)*verticalDistance;
			
			ArrayList<GraphNode> allNodes = one.getValue();
			Integer mid = (allNodes.size() + 1) / 2 ;
			mid = mid - 1; //because index value starts from zero.
			System.out.println("mid:"+mid+" Size:"+allNodes.size());
			GraphNode node = allNodes.get(mid);
			node.y = level;
			node.x = (2*horizontalDistance);
			System.out.println("mid name:"+node.label);
			out.write(writeNode(node));
			
			Integer subadd = horizontalDistance;
			
			for(int i = mid-1;i>=0;i--)
			{
				node = allNodes.get(i);
				node.y = level;
				node.x = (2*horizontalDistance) - (subadd);
				out.write(writeNode(node));
				subadd = subadd + horizontalDistance;
			}
			
			subadd = horizontalDistance;
			
			for(int i = mid+1;i<allNodes.size();i++)
			{
				node = allNodes.get(i);
				node.y = level;
				node.x = (2*horizontalDistance) + (subadd);
				out.write(writeNode(node));
				subadd = subadd + horizontalDistance;
			}	
		}
		

			GraphNode node;
			for(Map.Entry<GraphNode,Integer> one : graphNodeMap.entrySet())
			{
				if(maxLevel < one.getValue())
				{
					maxLevel = one.getValue();
				}
			}
			
			//Second create Edges
			
			Iterator<EdgeNode> it = graphEdges.iterator();
			
			while(it.hasNext())
			{
				EdgeNode edge = it.next();
				System.out.println("EDGE LABLE:"+edge.start.label);
				out.write(writeEdge(edge));
			}
			
			out.write("}");
			out.close();
			
		} catch (FileNotFoundException e) {
			System.err.println("Problem in File Creation or Writing!!!!");
			e.printStackTrace();
		}
		
		return dotfile;
	}
	
	public String writeNode(GraphNode node)
	{
		 // nextInt is normally exclusive of the top value,
		 // so add 1 to make it inclusive
		 Integer randomNum = 1 + (int)(Math.random() * ((7 - 1) + 1));
		 String line;
		 if(node.blockName.equals("START") || node.blockName.equals("END") || node.blockName.contains("JOIN"))
		 {
			 line = " "+node.label + " [label = \""+ node.blockName+"\", pos=\""+node.x +","+node.y+"\", color=\""+colors.get(1)+"\", style=\"filled\" width=\""+widthRectangle+"\", height=\""+heightRectangle+"\"]";
		 }
		 else if(node.isConditional)
		 {
			 line = " " +node.label + "  [shape=diamond width=\""+widthDiamond+"\", height=\""+heightDiamond+"\"  label = \""+ node.blockName+"\", pos=\""+node.x +","+node.y+"\", color=\""+colors.get(2)+"\", style=\"filled\" width=\""+widthDiamond+"\", height=\""+heightDiamond+"\"]";
		 }
		 else
		 {
			 line = " "+node.label + "  [shape=box label = \""+ node.blockName.replace("\n", "\\n")+"\", pos=\""+node.x +","+node.y+"\", color=\""+colors.get(3)+"\", style=\"filled\" width=\""+widthRectangle+"\", height=\""+heightRectangle+"\"]";
		 }
		 System.out.println(line);
	   	 return line+"\n";
	}

	public String writeEdge(EdgeNode edge)
	{
		Integer edgeStartY = edge.start.y;
		Integer edgeEndY; 
		
		if(edge.end.isConditional)
		{
			edgeEndY = (int) (edge.end.y + diamondYAdjustment);
		}
		else
		{
			edgeEndY = (int) (edge.end.y + rectangleYAdjustment);
		}
		
		
		String line="";
		if(edge.label.equals("TRUE") || edge.label.equals("FALSE"))
		{
			line = " "+edge.start.label + " -> "+edge.end.label+" [label = " +"\""+edge.label+"\", lp=\""+(edge.start.x+edge.end.x)/2+","+(edge.start.y+edge.end.y)/2+"\", pos=\"e,"+edge.end.x+","+edgeEndY+" "+edge.start.x+","+edgeStartY+"\"]";
		}
		else if(edge.label.equals("BACKEDGE-FALSE"))
		{
			line = " "+edge.start.label + " -> "+edge.end.label+" [label = \"FALSE\", lp=\""+(edge.start.x+(horizontalDistance/2)+25)+","+(edgeStartY+rectangleYAdjustment+edgeEndY-diamondYAdjustment)/2+"\", pos=\"e,"+(edge.end.x+diamondXAdjustment)+","+(edgeEndY-diamondYAdjustment)+" "+(edge.start.x+rectangleXAdjustment-30)+","+(edgeStartY+rectangleYAdjustment-30)+" " +(edge.start.x+(horizontalDistance/2))+","+(edgeStartY+rectangleYAdjustment+edgeEndY-diamondYAdjustment)/2+"\"]";
		}
		else if(edge.label.equals("BACKEDGE"))
		{
			line = " "+edge.start.label + " -> "+edge.end.label+" [pos=\"e,"+(edge.end.x+diamondXAdjustment)+","+(edgeEndY-diamondYAdjustment)+" "+(edge.start.x+rectangleXAdjustment-30)+","+(edgeStartY+rectangleYAdjustment-30)+" " +(edge.start.x+(horizontalDistance/2))+","+(edgeStartY+rectangleYAdjustment+edgeEndY-diamondYAdjustment)/2+"\"]";
		}
		else
		{
			line = " "+edge.start.label + " -> "+edge.end.label+" [pos=\"e,"+edge.end.x+","+(edgeEndY)+" "+edge.start.x+","+(edgeStartY)+"\"]";
		}
		System.out.println(line+"\n");
		return line + "\n";
		
	}
}

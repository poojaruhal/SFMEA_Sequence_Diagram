/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sampleui;

/**
 *
 * @author Prateek_Sharma
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class Flags{
	boolean isALT = false;
	boolean isOPT = false;
	boolean isLOOP = false;
	boolean isBREAK = false;
	boolean isGuard = false;
	boolean isExtension = false;
	boolean isSpecification = false;
	boolean isOperand = false;
	boolean isElse = false;
	
	public Flags(boolean isALT, boolean isOPT, boolean isLOOP, boolean isBREAK,
			boolean isGuard, boolean isExtension, boolean isSpecification,
			boolean isOperand, boolean isElse) {
		super();
		this.isALT = isALT;
		this.isOPT = isOPT;
		this.isLOOP = isLOOP;
		this.isBREAK = isBREAK;
		this.isGuard = isGuard;
		this.isExtension = isExtension;
		this.isSpecification = isSpecification;
		this.isOperand = isOperand;
		this.isElse = isElse;
	}
	
};

public class ParseXMI {
	public String file_name;
	LinkedHashMap<String,String> messages = new LinkedHashMap<String,String>();
	LinkedHashMap<String,Boolean> messagesDisplayStatus = new LinkedHashMap<String,Boolean>();
	LinkedHashMap<String,Integer> pendingMessages = new LinkedHashMap<String,Integer>();
	boolean isSeqDetected = false;
	boolean isValidMessage = false;
	boolean isALT = false;
	boolean isOPT = false;
	boolean isLOOP = false;
	boolean isBREAK = false;
	boolean isGuard = false;
	boolean isExtension = false;
	boolean isSpecification = false;
	boolean isOperand = false;
	boolean isElse = false;
	boolean isStartMessage = true;
	int countOwnedMember = 0;
	int countOperand = 0;
	int countClosingOperand = 0;
	int currentNestedLevel = 0;
	String indent = "";
	String lastKey = "";
	String pseudoCode = "";
	String temp = "";
	Stack<Flags> flagStatus = new Stack<Flags>();
	Stack<Integer> indentCount = new Stack<Integer>();
	
   public String generatePseudoCode(String filename) {
	   
    try {

	SAXParserFactory factory = SAXParserFactory.newInstance();
	SAXParser saxParser = factory.newSAXParser();

	DefaultHandler handler = new DefaultHandler() {



	public void startElement(String uri, String localName,String qName, 
                Attributes attributes) throws SAXException {
		
		////system.out.println("Start Element :" + qName);
		
		if(qName.equals("ownedMember")){
			
			if(attributes.getType("xmi:type")!= null && attributes.getValue("xmi:type").equalsIgnoreCase("uml:Collaboration")){
				isSeqDetected = true;
				isValidMessage = true;
			}
			else if(attributes.getType("xmi:type")!= null && attributes.getValue("xmi:type").equalsIgnoreCase("uml:InteractionOperand")  && isSeqDetected){
				countOwnedMember++;
				if(isALT && countOperand>0){
					//system.out.print(indent+"ELSE ");
					//pseudoCode += indent+"ELSE ";
					temp += indent+"ELSE\n";
					isElse = true;
				}
				countOperand++;
				isOperand = true;
			}
			else if(attributes.getType("xmi:type")!= null && attributes.getValue("xmi:type").equalsIgnoreCase("uml:CombinedFragment")  && isSeqDetected){
				countOwnedMember++;
				//countOperand--;
				if(attributes.getType("interactionOperator")!= null && attributes.getValue("interactionOperator").equalsIgnoreCase("opt")){
					if(isOPT || isALT || isBREAK || isLOOP){
						indent+="\t";
						indentCount.push(new Integer(1));
						//push to stack all flags
						flagStatus.push(new Flags(isALT , isOPT , isLOOP , isBREAK , isGuard , isExtension , isSpecification , isOperand , isElse ));
					}
					isOPT = true; isALT = false; isBREAK = false; isLOOP = false; countOperand = 0; countClosingOperand = 0;
					currentNestedLevel++;
					////system.out.println("CombinedFragment: "+attributes.getValue("interactionOperator"));
				}
				else if(attributes.getType("interactionOperator")!= null && attributes.getValue("interactionOperator").equalsIgnoreCase("alt")){
					if(isOPT || isALT || isBREAK || isLOOP){
						indent+="\t";
						indentCount.push(new Integer(1));
						//push to stack all flags
						flagStatus.push(new Flags(isALT , isOPT , isLOOP , isBREAK , isGuard , isExtension , isSpecification , isOperand , isElse ));
					}
					isOPT = false; isALT = true; isBREAK = false; isLOOP = false; countOperand = 0; countClosingOperand = 0;
					currentNestedLevel++;
					////system.out.println("CombinedFragment: "+attributes.getValue("interactionOperator"));
				}
				else if(attributes.getType("interactionOperator")!= null && attributes.getValue("interactionOperator").equalsIgnoreCase("break")){
					if(isOPT || isALT || isBREAK || isLOOP){
						indent+="\t";
						indentCount.push(new Integer(1));
						//push to stack all flags
						flagStatus.push(new Flags(isALT , isOPT , isLOOP , isBREAK , isGuard , isExtension , isSpecification , isOperand , isElse ));
					}
					isOPT = false; isALT = false; isBREAK = true; isLOOP = false; countOperand = 0; countClosingOperand = 0;
					currentNestedLevel++;
					////system.out.println("CombinedFragment: "+attributes.getValue("interactionOperator"));
				}
				else if(attributes.getType("interactionOperator")!= null && attributes.getValue("interactionOperator").equalsIgnoreCase("loop")){
					if(isOPT || isALT || isBREAK || isLOOP){
						indent+="\t";
						indentCount.push(new Integer(1));
						//push to stack all flags
						flagStatus.push(new Flags(isALT , isOPT , isLOOP , isBREAK , isGuard , isExtension , isSpecification , isOperand , isElse ));
					}
					isOPT = false; isALT = false; isBREAK = false; isLOOP = true; countOperand = 0; countClosingOperand = 0;
					currentNestedLevel++;
					////system.out.println("CombinedFragment: "+attributes.getValue("interactionOperator"));
				}
			}
		}
		
		if(qName.equals("guard") && isSeqDetected){
			
			isGuard = true;
		}
		
		if(qName.equals("specification") && isGuard && isSeqDetected){
			
			if(attributes.getType("value")!= null ){
				//take new block in temporary string and load messages in between current and previous block when a new message is detected.
				isSpecification = true;
				////system.out.println("specification: "+indent+"IF "+attributes.getValue("value")+" THEN");
				//countOperand--;
				if(isOPT || isALT || isBREAK){
					if(isElse){
						//system.out.println("IF "+attributes.getValue("value")+" THEN");
						//pseudoCode += "IF "+attributes.getValue("value")+" THEN\n";
						indent+="\t";
						temp += indent+"IF "+attributes.getValue("value")+" THEN\n";
						isElse = false;
						indent+="\t";
						indentCount.push(new Integer(2));
					}
					else{
						//system.out.println(indent+"IF "+attributes.getValue("value")+" THEN");
						//pseudoCode += indent+"IF "+attributes.getValue("value")+" THEN\n";
						temp += indent+"IF "+attributes.getValue("value")+" THEN\n";
						indent+="\t";
						indentCount.push(new Integer(1));
					}
				}
				else if(isLOOP){
					if(isElse){
						indent+="\t";
						//system.out.println("\n"+indent+"LOOP "+attributes.getValue("value"));
						//pseudoCode += "\n"+indent+"LOOP "+attributes.getValue("value")+"\n";
						//temp += "\n"+indent+"LOOP "+attributes.getValue("value")+"\n";
						temp += indent+"WHILE "+attributes.getValue("value")+"\n";
						isElse = false;
					}
					else{
						//system.out.println(indent+"LOOP "+attributes.getValue("value"));
						//pseudoCode += indent+"LOOP "+attributes.getValue("value")+"\n";
						temp += indent+"WHILE "+attributes.getValue("value")+"\n";
					}
					indent+="\t";
					indentCount.push(new Integer(1));
				}
				
			}
		}
		
		if(qName.equals("xmi:Extension") && !isGuard && isSeqDetected && countOwnedMember>0){
			isExtension = true;
		}
		
		//process message tag
		
		if(qName.equals("message") && isSeqDetected ){
			
			if(!isExtension && isValidMessage){
				messages.put(attributes.getValue("xmi:id"),attributes.getValue("name"));
				messagesDisplayStatus.put(attributes.getValue("xmi:id"),false);
				////system.out.println("Message Added:"+attributes.getValue("name")); 
			}
			else{
				if(isStartMessage){
					String startingMessages = "";
					isStartMessage = false;
					for(Entry<String, String> it : messages.entrySet()){
						if(it.getKey().equalsIgnoreCase(attributes.getValue("xmi:value"))){
							break;
						}
						if(isAllowedToPrint(it.getKey())){
							startingMessages+=it.getValue()+"\n";
							messagesDisplayStatus.put(it.getKey(), true);							
						}
						else{
							//working here
							printPendingMessages();
							pendingMessages.put(it.getKey(), currentNestedLevel);
						}
						//messages.remove(it.getKey());
					}
					pseudoCode = startingMessages+pseudoCode;
					startingMessages = "";
				}
				if(!temp.equals("")){
					boolean startAddingMessages = false;
					//-------------
					if(startAddingMessages && isAllowedToPrint(attributes.getValue("xmi:value"))){
			    		temp = temp+indent+messages.get(attributes.getValue("xmi:value"))+" here \n";
			    		//pseudoCode = pseudoCode+indent+it.getValue()+"\n";
			    		messagesDisplayStatus.put(attributes.getValue("xmi:value"), true);
			    	}else if(startAddingMessages){
			    		printPendingMessages();
						pendingMessages.put(attributes.getValue("xmi:value"), currentNestedLevel);
			    	}
					//-------------
				    /*for(Entry<String, String> it : messages.entrySet()){
				    	//if(lastKey==""){
				    	//	startAddingMessages = true;
				    	//}
				    	if(it.getKey().equalsIgnoreCase(attributes.getValue("xmi:value"))){
				    		startAddingMessages = false;
					    }
				    	//working here
				    	if(startAddingMessages && isAllowedToPrint(it.getKey())){
				    		temp = temp+indent+it.getValue()+" here \n";
				    		//pseudoCode = pseudoCode+indent+it.getValue()+"\n";
				    		messagesDisplayStatus.put(it.getKey(), true);
				    	}else if(startAddingMessages){
				    		printPendingMessages();
							pendingMessages.put(it.getKey(), currentNestedLevel);
				    	}
				    	if(it.getKey().equalsIgnoreCase(lastKey)){
				    		startAddingMessages = true;
				    	}
				    }*/
					pseudoCode += temp;
					temp = "";
				}
				//Need to work here and decrement currentnestedcount 
				////system.out.println("Message id:"+indent+attributes.getValue("xmi:value"));
				if(isElse){
					//system.out.println("\n\t"+indent+messages.get(attributes.getValue("xmi:value")));
					//pseudoCode += "\n\t"+indent+messages.get(attributes.getValue("xmi:value"))+"\n";
					if(isAllowedToPrint(attributes.getValue("xmi:value"))){
						pseudoCode += "\t"+indent+messages.get(attributes.getValue("xmi:value"))+"\n";
						messagesDisplayStatus.put(attributes.getValue("xmi:value"), true);							
					}
					else{
						//working here
						printPendingMessages();
						pendingMessages.put(attributes.getValue("xmi:value"), currentNestedLevel);
					}
					//pseudoCode += "\t"+indent+messages.get(attributes.getValue("xmi:value"))+"\n";
					isElse = false;
				}
				else{
					//system.out.println(indent+messages.get(attributes.getValue("xmi:value")));
					if(isAllowedToPrint(attributes.getValue("xmi:value"))){
						pseudoCode += indent+messages.get(attributes.getValue("xmi:value"))+"\n";
						messagesDisplayStatus.put(attributes.getValue("xmi:value"), true);
					}
					else{
						//working here
						printPendingMessages();
						pendingMessages.put(attributes.getValue("xmi:value"), currentNestedLevel);
					}
					//pseudoCode += indent+messages.get(attributes.getValue("xmi:value"))+"\n";
				}
				lastKey = attributes.getValue("xmi:value");
				//messages.remove(attributes.getValue("xmi:value"));
			}
		}
		
		if(qName.equals("fragment") && isSeqDetected && !messages.isEmpty()){
			isValidMessage = false;
			if(attributes.getType("interactionOperator")!= null && attributes.getValue("interactionOperator").equalsIgnoreCase("opt")){
				flagStatus.push(new Flags(isALT , isOPT , isLOOP , isBREAK , isGuard , isExtension , isSpecification , isOperand , isElse ));
				isOPT = true; isALT = false; isBREAK = false; isLOOP = false; countOperand = 0; countClosingOperand = 0;
				currentNestedLevel++;
				////system.out.println("fragment: "+attributes.getValue("interactionOperator"));
			}
			else if(attributes.getType("interactionOperator")!= null && attributes.getValue("interactionOperator").equalsIgnoreCase("alt")){
				flagStatus.push(new Flags(isALT , isOPT , isLOOP , isBREAK , isGuard , isExtension , isSpecification , isOperand , isElse ));
				isOPT = false; isALT = true; isBREAK = false; isLOOP = false; countOperand = 0; countClosingOperand = 0;
				currentNestedLevel++;
				////system.out.println("fragment: "+attributes.getValue("interactionOperator"));
			}
			else if(attributes.getType("interactionOperator")!= null && attributes.getValue("interactionOperator").equalsIgnoreCase("break")){
				flagStatus.push(new Flags(isALT , isOPT , isLOOP , isBREAK , isGuard , isExtension , isSpecification , isOperand , isElse ));
				isOPT = false; isALT = false; isBREAK = true; isLOOP = false; countOperand = 0; countClosingOperand = 0;
				currentNestedLevel++;
				////system.out.println("fragment: "+attributes.getValue("interactionOperator"));
			}
			else if(attributes.getType("interactionOperator")!= null && attributes.getValue("interactionOperator").equalsIgnoreCase("loop")){
				flagStatus.push(new Flags(isALT , isOPT , isLOOP , isBREAK , isGuard , isExtension , isSpecification , isOperand , isElse ));
				isOPT = false; isALT = false; isBREAK = false; isLOOP = true; countOperand = 0; countClosingOperand = 0;
				currentNestedLevel++;
				////system.out.println("fragment: "+attributes.getValue("interactionOperator"));
			}
		}
		
	}

	private void printPendingMessages() {
		// TODO Auto-generated method stub
		if(!pendingMessages.isEmpty()){
		for(Entry<String, Boolean> it : messagesDisplayStatus.entrySet()){
			if(it.getValue().booleanValue()==false){
				if(!pendingMessages.isEmpty() && pendingMessages.containsKey(it.getKey()) && pendingMessages.get(it.getKey())==currentNestedLevel){
					if(!temp.equals(""))
						temp += indent+messages.get(it.getKey())+"\n";
					else
						pseudoCode += indent+messages.get(it.getKey())+"\n";
					messagesDisplayStatus.put(it.getKey(), true);
					pendingMessages.remove(it.getKey());
				}
				else{
					break;
				}
			}
		}
		}
	}

	private boolean isAllowedToPrint(String key) {
		// TODO Auto-generated method stub
		for(Entry<String, Boolean> it : messagesDisplayStatus.entrySet()){
			if(it.getKey().equalsIgnoreCase(key)){
				break;
			}
			if(it.getValue().booleanValue()==false){
				return false;
			}
		}
		if(messagesDisplayStatus.get(key))
			return false;
		else
			return true;
	}

	public void endElement(String uri, String localName,
		String qName) throws SAXException {

		if(qName.equals("ownedMember")){
			countOwnedMember--;
			if(countOperand>0){
				countClosingOperand++;
			}
			////system.out.println("diff:"+(countOperand-countClosingOperand));
			if((countOperand-countClosingOperand) == -1){
				countClosingOperand=countOperand-1;
				if(isALT || isOPT){
					//system.out.println(indent+"ENDIF");
					pseudoCode += indent+"ENDIF"+"\n";
					if (indent.length() > 0) {
						indent = indent.substring(0, indent.length()-indentCount.pop());
					}
				}
				else if(isBREAK){
					//system.out.println(indent+"\tEXIT");
					pseudoCode += indent+"\tEXIT"+"\n";
					//system.out.println(indent+"ENDIF");
					pseudoCode += indent+"ENDIF"+"\n";
					if (indent.length() > 0) {
						indent = indent.substring(0, indent.length()-indentCount.pop());
					}
				}
				else if(isLOOP){
					//system.out.println(indent+"END LOOP");
					pseudoCode += indent+"END WHILE"+"\n";
					if (indent.length() > 0) {
						indent = indent.substring(0, indent.length()-indentCount.pop());
					}
				}
				
				currentNestedLevel--;
				printPendingMessages();
				
				if(!flagStatus.empty())
					reloadFlags((Flags) flagStatus.pop());
			}
			
			if(isSpecification){
				isSpecification = false;
				if (indent.length() > 0) {
					indent = indent.substring(0, indent.length()-indentCount.pop());
				}
			}
			
			if(isOperand){
				isOperand = false;
			}
			
			
		}
		
		if(qName.equals("guard")){	
			isGuard = false;
			
		}
		
		if(qName.equals("xmi:Extension")){
			isExtension = false;
		}
		
		if(qName.equals("fragment")){
			
		}

	}

	private void reloadFlags(Flags flag) {
		// TODO Auto-generated method stub
		//flag = (Flags)flag;
		isALT = 			flag.isALT;
		isOPT = 			flag.isOPT;
		isLOOP = 			flag.isLOOP;
		isBREAK = 			flag.isBREAK;
		isGuard = 			flag.isGuard;
		isExtension = 		flag.isExtension;
		isSpecification = 	flag.isSpecification;
		isOperand = 		flag.isOperand;
		isElse = 			flag.isElse;
	}

	public void characters(char ch[], int start, int length) throws SAXException {

	}

     };
       
     
     saxParser.parse(filename, handler);
     
       
       boolean startAddingMessages = false;
       for(Entry<String, String> it : messages.entrySet()){
    	   if(lastKey==""){
	    		startAddingMessages = true;
	    	}
    	   if(startAddingMessages && !messagesDisplayStatus.get(it.getKey())){
    		   pseudoCode = pseudoCode+it.getValue()+"\n";
    	   }
    	   if(it.getKey().equalsIgnoreCase(lastKey)){
    		   startAddingMessages = true;
    	   }
       }
       
       System.out.println(pseudoCode);
 
     } catch (Exception e) {
       e.printStackTrace();
     }
	return pseudoCode;
  
   }



}
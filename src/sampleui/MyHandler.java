/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sampleui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MyHandler extends DefaultHandler {
    private List<MessageBean> msgList;
    MessageBean msg;
    
    private Map<String,List<String>> life_actMap = new HashMap<String,List<String>>();
    
    String msgName;
    String type;
    boolean isReply;
    String tag,senderObj,receiverObj,toActivation,fromActivation,objectName,lifeLineID;
    String sendEvent;
    String receiveEvent;
    int seqNo=1;
    
    boolean lifeline = false;
    boolean messageFlag = false;
    boolean fromActivationFlag = false;
    boolean toActivationFlag = false;
    boolean lifeLine=false;
    List<String> actList;
    
    
    public List<MessageBean> getMsgList() {
        int count = seqNo;
        return msgList.subList(0, count-1);
        
        
        //return msgList;
    }
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            if(qName.equalsIgnoreCase("message"))
            {
               if(msgList==null)
                msgList=new ArrayList<MessageBean>();
               
                if(attributes.getValue("name")!=null){
                    messageFlag=true;
                 msg=new MessageBean();                 
                 System.out.println("seq no is "+seqNo);
                 msg.setSeqNo(seqNo);
                 seqNo++;
                 msgName=attributes.getValue("name");
                
                    System.out.println("in message attribute "+msgName);
                msg.setMessageName(msgName);
                }
                //sendEvent=attributes.getValue("sendEvent");
               // receiveEvent=attributes.getValue("receiveEvent");
                msg.setFrom("from");
                msg.setTo("to");         
                
            }
            
            else if(qName.equals("fromActivation")){
                fromActivationFlag=true;
            }
            else if(qName.equals("toActivation")){
                toActivationFlag=true;
            }
            
            else if(messageFlag && qName.equals("activation")){
                
                if(fromActivationFlag){
                    fromActivation = attributes.getValue("xmi:value");
                    msg.setFromActivation(fromActivation);
                }
                
                if(toActivationFlag){
                    toActivation = attributes.getValue("xmi:value");
                    msg.setToActivation(toActivation);
                }               
            }
            
            else if(qName.equals("lifeline"))
            {
                
                if(attributes.getValue("xmi:id")!=null){
                 lifeLine=true;
                 actList=new ArrayList<String>();
                 objectName=attributes.getValue("name");
                 lifeLineID=attributes.getValue("xmi:id");
                 if(attributes.getValue("represents")!=null){
                 //lifeline_represent=attributes.getValue("represents");
                 System.out.println("lieline id is "+lifeLineID);
                }}}
            
            else if(qName.equals("activation") && lifeLine)
            {
               actList.add(attributes.getValue("xmi:id"));
            }
            
            else if(qName.equalsIgnoreCase("signature"))
            {
             if(attributes.getValue("xmi:type").equalsIgnoreCase("uml:ReplyAction"))
                {
                    isReply=true;
                }
                else
                  isReply=false;
                msg.setIsReply(isReply);
            }
            else if(qName.equalsIgnoreCase("number"))
            {
                tag=attributes.getValue("xmi:value");
                msg.setTag(tag);
            }
            else if(qName.equalsIgnoreCase("asynshronous"))
            {
                if(attributes.getValue("xmi:value").equalsIgnoreCase("false"))
                    type="Synchronous";
                else
                    type="Asynchronous";
                   msg.setType(type);
              }
        }
     public void endElement(String uri, String localName, String qName) throws SAXException{
         
         if(qName.equals("lifeline") && lifeLine)
         {
             getLife_actMap().put(objectName, actList);
             lifeLine = false;
             System.out.println("hashmap size is "+getLife_actMap().size());          
         }
         
         if(qName.equalsIgnoreCase("message")){
           
            msgList.add(msg);
             if(messageFlag){
            messageFlag=false;
         
        }
        }
        if(qName.equals("fromActivation"))
            fromActivationFlag=false;
    
        if(qName.equals("toActivation"))
            toActivationFlag=false;    
     }

    /**
     * @return the life_actMap
     */
    public Map<String,List<String>> getLife_actMap() {
        return life_actMap;
    }
    }
            
        
    


package com.nothing.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nothing.object.Message;
import com.nothing.object.Users;

public class MapFactory {
	
	/**  �����û��б� */
	public static String onlineUsers = "";

	/** �û������϶��� */
	public static Map<String, Users> userMap = new HashMap<String, Users>();
	
	/** Ⱥ��Ϣ��Ŷ��� */
	public static List<Message> groupMsgList = new ArrayList<Message>();
	/** ��ֹ�����޸��쳣 */
	/*public synchronized static void removeGroupMsg(Message msg){
		groupMsgList.remove(msg);
	}*/
	
//	public static Vector<Message> groupMsgList = new Vector<Message>();
	
	/*public static List<Message> getGroupMsgList() {
		return groupMsgList;
	}

	public synchronized static void addGroupMsg(Message msg){
		groupMsgList.add(msg);
	}
	
	public synchronized static void removeGroupMsg(Message msg){
		groupMsgList.remove(msg);
	}
	
	public synchronized static int sizeGroupMsg(){
		return groupMsgList.size();
	}
	
	public synchronized static List<Message> listGroupMsg(){
		List<Message> l = new ArrayList<Message>();
		for(Message m:groupMsgList){
			l.add(m);
		}
		return l;
	}*/
}

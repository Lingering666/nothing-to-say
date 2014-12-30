package com.nothing.s.socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.nothing.factory.UIFactory;
import com.nothing.global.MSGType;
import com.nothing.global.Var;
import com.nothing.object.Message;
import com.nothing.util.Tools;

public class StoCThread extends Thread {
	
	public Socket s;
	public ObjectOutputStream oos = null;
	public ObjectInputStream ois = null;
	public boolean bConnect = false;
	
	public StoCThread(Socket s){
		this.s = s;
		bConnect = true;
		/*try {
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			bConnect = true;
		} catch (IOException e) {
			System.out.println("clients exit!");
//			e.printStackTrace();
		}*/
	}
	
	public void send(Message m){
		StoCThread sc = ManageClientThread.getCurrentClientThread(m.getRecver());
		try {
			oos = new ObjectOutputStream(sc.s.getOutputStream());
			oos.writeObject(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			while(bConnect){
				ois = new ObjectInputStream(s.getInputStream());
				Object o = ois.readObject();
				if(o instanceof Message){
					Message m = (Message)o;
					if(m.getMsgType() == MSGType.TEXTMSG){
						//���������жϽ������Ƿ�����
						if(ManageClientThread.reLogin(m.getRecver())){
							Tools.addMessage(UIFactory.monitor, "�յ���Ϣ��:"+m.getSender()+"��"+m.getRecver()+"˵:"+m.getMsg()+Tools.getnow());
							this.send(m);
						}else{
							ManageClientThread.msgList.add(ManageClientThread.msgList.size(), m);
							Tools.addMessage(UIFactory.monitor, m.getRecver()+"�����ߣ��ݴ���������Ϣ��"+Tools.getnow());
//							System.out.println("������Ϣ:StoCThread:msg.size="+ManageClientThread.msgList.size());
						}
					}else if(m.getMsgType() == MSGType.SYSEXIT){
						ManageClientThread.removeCurrentClientThread(m.getSender());
						//���ص�ǰ�������б����¡�
						ManageClientThread.notifyOthers(m.getSender(), MSGType.ONLINEUSERS, Var.OFFLINE);
						Tools.addMessage(UIFactory.monitor, m.getSender()+"�˳�ϵͳ��"+Tools.getnow());
						System.out.println(m.getSender()+"�˳�ϵͳ��");
					}else if(m.getMsgType() == MSGType.GROUPMSG){
//						String recvers = m.getRecver();
						//��ȡȺ������������list
						List<String> onlineRecver = Tools.getAllSameElement(ManageClientThread.getOnlineUserIDs(), m.getRecver());
//						System.out.println("StoCThread.Ⱥ��Ϣ�������б�:"+onlineRecver.toString());
//						System.out.println("StoCThread."+onlineRecver.contains(Nothing.uID)+"--"+Nothing.uID);
//						if(onlineRecver.contains(Nothing.uID)){
//							onlineRecver.remove(Nothing.uID);
//						}
//						System.out.println("StoCThread.Ⱥ��Ϣ�������б��Լ������գ�:"+onlineRecver.toString());
						for(String recver:onlineRecver){
							m.setRecver(recver);
							this.send(m);
						}
						
					}else if(m.getMsgType() == MSGType.SENDFILEREQUEST || m.getMsgType() == MSGType.SENDFILERESPONSE){
						if(ManageClientThread.hm.containsKey(m.getRecver())){
							if(m.getMsgType() == MSGType.SENDFILEREQUEST){
								String ip = ManageClientThread.getCurrentClientThread(m.getRecver()).s.getInetAddress().toString().substring(1);
								m.setComment(ip);
							}
							this.send(m);
						}else{
							//�����ӵ����ط���������
							String sender = m.getSender();
							m.setSender(m.getRecver());
							m.setRecver(sender);
							m.setMsg("0");
							this.send(m);
						}
					}else if(m.getMsgType() == MSGType.VOICECHATREQUEST || m.getMsgType() == MSGType.VOICECHATRESPONSE){
						if(ManageClientThread.hm.containsKey(m.getRecver())){
							if(m.getMsgType() == MSGType.VOICECHATREQUEST){
								String ip = ManageClientThread.getCurrentClientThread(m.getRecver()).s.getInetAddress().toString().substring(1);
								m.setComment(ip);
							}
							this.send(m);
						}else{
							//�����ӵ����ط���������
							String sender = m.getSender();
							m.setSender(m.getRecver());
							m.setRecver(sender);
							m.setMsg("0");
							this.send(m);
						}
					}else if(m.getMsgType() == MSGType.VOICECHATINTERRUPT){
						//��������Ƶ�ж�֪ͨ
						System.out.println("�ж�����");
						String sender = m.getSender();
						m.setSender(m.getRecver());
						m.setRecver(sender);
						this.send(m);
					}else if(m.getMsgType() == MSGType.LOVEINTHEHOUSE){
						this.send(m);
					}
				}else if(o instanceof String){
					System.out.println("������յ��ִ���Ϣ��"+o.toString());
				}else{
					Tools.alert("δ֪��Ϣ��ʽ��");
				}
			}
		} catch (IOException e) {
			System.out.println("Client is exit!");
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
	}
}

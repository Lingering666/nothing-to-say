package com.nothing.c.socket;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.nothing.c.filetrans.ProgressView;
import com.nothing.c.filetrans.ReceiveProgress;
import com.nothing.c.filetrans.RecvFile;
import com.nothing.c.filetrans.SendFile;
import com.nothing.c.filetrans.SendProgress;
import com.nothing.c.tree.UserListOpera;
import com.nothing.c.voice.VoiceClient;
import com.nothing.c.voice.VoiceServer;
import com.nothing.clients.ChatForm;
import com.nothing.clients.GroupForm;
import com.nothing.clients.LoveShow;
import com.nothing.clients.ManageChatForm;
import com.nothing.clients.Nothing;
import com.nothing.factory.DBFactory;
import com.nothing.factory.MapFactory;
import com.nothing.factory.UIFactory;
import com.nothing.global.MSGType;
import com.nothing.global.Var;
import com.nothing.object.Message;
import com.nothing.object.Users;
import com.nothing.util.Tools;
import com.nothing.util.fadeOut;

public class CtoSThread extends Thread{

	public Socket s;
	public ObjectOutputStream oos = null;
	public ObjectInputStream ois = null;
	public boolean bConnect = false;
	//nothing tmp
	RecvFile rf = null;
	public CtoSThread(){
		
	}
	public CtoSThread(Socket s){
		this.s = s;
		bConnect = true;
		/*try {
			oos = new ObjectOutputStream(s.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public void send(Message m){
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
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
						String cfID = m.getRecver()+"-"+m.getSender();
						if(ManageChatForm.isInMap(cfID)){
//							System.out.println("------------------1:CtoS");
							ChatForm cf = ManageChatForm.getChatFormByID(m.getRecver()+"-"+m.getSender());
							Tools.addMessage(cf.tpRecv, m.getSender(), m.getRecver(), m.getMsg());
							System.out.println("revt PACK:"+m.getSender()+"-"+m.getRecver()+":"+m.getMsg());
						}else{
//							System.out.println("------------------2:CtoS");
							ManageChatForm.msgList.add(ManageChatForm.msgList.size(), m);
							Client.setHasMsg(ManageChatForm.msgList);
							System.out.println("m:"+m.getSender()+"--"+m.getRecver()+":"+m.getMsg());
						}
					}else if(m.getMsgType() == MSGType.GROUPMSG){
						String gID = m.getComment();
//						System.out.println("CtoSThread.gID-list:"+UIFactory.groupFormMap.toString());
						if(UIFactory.groupFormMap.containsKey(gID)){
							GroupForm gf = UIFactory.groupFormMap.get(gID);
							Tools.addMessage(gf.tpRecv, m.getSender(), m.getMsg());
							System.out.println(gID+"Ⱥ��Ϣ��"+m.getSender()+" ˵��"+m.getMsg());
						}else{
							MapFactory.groupMsgList.add(MapFactory.groupMsgList.size(),m);
							Client.setHasMsg(MapFactory.groupMsgList);
							System.out.println(gID+"����δ��-Ⱥ��Ϣ��"+m.getSender()+" ˵��"+m.getMsg());
						}
					}else if(m.getMsgType() == MSGType.ONLINEUSERS){
//						UserListOpera ul = ManageUserlist.getUserListByID(m.getSender());
						UserListOpera ul = UIFactory.nothingTree;
						ul.setOnlineUsers(m.getMsg());
//						Client.hmONline.put(m.getSender(), m.getMsg());
						MapFactory.onlineUsers = m.getMsg();
//						System.out.println("CtoSThread.���ߺ��ѣ�"+MapFactory.onlineUsers);
						UserListOpera.tree.repaint();
						//nothing
//						System.out.println(Nothing.uID+"�������ִ��ڣ�"+m.getRecver());
						if(!Nothing.uID.equals(m.getRecver())){
							if(m.getStates() == Var.ONLINE){
								fadeOut fade = new fadeOut("����������ʾ","��ĺ���"+m.getRecver()+"�Ѿ����ߣ�");
								fade.showDlg();
							}
						}
					}else if(m.getMsgType() == MSGType.SENDFILEREQUEST){
						ChatForm cf = ManageChatForm.getChatFormByID(m.getRecver()+"-"+ m.getSender());
						Users user = DBFactory.getUserByID(m.getSender());
						cf.headBtn.setIcon(Tools.getImageIcon(Var.HEADIMGPATH+user.getuHeadImg(), 30, 30));
						cf.lblGameOver.setText(user.getuNickName());
						cf.lblSomeWords.setText(user.getuWords());
						if(m.getMsg().trim().equals("0")){
							cf.tpRecv.setText(cf.tpRecv.getText()+"�Է������ߣ������ļ�ʧ�ܣ�");
						}else{
							String fileInfos[] = m.getMsg().split(",");
							String cfid = m.getRecver()+"-"+m.getSender();
							cf.setVisible(true);
//							cf.setFocusableWindowState(true);
//							ManageChatForm.ikey(1);
							
							//�����ߡ������ߵߵ������ظ�������
							String sender = m.getSender();
							m.setSender(m.getRecver());
							m.setRecver(sender);
							
							String fileInfo = "�����ļ�����\n�ļ���:"+fileInfos[0]+"\n�ļ���С:"+fileInfos[2];
							int option = JOptionPane.showOptionDialog(cf, fileInfo, "�ļ�����", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
							if(option == JOptionPane.YES_OPTION){
								JFileChooser fc = new JFileChooser();
								fc.setSelectedFile(new File(fileInfos[0]));
								int re = fc.showSaveDialog(cf);
								if(re == JFileChooser.APPROVE_OPTION){
									System.out.println(m.getRecver()+"ͬ������ļ���"+fileInfos[0]);
									File file = fc.getSelectedFile();
									long size = (long) ((Float.parseFloat(fileInfos[2].substring(0, fileInfos[2].length() - 2)) * 1000) - 4);
									
									//nothing
									ReceiveProgress rpro = new ReceiveProgress(file, size);
									// ��ʾ�ļ����ս���
									ProgressView pview = new ProgressView(file, rpro, "receive", cfid);
									pview.initialize();
									cf.fileTransView(pview);
//									ManageChatForm.addProgressView(cfid, pview);
//									cf.repaint();
									//nothing tmp��ʱ����취
									if(!ManageChatForm.b){
										rf = new RecvFile(cfid, file);
										rf.start();
										ManageChatForm.b = true;
									}else{
										rf.setFile(file);
									}
									m.setMsgType(MSGType.SENDFILERESPONSE);
									m.setStates(Var.YES);
									this.send(m);
								}else{
									m.setMsgType(MSGType.SENDFILERESPONSE);
									m.setStates(Var.NO);
									this.send(m);
									Tools.addMessage(cf.tpRecv, "��ܾ���"+sender+"�����ļ���"+fileInfos[0]);
								}
							}else{
								m.setMsgType(MSGType.SENDFILERESPONSE);
								m.setStates(Var.NO);
								this.send(m);
								Tools.addMessage(cf.tpRecv, "��ܾ���"+sender+"�����ļ���"+fileInfos[0]);
							}
						}
					}else if(m.getMsgType() == MSGType.SENDFILERESPONSE){
						String cfid = m.getRecver()+"-"+m.getSender();
						ChatForm cf = ManageChatForm.getChatFormByID(cfid);
						System.out.println("cfid="+cfid);
						System.out.println(m.getSender()+"�յ��ļ�����Ӧ��");
						String fileInfos[] = m.getMsg().split(",");
						if(m.getStates() == Var.YES){
							System.out.println("�Է�ͬ������ļ�");
							String filePath = fileInfos[1];
//							System.out.println(fileInfos[0]+"-"+fileInfos[1]);
							File file = new File(filePath);
							String ip = m.getComment();
							SendFile sf = new SendFile(file, cfid, ip);
							sf.start();// ��ʾ�ļ����ͽ���
//							ManageChatForm.operaFTCount(cfid, "+");		//����ͬ�ⷢ���ļ��󽫼�����һ
//							System.out.println(cfid+":��ǰ����ͬʱ�����ļ�������"+ManageChatForm.getFTCount(cfid));
							SendProgress spro = new SendProgress(sf, file.length());
							ProgressView pview = new ProgressView(file, spro, "send", cfid);
							pview.initialize();
							cf.fileTransView(pview);
//							ManageChatForm.addProgressView(cfid, pview);
							/*System.out.println("send:"+pview.progress.getProgress());
							if(pview.progress.getProgress() == 100){
								System.out.println("-----------");
								cf.defaultView(pview);
							}*/
						}else{
							Tools.addMessage(cf.tpRecv, "�Է��ܾ������ļ�:"+fileInfos[0]);
						}
					}else if(m.getMsgType() == MSGType.VOICECHATREQUEST){
						ChatForm cf = ManageChatForm.getChatFormByID(m.getRecver()+"-"+ m.getSender());
						Users user = DBFactory.getUserByID(m.getSender());
						cf.headBtn.setIcon(Tools.getImageIcon(Var.HEADIMGPATH+user.getuHeadImg(), 30, 30));
						cf.lblGameOver.setText(user.getuNickName());
						cf.lblSomeWords.setText(user.getuWords());
						if(m.getMsg()!=null && m.getMsg().trim().equals("0")){
							Tools.addMessage(cf.tpRecv, "�Է������ߣ���������ʧ�ܣ�");
						}else{
							String cfid = m.getRecver()+"-"+m.getSender();
							cf.setVisible(true);
//							cf.setFocusableWindowState(true);
//							ManageChatForm.ikey(1);
							
							//�����ߡ������ߵߵ������ظ�������
							String sender = m.getSender();
							m.setSender(m.getRecver());
							m.setRecver(sender);
							
							String voiceInfo = sender+"���������������ͨ����";
							int option = JOptionPane.showOptionDialog(cf, voiceInfo, "����ͨ������", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
							if(option == JOptionPane.OK_OPTION){
								VoiceServer vs = new VoiceServer(cfid, 9200);
								vs.start();
								//nothing
//								ManageVoiceThread.addVoiceServerThread(cfid, VoiceServer.currentThread());
								m.setMsgType(MSGType.VOICECHATRESPONSE);
								m.setStates(Var.YES);
								send(m);
								Tools.addMessage(cf.tpRecv, "��ͬ����"+sender+"����������");
							}else{
								m.setMsgType(MSGType.VOICECHATRESPONSE);
								m.setStates(Var.NO);
								send(m);
								Tools.addMessage(cf.tpRecv, "��ܾ���"+sender+"����������");
							}
						}
					}else if(m.getMsgType() == MSGType.VOICECHATRESPONSE){
						String cfid = m.getRecver()+"-"+m.getSender();
						ChatForm cf = ManageChatForm.getChatFormByID(cfid);
						System.out.println("cfid="+cfid);
						System.out.println(m.getSender()+"�ӵ���������Ӧ��");
						if(m.getStates() == Var.YES){
							Tools.addMessage(cf.tpRecv, "�Է�ͬ����������");
//							System.out.println(fileInfos[0]+"-"+fileInfos[1]);
							String ip = m.getComment();
							VoiceClient vc = new VoiceClient(cfid, ip, 9200);
							vc.start();
//							ManageVoiceThread.addVoiceClientThread(cfid, VoiceClient.currentThread());
						}else{
							Tools.addMessage(cf.tpRecv, "�Է��ܾ�����ͨ����");
						}
					}else if(m.getMsgType() == MSGType.VOICECHATINTERRUPT){
						String cfid = m.getSender()+"-"+m.getRecver();
						ChatForm cf = ManageChatForm.getChatFormByID(cfid);
//						System.out.println("cfid="+cfid);
						System.out.println(m.getSender()+"�ӵ������жϡ�");
						
						Tools.alert(cf, "����ͨ�����ж�!");
						cf.tpRecv.setText("����ͨ�����ж�!");
						System.out.println("END!"+cfid);
						System.out.println(cf);
					}else if(m.getMsgType() == MSGType.LOVEINTHEHOUSE){
						String s = "���⣡���⣡\n�����������"+m.getSender()+"<br>�˿�����ĬĬϲ���㣡<br>LET'S MOVE!";
						LoveShow love = new LoveShow(s);
						love.setVisible(true);
					}
					
				}else if(o instanceof String){
					System.out.println("�ͻ����յ���Ϣ��"+o.toString());
				}else{
					Tools.alert("δ֪��Ϣ��ʽ��");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
	}
}

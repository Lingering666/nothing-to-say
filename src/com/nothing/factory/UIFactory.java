package com.nothing.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextPane;

import com.nothing.c.filetrans.ProgressView;
import com.nothing.c.tree.GroupTree;
import com.nothing.c.tree.RecentTree;
import com.nothing.c.tree.TreeBase;
import com.nothing.c.tree.UserListOpera;
import com.nothing.clients.GroupForm;

public class UIFactory {

	/** ���ڴ洢����˼������Ĺ��� */
	public static JTextPane monitor;
	
	/** ���ڴ���ļ������view */
	public static List<ProgressView> proview = new ArrayList<ProgressView>();
	
	/** ���ڴ��Nothing�б���� */
	public static UserListOpera nothingTree;
	
	/** ���ڴ��Ⱥ�б���� */
	public static GroupTree groupTree;
	
	/** ���Ⱥ�û��б����-����Ⱥ����洢 */
	public static Map<String, TreeBase> groupUserTreeMap = new HashMap<String, TreeBase>();
	
	/** Ⱥ���촰�ڵĶ��� */
	public static Map<String, GroupForm> groupFormMap = new HashMap<String, GroupForm>();
	
	/** ���ڴ�������ϵ�б���� */
	public static RecentTree recentTree;
}

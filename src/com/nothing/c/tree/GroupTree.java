package com.nothing.c.tree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.nothing.clients.ChatForm;
import com.nothing.factory.DBFactory;
import com.nothing.factory.MapFactory;
import com.nothing.object.Groups;
import com.nothing.object.Users;
import com.nothing.util.Tools;

public class GroupTree implements TreeSelectionListener {

	protected JTree tree;
	protected DefaultMutableTreeNode root;
	public List<Groups> qunlist = new ArrayList<Groups>();
	protected String uID;
	
	public GroupTree(JTree Tree, final String uID){
		this.uID = uID;
		root = new DefaultMutableTreeNode("root");
		TreeModel tm = new DefaultTreeModel(root);
		
		createGroupLists(root);
		tree = Tree;
		tree.setModel(tm);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new UserListRenderer());
		tree.setToggleClickCount(1);	//���õ���չ���ڵ�
		tree.setRowHeight(24);
		tree.putClientProperty("JTree.lineStyle", "None");
		tree.addMouseListener(new TreeListener(tree, true));
		tree.expandRow(0);
	}
	
	protected void createGroupLists(DefaultMutableTreeNode root){
//		System.out.println("GroupTree.�����û�������"+MapFactory.userMap.size());
		DefaultMutableTreeNode qunNode = new DefaultMutableTreeNode("Ⱥ�б�");
		root.add(qunNode);
		qunlist = DBFactory.getGroupsByID(uID);
		for(Groups g:qunlist){
			qunNode.add(new DefaultMutableTreeNode(g));
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub

	}

}

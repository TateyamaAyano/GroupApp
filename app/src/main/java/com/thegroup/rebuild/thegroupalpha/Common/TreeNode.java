package com.thegroup.rebuild.thegroupalpha.Common;

import java.util.ArrayList;

/**
 * Created by yeelee on 17/3/6.
 */

public class TreeNode {
    protected TreeNode parent;
    protected ArrayList<TreeNode> children;
    public TreeNode(){

    }
    public TreeNode(TreeNode parent){
        this.parent=parent;
        this.parent.addChild(this);
    }
    public void addChild(TreeNode child){
        this.children.add(child);
    }
    public void removeChild(TreeNode child){
        this.children.remove(child);
    }
}

package com.reemii.com.testforcoolview.nodetree;

import com.reemii.com.testforcoolview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huhanghao on 2017/10/12.
 *
 * @description 节点帮助类
 */
public class NodeHelper {
    /**
     * 传入所有的要展示的节点数据
     *
     * @param NodeBasicList 返回值是所有的根节点
     * @return
     */
    public static List<NodeBasic> sortNodes(List<NodeBasic> NodeBasicList) {
        List<NodeBasic> rootNodeBasics = new ArrayList<>();
        int size = NodeBasicList.size();
        NodeBasic m;
        NodeBasic n;
        //两个for循环整理出所有数据之间的父子关系，最后会构造出一个森林（就是可能有多棵树）
        for (int i = 0; i < size; i++) {
            m = NodeBasicList.get(i);
            for (int j = i + 1; j < size; j++) {
                n = NodeBasicList.get(j);
                if (m.isParent(n)) {
                    m.get_childrenList().add(n);
                    n.set_parent(m);
                } else if (m.isChild(n)) {
                    n.get_childrenList().add(m);
                    m.set_parent(n);
                }
            }
        }
        //找出所有的树根，同事设置相应的图标（后面你会发现其实这里的主要
        // 作用是设置叶节点和非叶节点的图标）
        for (int i = 0; i < size; i++) {
            m = NodeBasicList.get(i);
            if (m.isRoot()) {
                rootNodeBasics.add(m);
            }
            setNodeIcon(m);
        }
        NodeBasicList.clear();//此时所有的关系已经整理完成了
        // ，森林构造完成，可以清空之前的数据，释放内存，提高性能
        // ，如果之前的数据还有用的话就不清空
        NodeBasicList = rootNodeBasics;//返回所有的根节点
        rootNodeBasics = null;
        return NodeBasicList;
    }

    /**
     * 设置图标
     *
     * @param NodeBasic
     */
    private static void setNodeIcon(NodeBasic NodeBasic) {
        if (!NodeBasic.isLeaf()) {
            if (NodeBasic.isExpand()) {
                NodeBasic.set_icon(R.mipmap.down_arrow);
            } else {
                NodeBasic.set_icon(R.mipmap.right_arrow);
            }
        } else {
            NodeBasic.set_icon(-1);
        }
    }
}

package com.reemii.com.testforcoolview.nodetree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.reemii.com.testforcoolview.R;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by huhanghao on 2017/10/12.
 *
 * @description 适配器类，就是listview最常见的适配器写法
 */
public class NodeTreeAdapter extends BaseAdapter {
    //大家经常用ArrayList，但是这里为什么要使用LinkedList
    // ，后面大家会发现因为这个list会随着用户展开、收缩某一项而频繁的进行增加、删除元素操作，
    // 因为ArrayList是数组实现的，频繁的增删性能低下，而LinkedList是链表实现的，对于频繁的增删
    //操作性能要比ArrayList好。
    private LinkedList<NodeBasic> nodeBasicLinkedList;
    private LayoutInflater inflater;
    private int retract;//缩进值
    private Context context;

    public NodeTreeAdapter(Context context, ListView listView, LinkedList<NodeBasic> linkedList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        nodeBasicLinkedList = linkedList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                expandOrCollapse(position);
            }
        });
        //缩进值，大家可以将它配置在资源文件中，从而实现适配
        retract = (int) (context.getResources().getDisplayMetrics().density * 10 + 0.5f);
    }

    /**
     * 展开或收缩用户点击的条目
     *
     * @param position
     */
    private void expandOrCollapse(int position) {
        NodeBasic nodeBasic = nodeBasicLinkedList.get(position);
        if (nodeBasic != null && !nodeBasic.isLeaf()) {
            boolean isExpand = nodeBasic.isExpand();
            if (isExpand) { // 当为展开的时候，获取当前节点的所有子节点
                List<NodeBasic> nodeBasicList = nodeBasic.get_childrenList();
                int size = nodeBasicList.size();
                NodeBasic tmp = null;
                for (int i = 0; i < size; i++) { // 遍历所有子节点，将它们一一关闭
                    tmp = nodeBasicList.get(i);
                    if (tmp.isExpand()) {
                        collapse(tmp, position + 1);
                    }
                    nodeBasicLinkedList.remove(position + 1);
                }
            } else { // 若没展开则将该节点下的所有子节点加入
                nodeBasicLinkedList.addAll(position + 1, nodeBasic.get_childrenList());
            }
            nodeBasic.setIsExpand(!isExpand);
            notifyDataSetChanged();
        }
    }

    /**
     * 递归收缩用户点击的条目
     * 因为此中实现思路是：当用户展开某一条时，就将该条对应的所有子节点加入到nodeBasicLinkedList
     * ，同时控制缩进，当用户收缩某一条时，就将该条所对应的子节点全部删除，而当用户跨级缩进时
     * ，就需要递归缩进其所有的孩子节点，这样才能保持整个nodeBasicLinkedList的正确性，同时这种实
     * 现方式避免了每次对所有数据进行处理然后插入到一个list，最后显示出来，当数据量一大，就会卡顿，
     * 所以这种只改变局部数据的方式性能大大提高。
     *
     * @param position
     */
    private void collapse(NodeBasic nodeBasic, int position) {
        nodeBasic.setIsExpand(false);  // 将展开状态设置为关闭
        List<NodeBasic> nodeBasics = nodeBasic.get_childrenList();
        int size = nodeBasics.size();
        NodeBasic tmp = null;
        for (int i = 0; i < size; i++) {
            tmp = nodeBasics.get(i);
            if (tmp.isExpand()) {
                collapse(tmp, position + 1);
            }
            nodeBasicLinkedList.remove(position + 1);
        }
    }

    @Override
    public int getCount() {
        return nodeBasicLinkedList.size();
    }

    @Override
    public Object getItem(int position) {
        return nodeBasicLinkedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tree_listview_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.id_treenode_icon);
            holder.label = (TextView) convertView.findViewById(R.id.id_treenode_label);
//            holder.confirm = (LinearLayout) convertView.findViewById(R.id.id_confirm);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NodeBasic nodeBasic = nodeBasicLinkedList.get(position);
        holder.label.setText(nodeBasic.get_label());
        if (nodeBasic.get_icon() == -1) {
            holder.imageView.setVisibility(View.INVISIBLE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(nodeBasic.get_icon());
        }
//        holder.confirm.setTag(position);
//        holder.confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "选中:" + v.getTag(), Toast.LENGTH_SHORT).show();
//            }
//        });
        convertView.setPadding(nodeBasic.get_level() * retract, 5, 5, 5);//处理缩进
        return convertView;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView label;
        public LinearLayout confirm;
    }
}

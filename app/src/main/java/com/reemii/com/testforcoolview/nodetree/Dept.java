package com.reemii.com.testforcoolview.nodetree;

/**
 * Created by huhanghao on 2017/10/12.
 *
 * @description 部门类（继承Node），此处的泛型Integer是因为ID和parentID都为int
 * ，如果为String传入泛型String即可，如果传入String，记得修改<span style="font-family: Arial, Helvetica, sans-serif;">parent和child方法，因为比较相等的方式不同。</span>
 */
public class Dept extends NodeBasic<Integer> {
    private int id;//部门ID
    private int parentId;//父亲节点ID
    private String name;//部门名称

    public Dept() {
    }

    public Dept(int id, int parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    /**
     * 此处返回节点ID
     *
     * @return
     */
    @Override
    public Integer get_id() {
        return id;
    }

    /**
     * 此处返回父亲节点ID
     *
     * @return
     */
    @Override
    public Integer get_parentId() {
        return parentId;
    }

    @Override
    public String get_label() {
        return name;
    }

    @Override
    public boolean isParent(NodeBasic dest) {
        if (id == ((Integer) dest.get_parentId()).intValue()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isChild(NodeBasic dest) {
        if (parentId == ((Integer) dest.get_id()).intValue()) {
            return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

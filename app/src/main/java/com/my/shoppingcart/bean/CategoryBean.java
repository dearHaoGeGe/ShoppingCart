package com.my.shoppingcart.bean;

import java.util.List;

/**
 * 牛奶产品的的实体类
 * <p>
 * Created by YJH on 2016/12/21 22:07.
 */
public class CategoryBean {
    private String typeName;
    private List<ProductBean> list;
    private boolean flag;   //作为左面ListView是否被点击的标识
    private int buyNum;     //左面ListView显示订购数量

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<ProductBean> getList() {
        return list;
    }

    public void setList(List<ProductBean> list) {
        this.list = list;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }
}

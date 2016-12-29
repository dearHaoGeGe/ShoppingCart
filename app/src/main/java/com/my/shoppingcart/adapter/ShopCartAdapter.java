package com.my.shoppingcart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.shoppingcart.MainActivity;
import com.my.shoppingcart.R;
import com.my.shoppingcart.bean.ProductBean;

import java.util.List;

/**
 * 底部购物车Adapter
 * <p>
 * Created by YJH on 2016/12/22 23:22.
 */

public class ShopCartAdapter extends BaseAdapter {

    private Context mContext;
    private List<ProductBean> scBeanList;

    public ShopCartAdapter(Context mContext, List<ProductBean> scBeanList) {
        this.mContext = mContext;
        this.scBeanList = scBeanList;
    }

    @Override
    public int getCount() {
        return scBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return scBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.shop_cart_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductBean bean = scBeanList.get(position);
        holder.tv_name.setText(bean.getProductName());
        holder.tv_count.setText(String.valueOf(bean.getBuyNum()));
        holder.tv_price.setText(String.valueOf(bean.getPrice()));


        OnClickListener onClick = new OnClickListener(position);
        holder.iv_add.setOnClickListener(onClick);
        holder.iv_remove.setOnClickListener(onClick);

        return convertView;
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_price;
        TextView tv_count;
        ImageView iv_add;
        ImageView iv_remove;

        private ViewHolder(View convertView) {
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
            iv_remove = (ImageView) convertView.findViewById(R.id.iv_remove);
        }
    }

    private class OnClickListener implements View.OnClickListener {
        private int position;

        private OnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            int buyNum = scBeanList.get(position).getBuyNum();
            switch (v.getId()) {
                case R.id.iv_add:
                    scBeanList.get(position).setBuyNum(++buyNum);
                    break;

                case R.id.iv_remove:
                    if (buyNum > 0) {
                        scBeanList.get(position).setBuyNum(--buyNum);
                        if (buyNum == 0) {
                            scBeanList.remove(position);
                        }
                    }
                    break;
            }
            if (scBeanList.size() != 0) {
                notifyDataSetChanged();
            }
            ((MainActivity) mContext).fromCarGetData();
        }
    }

    public List<ProductBean> getScBeanList() {
        return scBeanList;
    }

    public void setScBeanList(List<ProductBean> scBeanList, boolean isNotifyDataSetChanged) {
        this.scBeanList = scBeanList;
        if (isNotifyDataSetChanged) {
            notifyDataSetChanged();
        }
    }

}

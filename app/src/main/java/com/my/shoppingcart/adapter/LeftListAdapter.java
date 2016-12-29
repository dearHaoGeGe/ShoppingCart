package com.my.shoppingcart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.my.shoppingcart.MainActivity;
import com.my.shoppingcart.R;
import com.my.shoppingcart.bean.CategoryBean;
import com.my.shoppingcart.bean.ProductBean;

import java.util.List;

/**
 * 左面ListView的Adapter（只是一个普通的ListView）
 * <p>
 * Created by YJH on 2016/12/21 23:13.
 */

public class LeftListAdapter extends BaseAdapter {

    private Context context;
    private List<CategoryBean> data;

    public LeftListAdapter(Context context, List<CategoryBean> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.left_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.updateBackgroundColor(position);
        int buyNum = data.get(position).getBuyNum();
        if (buyNum != 0) {
            holder.tv_count.setVisibility(View.VISIBLE);
            holder.tv_count.setText(String.valueOf(buyNum));
        } else {
            holder.tv_count.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView left_list_item;
        private TextView tv_count;

        private ViewHolder(View convertView) {
            left_list_item = (TextView) convertView.findViewById(R.id.tv_left_list_item);
            tv_count = (TextView) convertView.findViewById(R.id.tv_count);
        }

        /**
         * 更新选中与为选中的状态
         *
         * @param position position
         */
        private void updateBackgroundColor(int position) {
            CategoryBean bean = data.get(position);
            left_list_item.setText(bean.getTypeName());
            if (bean.isFlag()) {
                left_list_item.setTextColor(context.getResources().getColor(R.color.black));
                left_list_item.setBackgroundResource(R.drawable.left_stroke);
            } else {
                left_list_item.setTextColor(context.getResources().getColor(R.color.gray));
                left_list_item.setBackgroundResource(0);
            }
        }
    }

    /**
     * 重新设置adapter数据，并且控制是否刷新
     *
     * @param data                   新的数据data
     * @param isNotifyDataSetChanged 是否刷新
     */
    public void setData(List<CategoryBean> data, boolean isNotifyDataSetChanged) {
        this.data = data;
        if (isNotifyDataSetChanged) {
            notifyDataSetChanged();
        }
    }

    public List<CategoryBean> getData() {
        return data;
    }

    /**
     * 当点击右边的ListView添加产品时，
     * 左边的ListView也要刷新数据并且更新UI，
     * 并且也要通知MainActivity中底部购物车右上角数量刷新
     *
     * @param section  section
     * @param position position
     * @param cBean    CategoryBean的BuyNum
     * @param pBean    ProductBean的BuyNum
     */
    protected void showBuyNum(int section, int position, int cBean, int pBean) {
        CategoryBean cb = data.get(section);
        ProductBean pb = cb.getList().get(position);
        pb.setBuyNum(pBean);
        cb.setBuyNum(cBean);
        cb.getList().set(position, pb);
        data.set(section, cb);

        notifyDataSetChanged();
        //通知MainActivity中底部购物车右上角数量刷新
        ((MainActivity) context).refreshShopCarNum(data);

    }
}

package com.my.shoppingcart.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.my.shoppingcart.MainActivity;
import com.my.shoppingcart.R;
import com.my.shoppingcart.bean.CategoryBean;
import com.my.shoppingcart.bean.ProductBean;

import java.util.List;

/**
 * 右侧ListView的Adapter
 * <p>
 * Created by YJH on 2016/12/22 9:57.
 */

public class RightAdapter extends SectionedBaseAdapter {

    private final String TAG = getClass().getSimpleName() + "--->";
    private final int ADD = 2;
    private final int REMOVE = 1;
    private Context context;
    private List<CategoryBean> cateBeanList;
    private LeftListAdapter leftAdapter;

    public RightAdapter(Context context, List<CategoryBean> cateBeanList, LeftListAdapter leftAdapter) {
        this.context = context;
        this.cateBeanList = cateBeanList;
        this.leftAdapter = leftAdapter;
    }

    @Override
    public Object getItem(int section, int position) {
        return cateBeanList.get(section).getList().get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public int getSectionCount() {
        return cateBeanList.size();
    }

    @Override
    public int getCountForSection(int section) {
        return cateBeanList.get(section).getList().size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.right_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductBean bean = cateBeanList.get(section).getList().get(position);
        holder.tv_product_name.setText(bean.getProductName());     //设置产品名称

        if (bean.getBuyNum() == 0) {
            holder.setIsVisible(View.INVISIBLE);
        } else {
            holder.setIsVisible(View.VISIBLE);
            holder.tv_product_num.setText(String.valueOf(bean.getBuyNum()));
        }

        OnClickListenerRightItem onClickListener = new OnClickListenerRightItem(section, position, holder);
        holder.ll_right_list.setOnClickListener(onClickListener);
        holder.iv_add_product.setOnClickListener(onClickListener);
        holder.iv_remove_product.setOnClickListener(onClickListener);
        return convertView;
    }

    /**
     * 点击事件实现类
     */
    private class OnClickListenerRightItem implements View.OnClickListener {

        private int section;
        private int position;
        private ViewHolder holder;

        private OnClickListenerRightItem(int section, int position, ViewHolder holder) {
            this.section = section;
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_right_list:
                    Toast.makeText(context, "" + cateBeanList.get(section).getList().get(position).getProductName(), Toast.LENGTH_SHORT).show();
                    break;

                case R.id.iv_add_product:
                    addAndRemoveNum(section, position, ADD, holder);
                    break;

                case R.id.iv_remove_product:
                    addAndRemoveNum(section, position, REMOVE, holder);
                    break;
            }
        }
    }

    private class ViewHolder {
        TextView tv_product_name;
        LinearLayout ll_right_list;
        ImageView iv_remove_product;
        ImageView iv_add_product;
        TextView tv_product_num;

        private ViewHolder(View convertView) {
            tv_product_name = (TextView) convertView.findViewById(R.id.tv_right_item);
            ll_right_list = (LinearLayout) convertView.findViewById(R.id.ll_right_list);
            iv_remove_product = (ImageView) convertView.findViewById(R.id.iv_remove_product);
            iv_add_product = (ImageView) convertView.findViewById(R.id.iv_add_product);
            tv_product_num = (TextView) convertView.findViewById(R.id.tv_product_num);
        }

        /**
         * 控制减少数量按钮和数量tv是否显示
         *
         * @param visible View.GONE     View.INVISIBLE    View.VISIBLE
         */
        private void setIsVisible(int visible) {
            iv_remove_product.setVisibility(visible);
            tv_product_num.setVisibility(visible);
        }
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        HeaderViewHolder holder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.header_item, parent, false);
            holder = new HeaderViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        holder.tv_right_header_item.setText(cateBeanList.get(section).getTypeName() + "(" + cateBeanList.get(section).getList().size() + ")");
        return convertView;
    }

    private class HeaderViewHolder {
        LinearLayout ll_right_header;
        TextView tv_right_header_item;

        private HeaderViewHolder(View convertView) {
            ll_right_header = (LinearLayout) convertView.findViewById(R.id.ll_right_header);
            tv_right_header_item = (TextView) convertView.findViewById(R.id.tv_right_header_item);
        }
    }

    public List<CategoryBean> getCateBeanList() {
        return cateBeanList;
    }

    /**
     * 重新设置adapter数据，并且控制是否刷新
     *
     * @param cateBeanList           新的cateBeanList
     * @param isNotifyDataSetChanged 是否刷新
     */
    public void setCateBeanList(List<CategoryBean> cateBeanList, boolean isNotifyDataSetChanged) {
        this.cateBeanList = cateBeanList;
        if (isNotifyDataSetChanged) {
            notifyDataSetChanged();
        }
    }

    /**
     * 点击添加或者删除按钮，通知左边的ListView数据变化并且刷新
     *
     * @param section  section
     * @param position position
     * @param mode     1 = remove，2 = add
     */
    private void addAndRemoveNum(int section, int position, int mode, ViewHolder holder) {
        CategoryBean cb = cateBeanList.get(section);
        ProductBean pb = cb.getList().get(position);
        int pb_Num = pb.getBuyNum();
        int cb_Num = cb.getBuyNum();
        switch (mode) {
            case ADD:
                holder.iv_remove_product.setAnimation(pb_Num < 1 ? getShowAnimation() : null);  //动画
                pb.setBuyNum(++pb_Num);
                cb.setBuyNum(++cb_Num);
                addProductAnimation(holder.iv_add_product);     //动画
                break;

            case REMOVE:
                if (pb_Num > 0) {
                    holder.iv_remove_product.setAnimation(pb_Num < 2 ? getHiddenAnimation() : null);    //动画
                    pb.setBuyNum(--pb_Num);
                    cb.setBuyNum(--cb_Num);
                }

                break;

            default:
                Log.e(TAG, "模式设置错误");
        }
        cb.getList().set(position, pb);     //设置本类中的数据
        cateBeanList.set(section, cb);      //设置本类中的数据
        leftAdapter.showBuyNum(section, position, cb_Num, pb_Num);  //通知左边的ListView修改数据然后刷新
        notifyDataSetChanged();


    }

    /**
     * 添加 添加产品动画
     *
     * @param v View
     */
    private void addProductAnimation(View v) {
        int[] startLocation = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
        v.getLocationInWindow(startLocation);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
        ImageView ball = new ImageView(context);
        ball.setImageResource(R.mipmap.number);
        ((MainActivity) context).setAnim(ball, startLocation);// 开始执行动画
    }

    /**
     * 显示减号的动画
     *
     * @return Animation
     */
    private Animation getShowAnimation() {
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 2f
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

    /**
     * 隐藏减号的动画
     *
     * @return Animation
     */
    private Animation getHiddenAnimation() {
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 2f
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

}

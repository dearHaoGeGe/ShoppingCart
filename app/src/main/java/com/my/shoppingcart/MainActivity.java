package com.my.shoppingcart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.my.shoppingcart.adapter.LeftListAdapter;
import com.my.shoppingcart.adapter.RightAdapter;
import com.my.shoppingcart.adapter.ShopCartAdapter;
import com.my.shoppingcart.bean.CategoryBean;
import com.my.shoppingcart.bean.ProductBean;
import com.my.shoppingcart.bean.TestData;
import com.my.shoppingcart.view.MyListView;
import com.my.shoppingcart.view.PinnedHeaderListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, View.OnClickListener {

    private ListView lv_left;
    private LeftListAdapter leftAdapter;
    private RightAdapter rightAdapter;
    private PinnedHeaderListView pinnedListView;
    private boolean isScroll = true;
    private List<CategoryBean> cateBeanList;
    int x = 0;
    int y = 0;
    int z = 0;

    //底部和购物车
    private BottomSheetLayout bottomSheetLayout;
    private View bottomSheet;
    private LinearLayout ll_shopcar;
    private TextView tv_total_num;
    private TextView tv_total_money;
    private TextView tv_car;
    private ShopCartAdapter shopCartAdapter;
    private List<ProductBean> carList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MenuTextAllCaps);  //主题必须要设置(必须要在设置布局之前)
        setContentView(R.layout.activity_main);

        initToolbar("选择产品");

        cateBeanList = TestData.setFalseData();
        initListView();
        initPinnedHeaderListView();
        initBottomLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     //返回按钮
                finish();
                return true;
            case R.id.search_menu:      //搜索按钮
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_shopcar:
                showBottomSheet();
                break;

            case R.id.clear:
                clearEmptyCar();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_left:
                isScroll = false;
                //左侧的ListView选中的效果
                for (int i = 0; i < cateBeanList.size(); i++) {
                    CategoryBean cb = cateBeanList.get(i);
                    if (i == position) {
                        cb.setFlag(true);
                    } else {
                        cb.setFlag(false);
                    }
                }
                leftAdapter.setData(cateBeanList, true);

                //点击左侧的时候和右侧的ListView设置关联
                int rightSection = 0;
                for (int i = 0; i < position; i++) {
                    rightSection += rightAdapter.getCountForSection(i) + 1;
                }
                pinnedListView.setSelection(rightSection);
                break;
        }
    }


    private void initListView() {
        lv_left = (ListView) findViewById(R.id.lv_left);
        leftAdapter = new LeftListAdapter(this, cateBeanList);
        lv_left.setAdapter(leftAdapter);
        lv_left.setOnItemClickListener(this);
    }

    private void initPinnedHeaderListView() {
        pinnedListView = (PinnedHeaderListView) findViewById(R.id.pinnedListView);
        rightAdapter = new RightAdapter(this, cateBeanList, leftAdapter);
        pinnedListView.setAdapter(rightAdapter);
        pinnedListView.setOnScrollListener(this);
    }

    private void initBottomLayout() {
        ll_shopcar = (LinearLayout) findViewById(R.id.ll_shopcar);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomSheetLayout);
        tv_total_num = (TextView) findViewById(R.id.tv_total_num);
        tv_total_money = (TextView) findViewById(R.id.tv_total_money);
        tv_car = (TextView) findViewById(R.id.tv_car);
        ll_shopcar.setOnClickListener(this);
        carList = new ArrayList<>();
        tv_total_num.setVisibility(tv_total_num.getText().equals("0") ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * 显示底部购物车
     */
    private void showBottomSheet() {
        bottomSheet = createBottomSheetView();
        if (bottomSheetLayout.isSheetShowing()) {
            bottomSheetLayout.dismissSheet();
        } else {
            if (carList.size() != 0) {
                bottomSheetLayout.showWithSheetView(bottomSheet);
            }
        }
    }

    /**
     * 创建购物车view
     *
     * @return view
     */
    private View createBottomSheetView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet,
                (ViewGroup) getWindow().getDecorView(), false);
        MyListView myListView = (MyListView) view.findViewById(R.id.lv_product);
        TextView tv_clear = (TextView) view.findViewById(R.id.clear);
        tv_clear.setOnClickListener(this);
        fromRightLVGetCarData();
        shopCartAdapter = new ShopCartAdapter(this, carList);
        myListView.setAdapter(shopCartAdapter);
        return view;
    }

    /**
     * 从右面ListView中获得购物车ListView的数据
     */
    private void fromRightLVGetCarData() {
        carList.clear();    //每次添加数据之前必须先把之前的clear，否则会出现重复情况
        List<CategoryBean> cb = rightAdapter.getCateBeanList();
        for (int i = 0; i < cb.size(); i++) {
            List<ProductBean> pb = cb.get(i).getList();
            for (int j = 0; j < pb.size(); j++) {
                if (pb.get(j).getBuyNum() != 0) {
                    carList.add(pb.get(j));
                }
            }
        }
    }

    /**
     * 当在购物车里面点击ListView中的add或者remove图标，
     * 从购物车ListView中获得数据，
     * 通知左边ListView和右边ListView刷新数据和UI，
     * 同时购物车的价格和数量角标也要刷新
     */
    public void fromCarGetData() {
        List<ProductBean> pbList = shopCartAdapter.getScBeanList();
        if (pbList.size() == 0) {
            bottomSheetLayout.dismissSheet();
        }

        //计算一件产品买了几个
        for (int i = 0; i < pbList.size(); i++) {
            ProductBean pb = pbList.get(i);
            List<ProductBean> list = cateBeanList.get(pb.getSection()).getList();
            for (int j = 0; j < list.size(); j++) {
                if (pb.getProductName().equals(list.get(j).getProductName())) {
                    list.set(j, pb);
                    cateBeanList.get(pb.getSection()).setList(list);
                }
            }
        }

        //计算一共买了多少件产品（包含重复）
        for (int i = 0; i < cateBeanList.size(); i++) {
            int cateBuyNum = 0;
            List<ProductBean> proBeanList = cateBeanList.get(i).getList();
            for (int j = 0; j < proBeanList.size(); j++) {
                cateBuyNum += proBeanList.get(j).getBuyNum();

            }
            cateBeanList.get(i).setBuyNum(cateBuyNum);
        }

        leftAdapter.setData(cateBeanList, true);
        rightAdapter.setCateBeanList(cateBeanList, true);

        refreshShopCarNum(cateBeanList);
    }

    /**
     * 刷新下面购物车图标右上角的数量和价格
     *
     * @param cbList cbList
     */
    public void refreshShopCarNum(List<CategoryBean> cbList) {
        int carBuyNum = 0;
        float total_money = 0.0f;
        for (int i = 0; i < cbList.size(); i++) {
            carBuyNum += cbList.get(i).getBuyNum();

            List<ProductBean> pbList = cbList.get(i).getList();
            for (int j = 0; j < pbList.size(); j++) {
                if (pbList.get(j).getBuyNum() > 0) {
                    total_money += (pbList.get(j).getPrice()) * (pbList.get(j).getBuyNum());
                }
            }
        }
        if (carBuyNum > 0) {
            tv_total_num.setVisibility(View.VISIBLE);
            tv_total_num.setText(String.valueOf(carBuyNum));
            tv_total_money.setText(String.valueOf("￥" + total_money));
        } else {
            tv_total_num.setVisibility(View.INVISIBLE);
            tv_total_money.setText(String.valueOf("￥0.00"));
        }
    }

    /**
     * 清空购物车(把实体类中的所有BuyNum的数量设置为0)
     */
    private void clearEmptyCar() {
        int cbListSize = cateBeanList.size();
        for (int i = 0; i < cbListSize; i++) {
            cateBeanList.get(i).setBuyNum(0);
            List<ProductBean> pbList = cateBeanList.get(i).getList();
            for (int j = 0; j < pbList.size(); j++) {
                pbList.get(j).setBuyNum(0);
            }
        }
        leftAdapter.setData(cateBeanList, true);
        rightAdapter.setCateBeanList(cateBeanList, true);
        bottomSheetLayout.dismissSheet();
        refreshShopCarNum(cateBeanList);
    }

    //******************************** 动画 开始 ***********************************

    /**
     * 设置加一动画
     *
     * @param v             View
     * @param startLocation startLocation
     */
    public void setAnim(final View v, int[] startLocation) {
        ViewGroup anim_mask_layout = null;
        anim_mask_layout = createAnimLayout();
        anim_mask_layout.addView(v);//把动画小球添加到动画层
        final View view = addViewToAnimLayout(anim_mask_layout, v, startLocation);
        int[] endLocation = new int[2];// 存储动画结束位置的X、Y坐标
        tv_car.getLocationInWindow(endLocation);
        // 计算位移
        int endX = 0 - startLocation[0] + 40;// 动画位移的X坐标
        int endY = endLocation[1] - startLocation[1];// 动画位移的y坐标

        TranslateAnimation translateAnimationX = new TranslateAnimation(0, endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0, 0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationY.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(800);// 动画的执行时间
        view.startAnimation(set);
        // 动画监听事件
        set.setAnimationListener(new Animation.AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 创建动画层
     *
     * @return ViewGroup
     */
    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setId(Integer.MAX_VALUE - 1);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    private View addViewToAnimLayout(final ViewGroup parent, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }
    //******************************** 动画 结束 ***********************************


    //******************************** setOnScrollListener开始 ***********************************
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            //当不滚动时
            case SCROLL_STATE_IDLE:
                //判断滚动到底部
                if (pinnedListView.getLastVisiblePosition() == (pinnedListView.getCount() - 1)) {
                    lv_left.setSelection(ListView.FOCUS_DOWN);
                }
                //判断滚动到顶部
                if (pinnedListView.getFirstVisiblePosition() == 0) {
                    lv_left.setSelection(0);
                }
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (isScroll) {
            for (int i = 0; i < cateBeanList.size(); i++) {
                if (i == rightAdapter.getSectionForPosition(pinnedListView.getFirstVisiblePosition())) {
                    cateBeanList.get(i).setFlag(true);
                    x = i;
                } else {
                    cateBeanList.get(i).setFlag(false);
                }
            }
            if (x != y) {
                leftAdapter.setData(cateBeanList, true);  //左侧ListView刷新显示
                y = x;
                //左侧ListView滚动到最后位置
                if (y == lv_left.getLastVisiblePosition()) {
                    lv_left.setSelection(z);
                }
                //左侧ListView滚动到第一个位置
                if (x == lv_left.getFirstVisiblePosition()) {
                    lv_left.setSelection(z);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount - 1) {
                    lv_left.setSelection(ListView.FOCUS_DOWN);
                }
            }
        } else {
            isScroll = true;
        }
    }
    //******************************** setOnScrollListener结束 ***********************************

    /**
     * 创建右上角的搜索图标
     *
     * @param menu menu
     * @return true
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    /**
     * 设置Toolbar
     *
     * @param title 要显示的Title
     */
    private void initToolbar(String title) {
        setTitle(title);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

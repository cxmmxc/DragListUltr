package com.example.topnewgrid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.topnewgrid.R;

/**
 * 作者：陈新明
 * 创建日期：2016/9/22
 * 邮箱：herewinner@163.com
 * 描述：//TODO
 */

public class DragListView extends ListView {


    private boolean mIsEditStatus = false;//是否处于编辑状态，默认是false
    /** 点击时候的X位置 */
    public int downX;
    /** 点击时候的Y位置 */
    public int downY;
    /** 点击时候对应整个界面的X位置 */
    public int windowX;
    /** 点击时候对应整个界面的Y位置 */
    public int windowY;
    /** 长按时候对应postion */
    public int dragPosition;
    /** Up后对应的ITEM的Position */
    private int dropPosition;
    /** 开始拖动的ITEM的Position*/
    private int startPosition;
    /** item高 */
    private int itemHeight;
    /** item宽 */
    private int itemWidth;
    /** 长按的时候ITEM的VIEW*/
    private ViewGroup dragItemView = null;
    /** 屏幕上的X */
    private int win_view_x;
    /** 屏幕上的Y*/
    private int win_view_y;
    /** 拖动的里x的距离  */
    int dragOffsetX;
    /** 拖动的里Y的距离  */
    int dragOffsetY;
    /** 拖动的时候放大的倍数 */
    private double dragScale = 1.2D;
    /** 震动器  */
    private Vibrator mVibrator;
    /** */
    private WindowManager.LayoutParams windowParams = null;
    /** WindowManager管理器 */
    private WindowManager windowManager = null;
    /** 拖动的时候对应ITEM的VIEW */
    private View dragImageView = null;

    public DragListView(Context context) {
        this(context, null);
    }

    public DragListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    private void initData(Context context) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) ev.getX();
            downY = (int) ev.getY();
            windowX = (int) ev.getX();
            windowY = (int) ev.getY();
            setOnItemClickListener(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    public void setOnItemClickListener(final MotionEvent ev) {
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int x = (int) ev.getX();// 长安事件的X位置
                int y = (int) ev.getY();// 长安事件的y位置
                startPosition = position;// 第一次点击的postion
                dragPosition = position;
                ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
                ImageView dragTheImageView = (ImageView) dragViewGroup.findViewById(R.id.drag_img);
                //判断点击的位置是否在右边的view上
                int[] dragLocation = new int[2];
                dragTheImageView.getLocationOnScreen(dragLocation);
                int width = dragTheImageView.getWidth();
                int height = dragTheImageView.getHeight();
                if (!((x > dragLocation[0]) && (x < dragLocation[0] + width) && (y > dragLocation[1]) && (y < dragLocation[1] + height))) {
                    return false;
                }
                itemHeight = dragViewGroup.getHeight();
                itemWidth = dragViewGroup.getWidth();
                // 有效的位置，可以拖动
                if (dragPosition != AdapterView.INVALID_POSITION) {
                    win_view_x = windowX - dragViewGroup.getLeft();//VIEW相对自己的X，半斤
                    win_view_y = windowY - dragViewGroup.getTop();//VIEW相对自己的y，半斤
                    dragOffsetX = (int) (ev.getRawX() - x);//手指在屏幕的上X位置-手指在控件中的位置就是距离最左边的距离
                    dragOffsetY = (int) (ev.getRawY() - y);//手指在屏幕的上y位置-手指在控件中的位置就是距离最上边的距离
                    dragItemView = dragViewGroup;
                    dragViewGroup.destroyDrawingCache();
                    dragViewGroup.setDrawingCacheEnabled(true);
                    Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup.getDrawingCache());
                    mVibrator.vibrate(50);//设置震动时间
                    startDrag(dragBitmap, (int)ev.getRawX(),  (int)ev.getRawY());
//                    hideDropItem();
                    dragViewGroup.setVisibility(View.INVISIBLE);
//                    isMoving = false;
                    return true;// 消费掉此事件，onTouchEvent接收
                }

                return false;
            }
        });
    }

    public void startDrag(Bitmap dragBitmap, int x, int y) {
        stopDrag();
        windowParams = new WindowManager.LayoutParams();// 获取WINDOW界面的
        //Gravity.TOP|Gravity.LEFT;这个必须加
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
//		windowParams.x = x - (int)((itemWidth / 2) * dragScale);
//		windowParams.y = y - (int) ((itemHeight / 2) * dragScale);
        //得到preview左上角相对于屏幕的坐标
        windowParams.x = x - win_view_x;
        windowParams.y = y  - win_view_y;
//		this.windowParams.x = (x - this.win_view_x + this.viewX);//位置的x值
//		this.windowParams.y = (y - this.win_view_y + this.viewY);//位置的y值
        //设置拖拽item的宽和高
        windowParams.width = (int) (dragScale * dragBitmap.getWidth());// 放大dragScale倍，可以设置拖动后的倍数
        windowParams.height = (int) (dragScale * dragBitmap.getHeight());// 放大dragScale倍，可以设置拖动后的倍数
        this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        this.windowParams.format = PixelFormat.TRANSLUCENT;
        this.windowParams.windowAnimations = 0;
        ImageView iv = new ImageView(getContext());
        iv.setImageBitmap(dragBitmap);
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window"
        windowManager.addView(iv, windowParams);
        dragImageView = iv;
    }


    /** 停止拖动 ，释放并初始化 */
    private void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }
}

package com.example.topnewgrid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.topnewgrid.R;
import com.example.topnewgrid.adapter.DragAdapter;
import com.example.topnewgrid.adapter.InterAdapter;

/**
 * 作者：陈新明
 * 创建日期：2016/9/22
 * 邮箱：herewinner@163.com
 * 描述：//TODO
 */

public class DragListView extends ListView {


    private final String TAG = DragListView.this.getClass().getName();

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
    /* 移动时候最后个动画的ID */
    private String LastAnimationID;
    /** 每个ITEM之间的竖直间距 */
    private int mVerticalSpacing = 15;
    /** */
    private int holdPosition;
    /** 是否在移动 */
    private boolean isMoving = false;
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
        //事件分发
        if (dragImageView != null && dragPosition != AdapterView.INVALID_POSITION) {
//            super.onTouchEvent(ev);
            Log.w(TAG, "onTouchEvent");
            int x = (int) ev.getX ();
            int y = (int) ev.getY ();
            switch (ev.getAction ()) {
                case MotionEvent.ACTION_DOWN:
                    downX = (int) ev.getX ();
                    downY = (int) ev.getY ();
                    windowX = (int) ev.getX ();
                    windowY = (int) ev.getY ();
                    break;
                case MotionEvent.ACTION_MOVE:
                    onDrag (x, y, (int) ev.getRawX (), (int) ev.getRawY ());
                    Log.i(TAG, "isMoving=" + isMoving);
                    if (!isMoving) {
                        onMove (x, y);
                    }
                    if (pointToPosition (x, y) != AdapterView.INVALID_POSITION) {
                        break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    stopDrag();
                    onDrop(x, y);
                    requestDisallowInterceptTouchEvent(false);
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    /** 在松手下放的情况 */
    private void onDrop(int x, int y) {
        // 根据拖动到的x,y坐标获取拖动位置下方的ITEM对应的POSTION
        int tempPostion = pointToPosition(x, y);
//		if (tempPostion != AdapterView.INVALID_POSITION) {
        dropPosition = tempPostion;
        InterAdapter mDragAdapter = (InterAdapter) getAdapter();
        //显示刚拖动的ITEM
        mDragAdapter.setShowDropItem(true);
        //刷新适配器，让对应的ITEM显示
        mDragAdapter.notifyData ();
//		}
    }

    private void onMove (int x, int y) {
        //获取手指移动的下面的position
        int dPostion = pointToPosition (x, y);
        Log.v(TAG, "dPosition=" + dPostion);
        if ((dPostion == -1) || (dPostion == dragPosition)) {
            return;
        }
        dropPosition = dPostion;
        if (dragPosition != startPosition) {
            dragPosition = startPosition;
        }
        int movecount;
        if ((dragPosition == startPosition) || (dragPosition != dropPosition)) {
            movecount = dropPosition - dragPosition;
        }else {
            movecount = 0;
        }
        if (movecount == 0) {
            return;
        }

        int movecount_abs = Math.abs (movecount);
        if (dPostion != dragPosition) {
            ViewGroup dragGroup = (ViewGroup) getChildAt (dragPosition);
            dragGroup.setVisibility (ViewGroup.INVISIBLE);
            //y_vlaue移动的距离百分比（相对于自己宽度的百分比）
            float y_vlaue = ((float) mVerticalSpacing / (float) itemHeight) + 1.0f;
            for (int i = 0; i < movecount_abs; i++) {
                //x是无法移动的，移动的是y
                float to_y = y_vlaue;
                if (movecount > 0) {
                    //往下拖动
                    holdPosition = dragPosition + i + 1;
                    to_y = -(i + 1) * y_vlaue;
                }else {
                    holdPosition = dragPosition - i - 1;
                    to_y = (i + 1) * y_vlaue;
                }
                ViewGroup moveViewGroup = (ViewGroup) getChildAt (holdPosition);
                Log.e(TAG, "to_y==" + to_y);
                if (!isMoving) {
                    Animation moveAnimation = getMoveAnimation (0, to_y);
                    moveViewGroup.startAnimation (moveAnimation);
                    if (holdPosition == dropPosition) {
                        //到最后一个position移动动画了
                        LastAnimationID = moveAnimation.toString ();
                    }
                    moveAnimation.setAnimationListener (new Animation.AnimationListener () {
                        @Override
                        public void onAnimationStart (Animation animation) {
                            isMoving = true;
                            Log.w(TAG, "animStart,isMoving = " + isMoving);
                        }

                        @Override
                        public void onAnimationEnd (Animation animation) {
                            if (animation.toString().equalsIgnoreCase(LastAnimationID)) {
                                InterAdapter mDragAdapter = (InterAdapter) getAdapter();
                                mDragAdapter.exchange(startPosition,dropPosition);
                                startPosition = dropPosition;
                                dragPosition = dropPosition;
                                isMoving = false;
                                Log.w(TAG, "animEnd,isMoving = " + isMoving);
                            }
                        }

                        @Override
                        public void onAnimationRepeat (Animation animation) {

                        }
                    });
                }


            }
        }
    }


    /** 获取移动动画 */
    public Animation getMoveAnimation(float toXValue, float toYValue) {
        TranslateAnimation mTranslateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0F,
                Animation.RELATIVE_TO_SELF,toXValue,
                Animation.RELATIVE_TO_SELF, 0.0F,
                Animation.RELATIVE_TO_SELF, toYValue);// 当前位置移动到指定位置
        mTranslateAnimation.setFillAfter(true);// 设置一个动画效果执行完毕后，View对象保留在终止的位置。
        mTranslateAnimation.setDuration(350L);
        return mTranslateAnimation;
    }

    private void onDrag (int x, int y, int rawX, int rawY) {
        if (dragImageView != null) {
            windowParams.alpha = 0.6f;
            windowParams.x = rawX - win_view_x;
            windowParams.y = rawY - win_view_y;
            windowManager.updateViewLayout (dragImageView, windowParams);
        }
    }

    public void setOnItemClickListener(final MotionEvent ev) {
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "dragList--setOnItemClickListener");
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
                    Log.w(TAG, "BitmapCreate");
                    Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup.getDrawingCache());
                    mVibrator.vibrate(50);//设置震动时间
                    startDrag(dragBitmap, (int)ev.getRawX(),  (int)ev.getRawY());
                    hideDropItem();
                    dragViewGroup.setVisibility(View.INVISIBLE);
                    isMoving = false;
                    requestDisallowInterceptTouchEvent(true);
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

    /** 隐藏 放下 的ITEM*/
    private void hideDropItem() {
        ((InterAdapter) getAdapter()).setShowDropItem(false);
    }


    /** 停止拖动 ，释放并初始化 */
    private void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }

    /** 在ScrollView内，所以要进行计算高度 */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

package com.lip.verticalmarqueeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * Created by lip on 2016/12/23.
 * <p>
 * 竖直滚动跑马灯
 */

public class VerticalMarqueeView extends SurfaceView implements SurfaceHolder.Callback {

    public Context mContext;

    private float mTextSize = 100; //字体大小

    private int mTextColor = Color.RED; //字体的颜色

    private boolean mIsRepeat;//是否重复滚动

    private int mStartPoint;// 开始滚动的位置  0是从上面开始   1是从下面开始

    private int mDirection;//滚动方向 0 向上滚动   1向下滚动

    private int mSpeed;//滚动速度

    private SurfaceHolder holder;

    private TextPaint mTextPaint;

    private MarqueeViewThread mThread;

    private String margueeString;

    private int textWidth = 0, textHeight = 0;

    public int currentY = 0;// 当前y的位置

    public double sepY = 1;//每一步滚动的距离

    private Point point;//点，没啥用，懒得弄了

    private StaticLayout staticLayout;//绘制多行文本所需类

    private boolean isFirstDraw = true;//是否为某条文本的第一次绘制~

    private boolean isStop = false;
    private int sec = 5000;

    public VerticalMarqueeView(Context context) {
        this(context, null);
    }

    public VerticalMarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalMarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (isInEditMode()) {
            //防止编辑预览界面报错
            return;
        }
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalMarqueeTextView, defStyleAttr, 0);
        mTextColor = a.getColor(R.styleable.VerticalMarqueeTextView_VerticalMarqueeTextView_textColor, Color.RED);
        mTextSize = a.getDimension(R.styleable.VerticalMarqueeTextView_VerticalMarqueeTextView_textSize, 48);
        mIsRepeat = a.getBoolean(R.styleable.VerticalMarqueeTextView_VerticalMarqueeTextView_isRepeat, false);
        mStartPoint = a.getInt(R.styleable.VerticalMarqueeTextView_VerticalMarqueeTextView_startPoint, 0);
        mDirection = a.getInt(R.styleable.VerticalMarqueeTextView_VerticalMarqueeTextView_direction, 0);
        mSpeed = a.getInt(R.styleable.VerticalMarqueeTextView_VerticalMarqueeTextView_speed, 20);
        if (mSpeed < 20) {
            mSpeed = 20;
        }
        a.recycle();

        point = new Point(0, 0);
        holder = this.getHolder();
        holder.addCallback(this);
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        setZOrderOnTop(true);//使surfaceview放到最顶层
        getHolder().setFormat(PixelFormat.TRANSLUCENT);//使窗口支持透明度
    }

    public void setText(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            measurementsText(msg);
        }
    }

    protected void measurementsText(String msg) {
        margueeString = msg;
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStrokeWidth(0.5f);
        mTextPaint.setFakeBoldText(true);
        textWidth = (int) mTextPaint.measureText(margueeString);
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        if (mStartPoint == 0)
            currentY = 50;
        else
            currentY = height;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mThread != null)
            mThread.isRun = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mThread != null)
            mThread.isRun = false;
    }

    /**
     * 线程是否在运行
     *
     * @return 结果
     */
    public boolean isThreadRunning() {
        return mThread != null && mThread.isRun && !mThread.isInterrupted();
    }

    /**
     * 开始滚动
     *
     * @param isStop 是否停止显示
     * @param sec    停止显示时间
     */
    public void startScroll(boolean isStop, int sec) {
        if (mThread != null) {
            return;
        }
        this.isStop = isStop;
        this.sec = sec * 1000;
        /*
         * 设置绘制多行文本所需参数
         *
         * @param string      文本
         * @param textPaint   文本笔
         * @param canvas      canvas
         * @param point       点
         * @param width       宽度
         * @param align       layout的对齐方式，有ALIGN_CENTER， ALIGN_NORMAL， ALIGN_OPPOSITE 三种。
         * @param spacingmult 相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度。
         * @param spacingadd  在基础行距上添加多少
         * @param includepad  参数未知（不知道啥，反正填false）
         * @param height      绘制高度
         */
        staticLayout = new StaticLayout(margueeString, mTextPaint, getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.5f, 0, false);

        //获取所有字的累加高度
        textHeight = staticLayout.getHeight();
        isFirstDraw = true;
        mThread = new MarqueeViewThread(holder);//创建一个绘图线程
        mThread.isRun = true;
        mThread.start();
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        if (mThread != null) {
            mThread.isRun = false;
        }
        mThread = null;
    }

    /**
     * 暂停播放
     */
    public void pauseScroll() {
        if (mThread != null) {
            mThread.isRun = false;
            mThread = null;
        }
    }

    /**
     * 恢复播放
     */
    public void restartRoll() {
        mThread = new MarqueeViewThread(holder);
        mThread.isRun = true;
        mThread.start();
    }

    /**
     * 请空内容
     */
    public void clearText() {
        if (mThread != null && mThread.isRun) {
            margueeString = "";
        }
    }

    /**
     * 是否继续滚动
     */
    private boolean isGo = true;

    /**
     * 线程
     */
    class MarqueeViewThread extends Thread {

        private final SurfaceHolder holder;

        public boolean isRun;//是否在运行


        public MarqueeViewThread(SurfaceHolder holder) {
            this.holder = holder;
            isRun = true;
        }

        public void onDraw() {
            try {
                synchronized (holder) {
                    if (TextUtils.isEmpty(margueeString)) {
                        Thread.sleep(1000);//睡眠时间为1秒
                        return;
                    }
                    if (isGo) {
                        final Canvas canvas = holder.lockCanvas();
                        int paddingTop = getPaddingTop();
                        int paddingBottom = getPaddingBottom();

                        int contentHeight = getHeight() - paddingTop - paddingBottom;

                        if (mDirection == 0) {//向上滚动
                            if (currentY <= -textHeight) {
                                currentY = contentHeight;
                                if (!mIsRepeat) {//如果是不重复滚动
                                    mHandler.sendEmptyMessage(ROLL_OVER);
                                    holder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。
                                    return;
                                }
                            } else {
                                currentY -= sepY;
                            }
                            currentY -= sepY;
                        } else {//  向下滚动
                            if (currentY >= textHeight + sepY + 10) {
                                currentY = 0;
                                if (!mIsRepeat) {//如果是不重复滚动
                                    mHandler.sendEmptyMessage(ROLL_OVER);
                                    holder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。
                                    return;
                                }
                            } else {
                                currentY += sepY;
                            }
                        }

                        if (canvas != null) {
                            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
                            textCenter(canvas, currentY);
                            holder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。
                            if (isFirstDraw) {
                                mHandler.sendEmptyMessageDelayed(STOP_ROLL, 50);//暂停显示5秒
                                isFirstDraw = false;
                            }
                        }
                        Thread.sleep(mSpeed);//睡眠时间为移动的频率~~
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (isRun) {
                onDraw();
            }
        }
    }

    /**
     * 绘制多行文本
     *
     * @param canvas canvas
     * @param height 绘制高度
     */
    private void textCenter(Canvas canvas, int height) {
        canvas.save();
        canvas.translate(0, height);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    public static final int ROLL_OVER = 100;//一条播放完毕
    public static final int STOP_ROLL = 200;//停止滚动
    public static final int START_ROLL = 300;//开始滚动
    public static final int STOP_THREAT = 400;//停止线程a
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ROLL_OVER:
                    stopScroll();
                    if (mOnMargueeListener != null) {
                        mOnMargueeListener.onRollOver();
                    }
                    break;
                case STOP_ROLL:
                    isGo = false;
                    mHandler.sendEmptyMessageDelayed(START_ROLL, sec);
                    break;
                case START_ROLL:
                    isGo = true;
                    break;
                case STOP_THREAT:
                    stopScroll();
                    break;
            }
        }
    };

    /**
     * dip转换为px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void reset() {
        int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (mStartPoint == 0)
            currentY = 0;
        else
            currentY = contentHeight;
    }

    /**
     * 滚动回调
     */
    public interface OnMargueeListener {
        void onRollOver();//滚动完毕
    }

    OnMargueeListener mOnMargueeListener;

    public void setOnMargueeListener(OnMargueeListener mOnMargueeListener) {
        this.mOnMargueeListener = mOnMargueeListener;
    }

}

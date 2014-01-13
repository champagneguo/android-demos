package com.example.draggridview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.utils.MLog;

@SuppressLint("NewApi")
public class DragGridView extends GridView{
	/**
	 * DragGridView��item������Ӧ��ʱ�䣬 Ĭ����1000���룬Ҳ������������
	 */
	private long dragResponseMS = 1000;
	
	/**
	 * �Ƿ������ק��Ĭ�ϲ�����
	 */
	private boolean isDrag = false;
	
	private int mDownX;
	private int mDownY;
	private int moveX;
	private int moveY;
	/**
	 * ������ק��position
	 */
	private int mDragPosition;
	
	/**
	 * �տ�ʼ��ק��item��Ӧ��View
	 */
	private View mStartDragItemView = null;
	
	/**
	 * ������ק�ľ�������ֱ����һ��ImageView
	 */
	private ImageView mDragImageView;
	
	/**
	 * ����
	 */
	private Vibrator mVibrator;
	
	private WindowManager mWindowManager;
	/**
	 * item����Ĳ��ֲ���
	 */
	private WindowManager.LayoutParams mWindowLayoutParams;
	
	/**
	 * ������ק��item��Ӧ��Bitmap
	 */
	private Bitmap mDragBitmap;
	
	/**
	 * ���µĵ㵽����item���ϱ�Ե�ľ���
	 */
	private int mPoint2ItemTop ; 
	
	/**
	 * ���µĵ㵽����item�����Ե�ľ���
	 */
	private int mPoint2ItemLeft;
	
	/**
	 * DragGridView������Ļ������ƫ����
	 */
	private int mOffset2Top;
	
	/**
	 * DragGridView������Ļ��ߵ�ƫ����
	 */
	private int mOffset2Left;
	
	/**
	 * ״̬���ĸ߶�
	 */
	private int mStatusHeight; 
	
	/**
	 * DragGridView�Զ����¹����ı߽�ֵ
	 */
	private int mDownScrollBorder;
	
	/**
	 * DragGridView�Զ����Ϲ����ı߽�ֵ
	 */
	private int mUpScrollBorder;
	
	/**
	 * DragGridView�Զ��������ٶ�
	 */
	private static final int speed = 10;
	
	/**
	 * item�����仯�ص��Ľӿ�
	 */
	private OnChanageListener mOnChanageListener;
	
	private int mDelta; 
	
	
	public DragGridView(Context context) {
		this(context, null);
	}
	
	public DragGridView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mStatusHeight = getStatusHeight(context); //��ȡ״̬���ĸ߶�
		mDelta = context.getResources().getDimensionPixelSize(R.dimen.max_delta);
	}
	
	private Handler mHandler = new Handler();
	
	//���������Ƿ�Ϊ������Runnable
	private Runnable mLongClickRunnable = new Runnable() {
		@Override
		public void run() {
			if (mOnChanageListener != null) {
				mOnChanageListener.onStartDrag();
			}
			
			isDrag = true; //���ÿ�����ק
			mVibrator.vibrate(50); //��һ��
			
			mStartDragItemView.setVisibility(View.INVISIBLE);//���ظ�item
			//�������ǰ��µĵ���ʾitem����
			createDragImage(mDragBitmap, mDownX, mDownY);
			
			//����������϶�״̬����û��Moveǰ���ɿ�����쳣����
			//����ģ��һ��move
			MotionEvent ev = MotionEvent.obtain(
					SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
					MotionEvent.ACTION_MOVE, 
					mDownX, mDownY, 0);
			dispatchTouchEvent(ev);
		}
	};
	
	/**
	 * ���ûص��ӿ�
	 * @param listener
	 */
	public void setOnChangeListener(OnChanageListener listener){
		mOnChanageListener = listener;
	}
	
	/**
	 * ������Ӧ��ק�ĺ�������Ĭ����1000����
	 * @param dragResponseMS
	 */
	public void setDragResponseMS(long dragResponseMS) {
		this.dragResponseMS = dragResponseMS;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			MLog.i("onInterceptTouchEvent(), down");
		} else if (action == MotionEvent.ACTION_UP) {
			MLog.i("onInterceptTouchEvent(), up");
		}
		
		if (isDrag) {
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			MLog.i("----------------------");
			MLog.i("dispatchTouchEvent(), down");
			
			mDownX = (int) ev.getX();
			mDownY = (int) ev.getY();
			
			//���ݰ��µ�X,Y�����ȡ�����item��position
			mDragPosition = pointToPosition(mDownX, mDownY);
			MLog.i("mDragPosition: "+mDragPosition);
			
			if(mDragPosition == AdapterView.INVALID_POSITION){
				return super.dispatchTouchEvent(ev);
			}
			
			//ʹ��Handler�ӳ�dragResponseMSִ��mLongClickRunnable
			mHandler.postDelayed(mLongClickRunnable, dragResponseMS);
			
			//����position��ȡ��item����Ӧ��View
			mStartDragItemView = getChildAt(getIndex(mDragPosition));
			initAnimArgs(mStartDragItemView);
			
			//�����⼸�������ҿ��Բο��ҵĲ��������ͼ�������
			mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
			mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();
			
			mOffset2Top = (int) (ev.getRawY() - mDownY);
			mOffset2Left = (int) (ev.getRawX() - mDownX);
			
			//��ȡDragGridView�Զ����Ϲ�����ƫ������С�����ֵ��DragGridView���¹���
			mDownScrollBorder = getHeight() /4;
			//��ȡDragGridView�Զ����¹�����ƫ�������������ֵ��DragGridView���Ϲ���
			mUpScrollBorder = getHeight() * 3/4;
			
			//����mDragItemView��ͼ����
			mStartDragItemView.setDrawingCacheEnabled(true);
			//��ȡmDragItemView�ڻ����е�Bitmap����
			mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
			//��һ���ܹؼ����ͷŻ�ͼ���棬��������ظ��ľ���
			mStartDragItemView.destroyDrawingCache();
			
			break;
		case MotionEvent.ACTION_MOVE:
//			MLog.i("dispatchTouchEvent(), move");
			int moveX = (int)ev.getX();
			int moveY = (int) ev.getY();
			
			//��������ڰ��µ�item�����ƶ���ֻҪ������item�ı߽����ǾͲ��Ƴ�mRunnable
			if(!isTouchInItem(mStartDragItemView, moveX, moveY)){
				mHandler.removeCallbacks(mLongClickRunnable);
			}
			if (!isMoveSmall(mDownX, mDownY, moveX, moveY)) {
				mHandler.removeCallbacks(mLongClickRunnable);
			}
			break;
		case MotionEvent.ACTION_UP:
			MLog.i("dispatchTouchEvent(), up");
			mHandler.removeCallbacks(mLongClickRunnable);
			mHandler.removeCallbacks(mScrollRunnable);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	private boolean isMoveSmall(int orgX, int orgY, int newX, int newY) {
		boolean small = true;
		if ((Math.abs(orgX-newX) > mDelta)
				|| (Math.abs(orgY-newY) > mDelta)) {
			small = false;
		}
		return small;
	}
	
	/**
	 * �Ƿ�����GridView��item����
	 * @param itemView
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isTouchInItem(View dragView, int x, int y){
		if (dragView == null) {
			return false;
		}
		
		int leftOffset = dragView.getLeft();
		int topOffset = dragView.getTop();
		if(x < leftOffset || x > leftOffset + dragView.getWidth()){
			return false;
		}
		
		if(y < topOffset || y > topOffset + dragView.getHeight()){
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		MLog.i("isDrag: " + isDrag);
		if(isDrag && mDragImageView != null){
			switch(ev.getAction()){
			case MotionEvent.ACTION_DOWN:
				MLog.i("onTouchEvent(), down");
				break;
			case MotionEvent.ACTION_MOVE:
//				MLog.i("onTouchEvent(), move");
				moveX = (int) ev.getX();
				moveY = (int) ev.getY();
				//�϶�item
				onDragItem(moveX, moveY);
				break;
			case MotionEvent.ACTION_UP:
				MLog.i("onTouchEvent(), up");
				onStopDrag();
				isDrag = false;
				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * �����϶��ľ���
	 * @param bitmap 
	 * @param downX
	 * 			���µĵ���Ը��ؼ���X����
	 * @param downY
	 * 			���µĵ���Ը��ؼ���X����
	 */
	private void createDragImage(Bitmap bitmap, int downX , int downY){
		mWindowLayoutParams = new WindowManager.LayoutParams();
		mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //ͼƬ֮��������ط�͸��
		mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
		mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
		mWindowLayoutParams.alpha = 0.55f; //͸����
		mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
		mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
		mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  
	                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;
		  
		mDragImageView = new ImageView(getContext());  
		mDragImageView.setImageBitmap(bitmap);  
		mWindowManager.addView(mDragImageView, mWindowLayoutParams);  
	}
	
	/**
	 * �ӽ��������ƶ��϶�����
	 */
	private void removeDragImage(){
		if(mDragImageView != null){
			mWindowManager.removeView(mDragImageView);
			mDragImageView = null;
		}
	}
	
	/**
	 * �϶�item��������ʵ����item�����λ�ø��£�item���໥�����Լ�GridView�����й���
	 * @param x
	 * @param y
	 */
	private void onDragItem(int moveX, int moveY){
		mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
		mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
		mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //���¾����λ��
		onSwapItem(moveX, moveY);
		
		//GridView�Զ�����
		mHandler.post(mScrollRunnable);
	}
	
	/**
	 * ��moveY��ֵ�������Ϲ����ı߽�ֵ������GridView�Զ����Ϲ���
	 * ��moveY��ֵС�����¹����ı߽�ֵ������GridView�Զ����¹���
	 * ���򲻽��й���
	 */
	private Runnable mScrollRunnable = new Runnable() {
		
		@Override
		public void run() {
			int scrollY;
			if(moveY > mUpScrollBorder){
				 scrollY = -speed;
				 mHandler.postDelayed(mScrollRunnable, 25);
			}else if(moveY < mDownScrollBorder){
				scrollY = speed;
				 mHandler.postDelayed(mScrollRunnable, 25);
			}else{
				scrollY = 0;
				mHandler.removeCallbacks(mScrollRunnable);
			}
			
			//�����ǵ���ָ����GridView���ϻ������¹�����ƫ������ʱ�򣬿���������ָû���ƶ�������DragGridView���Զ��Ĺ���
			//�������������������onSwapItem()����������item
			onSwapItem(moveX, moveY);
			
//			smoothScrollToPositionFromTop(mDragPosition, view.getTop() + scrollY);
			
			//ʵ��GridView���Զ�����
			smoothScrollBy(-scrollY, 10);
		}
	};
	
	/**
	 * ����item,���ҿ���item֮�����ʾ������Ч��
	 * @param moveX
	 * @param moveY
	 */
	private void onSwapItem(int moveX, int moveY){
		//��ȡ������ָ�ƶ������Ǹ�item��position
		int tempPosition = pointToPosition(moveX, moveY);
		
		//����tempPosition �ı��˲���tempPosition������-1,����н���
		if(tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION){
//			setChildVisible(tempPosition, View.INVISIBLE);//�϶������µ�item,�µ�item���ص�
//			setChildVisible(mDragPosition, View.VISIBLE);//֮ǰ��item��ʾ����
			
//			if(mOnChanageListener != null){
//				mOnChanageListener.onChange(mDragPosition, tempPosition);
//			}
//			mDragPosition = tempPosition;

			doItemAnimation(mDragPosition, tempPosition);
		}
	}
	
	/**
	 * ֹͣ��ק���ǽ�֮ǰ���ص�item��ʾ���������������Ƴ�
	 */
	private void onStopDrag(){
		if (mOnChanageListener != null) {
			mOnChanageListener.onStopDrag();
		}
		setChildVisible(mDragPosition, View.VISIBLE);
		removeDragImage();
	}
	
	/**
	 * ��ȡ״̬���ĸ߶�
	 * @param context
	 * @return
	 */
	private static int getStatusHeight(Context context){
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        return statusHeight;
    }
	
	private void setChildVisible(int position, int visable) {
		getChildAt(getIndex(position)).setVisibility(visable);
	}
	
	private int getIndex(int position) {
		return position - getFirstVisiblePosition();
	}
	
	public interface OnChanageListener{
		void onStartDrag();
		void onStopDrag();
		
		/**
		 * ��item����λ�õ�ʱ��ص��ķ���������ֻ��Ҫ�ڸ÷�����ʵ�����ݵĽ�������
		 * @param from
		 * 			��ʼ��position
		 * @param to 
		 * 			��ק����position
		 */
		void onChange(int from, int to);
	}
	
	//-----------------------------
	private static final int ANIMATION_TIME = 300;
	private boolean isMoving;
	private int mColumns;
	private int mTempPosition;
	private String LastAnimationID;
	private float mXscale, mYscale;
	
	private void initAnimArgs(View item) {
		isMoving = false;
		mTempPosition = -1;
		mColumns = getNumColumns();
		
		mXscale = 1.0f + (1.0f*getHorizontalSpacing() / item.getWidth());
		mYscale = 1.0f + (1.0f*getVerticalSpacing() / item.getHeight());
	}
	
	private void doItemAnimation(int from, int to) {
		int moveNum;
		int dst, src;
		int vector;
		View item = null;
		Animation anim = null;
		
		if (isMoving) {
			return;
		}
		
		moveNum = to - from;
		if (moveNum == 0) {
			return;
		}
		
		vector = moveNum>0 ? 1 : -1;

		moveNum = Math.abs(moveNum);
		dst = from;
		for (int i=0; i<moveNum; i++) {
			src = dst + vector;
			MLog.i("" + src + " -> " + dst);
			item = getChildAt(getIndex(src)); 
			anim = startItemAnimation(src, dst);
			item.startAnimation(anim);
			dst = src;
		}
		LastAnimationID = anim.toString();
		mTempPosition = to;
	}
	
    public Animation startItemAnimation(int src, int dst){
    	float Xoffset,Yoffset;
    	
    	Xoffset = ((dst % mColumns) - (src % mColumns))*mXscale;
    	Yoffset = ((dst / mColumns) - (src / mColumns))*mYscale;
    	
        TranslateAnimation anim = new TranslateAnimation(
        		Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, Xoffset, 
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, Yoffset);
        //�Ƿ�ִ����ֹ���Ч����true��ʾʹ�ܸ�Ч����false��ʾ���ø�Ч����    
        anim.setFillAfter(false);
        anim.setDuration(ANIMATION_TIME);
        anim.setAnimationListener(mAnimListener);
        return anim;
    }
    
	private AnimationListener mAnimListener = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			isMoving = true;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {}

		@Override
		public void onAnimationEnd(Animation animation) {
			String animaionID = animation.toString();
			if (animaionID.equalsIgnoreCase(LastAnimationID)) {
				// adapter.exchange(startPosition, dropPosition);
				// startPosition = dropPosition;
				if (mOnChanageListener != null) {
					mOnChanageListener.onChange(mDragPosition, mTempPosition);
				}
				setChildVisible(mTempPosition, View.INVISIBLE);//�϶������µ�item,�µ�item���ص�
				setChildVisible(mDragPosition, View.VISIBLE);//֮ǰ��item��ʾ����
				mDragPosition = mTempPosition;
				isMoving = false;
			}
		}
	};
}

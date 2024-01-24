package jp.sourceforge.gokigen.aligner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;


/**
 *  折れ線グラフの描画クラス
 * 
 * @author MRSa
 *
 */
public class GokigenSideBodyDrawer implements IBodyDrawer
{	
	
	private Context parent = null;
	private ImageAdjuster imageSetter = null;
	
	private final float HEAD_MARGIN = 32;
	private final float CENTER_AREA = 8;

    private String messageToShow = "";
    
    private float positionX = -1;
    private float positionY = -1;

    private float previousPositionX = -1;
    private float previousPositionY = -1;

    private float posRadius = 3;

    private float screenWidth = 1;
    private float screenHeight = 1;

    private float downPositionX = -1;
    private float downPositionY = -1;
    
    private float myRadius = 16;
    
    private JointPosition headCenterPos = null;
    private JointPosition headBottomPos = null;
    private JointPosition neckPos = null;
    private JointPosition chestPos = null;
    private JointPosition bodyPos = null;
    private JointPosition waistPos = null;
    private JointPosition kneePos = null;
    private JointPosition anklePos = null;
    private JointPosition thighPos = null;
    private JointPosition legPos = null;

    private JointPosition currentPosition = null;

    /**
     *  コンストラクタ
     * 
     */
	public GokigenSideBodyDrawer(Context arg, ImageAdjuster holder)
	{
        parent = arg;
        imageSetter = holder;
        prepareInitialPositions();
	}	
	

	/**
	 *  準備
	 * 
	 */
	public void prepare()
	{
        // 表示するメッセージを初期化する
		setMessage(null);
	}
 
	/**
	 *  初期位置を設定する
	 * 
	 */
	private void prepareInitialPositions()
	{
	    headCenterPos = new JointPosition(((float) (HEAD_MARGIN + myRadius)) / (float) 300, 0);
	    headBottomPos = new JointPosition((float) (HEAD_MARGIN + myRadius + myRadius) / (float) 300, 0);

	    neckPos = new JointPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16) / (float) 300, 0);
	    chestPos = new JointPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32) / (float) 300, 0);
	    
	    bodyPos = new JointPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44) / (float) 300, 0);
	    waistPos = new JointPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44 + 32) / (float) 300, 0);

	    thighPos = new JointPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44 + 32 + 8 + 28) / (float) 300, 0);

	    kneePos = new JointPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44 + 32 + 8 + 44) / (float) 300, 0);
	    anklePos = new JointPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44 + 32 + 8 + 44) / (float) 300, 0);

	    legPos = new JointPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44 + 32 + 8 + 44 + 28) / (float) 300, 0);

	    extractPosition();
	}
	
	
	/**
	 *  表示するメッセージを設定する
	 * 
	 * @param message
	 */
	public void setMessage(String message)
	{
		if (message == null)
		{
	        // 表示するメッセージを設定する
			messageToShow = parent.getString(R.string.captureInfo);			
		}
		else
		{
            // 指定されたメッセージを設定する
            messageToShow = message;
		}
		return;
	}
	
	/**
	 *  描画メイン処理
	 * 
	 */
    public void drawOnCanvas(Canvas canvas, int reportType)
    {
    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        //Log.v(Main.APP_IDENTIFIER, "GokigenScaleDrawer::drawOnCanvas()");

        screenWidth = canvas.getWidth();
        screenHeight = canvas.getHeight();

        canvas.drawColor(Color.BLACK, Mode.CLEAR);

        // ビットマップデータの表示
    	if (imageSetter != null)
    	{
    		try
    		{
    	         Bitmap bitmap = imageSetter.getImageBitmap();
    	         if (bitmap != null)
    	         {
    	        	 canvas.drawBitmap(bitmap, 0, 0, paint);
    	         }
    	         else 
    	         {
    	        	 Log.v(Main.APP_IDENTIFIER, "BMP is NULL");
    	         }
    		}
    		catch (Exception ex)
    		{
    			Log.v(Main.APP_IDENTIFIER, "BITMAP " + ex.toString());
    		}
    	}

        // 体のラインを表示する
        paint.setColor(Color.BLACK);
        drawBody(canvas, paint, 1, 1);
      
        paint.setColor(Color.WHITE);
        drawBody(canvas, paint, 0, 0);

        // 文字列の大きさを設定する
        Rect bounds = new Rect();
        paint.getTextBounds(messageToShow, 0, messageToShow.length(), bounds);
        
        // インフォメーションを表示
        paint.setColor(Color.BLACK);
        canvas.drawText(messageToShow, (screenWidth - (bounds.width() + 4)), (screenHeight - (bounds.height() + 4)), paint);

        paint.setColor(Color.LTGRAY);
        canvas.drawText(messageToShow, (screenWidth - (bounds.width() + 5)), (screenHeight - (bounds.height() + 5)), paint);
        
        
        if (((positionX >= 0)&&(positionY >= 0))&&((downPositionX >= 0)&&(downPositionY >= 0)))
        {
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(positionX, positionY, posRadius, paint);
           // canvas.drawLine(downPositionX, downPositionY, positionX, positionY, paint);
        }
    }

    /**
     *  からだのラインを表示する
     * 
     * @param canvas
     * @param paint
     * @param offsetX
     * @param offsetY
     */
    private void drawBody(Canvas canvas, Paint paint, float offsetX, float offsetY)
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float centerPosY = ((float) height / (float) 2);
        
        // 頭を描画する
        paint.setStyle(Paint.Style.STROKE);
        float centerX = headCenterPos.getX() * width + offsetX;
        float centerY = headCenterPos.getY() * height + centerPosY + offsetY;
        float radius = ((float) (myRadius)) / (float) 300 * width;
        RectF oval = new RectF((centerX - radius), (centerY - radius / 3), (centerX + radius), (centerY + radius / 3));
        canvas.drawOval(oval, paint);

        
        // 首を描画する
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float startX = headBottomPos.getX() * width + offsetX;
        float startY = headBottomPos.getY() * height + centerPosY + offsetY;

        float finishX = neckPos.getX() * width + offsetX;
        float finishY = neckPos.getY() * height + centerPosY + offsetY;
        canvas.drawLine(startX, startY, finishX, finishY, paint);
        //canvas.drawCircle(finishX, finishY, posRadius, paint);
        

        // 腕と手を描画する
        /*
        startX = neckPos.getX() * width + offsetX;
        startY = neckPos.getY() * height + centerPosY + offsetY;
        centerY = startY;
        float length = (float) (40) / (float) 300 * width;
        float lengthY2 =  (((float) 5) / ((float) 300) * width);
        canvas.drawLine(startX, startY, (startX + length), startY - lengthY2, paint);

        startX = startX + length;
        length = (float) (56) / (float) 300 * width;
        canvas.drawLine(startX, startY - lengthY2, (startX + length), startY + lengthY2, paint);
        */

        // 胸を描画する        
        startX = neckPos.getX() * width + offsetX;
        startY = neckPos.getY() * height + centerPosY + offsetY;
        finishX = chestPos.getX() * width + offsetX;
        finishY = chestPos.getY() * height + centerPosY + offsetY;
        canvas.drawLine(startX, startY, finishX, finishY, paint);
        canvas.drawCircle(finishX, finishY, posRadius, paint);

        // 胴を描画する        
        startX = chestPos.getX() * width + offsetX;
        startY = chestPos.getY() * height + centerPosY + offsetY;
        finishX = waistPos.getX() * width + offsetX;
        finishY = waistPos.getY() * height + centerPosY + offsetY;
        canvas.drawLine(startX, startY, finishX, finishY, paint);
        canvas.drawCircle(finishX, finishY, posRadius, paint);
                
        // 太ももを描画する
        startX = waistPos.getX() * width + offsetX;
        startY = waistPos.getY() * height + centerPosY + offsetY;
        finishX = kneePos.getX() * width + offsetX;
        finishY = kneePos.getY() * height + centerPosY + offsetY;
        canvas.drawLine(startX, startY, finishX, finishY, paint);
        canvas.drawCircle(finishX, finishY, posRadius, paint);

        // 足を描画する
        startX = kneePos.getX() * width + offsetX;
        startY = kneePos.getY() * height + centerPosY + offsetY;
        finishX = anklePos.getX() * width + offsetX;
        finishY = anklePos.getY() * height + centerPosY + offsetY;
        canvas.drawLine(startX, startY, finishX, finishY, paint);
        canvas.drawCircle(finishX, finishY, posRadius, paint);
        
     }

    /**
     *  点をセットする
     * 
     * @param posX
     * @param posY
     */
    public void setPosition(JointPosition target, float targetX, float targetY, float posX, float posY)
    {
    	if (target == null)
    	{
    		return;
    	}

    	positionX = posX;
        positionY = posY;

    	// 動かす前に前の位置を覚えておく
    	previousPositionX = target.getX();
    	previousPositionY = target.getY();

    	if (target == headCenterPos)
    	{
    		// 頭の位置を移動させる
    	    if (targetX > headBottomPos.getX())
    	    {
    	    	targetX = headBottomPos.getX();
    	    }
    	    target.setPosition(targetX, 0);

    	    // 首の下の位置を更新する
    	    Log.v(Main.APP_IDENTIFIER, "HEAD BOTTOM : " + headBottomPos.getX());
    	    float radius = ((float) (myRadius) / (float) 300);
    	    headBottomPos.setPosition((targetX + radius), 0);

    	    // 首の付け根の位置を更新する
    	    neckPos.setPosition(headBottomPos.getX() + ((float) 16 / 300), 0);

    	    //  中心を頭の中心に合わせて移動させる ...のはやらない。(今は?)
    	    //moveCenterPosition(targetY);
    	    return;
        }
    	
    	if (target == neckPos)
    	{
    		// 首の位置を移動させる
    		if (targetX < headBottomPos.getX())
    		{
                targetX = headBottomPos.getX();
    		}
    		else if (targetX > chestPos.getX())
    		{
    			targetX = chestPos.getX();
    		}
    	    target.setPosition(targetX, headCenterPos.getY());
    		return;
    	}

    	if (target == chestPos)
    	{
    		// 首の位置を移動させる
    		if (targetX < neckPos.getX())
    		{
                targetX = neckPos.getX();
    		}
    		else if (targetX > bodyPos.getX())
    		{
    			targetX = bodyPos.getX();
    		}
    	    target.setPosition(targetX, targetY);
    		return;
    	}

    	if (target == waistPos)
    	{
    		// 腰の位置を移動させる
    		if (targetX < bodyPos.getX())
    		{
                targetX = bodyPos.getX();
    		}
    		else if (targetX > thighPos.getX())
    		{
    			targetX = thighPos.getX();
    		}
    		/*
    		if (targetY < bodyPos.getY())
    		{
    			targetY = bodyPos.getY();
    		}
    		else if (targetY > thighPos.getY())
    		{
    			targetY = thighPos.getY();
    		}
    		*/
    	    target.setPosition(targetX, targetY);
    		return;
    	}

    	if (target == kneePos)
    	{
    		// ひざの位置を移動させる
    		if (targetX < thighPos.getX())
    		{
                targetX = thighPos.getX();
    		}
    		else if (targetX > legPos.getX())
    		{
    			targetX = legPos.getX();
    		}
    	    target.setPosition(targetX, targetY);
    		return;    		
    	}

    	if (target == anklePos)
    	{
    		// 足の位置を移動させる
    		if (targetX < legPos.getX())
    		{
                targetX = legPos.getX();
    		}
    	    target.setPosition(targetX, targetY);
    		return;    		
    	}
	    target.setPosition(targetX, targetY);
	    return;    		
    }

    /**
     * 
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	boolean ret = false;
        int action = event.getAction();
        switch (action)
    	{
          case MotionEvent.ACTION_DOWN:
        	// 押した場所を記憶
            downPositionX = event.getX();
            downPositionY = event.getY();
            setPosition(null, -1, -1, -1, -1);
            break;

          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
        	// フリックされて離された場合...
        	currentPosition = decideMovePoint(event.getX(), event.getY());
            setPosition(currentPosition, (event.getX() / screenWidth), ((event.getY() - (screenHeight / 2))/ screenHeight), event.getX(), event.getY());
            ret = true;
            break;

          case MotionEvent.ACTION_MOVE:
        	// 移動中は何もしない
        	break;

          case MotionEvent.ACTION_OUTSIDE:
          default:
    	    Log.v(Main.APP_IDENTIFIER, "onTouchEvent() :" + event.getAction() + "(" + event.getX() + "," + event.getY() + ")");
    	    break;
    	}
        return (ret);
    }
    /**
     *  トラックボールが動かされたときの処理
     * 
     * @param event
     * @return
     */
    public boolean onTrackballEvent(MotionEvent event)
    {
    	boolean ret = true;
        //Log.v(Main.APP_IDENTIFIER, "onTrackballEvent() [" + event.getX() + "," + event.getY() + "]");
    	
    	if (currentPosition == null)
    	{
    		// 移動位置が未定...何もせずに終了する
    		return (ret);
    	}
    	float moveX = event.getX() + positionX;
    	float moveY = event.getY() + positionY;
        setPosition(currentPosition, (moveX / screenWidth), ((moveY - (screenHeight / 2))/ screenHeight), moveX, moveY);

        return (ret);
    }

    /**
     *  体全体を動かす
     * 
     * @param moveY
     */
    private void moveCenterPosition(float moveY)
    {
        movePositionY(headCenterPos, moveY);
        movePositionY(headBottomPos, moveY);
    	movePositionY(neckPos, moveY);
        movePositionY(chestPos, moveY);
        movePositionY(bodyPos, moveY);    	
        movePositionY(waistPos, moveY);
        movePositionY(thighPos, moveY);    	
        movePositionY(kneePos, moveY);
        movePositionY(legPos, moveY);    	
        movePositionY(anklePos, moveY);
    }

    /**
     *  体の縦位置を移動させる（メイン処理）
     * 
     * @param target
     * @param moveY
     */
    private void movePositionY(JointPosition target, float moveY)
    {
        if (target == null)
        {
        	return;
        }
        target.setPosition(target.getX(), moveY);
    }
    
    /**
     *  移動させた位置をリセットさせる
     * 
     */
    public void reset()
    {
	    headCenterPos.setPosition(((float) (HEAD_MARGIN + myRadius)) / (float) 300, 0);
	    headBottomPos.setPosition((float) (HEAD_MARGIN + myRadius + myRadius) / (float) 300, 0);

	    neckPos.setPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16) / (float) 300, 0);

	    chestPos.setPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32) / (float) 300, 0);
	    
	    bodyPos.setPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44) / (float) 300, 0);
	    waistPos.setPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44 + 32) / (float) 300, 0);

	    thighPos.setPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44 + 32 + 8 + 28) / (float) 300, 0);

	    kneePos.setPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44 + 32 + 8 + 44) / (float) 300, 0);

	    legPos.setPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44 + 32 + 8 + 44 + 28) / (float) 300, 0);

	    anklePos.setPosition((float) (HEAD_MARGIN + myRadius + myRadius + 16 + 32 + 44 + 32 + 8 + 44 + 56) / (float) 300, 0);

	    // 初期化位置を記憶する...
	    storePosition();
    }
    
    /**
     *  一つ前の位置に戻る
     * 
     */
    public void undo()
    {
    	if ((currentPosition != null)&&(previousPositionX > 0)&&(previousPositionY > 0))
    	{
            currentPosition.setPosition(previousPositionX, previousPositionY);
        }
    }

    /**
     * 
     * 
     * @param posX 新ポジション (X)
     * @param posY 新ポジション (Y)
     * @return
     */
    private JointPosition decideMovePoint(float checkPosX, float checkPosY)
    {
    	// 内部管理の座標系に置き換え
    	float oldPosX = downPositionX / screenWidth;
    	float oldPosY = downPositionY - (screenHeight / 2);
    	float newPosX = checkPosX / screenWidth;
    	float newPosY = checkPosY - (screenHeight / 2);
    	
		Log.v(Main.APP_IDENTIFIER, "decideMovePoint: (" + oldPosX + "," + oldPosY + ") => (" + newPosX + "," + newPosY + ")");
        if (oldPosX < headBottomPos.getX())
        {
            // 頭の位置を移動させる
        	return (headCenterPos);
        }
/*
        if ((oldPosX > headBottomPos.getX())&&(oldPosX < (neckPos.getX() + CENTER_AREA)))
    	{
    		float neckPosY = headBottomPos.getY();
    		if ((oldPosY > ((neckPosY) - CENTER_AREA))&&(oldPosY < ((neckPosY) + CENTER_AREA)))
            {
    			// 首の位置を移動させる
        		//Log.v(Main.APP_IDENTIFIER, "NECK (" + neckPosY + ")");
        		return (neckPos);
            }
    	}
*/
/*
        if ((oldPosX > headBottomPos.getX())&&(oldPosX < chestPos.getX()))
    	{
    		float neckPosY = headBottomPos.getY();
    		if ((oldPosY > ((neckPosY) - CENTER_AREA))&&(oldPosY < ((neckPosY) + CENTER_AREA)))
            {
    			// 首の位置を移動させる
        		//Log.v(Main.APP_IDENTIFIER, "NECK (" + neckPosY + ")");
        		return (neckPos);
            }

    		// 肩位置を移動させる...左右の肩のどちらかか判定する　
    		if (((oldPosY < neckPosY)&&(newPosY < neckPosY)))
    		{
        		//Log.v(Main.APP_IDENTIFIER, "LEFT SHOULDER (" + neckPosY + ")");
        		return (shoulder1Pos);    			
    		}
    		else if (((oldPosY > neckPosY)&&(newPosY > neckPosY)))
    		{
        		//Log.v(Main.APP_IDENTIFIER, "RIGHT SHOULDER (" + neckPosY + ")");
    		    return (shoulder2Pos);
    		}
    		//Log.v(Main.APP_IDENTIFIER, "Neck Position??? " + oldPosX + "," + oldPosY + "  " + newPosX + "," + newPosY);
            return (neckPos);
    	}
*/
    	if ((oldPosX >neckPos.getX())&&(oldPosX < bodyPos.getX()))
    	{
            return (chestPos);
    	}

    	if ((oldPosX > bodyPos.getX())&&(oldPosX < thighPos.getX()))
    	{
    		return (waistPos);
    	}
    	
    	if ((oldPosX > thighPos.getX())&&(oldPosX < legPos.getX()))
    	{
            return (kneePos);
    	}

    	if (oldPosX > legPos.getX())
    	{
            return (anklePos);
    	}
    	return (null);
    }

    /**
     *  現在位置を記憶する
     * 
     */
    public void storePosition()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("headCenterX", headCenterPos.getX());
        editor.putFloat("headCenterY", headCenterPos.getY());
        editor.putFloat("headBottomX", headBottomPos.getX());
        editor.putFloat("headBottomY", headBottomPos.getY());
        editor.putFloat("neckX",       neckPos.getX());
        editor.putFloat("neckY",       neckPos.getY());
        editor.putFloat("chestX",      chestPos.getX());
        editor.putFloat("chestY",      chestPos.getY());
        editor.putFloat("bodyX",       bodyPos.getX());
        editor.putFloat("bodyY",       bodyPos.getY());
        editor.putFloat("waistX",      waistPos.getX());
        editor.putFloat("waistY",      waistPos.getY());
        editor.putFloat("knee1X",      kneePos.getX());
        editor.putFloat("knee1Y",      kneePos.getY());
        editor.putFloat("ankle1X",     anklePos.getX());
        editor.putFloat("ankle1Y",     anklePos.getY());
        editor.putFloat("thighX",      thighPos.getX());
        editor.putFloat("thighY",      thighPos.getY());
        editor.putFloat("legX",        legPos.getX());
        editor.putFloat("legY",        legPos.getY());
        editor.commit();

        currentPosition = null;
    }

    

    /**
     *  位置を元に戻す
     * 
     */
    public void extractPosition()
    {
    	float posX, posY;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);

        posX = preferences.getFloat("headCenterX", headCenterPos.getX());
        posY = preferences.getFloat("headCenterY", headCenterPos.getY());
        headCenterPos.setPosition(posX, posY);

        posX = preferences.getFloat("headBottomX", headBottomPos.getX());
        posY = preferences.getFloat("headBottomY", headBottomPos.getY());
        headBottomPos.setPosition(posX, posY);

        posX = preferences.getFloat("neckX",       neckPos.getX());
        posY = preferences.getFloat("neckY",       neckPos.getY());
        neckPos.setPosition(posX, posY);

        posX = preferences.getFloat("chestX",      chestPos.getX());
        posY = preferences.getFloat("chestY",      chestPos.getY());
        chestPos.setPosition(posX, posY);


        posX = preferences.getFloat("bodyX",       bodyPos.getX());
        posY = preferences.getFloat("bodyY",       bodyPos.getY());
        bodyPos.setPosition(posX, posY);

        posX = preferences.getFloat("waistX",      waistPos.getX());
        posY = preferences.getFloat("waistY",      waistPos.getY());
        waistPos.setPosition(posX, posY);

        posX = preferences.getFloat("knee1X",      kneePos.getX());
        posY = preferences.getFloat("knee1Y",      kneePos.getY());
        kneePos.setPosition(posX, posY);

        posX = preferences.getFloat("ankle1X",     anklePos.getX());
        posY = preferences.getFloat("ankle1Y",     anklePos.getY());
        anklePos.setPosition(posX, posY);

        posX = preferences.getFloat("thighX",      thighPos.getX());
        posY = preferences.getFloat("thighY",      thighPos.getY());
        thighPos.setPosition(posX, posY);

        posX = preferences.getFloat("legX",        legPos.getX());
        posY = preferences.getFloat("legY",        legPos.getY());
        legPos.setPosition(posX, posY);

        currentPosition = null;
    }

    public void setDrawType(int type)
    {
    	// なにもしない...
    }

    private class JointPosition
    {
    	private float positionX = -1;
    	private float positionY = -1;

    	/**
    	 *  コンストラクタ
    	 * @param posX
    	 * @param posY
    	 */
        public JointPosition(float posX, float posY)
        {
        	positionX = posX;
        	positionY = posY;
        }
        
        public float getX()
        {
        	return (positionX);
        }
        
        public float getY()
        {
            return (positionY);
        }
        
        /**
         *  位置を設定する
         * 
         * @param posX
         * @param posY
         */
        private void setPosition(float posX, float posY)
        {
            positionX = posX;
            positionY = posY;        	
        }
    }
}

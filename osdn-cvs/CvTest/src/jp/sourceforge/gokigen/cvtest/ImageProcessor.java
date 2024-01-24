package jp.sourceforge.gokigen.cvtest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.sourceforge.gokigen.cvtest.QSteerControlDrawer.IRedrawer;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import android.hardware.Camera;
import android.util.Log;

/**
 * 
 * @author MRSa
 *
 */
public class ImageProcessor extends Thread implements ICameraDataReceiver, Runnable
{
    private IRedrawer trigger = null;
	private byte[] bufferImage = null;
    private boolean isThreadRun = false;
    private int tick = 0;
	private Bitmap targetBitmap = null;
	private Canvas targetCanvas = null;

	private boolean isBackgroundCaptured = false;
	private Mat backgroundImage = null;

	private Mat mIntermediateMat = null;
	private Mat mIntermediateMat2 = null;
    private Mat mGraySubmat = null;
    private Mat mYuv = null;
    private Mat mRgba = null;
    
    private double momentX = -1.0;
    private double momentY = -1.0;
    private double rateX = 1.0;
    private double rateY = 1.0;
    private double momentSize = 0;

    private final double POSITION_LIMIT = 200.0;
    private List<Position> history = new ArrayList<Position>();
    private boolean isDrawHistory = false;

    private int appearTick = -1;
    private double appearMomentX = -1.0;
    private double appearMomentY = -1.0;
    private boolean isPublishMove = false;
    private double STAY_THRESHOLD = 5.0d;
    private int STAY_THRESHOLD_COUNT = 10;
    private final int DETECT_OBJECT_SIZE = 160;

    
    /**
     *    �R���X�g���N�^
     */
	public ImageProcessor(IRedrawer trigger)
    {
        Log.i(GokigenSymbols.APP_IDENTIFIER, ">Instantiated new " + this.getClass());
		this.trigger = trigger;
		history.clear();
    }
	
	/**
	 *   �N�����̏���...
	 */
	public void prepareToStart(int width, int height)
    {
		Log.v(GokigenSymbols.APP_IDENTIFIER, "ImageProcessor::prepareToStart()" + " w:" + width + " h:" + height);
    }

	/**
	 *   �N�����̏���...
	 */
	public void prepareToReceive()
	{
		Log.v(GokigenSymbols.APP_IDENTIFIER, "ImageProcessor::prepareToReceive()");
	}

	/**
	 *    �X���b�h�ŉ摜�����H���鏈��...
	 * 
	 */
    public void run()
    {
        isThreadRun = true;
        Log.i(GokigenSymbols.APP_IDENTIFIER, "Starting processing thread...");
        while (isThreadRun)
        {
        	imageProcessMain();
        }
        Log.i(GokigenSymbols.APP_IDENTIFIER, "Finished processing thread...");
     }

    /**
	 *   �I������Ƃ��̏����B�i�̈���J������B�j
	 */
	public void finished()
	{
        isThreadRun = false;
        Log.v(GokigenSymbols.APP_IDENTIFIER, "ImageProcessor::finished()");
        
		if (targetBitmap != null)
		{
            targetBitmap.recycle();
		}
        if (mYuv != null)
        {
        	mYuv.release();
        }
        if (mRgba != null)
        {
            mRgba.release();
        }
        if (mGraySubmat != null)
        {
        	mGraySubmat.release();
        }
        if (mIntermediateMat != null)
        {
            mIntermediateMat.release();
        }
        if (mIntermediateMat2 != null)
        {
            mIntermediateMat2.release();
        }
        if (backgroundImage != null)
        {
        	backgroundImage.release();
        }
        mYuv = null;
        mRgba = null;
        mGraySubmat = null;
        mIntermediateMat = null;
        mIntermediateMat2 = null;
        backgroundImage = null;
	}

	/**
     *     �������\�b�h...
     * 
     */
    private synchronized void imageProcessMain()
    {
        boolean isDraw = false;
        try
        {
            ImageProcessor.this.wait();
            isDraw = processFrame(bufferImage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.v(GokigenSymbols.APP_IDENTIFIER, "ImageProcessor::imageProcessMain() " + e.toString());
        }
        if ((isDraw == true)&&(trigger != null))
        {
            // �r�b�g�}�b�v�̕`�揈��...
        	trigger.redraw();
        }
    }

	/**
	 * 
	 * 
	 */
	public synchronized void onPreviewFrame(byte[] arg0, Camera arg1)
	{
		int bufSize = arg0.length;
		bufferImage = new byte[bufSize];
		System.arraycopy(arg0, 0, bufferImage, 0, bufSize);
		try
		{
			ImageProcessor.this.notify();
		}
		catch (Exception e)
		{
            Log.v(GokigenSymbols.APP_IDENTIFIER, "onPreviewFrame() EX>" + e.toString() + " ");
		}
	}

	/**
	 *    preview ���J�n�������ɌĂ΂��...
	 * 
	 */
	public void onPreviewStared(int width, int height, int frameWidth, int frameHeight)
	{	
		//
		Log.v(GokigenSymbols.APP_IDENTIFIER, "ImageProcessor::onPreviewStared() w:" + width + " h:" +height + " fw:" + frameWidth + " fh:" + frameHeight);
		prepare(frameWidth, frameHeight);

		rateX = (double) ((double) width) / ((double) frameWidth);
		rateY = (double) ((double) height) / ((double) frameHeight);
		
		(new Thread(this)).start();
	}

	/****************************************************************************/

	public void updateBackgroundImage()
	{
		// ���݂̃C���[�W��ۊǂ���B
        //backgroundImage = mRgba.clone();
		backgroundImage = mGraySubmat.clone();
		isBackgroundCaptured = true;

		// �ړ��ʒu���̃N���A
		history.clear();
		momentX = -1.0;
		momentY = -1.0;
		momentSize = 0.0;
	}
	
	/**
	 *   ���H�O����
	 * @param width  ��
	 * @param height ����
	 */
	private void prepare(int width, int height)
	{
		try
        {
		    synchronized (this)
		    {
	        	mYuv = new Mat(height + height / 2, width, CvType.CV_8UC1);
	        	mGraySubmat = mYuv.submat(0, height, 0, width);
	        	mRgba = new Mat();
	        	mIntermediateMat = new Mat();
	        	mIntermediateMat2 = new Mat();
		    }
        	targetBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		    targetCanvas = new Canvas(targetBitmap);
        }
		catch (Exception e)
		{
            targetBitmap = null;
            targetCanvas = null;
            
            mYuv = null;
            mGraySubmat = null;
            mRgba = null;
            mIntermediateMat = null;
            mIntermediateMat2 = null;
		}
	}
	
	/**
	 *    �摜���H�̎�����
	 * 
	 * @param data
	 * @return
	 */
	private boolean processFrame(byte[] data)
	{
        // Log.i(GokigenSymbols.APP_IDENTIFIER, "ImageProcessor::processFrame() ... : " + data.length);

		// ��ʑS�̂��N���A����
        //targetCanvas.drawColor(0x00, PorterDuff.Mode.CLEAR);
        targetBitmap.eraseColor(Color.argb(0, 0, 0, 0)); 
        {
        	// �t���[���J�E���g�i�ŏ����牽���ڂ��j��\������
        	Paint paint = new Paint();
        	paint.setColor(Color.argb(0, 0, 0, 0));
        	targetCanvas.drawRect(10.0f, 180.0f, 90.0f, 210.0f, paint);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setTextSize(12.0f);
            paint.setStrokeWidth(1.0f);
            targetCanvas.drawText("" + tick, 20.0f, 190.0f, paint);
            tick++;        
        }

        mYuv.put(0, 0, data);
        if ((isBackgroundCaptured == true)&&(backgroundImage != null))
        {
        	// �w�i�摜�Ƃ̍�����\������
            Core.absdiff(mGraySubmat, backgroundImage, mIntermediateMat2);

            // �O���[�X�P�[����
            //Imgproc.cvtColor(mIntermediateMat2, mIntermediateMat, Imgproc.COLOR_BGR2GRAY);
            //Imgproc.Canny(mIntermediateMat2, mIntermediateMat, 80, 100);

            // �摜���l������
            //Imgproc.threshold(mIntermediateMat, mIntermediateMat, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
            Imgproc.threshold(mIntermediateMat2, mIntermediateMat, 20.0, 255.0, Imgproc.THRESH_BINARY);

            // �m�C�Y����
            Imgproc.medianBlur(mIntermediateMat, mIntermediateMat, 11);
            
            // �����_���o
            Mat hierarchy = new Mat();
            List<MatOfPoint> contours =new ArrayList<MatOfPoint>();
            Imgproc.findContours(mIntermediateMat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            // �Ƃ肠�����A�����_���ǂꂭ�炢���邩���ׂĂ݂悤�B�B�B
            double maxSize = -1.0;
            int maxIndex = -1;
            int limit =  contours.size();
            for (int index = 0; index < limit; index++)
            {
                double areaSize = Imgproc.contourArea(contours.get(index));
                if (areaSize > maxSize)
                {
                	maxIndex = index;
                	maxSize = areaSize;
                }
            }
            
        	// �̈�̍ő�ʒu�ƍő�T�C�Y����������
            //Log.v(GokigenSymbols.APP_IDENTIFIER, "contours[" + maxIndex + "] " + maxSize);

            momentSize = maxSize;

            // �d�S�����
            if (maxSize > DETECT_OBJECT_SIZE)
            {
                Moments moment = Imgproc.moments(contours.get(maxIndex));
                double newMomentX = Math.round(moment.get_m10() / moment.get_m00());
                double newMomentY = Math.round(moment.get_m01() / moment.get_m00());

                if ((Math.abs(newMomentX - momentX) > POSITION_LIMIT)||(Math.abs(newMomentY - momentY) > POSITION_LIMIT))
                {
                	// �d�S�̈ʒu���ړ������̂ŁA�L������B
                	history.add(new Position(newMomentX, newMomentY));
                }

                if (momentX == -1.0d)
                {
                	// �d�S�����o������
                	appearTick = tick;
                	appearMomentX = newMomentX;
                	appearMomentY = newMomentY;
                }
                else
                {
//                    if ((appearMomentX == newMomentX)&&(appearMomentY == newMomentY))
                	if (((Math.abs(appearMomentX - newMomentX) < STAY_THRESHOLD)&&
                		(Math.abs(appearMomentY) - newMomentY) < STAY_THRESHOLD))
                    {
                    	if ((tick - appearTick) > STAY_THRESHOLD_COUNT)
                    	{
                            isPublishMove = true;
                    	}
                    }
                    else
                    {
                    	appearTick = tick;
                    	appearMomentX = newMomentX;
                    	appearMomentY = newMomentY;                    	
                    }
                }
                momentX = newMomentX;
                momentY = newMomentY;
                momentSize = maxSize;
            }
            else
            {
            	// �d�S���Ȃ��Ȃ����I
                momentX = -1.0d;
                momentY = -1.0d;
                
                // ���������I
                appearTick = -1;
                isPublishMove = false;
                
                // ���̎��ɁA�q�X�g���̃_���v�����Ă݂�B
                dumpHistory();
                
            }
        }
        else
        {
            Imgproc.Canny(mGraySubmat, mIntermediateMat, 80, 100);
        }
        Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);                	
        Utils.matToBitmap(mRgba, targetBitmap);
	    return (true);
	}

	/**
	 *    �ړ������O�Ղ����O�Ƀ_���v����
	 * 
	 */
	private void dumpHistory()
	{
		int index = 0;
        double prevX = 0.0;
        double prevY = 0.0;
        Iterator<Position> ite = history.iterator();
        while (ite.hasNext())
        {
            Position value = ite.next();
        	double currentX = value.getX();
        	double currentY = value.getY();
        	Log.d(GokigenSymbols.APP_IDENTIFIER, "POINT[" + index + "] " + currentX + "," + currentY + " [" + (currentX - prevX)+ "," + (currentY - prevY) + "]");
        	index++;
        	prevX = currentX;
        	prevY = currentY;
        }
	}

	/**
     *    �w�i��`�悷��B
     * 
     * @param canvas
     */
    public void drawBackground(Canvas canvas)
    {

    }
    
    /**
     *    �I�u�W�F�N�g��`�悷��B
     * 
     * @param canvas
     */
    public boolean drawObjects(Canvas canvas)
    {
    	try
    	{
        	Paint paint = new Paint();
            canvas.drawBitmap(targetBitmap, 0, 0, paint);
            
            if ((momentX >= 0.0)&&(momentY >= 0.0))
            {
            	paint.setColor(Color.YELLOW);
            	canvas.drawCircle((float) momentX, (float) momentY, 4.0f, paint);
            	canvas.drawText("" + momentSize , 20.0f, 20.0f, paint);
            }
    	}
    	catch (Exception e)
    	{
    		Log.v(GokigenSymbols.APP_IDENTIFIER, "ImageProcessor::drawObjects() " + e.toString());
    	}
    	
    	// �R�}���h���s���𑗂�
    	return (isPublishMove);
    }

    /**
     *    �ړ����Ă��闚����\������
     * 
     * @param canvas
     */
    public void drawMovingHistory(Canvas canvas)
    {
    	try
    	{
        	Paint paint = new Paint();
            if ((momentX >= 0.0)&&(momentY >= 0.0))
            {
            	paint.setColor(Color.YELLOW);
            	canvas.drawCircle((float) (momentX * rateX), (float) (momentY * rateY), 4.0f, paint);
            }
 
            // �����̐��������Ă݂�...
            if ((isDrawHistory == true)&&(history.size() > 1))
            {
            	paint.setColor(Color.GREEN);
                Path path = new Path();
                Iterator<Position> ite = history.iterator();
                Position value = ite.next();
                path.moveTo((float) (value.getX() * rateX),  (float) (value.getY() * rateY));
                while (ite.hasNext())
                {
                	value = ite.next();
                    path.lineTo((float) (value.getX() * rateX), (float) (value.getY() * rateY));
                }
                canvas.drawPath(path, paint);
            }
    	}
    	catch (Exception e)
    	{
    		Log.v(GokigenSymbols.APP_IDENTIFIER, "ImageProcessor::drawMovingHistory() " + e.toString());
    	}    	
    }
    
    
    /**
     *   �ʒu�����L������N���X
     * 
     * @author MRSa
     *
     */
    public class Position
    {
        private double x = 0.0d;
        private double y = 0.0d;
        
        public Position(double x, double y)
        {
        	 //
        	this.x = x;
        	this.y = y;
        }
        
        public double getX()
        {
        	return (x);
        }
        
        public double getY()
        {
        	return (y);
        }
    }    
}

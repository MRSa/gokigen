package jp.sourceforge.gokigen.aligner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;


/**
 *  ��������O���t��ʂ̏����N���X
 * 
 * @author MRSa
 *
 */
public class GokigenGraphListener  implements OnClickListener, OnTouchListener, OnKeyListener, ICanvasDrawer, ICameraDataReceiver, IBitmapWriterCallback
{
    // �J�����摜�̃L���v�`�����{    
    private boolean isCapture = false;
    private boolean dataWriting = false;

    // ���p����N���X
    private Activity parent = null;                           // �e���̃N���X
    private IGokigenGraphDrawer    currentGraphDrawer = null; // �`��Ɏg�p���Ă���`��N���X
    private ExternalStorageFileUtility fileUtility = null;    // �t�@�C�����o�͂���Ƃ��Ɏg���N���X
    
    private BitmapWriter bmpWriter = null;                    // �摜���o�͂���N���X
    private String   writingMessage = null;

    /**
     *   �R���X�g���N�^
     * @param arg
     */
    public GokigenGraphListener(Activity arg)
    {
        parent = arg;

        // ���ۂɕ`��Ɏg�p����N���X��ݒ肷��
        currentGraphDrawer = new GokigenScaleDrawer(arg);
        currentGraphDrawer.prepare();

        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);    

        try
        {
        	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            String shapeStr = preferences.getString("showShapeType", "0");
            int shapeType = Integer.parseInt(shapeStr);
            Log.v(Main.APP_IDENTIFIER, "showShapeType : " + shapeType);
            currentGraphDrawer.setDrawType(shapeType);    
        }
        catch (Exception ex)
        {
        	// �Ȃɂ����Ȃ�
        	Log.v(Main.APP_IDENTIFIER, "ERR>" + ex.toString());
        }
    }

    /**
     *  �N�����Ƀf�[�^����������
     * 
     * @param myIntent
     */
    public void prepareExtraDatas(Intent myIntent)
    {
        try
        {
            // �f�[�^���\�z���A��ʂ̕\�����X�V����
            //prepareGokigenData();
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EXCEPTION :" + ex.getMessage());
        }        
    }
    
    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ����� (��ʂ��\�ɂ������̏���)
     * 
     */
    public void prepareListener()
    {
        // �O���t�����̗̈�
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.setCanvasDrawer(this);
        view.setOnClickListener(this);
        view.setOnTouchListener(this);
        view.setPixelFormat(PixelFormat.TRANSLUCENT);
        
        // �J�����\���̗̈�
        final CameraViewer cameraCanvas = (CameraViewer) parent.findViewById(R.id.CameraView);
        cameraCanvas.setOnClickListener(this);
        cameraCanvas.setPreviewCallback(this);
        cameraCanvas.setOnTouchListener(this);
    }

    /**
     *  ��ʂ����ɉ�����Ƃ��̏���
     * 
     */
    public void finishListener()
    {
    }
    
    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {
    }

    /**
     *  �I������
     */
    public void shutdown()
    {
    }

    /**
     *  ����ʂ���߂��Ă����Ƃ�...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // �������x���Ȃ邩������Ȃ����A�A�A�K�x�R�������{����B
        System.gc();
    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
       	Log.v(Main.APP_IDENTIFIER, "GokigenGraphListener::onClick()");

       	//int id = v.getId();

       	// �f�[�^��(��)�`�悷��
       	redrawScreen();
     }
    
    /**
     *  ��ʂ̍ĕ`������s
     * 
     */
    private void redrawScreen()
    {
        // �O���t�̍ĕ`����w������
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.doDraw();         
    }

    /**
     *    ���(GokigenSurfaceView)��G��ꂽ�Ƃ��̏���
     *    (�t���b�N�A�N�V�����̌��o���s���B)
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	boolean ret = false;
        int action = event.getAction() & 0x000000ff;
        Log.v(Main.APP_IDENTIFIER, "onTouchEvent() :" + action);

        if (action == MotionEvent.ACTION_DOWN)
        {
            // ��ʃL���v�`���w��
        	isCapture = true;
            return (true);
        }
        return (ret);
    }

    /**
     *  �L�[���͂��E��
     * 
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	boolean ret = false;
        Log.v(Main.APP_IDENTIFIER, "GokigenGraphListener::onKeyDown() :" + keyCode);

        int action = event.getAction();
        if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {
            // ��ʃL���v�`���w��
        	isCapture = true;        	
        	return (true);
        }
    	return (ret);
    }

    /**
     *  �g���b�N�{�[�����������ꂽ�Ƃ��̏���
     * 
     * @param event
     * @return
     */
    public boolean onTrackballEvent(MotionEvent event)
    {
        // �����ł͉������Ȃ�
        return (false);
    }

    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        // Log.v(Main.APP_IDENTIFIER, "onTouch()");
        return (onTouchEvent(event));
    }

    /**
     *  �L�[���������Ƃ��̑���
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
       	//Log.v(Main.APP_IDENTIFIER, "onKey() : " + keyCode);
       	return (onKeyDown(keyCode, event));
    }

    /**
     *   ���j���[�ւ̃A�C�e���ǉ�
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {
        return (menu);
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
        return;
    }

    /**
     *   ���j���[�̃A�C�e�����I�����ꂽ�Ƃ��̏���
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return (false);
    }

    /**
     *  �L�����o�X�Ƀf�[�^��`�悷��
     * 
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	try
    	{
    		// ��ʂ�h��Ԃ��Ă���ĕ`������s
    		//canvas.drawColor(Color.BLACK);
            currentGraphDrawer.drawOnCanvas(canvas, 0);
    	}
    	catch (Exception ex)
    	{
    		// ��O����...�ł����̂Ƃ��ɂ͉������Ȃ�
    		Log.v(Main.APP_IDENTIFIER, "drawOnCanvas()" + ex.getMessage());
    	}
    	return;
    }

    /**
     *  �摜���̎擾
     * 
     */
    public void onPreviewFrame(byte[] arg0, Camera arg1, int width, int height)
    {
    	if ((isCapture == true)&&((dataWriting == false))&&(arg0 != null))
    	{
            dataWriting = true;
            bmpWriter = null;
    		bmpWriter = new BitmapWriter(parent, fileUtility, this, arg0, width, height);
    		writingMessage = "WRITE";
    		bmpWriter.execute(writingMessage);
    	}
    }

    /**
     *  �摜�f�[�^�̏������ݓr��...
     */
    public void onProgressUpdate()
    {
    	// �������ݒ��\�����s��
    	currentGraphDrawer.setMessage(parent.getString(R.string.capturing));
    	
        // �O���t�̍ĕ`����w������
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.doDraw();    	
    }
    
    /**
     *  �摜�f�[�^�̏������ݏI��...
     */
    public void finishedWrite(boolean result)
    {
    	currentGraphDrawer.setMessage(parent.getString(R.string.captured));
    	
        // �O���t�̍ĕ`����w������
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.doDraw();         

        // �������݌��ʂ���������
    	Log.v(Main.APP_IDENTIFIER, "DATA WRITE DONE : " + result);
    	dataWriting = false;
		isCapture = false;
	    parent.finish();
	    System.gc();
        return;
    }
}

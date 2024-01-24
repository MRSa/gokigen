package jp.sourceforge.gokigen.qsteer.controller;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *  �J�����摜�\���N���X (��E�ς�����̃R�[�h���Q�l�ɂ����Ă����������B)
 *    �� http://www.saturn.dti.ne.jp/~npake/android/CameraEx/index.html
 *  
 *  �� ���O���t�B�b�N�X�`��Ɠ����\�����邽�߂ɁA���傢�Ɖ��H�B
 *    (���̏ꏊ�ɂ������R�[�h���Q�l�ɂ����Ă����������B)
 *   "Camera image as an OpenGL texture on top of the native camera viewfinder"
 *    �� http://nhenze.net/?p=172
 *
 *  
 * @author MRSa
 *
 */
public class CameraViewer extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback
{
	private final int TEXTURE_NAME = 10;
	
    private SurfaceHolder              holder = null;
    private Camera                       camera = null;
    private ICameraDataReceiver  cameraDataReceiver = null;

    private byte[]                         frameBuffer = null;
    private int                              frameWidth;
    private int                              frameHeight;

    /**
     *  �R���X�g���N�^
     * @param context
     */
    public CameraViewer(Context context)
    {
        super(context);
        initializeSelf(context, null);
        Log.i(GokigenSymbols.APP_IDENTIFIER, "Instantiated, new " + this.getClass());
    }

    /**
     *  �R���X�g���N�^ (���C�A�E�g�}�l�[�W���o�R�ŌĂяo���ꂽ�Ƃ��ɗ��p)
     * @param context
     * @param attrs
     */
    public CameraViewer(Context context, AttributeSet attrs)
    {
        super(context, attrs);        
        initializeSelf(context, attrs);
        Log.i(GokigenSymbols.APP_IDENTIFIER, ">>Instantiated new " + this.getClass());
    }

    /**
     *   �N���X�̏���������
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
        // �T�[�t�F�C�X�z���_�[�̐���        
        holder = getHolder();
        holder.addCallback(this);

        // �v�b�V���o�b�t�@�̎w��
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * 
     * 
     * @throws IOException
     */
    public void setPreview() throws IOException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            camera.setPreviewTexture(new SurfaceTexture(TEXTURE_NAME));
        }
        else
        {
            camera.setPreviewDisplay(null);
        }
	}

    /**
     *   �T�[�t�F�C�X�����C�x���g�̏���
     * 
     */
    public void surfaceCreated(SurfaceHolder aHolder)
    {
        synchronized (this)
        {
            // �J�����̏������ƃv���r���[�J�n
            try
            {
                camera = Camera.open();
                camera.setPreviewDisplay(aHolder);
               
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(getWidth(), getHeight());
                camera.setParameters(parameters);

                int previewFormat = parameters.getPreviewFormat();
                int bitsPerPixel = ImageFormat.getBitsPerPixel(previewFormat);
                // int byteperpixel = bitsPerPixel / 8;
                Camera.Size camerasize = parameters.getPreviewSize();
                frameWidth = camerasize.width;
                frameHeight = camerasize.height;
                int frame_size = ((frameWidth * frameHeight) * bitsPerPixel) / 8;                
                frameBuffer = new byte[frame_size];

                camera.addCallbackBuffer(frameBuffer);
        		camera.setPreviewCallbackWithBuffer(this);  // ����� onPreviewFrame() ���Ă΂��B
                camera.startPreview();
                // camera.setPreviewCallback(this);
            }
            catch (Exception ex)
            {
                // �J�������ݒ�ł��Ȃ��ꍇ�ɂ́A���̌��������O���ďI���(�������Ȃ�)
            	Log.v(GokigenSymbols.APP_IDENTIFIER, "surfaceCreated() " + ex.toString());
            }
        }
    }

    /**
     *  �v���r���[�R�[���o�b�N�����N���X��ݒ肷��
     * @param callback
     */
    public void setPreviewCallback(ICameraDataReceiver callback)
    {
    	cameraDataReceiver = callback;
    	if (cameraDataReceiver != null)
    	{
    		cameraDataReceiver.prepareToReceive();
    	}
    }    
    
    /**
     *  �T�[�t�F�C�X�ύX�C�x���g�̏���
     * 
     */
    public void surfaceChanged(SurfaceHolder aHolder, int format, int width, int height)
    {
    	// �������Ȃ� (�ʂ̂Ƃ���Ŏ��{)
    	configureFormat(format, width, height);

    	synchronized (this)
	    {
            if (cameraDataReceiver != null)
            {
            	cameraDataReceiver.onPreviewStared(width, height, frameWidth, frameHeight);
            }
	    }
    }

    /**
     * 
     * @param format
     * @param width
     * @param height
     */
    private void configureFormat(int format, int width, int height)
    {
        // �Ƃ肠�������O�o�͂���
    	Log.v(GokigenSymbols.APP_IDENTIFIER, "configureFormat()  f:" + format + " w:" + width + " h:" + height);
        if (camera != null)
        {
            Camera.Parameters params = camera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            frameWidth = width;
            frameHeight = height;

            // �J�����̃v���r���[�T�C�Y�͍ŏ�������...
            {
                int  minDiff = Integer.MAX_VALUE;
                for (Camera.Size size : sizes)
                {
                    if (Math.abs(size.height - height) < minDiff)
                    {
                        frameWidth = size.width;
                        frameHeight = size.height;
                        minDiff = Math.abs(size.height - height);
                    }
                }
            }
            params.setPreviewSize(frameWidth, frameHeight);
            camera.setParameters(params);

            params = camera.getParameters();
        }
    }
    
    /**
     *  �T�[�t�F�C�X�J���C�x���g�̏���
     * 
     */
    public void surfaceDestroyed(SurfaceHolder aHolder)
    {
        synchronized (this)
        {
            // �J�����̃v���r���[��~����
            try
            {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
                cameraDataReceiver = null;
            }
            catch (Exception ex)
            {
                //            
            }
            
            // �ꉞ�A�K�x�R��������Ă���...
            System.gc();
        }
    }

    /**
     *   �J�����̃v���r���[�摜����M�����Ƃ��̏���
     *   (���̃v���r���[�����N���X�Ƀp�X�X���[����)
     */
    public void onPreviewFrame(byte[] arg0, Camera arg1)
	{
        if (cameraDataReceiver != null)
        {
        	cameraDataReceiver.onPreviewFrame(arg0, arg1);
        }

        // �L�^�o�b�t�@���w�肷��B�i�ė��p�j
        camera.addCallbackBuffer(frameBuffer);
	}
}

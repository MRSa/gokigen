package jp.sourceforge.gokigen.aligner;

import android.content.Context;
import android.util.AttributeSet;
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
    private SurfaceHolder          holder = null;
    private Camera                 camera = null;
    private ICameraDataReceiver    cameraDataReceiver = null;
    
    /**
     *  �R���X�g���N�^
     * @param context
     */
    public CameraViewer(Context context)
    {
        super(context);
        initializeSelf(context, null);
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
                //parameters.setPreviewFormat(PixelFormat.JPEG);
                camera.setParameters(parameters);

                camera.startPreview();
        		camera.setPreviewCallback(this);
            }
            catch (Exception ex)
            {
                //
                // �J�������ݒ�ł��Ȃ��ꍇ�ɂ́A�������Ȃ�
                // 
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
    }    
    
    /**
     *  �T�[�t�F�C�X�ύX�C�x���g�̏���
     * 
     */
    public void surfaceChanged(SurfaceHolder aHolder, int format, int width, int height)
    {
    	// �������Ȃ� (�ʂ̂Ƃ���Ŏ��{)
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
        	cameraDataReceiver.onPreviewFrame(arg0, arg1, getWidth(), getHeight());
        }
	}
}

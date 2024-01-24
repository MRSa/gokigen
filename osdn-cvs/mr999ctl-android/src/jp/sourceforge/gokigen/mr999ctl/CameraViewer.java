package jp.sourceforge.gokigen.mr999ctl;

import android.content.Context;
import android.util.AttributeSet;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *  �J�����摜�\���N���X (��E�ς�����̃R�[�h���Q�l�ɂ����Ă����������B)
 *    �� http://www.saturn.dti.ne.jp/~npake/android/CameraEx/index.html
 *  
 * @author MRSa
 *
 */
public class CameraViewer extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder holder = null;
    private Camera        camera = null;
    
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
        // �J�����̏�����
        try
        {
            camera = Camera.open();
            camera.setPreviewDisplay(aHolder);
        }
        catch (Exception ex)
        {
            //
            // �J�������ݒ�ł��Ȃ��ꍇ�ɂ́A�������Ȃ�
            // 
        }
    }
    
    /**
     *  �T�[�t�F�C�X�ύX�C�x���g�̏���
     * 
     */
    public void surfaceChanged(SurfaceHolder aHolder, int format, int width, int height)
    {
        //  �J�����̃v���r���[�J�n����
        try
        {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            camera.setParameters(parameters);
        
            camera.startPreview();
        }
        catch (Exception ex)
        {
            //
            //
            //
        }
    }

    /**
     *  �T�[�t�F�C�X�J���C�x���g�̏���
     * 
     */
    public void surfaceDestroyed(SurfaceHolder aHolder)
    {
        // �J�����̃v���r���[��~����
        try
        {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        catch (Exception ex)
        {
            //            
        }        
    }
}

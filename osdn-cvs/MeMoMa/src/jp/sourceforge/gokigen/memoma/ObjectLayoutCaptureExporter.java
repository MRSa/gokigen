package jp.sourceforge.gokigen.memoma;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;

/**
 *  �f�[�^���t�@�C���ɕۑ�����Ƃ��p �A�N�Z�X���b�p (�񓯊����������s)
 *  View�̏����摜�`���ipng�`���j�ŕۑ�����B
 *  �ǂ�View��ۑ�����̂��́AICaptureExporter.getCaptureTargetView()�N���X���g���ċ����Ă��炤�B
 *  
 *  AsyncTask
 *    String       : ���s���ɓn���N���X(Param)           : �t�@�C���������炤
 *    Integer    : �r���o�߂�`����N���X(Progress)   : ����͎g���Ă��Ȃ�
 *    String      : �������ʂ�`����N���X(Result)      : ���ʂ���������B
 *    
 * @author MRSa
 *
 */
public class ObjectLayoutCaptureExporter extends AsyncTask<String, Integer, String>
{
	private static final int OUTPUT_MARGIN = 8;
	private static final int OUTPUT_MARGIN_TOP = 50;
	
	private static final int MINIMUM_WIDTH = 800;
	private static final int MINIMUM_HEIGHT = 600;
	
	private Activity parent = null;
	private ICaptureLayoutExporter receiver = null;
	private ExternalStorageFileUtility fileUtility = null;	
	private String exportedFileName = null;	
	private MeMoMaObjectHolder objectHolder = null;
	private MeMoMaCanvasDrawer canvasDrawer = null;
	private ProgressDialog savingDialog = null;
	private float offsetX = 0.0f;
	private float offsetY = 0.0f;

	/**
	 *   �R���X�g���N�^
	 */
    public ObjectLayoutCaptureExporter(Activity context, ExternalStorageFileUtility utility,  MeMoMaObjectHolder holder, MeMoMaCanvasDrawer drawer, ICaptureLayoutExporter resultReceiver)
    {
    	receiver = resultReceiver;
    	fileUtility = utility;
    	objectHolder = holder;
    	canvasDrawer = drawer;
    	parent = context;

        //  �v���O���X�_�C�A���O�i�u�ۑ���...�v�j��\������B
    	savingDialog = new ProgressDialog(context);
    	savingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	savingDialog.setMessage(context.getString(R.string.dataSaving));
    	savingDialog.setIndeterminate(true);
    	savingDialog.setCancelable(false);
    	savingDialog.show();

    	/** �t�@�C�����o�b�N�A�b�v����f�B���N�g�����쐬���� **/
    	File dir = new File(fileUtility.getGokigenDirectory() + "/exported");
    	dir.mkdir();
    }
	
    /**
     *  �񓯊��������{�O�̑O����
     * 
     */
    @Override
    protected void onPreExecute()
    {
        // �Ȃɂ����Ȃ��B
    }
    
    /**
     *    �r�b�g�}�b�v�f�[�^��(PNG�`����)�ۊǂ���B
     * 
     * @param fileName
     * @param objectHolder
     * @return
     */
    private String exportToFile(String fileName, Bitmap targetBitmap)
    {
    	String resultMessage = "";
        try
        {
        	if (targetBitmap == null)
        	{
        		// �r�b�g�}�b�v�����Ȃ����߁A�����Ő܂�Ԃ��B
        		return ("SCREEN DATA GET FAILURE...");
        	}
        	
        	// �G�N�X�|�[�g����t�@�C���������肷��
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            exportedFileName = fileName + "_" + outFormat.format(calendar.getTime()) + ".png";

            // PNG�`���Ńt�@�C���o�͂��s���B
            OutputStream out = new FileOutputStream(exportedFileName);
            targetBitmap.compress(CompressFormat.PNG, 100, out);
            out.flush();
            out.close();            
        }
        catch (Exception e)
        {
        	resultMessage = " ERR(png)>" + e.toString();
            Log.v(Main.APP_IDENTIFIER, resultMessage);
            e.printStackTrace();
        } 
        return (resultMessage);
    }
    
    /**
     *    �L�����o�X�̑傫�����ǂꂭ�炢�K�v���A�`�F�b�N����B
     * 
     * @return
     */
    private Rect checkCanvasSize()
    {
        Rect canvasSize = new Rect();

        // �I�u�W�F�N�g�̔z�u�ʒu��T��B
    	Enumeration<Integer> keys = objectHolder.getObjectKeys();
        while (keys.hasMoreElements())
        {
            Integer key = keys.nextElement();
            MeMoMaObjectHolder.PositionObject pos = objectHolder.getPosition(key);
            if (canvasSize.left > pos.rect.left)
            {
            	canvasSize.left = (int) pos.rect.left;
            }
            if (canvasSize.right < pos.rect.right)
            {
            	canvasSize.right = (int) pos.rect.right;
            }
            if (canvasSize.top > pos.rect.top)
            {
            	canvasSize.top = (int) pos.rect.top;
            }
            if (canvasSize.bottom < pos.rect.bottom)
            {
            	canvasSize.bottom = (int) pos.rect.bottom;
            }
        }
        
        // �`��̈�ɂ�����Ɨ]�T����������
        canvasSize.left = canvasSize.left - OUTPUT_MARGIN;
        canvasSize.right = canvasSize.right + OUTPUT_MARGIN;
        canvasSize.top = canvasSize.top - OUTPUT_MARGIN_TOP;
        canvasSize.bottom = canvasSize.bottom + OUTPUT_MARGIN;
        canvasSize.sort();

        // ���݂̉�ʃT�C�Y���擾
        Display display = parent.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        if (width < MINIMUM_WIDTH)
        {
        	width = MINIMUM_WIDTH;
        }
        if (height < MINIMUM_HEIGHT)
        {
        	height = MINIMUM_HEIGHT;
        }        

        // �o�͂̍ŏ��T�C�Y��(�\����ʃT�C�Y��)�ݒ�
        if (canvasSize.width() < width)
        {
        	canvasSize.right = canvasSize.left + width;
        }
        if (canvasSize.height() < height)
        {
        	canvasSize.bottom = canvasSize.top + height;
        }
        
        
        // �摜�ʒu�i�L�����o�X�ʒu�j�̒����B�B�B
        offsetX = 0.0f - canvasSize.left - (OUTPUT_MARGIN);
        offsetY = 0.0f - canvasSize.top - (OUTPUT_MARGIN);

        // �o�͂���摜�f�[�^�̃T�C�Y��\������
        Log.v(Main.APP_IDENTIFIER, "ObjectLayoutCaptureExporter::checkCanvasSize() w:" + canvasSize.width() + " , h:" + canvasSize.height() + "  offset :(" + offsetX + "," + offsetY + ")");
        return (canvasSize);
    }    

    /**
     *  �񓯊�����
     *  �i�o�b�N�O���E���h�Ŏ��s����(���̃��\�b�h�́AUI�X���b�h�ƕʂ̂Ƃ���Ŏ��s����)�j
     * 
     */
    @Override
    protected String doInBackground(String... datas)
    {
    	Rect canvasSize = checkCanvasSize();
    	Bitmap targetBitmap = Bitmap.createBitmap(canvasSize.width(), canvasSize.height(), Bitmap.Config.RGB_565);
    	Canvas targetCanvas = new Canvas(targetBitmap);
    	
    	// �I�u�W�F�N�g���r�b�g�}�b�v�̒��ɏ�������
    	canvasDrawer.drawOnBitmapCanvas(targetCanvas, offsetX, offsetY);

    	// �t�@�C�����̐ݒ� ... (�g���q�Ȃ�)
    	String fileName = fileUtility.getGokigenDirectory() + "/exported/" + datas[0];

    	// �f�[�^��ۊǂ���
        String result = exportToFile(fileName, targetBitmap);

        System.gc();

        return (result);
    }

    /**
     *  �񓯊������̐i���󋵂̍X�V
     * 
     */
	@Override
	protected void onProgressUpdate(Integer... values)
	{
        // ����͉������Ȃ�
	}

    /**
     *  �񓯊������̌㏈��
     *  (���ʂ���������)
     */
    @Override
    protected void onPostExecute(String result)
    {
    	try
    	{
            if (receiver != null)
            {
            	receiver.onCaptureLayoutExportedResult(exportedFileName, result);
            }
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "ViewCaptureExporter::onPostExecute() : " + ex.toString());
    	}
    	// �v���O���X�_�C�A���O������
    	if (savingDialog != null)
    	{
            savingDialog.dismiss();
    	}
    	return;
    }     
 
    /**
     *    ���ʕ񍐗p�̃C���^�t�F�[�X
     *    
     * @author MRSa
     *
     */
    public interface ICaptureLayoutExporter
    {
        /**  �ۑ����ʂ̕� **/
        public abstract void onCaptureLayoutExportedResult(String exportedFileName, String detail);
    }
}

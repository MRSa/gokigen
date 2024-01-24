package jp.sourceforge.gokigen.aligner;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 *  �f�[�^�������݃N���X
 * 
 *  
 *  AsyncTask
 *    String  : ���s���ɓn���N���X(Param)
 *    Integer : �r���o�߂�`����N���X(Progress)
 *    Boolean  : �������ʂ�`����N���X(Result)
 *    
 * @author MRSa
 *
 */
public class BitmapWriter extends AsyncTask<String, Integer, Boolean>
{
	private ExternalStorageFileUtility fileUtility = null;
	private Bitmap bitmapData = null;
	private Context parent = null;
	private IBitmapWriterCallback callbackReceiver = null;
	
	private byte[] byteData = null;
	private int dataWidth = 1;
	private int dataHeight = 1;
	
	private ProgressDialog  progressDialog = null;
	
    /**
     *  �R���X�g���N�^
     * 
     */
	public BitmapWriter(Context arg, ExternalStorageFileUtility utility, IBitmapWriterCallback callback, byte[] data, int width, int height)
    {
		parent = arg;
		fileUtility = utility;
		dataWidth = width;
		dataHeight = height;
		callbackReceiver = callback;
		byteData = data;
    }

	/**
     *  �t�@�C�������L�^����
     * 
     * @param key    �L������f�[�^�̎擾�L�[
     * @param value  �L������f�[�^
     */
	private void storeKeyAndValue(String key, String value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();        
    }

	/**
	 * converts YUV420 to BMP
	 * 
	 * @param rgb
	 * @param yuv420sp
	 * @param width
	 * @param height
	 */
    static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height)
    {
        final int frameSize = width * height;
        for (int j = 0, yp = 0; j < height; j++)
        {
    		int uvp = frameSize + (j >> 1) * width;
    		int u = 0;
    		int v = 0;
    		for (int i = 0; i < width; i++)
    		{
    			int y = (0xff & ((int) yuv420sp[yp])) - 16;
    			if (y < 0)
    			{
    		        y = 0;
    			}
    			if ((i & 1) == 0)
    			{
    				v = (0xff & yuv420sp[uvp++]) - 128;
    				u = (0xff & yuv420sp[uvp++]) - 128;
    			}
    			
    			int y1192 = 1192 * y;
    			int r = (y1192 + 1634 * v);
    			int g = (y1192 - 833 * v - 400 * u);
    			int b = (y1192 + 2066 * u);
    			
    			if (r < 0)
    			{
    				r = 0;
    			}
				else if (r > 262143)
				{
				    r = 262143;
				}

    			if (g < 0)
    			{
    				g = 0;
    			}
    			else if (g > 262143)
    			{
    				g = 262143;
    			}
    			
    			if (b < 0)
    			{
    				b = 0;
    			}
    			else if (b > 262143)
    			{
    				b = 262143;
    			}
    			
    			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    			yp = yp + 1;
    		}
    	}
    }

    /**
     *  �񓯊��������{�O�̑O����
     * 
     */
    @Override
    protected void onPreExecute()
    {
    	progressDialog = null;
    	progressDialog = new ProgressDialog(parent);
    	progressDialog.setMessage(parent.getString(R.string.dataWriting));
    	progressDialog.setIndeterminate(true);
    	progressDialog.setCancelable(false);
    	progressDialog.show();
    }

    /**
     *  �񓯊�����
     *  �i�o�b�N�O���E���h�Ŏ��s����(���̃��\�b�h�́AUI�X���b�h�ƕʂ̂Ƃ���Ŏ��s)�j
     * 
     */
    @Override
    protected Boolean doInBackground(String... datas)
    {
        boolean ret = false;
        int[] rgb = null;
        if (byteData == null)
        {
        	return (false);
        }
        try
        {
            // �r�b�g�}�b�v�f�[�^�ɕϊ�����
        	bitmapData = null;
        	bitmapData = Bitmap.createBitmap(dataWidth, dataHeight, Bitmap.Config.ARGB_8888);

        	rgb = new int[(dataWidth * dataHeight)];
        	decodeYUV420SP(rgb, byteData, dataWidth, dataHeight);
        	bitmapData.setPixels(rgb, 0, dataWidth, 0, 0, dataWidth, dataHeight);

        	// �t�@�C���Ƀf�[�^���o�͂���
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
            String fileName  = timeFormat.format(calendar.getTime()) + "Pics.png";
        	String targetFileName = fileUtility.decideFileNameWithDate(fileName);
            FileOutputStream oStream = fileUtility.openFileStream(targetFileName, false);
            bitmapData.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.close();
            
            // �L�^�����t�@�C�������N���A����
            String fullPath = fileUtility.getGokigenDirectory() + "/" + targetFileName;
            storeKeyAndValue(Main.APP_EXAMINE_FILENAME, fullPath);
            
        	Log.v(Main.APP_IDENTIFIER, "WRITTEN :" + fullPath);
            ret = true;
        }
        catch (Exception ex)
        {
        	Log.v(Main.APP_IDENTIFIER, "FILE SAVE FAILURE... " + " (" + rgb.length + "," + byteData.length + ")" + " w:" + dataWidth + " h:" + dataHeight + "  " +  ex.toString());
            ex.printStackTrace();
            ret = false;
        }
        return (ret);
    }

    /**
     *  �񓯊������̐i���󋵂̍X�V
     * 
     */
	@Override
	protected void onProgressUpdate(Integer... values)
	{
    	try
    	{
    		// �������ݓr����ʒm����
    	    callbackReceiver.onProgressUpdate();
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "DataWriter::onProgressUpdate() : " + ex.toString());
    	}
        return;
	}

    /**
     *  �񓯊������̌㏈��
     *  (���ʂ���������)
     */
    @Override
    protected void onPostExecute(Boolean result)
    {
    	try
    	{
            // �v���O���X�_�C�A���O����������
    	    if (progressDialog != null)
    	    {
    	    	progressDialog.dismiss();
    	    	progressDialog = null;
    	    }

    	    // �������݌��ʂ�ʒm����
    	    callbackReceiver.finishedWrite(result);
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "DataWriter::onPostExecute() : " + ex.toString());
    	}
        return;
    }
}

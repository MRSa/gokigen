package jp.sourceforge.gokigen.memoma;

import java.io.File;
import java.io.InputStream;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 *  �摜�C���[�W��ǂݍ���
 * 
 * @author MRSa
 *
 */
public class ImageLoader
{
    ProgressDialog loadingDialog = null;
    Context  parent = null;

    // �摜��\������
    String imageFile = null;
	Bitmap imageBitmap = null;
	int imageWidth     = 1;
	int imageHeight    = 1;
	ImageView imageView = null;
    
    public ImageLoader(Context context)
    {
        loadingDialog = new ProgressDialog(context);
        parent = context;
    }
    
    /**
     *  �C���[�W�t�@�C���̈ꗗ���擾����
     *  (��x�������������Ƃ̂Ȃ��R�[�h�Ȃ̂Œ��ӁI)
     * @param activity
     * @return �C���[�W�t�@�C�����̈ꗗ
     */
    /*
    public static String[] getImageFileList(Activity activity)
    {
        try
        {
        	HashSet<String> list = new HashSet<String>();
        	
        	Cursor c = activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        	while(c.moveToNext())
        	{
                String imagefile = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                File file = new File(imagefile);
                list.add(file.getParent());
        	}
            return (String[]) list.toArray(new String[list.size()]);
        }
        catch (Exception ex)
        {
            //
        }
        return (null);
    }
    */

    /**
	 *  URI����ϊ�
	 * 
	 * @param imageFile
	 * @return
	 */
	public static Uri parseUri(String imageFile)
	{
	    if (imageFile.startsWith("content://") == true)
	    {
	    	return (Uri.parse(imageFile));
	    }

    	File picFile = new File(imageFile);
    	return (Uri.fromFile(picFile));	
	}

	/**
	 *   ��ʂɃC���[�W��\������ 
	 *
	 */
	public static void setImage(Context context, ImageView view, String imageFile)
    {
    	// �摜��\������
		Bitmap bitmap = null;
		int width = view.getWidth();
		int height = view.getHeight();
        if (imageFile.startsWith("content://") == true)
        {
        	// URI����摜��ݒ肷��...OutOfMemory�΍��t��
        	bitmap = getBitmapFromUri(context, Uri.parse(imageFile), width, height);
        }
        else
        {
        	// OutOfMemory�΍��t��...�r�b�g�}�b�v�̃T�C�Y�����k���ĕ\��
        	bitmap = getBitmap(imageFile, view.getWidth(), view.getHeight());
        }
    	view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setImageBitmap(bitmap);
    }

	/**
	 *   ��ʂɃC���[�W��\������ (���[�h���_�C�A���O�\����)
	 * 
	 */
	public void setImage(ImageView view, String targetFile)
    {

        //  �v���O���X�_�C�A���O�i�u���[�h��...�v�j��\������B
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(parent.getString(R.string.dataLoading));
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        imageFile = targetFile;
        imageView = view;
        imageBitmap = null;
        imageWidth = view.getWidth();
        imageHeight = view.getHeight();        
        
        /**
         *  �_�C�A���O�\�����̏���
         * 
         */
        Thread thread = new Thread(new Runnable()
        {  
            public void run()
            {
            	try
            	{            		
            		if (imageFile.startsWith("content://") == true)
                    {
                    	// URI����摜��ݒ肷��...OutOfMemory�΍��t��
                    	imageBitmap = getBitmapFromUri(parent, Uri.parse(imageFile), imageWidth, imageHeight);
                    }
                    else
                    {
                    	// OutOfMemory�΍��t��...�r�b�g�}�b�v�̃T�C�Y�����k���ĕ\��
                    	imageBitmap = getBitmap(imageFile, imageWidth, imageHeight);
                    }
            		handler.sendEmptyMessage(0);
            	}
                catch (Exception ex)
            	{
            		handler.sendEmptyMessage(0);
            	}
            }

            /**
             *   ��ʂ̍X�V
             */
            private final Handler handler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                	if ((imageBitmap != null)&&(imageView != null))
                	{
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView.setImageBitmap(imageBitmap);
                	}
                    imageFile = null;
                    imageView = null;
                    imageBitmap = null;
                    imageWidth = 1;
                    imageHeight = 1;

                	loadingDialog.dismiss();
                
                }
            };   
        });
        try
        {
            thread.start();
        }
        catch (Exception ex)
        {

        }
    }
	
	/**
	 *   URI�o�R�Ńr�b�g�}�b�v�f�[�^���擾����
	 * 
	 * @param context
	 * @param uri
	 * @param width
	 * @param height
	 * @return
	 */
    public static Bitmap getBitmapFromUri(Context context, Uri uri, int width, int height)
    {
        // �t�@�C���̕\�����@���኱�ύX���� �� Uri.Parse() ���� BitmapFactory�𗘗p������@�ցB
        BitmapFactory.Options opt = new BitmapFactory.Options();

        // OutOfMemory�G���[�΍�...��x�ǂݍ���ŉ摜�T�C�Y���擾
        opt.inJustDecodeBounds = true;
        opt.inDither = true;
        opt.inPurgeable = true;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        InputStream input = null; 
        try
        { 
            input = context.getContentResolver().openInputStream(uri); 
            BitmapFactory.decodeStream(input, null, opt); 
            input.close();
        }
        catch (Exception ex)
        {
        	Log.v(Main.APP_IDENTIFIER, "Ex(1): " + ex.toString());
        	if (input != null)
        	{
        		try
        		{
        	        input.close();
        		}
        		catch (Exception e)
        		{
        			//
        		}
        	}
        }
        // �\���T�C�Y�ɍ��킹�ďk��...�\���T�C�Y���擾�ł��Ȃ������ꍇ�ɂ́AQVGA�T�C�Y�Ɖ��肷��
        if (width < 10)
        {
            width = 320;
        }
        if (height < 10)
        {
        	height = 240;
        }

        // �摜�̏k���T�C�Y�����肷�� (�c���A�����̏������ق��ɂ��킹��)
        int widthBounds = opt.outWidth / width;
        int heightBounds = opt.outHeight / height;
        opt.inSampleSize=Math.min(widthBounds, heightBounds);
        opt.inJustDecodeBounds = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        
        // �摜�t�@�C������������
        input = null; 
        Bitmap retBitmap = null;
        try
        { 
            input = context.getContentResolver().openInputStream(uri); 
            retBitmap = BitmapFactory.decodeStream(input, null, opt); 
            input.close();
        }
        catch (Exception ex)
        {
        	Log.v(Main.APP_IDENTIFIER, "Ex(2): " + ex.toString());
        	if (input != null)
        	{
        		try
        		{
        	        input.close();
        		}
        		catch (Exception e)
        		{
        			//
        		}
        	}
        }
        return (retBitmap);
    }        
    
    /**
     *   �r�b�g�}�b�v�f�[�^���擾����
     * 
     * @param pictureString
     * @param width
     * @param height
     * @return �r�b�g�}�b�v�f�[�^
     */
    public static Bitmap getBitmap(String pictureString, int width, int height)
    {

        // �t�@�C���̕\�����@���኱�ύX���� �� Uri.Parse() ���� BitmapFactory�𗘗p������@�ցB
        BitmapFactory.Options opt = new BitmapFactory.Options();

        // OutOfMemory�G���[�΍�...��x�ǂݍ���ŉ摜�T�C�Y���擾
        opt.inJustDecodeBounds = true;
        opt.inDither = true;
        BitmapFactory.decodeFile(pictureString, opt);

        // �\���T�C�Y�ɍ��킹�ďk��...�\���T�C�Y���擾�ł��Ȃ������ꍇ�ɂ́AQVGA�T�C�Y�Ɖ��肷��
        if (width < 10)
        {
            width = 320;
        }
        if (height < 10)
        {
        	height = 240;
        }

        // �摜�̏k���T�C�Y�����肷�� (�c���A�����̏������ق��ɂ��킹��)
        int widthBounds = opt.outWidth / width;
        int heightBounds = opt.outHeight / height;
        opt.inSampleSize=Math.min(widthBounds, heightBounds);
        opt.inJustDecodeBounds = false;
        
        // �摜�t�@�C������������
    	return (BitmapFactory.decodeFile(pictureString, opt));
    }    
}

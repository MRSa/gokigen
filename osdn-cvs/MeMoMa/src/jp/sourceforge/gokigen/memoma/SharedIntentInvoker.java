package jp.sourceforge.gokigen.memoma;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 *   ���LIntent�𔭍s����N���X�B
 * 
 * @author MRSa
 *
 */
public class SharedIntentInvoker
{
	private static final String  IDENTIFIER = "Gokigen";
	
    /**
     *    ���[�����M�p��Intent�𔭍s���鏈���B
     * @param parent   �Ăяo����Activity
     * @param id          Intent���Ăяo����Activity�ɖ߂������ɁA�Ă΂�Ă����͉̂������ʂ���ID
     * @param mailTitle         ���L�f�[�^�^�C�g��
     * @param mailMessage   ���L�f�[�^�{��
     * @param fileName         �Y�t�f�[�^�t�@�C������
     * @param fileType           �Y�t�f�[�^�t�@�C���̌` (text/plain �Ƃ�  image/* �Ƃ� ...)
     */
    static public void shareContent(Activity parent, int id, String mailTitle, String mailMessage, String fileName, String fileType)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        try
        {
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, mailTitle);
            intent.putExtra(Intent.EXTRA_TEXT, mailMessage);
            try
            {
            	if ((fileName.isEmpty() == false)&&(fileType.isEmpty() == false))
            	{
                	// �t�@�C����Y�t����
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
                    intent.setType(fileType);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileName));
                    Log.v(IDENTIFIER, "Attached :" + fileName);
            	}
            }
            catch (Exception ee)
            {
            	// 
                Log.v(IDENTIFIER, "attach failure : " + fileName + "  " + ee.toString() + " " + ee.getMessage());
            }
            parent.startActivityForResult(intent, id);          	
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(parent, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.v(IDENTIFIER, "android.content.ActivityNotFoundException : " + ex.toString() + " " + ex.getMessage());
        }
        catch (Exception e)
        {
            Log.v(IDENTIFIER, "xxx : " + e.toString() + " " + e.getMessage());
        }
    }
}

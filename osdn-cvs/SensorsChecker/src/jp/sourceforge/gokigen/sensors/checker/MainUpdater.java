package jp.sourceforge.gokigen.sensors.checker;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  ��ʕ\���̍X�V�N���X
 *  
 * @author MRSa
 */
public class MainUpdater
{
    private Activity parent = null;

    public static final int SHOWMETHOD_DONTCARE = 0;
    public static final int SHOWMETHOD_TOAST = 1;

    /**
     *  �R���X�g���N�^
     *  
     * @param argument �e�N���X
     */
    public MainUpdater(Activity argument)
    {
        super();
        parent = argument;
    }

    /**
     *   ��ʕ\���̍X�V
     */
    public void updateScreen()
    {
         // 
    }

    /**
     *  TextView��String�^�̒l��ݒ肷��
     * @param id
     * @param value
     */
    public void setValueToTextView(int id, String stringData)
    {
        try
        {
            TextView view = (TextView) parent.findViewById(id);
            view.setText(stringData.toCharArray(), 0, stringData.length());
        }
        catch (Exception ex)
        {
            //
        }
    }
    
    /**
     *  ���b�Z�[�W��\������
     * @param message     �\�����郁�b�Z�[�W
     * @param showMethod  �\������
     */
    public void showMessage(String message, int showMethod)
    {
        Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();
    }
}

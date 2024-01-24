package jp.sourceforge.gokigen.mr999ctl;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  ���C����ʂ̕\�����X�V����l
 *  (����͎g��Ȃ����ȁH)
 *  
 * @author MRSa
 *
 */
public class MainScreenUpdater
{
    private Activity parent = null;

    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public MainScreenUpdater(Activity argument)
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
     *  ���b�Z�[�W��\������(�f�o�b�O�p)
     * @param message
     */
    public void toastMessage(String message)
    {
        Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();
    }
}

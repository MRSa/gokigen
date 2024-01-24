package jp.sourceforge.gokigen.warikan;

import jp.sourceforge.gokigen.warikan.R;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 *   ���C����ʂ̃C�x���g����������N���X
 * @author MRSa
 *
 */
public class MainScreenListener  implements OnClickListener, OnCheckedChangeListener
{
    private Activity parent = null;            // �e��
    private MainScreenUpdater updater = null;  // �q��

    /**
     *  �R���X�g���N�^
     * @param argument �Ăяo�����ƁiActivity�N���X�j
     */
    public MainScreenListener(Activity argument, MainScreenUpdater screenUpdater)
    {
        super();
        parent = argument;
        updater = screenUpdater;
    }

    /**
     *   �{�^���������ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
         showTenKey(v.getId());
    }
    
    /**
     *   �`�F�b�N�{�b�N�X���ύX�ƂȂ����Ƃ��̏���
     *   �iOFF �� ON�̂Ƃ��ɂ̓e���L�[��\������j
     */
    public void onCheckedChanged(CompoundButton v, boolean isChecked)
    {
        int id = v.getId();
        if (isChecked == false)
        {
            // �`�F�b�N���N���A���ꂽ�Ƃ��̏���
            updater.clearedCheckBox(id);
            return;
        }
        
        if (id == R.id.chkAddCharge)
        {
            // �w�`�b�v���Z�x�̃`�F�b�N�������Ƃ��̏���
            showTenKey(id);
            return;
        }
    }

    /**
     *  �e���L�[��ʂ�\�����鏈��
     * @param buttonId  �\������g���K�ƂȂ����I�u�W�F�N�g��ID
     */
    private void showTenKey(int buttonId)
    {
        try
        {
            // �ҏW�w���𑗂邱�ƂŁA�e���L�[���Ăяo���Ă݂�
            int resId = decideInformationMessageId(buttonId);
            int initialValue = decideInitialValue(buttonId);
            Intent editIntent = new Intent(parent, jp.sourceforge.gokigen.warikan.InputTenKey.class);
            editIntent.putExtra(InputTenKey.INPUT_INFORMATION, (int) resId);
            editIntent.putExtra(InputTenKey.INITIAL_VALUE, (int) initialValue);
            parent.startActivityForResult(editIntent, buttonId);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        }
    }

    /**
     *  ���͂��ꂽ�{�^���ɑΉ��������\�[�X(�\�����ׂ����b�Z�[�W)�̃��\�[�XID����������
     * @param buttonId
     * @return
     */
    private int decideInformationMessageId(int buttonId)
    {
        int resId = InputTenKey.NO_INFORMATION_MESSAGE;
        
        // �x�������z����͂���ꍇ...
        if (buttonId == R.id.totalPaymentData)
        {
            resId = R.string.payment_Message;
            return (resId);
        }

        // �w�`�b�v���Z�x�ŉ��Z����ꍇ...
        if (buttonId == R.id.chkAddCharge)
        {
            resId = R.string.serviceCharge_Message;
            return (resId);
        }

        // �w�X�|���T�[�x�̐l������͂���ꍇ...
        if (buttonId == R.id.numberOfGentlemen)
        {
            resId = R.string.numberOfGentlemen_Message;
            return (resId);
        }

        // �w�j���x�̐l������͂���ꍇ...
        if (buttonId == R.id.numberOfMen)
        {
            resId = R.string.numberOfMen_Message;
            return (resId);
        }

        // �w�����x�̐l������͂���ꍇ...
        if (buttonId == R.id.numberOfWomen)
        {
            resId = R.string.numberOfWomen_Message;
            return (resId);
        }
        
        // �w�X�|���T�[�x�̎x���z��ύX����ꍇ...
        if (buttonId == R.id.GentlemenMoney)
        {
            resId = R.string.paymentGentlemen_Message;
            return (resId);
        }

        // �w�j���x�̎x���z��ύX����ꍇ...
        if (buttonId == R.id.MenMoney)
        {
            resId = R.string.paymentMen_Message;
            return (resId);
        }

        // �w�����x�̎x���z��ύX����ꍇ...
        if (buttonId == R.id.WomenMoney)
        {
            resId = R.string.paymentWomen_Message;
            return (resId);
        }
        return (resId);
    }

    /**
     *   ���͂��ꂽ�{�^���ɑΉ����������f�[�^�l����������
     * @param buttonId
     * @return
     */
    private int decideInitialValue(int buttonId)
    {
        int value = 0;
        
        try
        {
            // �w�`�b�v���Z�x�ŉ��Z����ꍇ...
            if (buttonId == R.id.chkAddCharge)
            {
                // �Ƃ肠�����A���l��15���w�肵�Ă�����
                value = 15;
                return (value);
            }

            // �x�������z or �l������͂���ꍇ...
            if ((buttonId == R.id.totalPaymentData)||
                (buttonId == R.id.numberOfGentlemen)||
                (buttonId == R.id.numberOfMen)||
                (buttonId == R.id.numberOfWomen)||
                (buttonId == R.id.GentlemenMoney)||
                (buttonId == R.id.MenMoney)||
                (buttonId == R.id.WomenMoney))
            {
                // ���ݐݒ肳��Ă���l�����o���ď����l�ɂ���
                TextView area = (TextView) parent.findViewById(buttonId);
                value = Integer.valueOf(area.getText().toString());
                return (value);
            }
        }
        catch (Exception ex)
        {
            value = 0;
        }
        return (value);
    }
}

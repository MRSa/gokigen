package jp.sourceforge.gokigen.warikan;

import jp.sourceforge.gokigen.warikan.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.CheckBox;

/**
 *  TheWarikan�̃��C�����
 * @author MRSa
 *
 */
public class TheWarikan extends Activity
{
    MainScreenUpdater updater = null;    // ��ʕ\���X�V�N���X
    MainScreenListener listener = null;  // �C�x���g���X�i�N���X
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /**  ��ʏ��\���N���X�̐��� **/
        updater = new MainScreenUpdater((Activity) this);        
        
        /**  ��ʃI�u�W�F�N�g�ɑ΂��A�C�x���g����������N���X��ݒ肷��   **/
        /**  (Qt�ł����Ƃ���́ASignal-Slot �ڑ��݂����Ȃ��̂���...)      **/
        listener = new MainScreenListener((Activity) this, updater);

        final TextView totalPayment = (TextView) findViewById(R.id.totalPaymentData);
        totalPayment.setOnClickListener(listener);

        final TextView gentlemen = (TextView) findViewById(R.id.numberOfGentlemen);
        gentlemen.setOnClickListener(listener);
        
        final TextView men = (TextView) findViewById(R.id.numberOfMen);
        men.setOnClickListener(listener);
        
        final TextView women = (TextView) findViewById(R.id.numberOfWomen);
        women.setOnClickListener(listener);

        final TextView gentlemenPayment = (TextView) findViewById(R.id.GentlemenMoney);
        gentlemenPayment.setOnClickListener(listener);
        
        final TextView menPayment = (TextView) findViewById(R.id.MenMoney);
        menPayment.setOnClickListener(listener);
        
        final TextView womenPayment = (TextView) findViewById(R.id.WomenMoney);
        womenPayment.setOnClickListener(listener);
        
        final CheckBox chargeCheck = (CheckBox) findViewById(R.id.chkAddCharge);
        chargeCheck.setOnCheckedChangeListener(listener);
    }
    
    /**
     *  ��ʂ����ɉ�����Ƃ��̏���
     */
    @Override
    public void onPause()
    {
        super.onPause();
    }
    
    /**
     *  ��ʂ��\�ɏo�Ă����Ƃ��̏���
     */
    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            // ��ʕ\�����e�̍X�V���s���悤�A��ʏ��\���N���X�Ɉ˗�����
            updater.updateScreen();
        }
        catch (Exception ex)
        {
            // �Ȃɂ����Ȃ�
        }
    }
    
    /**
     *  �q��ʂ��牞������������Ƃ��̏���
     *  �i�������� ��ʏ��\���N���X�̐����ōs���B�j
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            // ��ʕ\����������ʏ��\���N���X�Ɉ˗�����
            updater.onActivityResult(requestCode, resultCode, data);
        }
        catch (Exception ex)
        {
            // ��O�����������Ƃ��ɂ́A�������Ȃ��B
        }
    }
}

package jp.sourceforge.gokigen.warikan;

import jp.sourceforge.gokigen.warikan.R;
import android.app.Activity;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  ���C����ʂ̕\�����X�V����l
 * @author MRSa
 *
 */
public class MainScreenUpdater
{
    private Activity parent    = null;
    private int     totalValue = 0;
    
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
     *  �\���f�[�^�̍X�V�w������M�����I
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode < 0)
        {
            //  �w�肳�ꂽ�l�����Ȃ�A�Ȃɂ����Ȃ� (�ُ�l)
            return;
        }

        boolean needCalculate = false;
        String stringData = "";
        switch (requestCode)
        {
          case R.id.totalPaymentData:
            // �x�������z�̕ύX����
            needCalculate = setPaymentValue(resultCode);
            break;

          case R.id.chkAddCharge:
            // �`�b�v���Z�������x�������z�̕ύX����
            updateTotalValueWithServiceCharge(resultCode);
            showTotalPaymentData();
            needCalculate = true;
            break;
              
          case R.id.numberOfGentlemen:
          case R.id.numberOfMen:
          case R.id.numberOfWomen:
            // �l��ݒ肷��
            stringData = stringData + resultCode;
            setValueToTextView(requestCode, stringData);
            needCalculate = true;
            break;
            
          case R.id.GentlemenMoney:
          case R.id.MenMoney:
          case R.id.WomenMoney:
            // �x���z���␳���ꂽ�ꍇ...
            modifyPayment(requestCode, resultCode);
            needCalculate = false;
            break;

          default:
            // do nothing!
            break;
        }
        if (needCalculate == true)
        {
            // ��肩��v�Z���s���A��ʕ\�����X�V����B
            calculatePayment();
        }
    }

    /**
     *  �`�F�b�N�{�b�N�X���N���A���ꂽ�Ƃ��̏���
     * @param id
     */
    public void clearedCheckBox(int id)
    {
        if (id == R.id.chkAddCharge)
        {
            // �`�b�v���Z�̃`�F�b�N���͂����ꂽ�I
            clearAddChargeCheckBox();
            return;
        }
    }

    /**
     *  TextView��String�^�̒l��ݒ肷��
     * @param id
     * @param value
     */
    private void setValueToTextView(int id, String stringData)
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
     *  TextView���琮���^�̒l���擾����
     * @param id
     * @param value
     */
    private int getIntValueFromTextView(int id)
    {
        int value = Integer.MIN_VALUE;
        try
        {
             TextView view = (TextView) parent.findViewById(id);
            value = Integer.valueOf(view.getText().toString());
        }
        catch (Exception ex)
        {
            //
        }
        return (value);
    }    

    /**
     *  �w�`�b�v���Z�x�̃`�F�b�N���͂����ꂽ�Ƃ��̏���
     */
    private void clearAddChargeCheckBox()
    {
        try
        {
            // �`�F�b�N���͂����ꂽ�Ƃ��̏���
            String data = "";
            setValueToTextView(R.id.serviceCharge, data);

            // �x�������z���X�V���� (�`�b�v���Z���͂���)
            totalValue = getIntValueFromTextView(R.id.totalPaymentData);
            if (totalValue < 0)
            {
                totalValue = 0;
            }
            // ���b�Z�[�W�̕\��
            showTotalPaymentData();
            
            // ���z�̍Čv�Z���s���A��ʂ��ĕ\������
            calculatePayment();
        }
        catch (Exception ex)
        {
            //
        }
    }
    
    /**
     *  �x�������z��ݒ肷��
     * @param value
     */
    private boolean setPaymentValue(int value)
    {
        boolean isUpdated = false;
        try
        {
            //  �ݒ肳�ꂽ�l���u�x�����z�v���ɕ\������
            String data = "" + value;
            setValueToTextView(R.id.totalPaymentData, data);
            
            // �x�������z��ݒ肷��
            totalValue = value;

            //  �x�������z���ύX�ɂȂ����Ƃ��́A�u�`�b�v���Z�v�̓N���A����
            //  �i�`�F�b�N�{�b�N�X�̏�ԍX�V�C�x���g�ŁA�\���f�[�^�͏����j
            CheckBox chargeCheck = (CheckBox) parent.findViewById(R.id.chkAddCharge);
            if (chargeCheck.isChecked() == false)
            {
                // �`�F�b�N�{�b�N�X�����Ă��Ȃ�...�Čv�Z����
                isUpdated = true;

                // �u�`�b�v���Z�v�̗̈�ɕ\�����Ă���f�[�^����������(�ی�)
                data = "";
                setValueToTextView(R.id.serviceCharge, data);

                // ���v���z��\��                
                showTotalPaymentData();
            }
            else
            {
                // �`�F�b�N�{�b�N�X�̏�ԕω��C�x���g��M���ɍČv�Z����
                chargeCheck.setChecked(false);
            }
        }
        catch (Exception ex)
        {
            //
        }        
        return (isUpdated);
    }

    /**
     *  �`�b�v���܂񂾎x�����z���v�Z���A��ʕ\�����X�V����
     * @param value
     */
    private void updateTotalValueWithServiceCharge(int value)
    {
        String   outputData  = "";
        try
        {
            // �`�b�v�̊������v�Z���A�x�������z�����߂�B
            String   unit = parent.getString(R.string.Yen);
            outputData = value + " %  ";
            int payment = getIntValueFromTextView(R.id.totalPaymentData);
            if (payment < 0)
            {
                totalValue = 0;
            }
            totalValue   = payment * value;
            totalValue = totalValue / 100;
            totalValue = totalValue + payment;

            // �x�����z����ʕ\��
            outputData = outputData + "(" + totalValue + unit + " )";
            setValueToTextView(R.id.serviceCharge, outputData);
        }
        catch (Exception ex)
        {
            //
        }
    }

    
    /**
     *  �x�����z���ς�����̂ŁA���ꂼ��̎x���z��␳���Ă���A�\������
     *  
     *  @param id �x���z��ύX�����ӏ�
     *  @param value �x���z�̕ύX��̒l
     */
    private void modifyPayment(int id, int value)
    {
        try
        {
            int nofGentlemen = getIntValueFromTextView(R.id.numberOfGentlemen);
            int nofMen = getIntValueFromTextView(R.id.numberOfMen);
            int nofWomen = getIntValueFromTextView(R.id.numberOfWomen);

            if ((nofGentlemen == 0)&&(nofMen == 0)&&(nofWomen == 0))
            {
                // �l�����[���l...�v�Z�����ɏI������ (���肦�Ȃ��͂�)
                return;
            }
            
            int gentlemen = getIntValueFromTextView(R.id.GentlemenMoney);
            int men = getIntValueFromTextView(R.id.MenMoney);
            int women = getIntValueFromTextView(R.id.WomenMoney);

            int restValue = totalValue;
            if (id == R.id.WomenMoney)
            {
                // �����̋��z���ύX�ƂȂ����ꍇ...
                if (((value - men) <= 300)&&((value - men) >= -300))
                {
                    // Men�� Women�̎x�����z���덷�͈͂ɓ������ꍇ...500�~�P�ʂ̊��芨�ɂ���
                    restValue = totalValue - nofGentlemen * gentlemen;

                    men = calculateSingleCategory((nofMen + nofWomen), restValue, 500);
                    women = men;
                    updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                   }
                else
                {
                    // �����̋��z����͂����l�ɒu��������
                    women = value;
                    int paymentValue = totalValue - women * nofWomen;

                    // �X�|���T�[�̎x�����z�����肷��
                    gentlemen = calculateGentlemen(paymentValue, nofGentlemen, nofMen, 0);
 
                    // �j���̎x���z���v�Z���� (100�~�P�ʂ�)
                    restValue = paymentValue - nofGentlemen * gentlemen;
                    men = calculateSingleCategory(nofMen, restValue, 100);
                    if ((nofMen > 0)&&(men < 1000)&&(nofWomen > 0))
                    {
                        // �j���̂ق����x�������z�����Ȃ��Ȃ�ꍇ... 500�~�P�ʂł̒j�����z�̊��芨�Ƃ���
                        restValue = paymentValue - gentlemen * nofGentlemen;
                        men = calculateSingleCategory((nofMen + nofWomen), restValue, 500);
                        women = men;
                    }

                    // �x�������ʂ�\������
                    updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                }
                return;
            }
            if (id == R.id.GentlemenMoney)
            {
                // �X�|���T�[�̋��z��ύX���悤�Ƃ����Ƃ��A�{���ɕύX���邩�A�m�F�����߂�B
                boolean check = confirmToChangeGentlemenPayment(nofGentlemen, gentlemen, value);
                if (check == false)
                {
                    // �x�����z�̕ύX�𒆎~����
                    return;
                }

            	// �X�|���T�[�̋��z����͂����l�ɒu��������
                gentlemen = value;
                restValue= totalValue - gentlemen * nofGentlemen;
                if (restValue <= 0)
                {
                    // �x���z�����肵��... ���^�[������
                    updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                    return;
                }

                // �����̎x�������z���v�Z����
                women = calculateWomen(restValue, nofGentlemen, nofMen, nofWomen);

                // �j���̎x���z���v�Z���� (500�~�P�ʂ�)
                restValue = restValue - nofWomen * women;
                men = calculateSingleCategory(nofMen, restValue, 500);
                if ((nofMen > 0)&&(men < 1000)&&(nofWomen > 0))
                {
                    // �j���̂ق������z�����Ȃ��Ȃ�ꍇ... 500�~�P�ʂł̒j�����z�̊��芨�Ƃ���
                    restValue = totalValue - gentlemen * nofGentlemen;
                    men = calculateSingleCategory((nofMen + nofWomen), restValue, 500);
                    women = men;
                }
                // �x���z�����肵��... ���^�[������
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }

            if (id == R.id.MenMoney)
            {
                // �j���̋��z��ύX�����Ƃ��A�{���ɕύX���邩�A�m�F�����߂�B
                boolean check = confirmToChangeMenPayment(nofGentlemen, gentlemen, value);
                if (check == false)
                {
                    // �x�����z�̕ύX�𒆎~����
                    return;
                }

                // �j���̎x���z��ύX����
                men = value;
                restValue = totalValue - gentlemen * nofGentlemen - men * nofMen;
                if (restValue > 0)
                {
                    // �����̎x�������z���v�Z����
                    women = calculateSingleCategory(nofWomen, restValue, 1000);
                }

                // �x���z�����肵��... ���^�[������
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }
        }
        catch (Exception ex)
        {
            //
        }
    }

    /**
     *  �l���Ǝx�������z����A���ꂼ��̎x�����z���v�Z���A�\������
     */
    private void calculatePayment()
    {
        if (totalValue <= 0)
        {
            // ���z�����͂���Ă��Ȃ������̂ŁA�Ȃɂ������ɏI������
            return;
        }

        try
        {
            // ���ꂼ��̐l�����擾����
            int nofGentlemen = getIntValueFromTextView(R.id.numberOfGentlemen);
            int nofMen = getIntValueFromTextView(R.id.numberOfMen);
            int nofWomen = getIntValueFromTextView(R.id.numberOfWomen);

            if ((nofGentlemen <= 0)&&(nofMen <= 0)&&(nofWomen <= 0))
            {
                // �l�����[���l...�v�Z�����ɏI������
                updatePayment(0, 0, 0, 0, 0, 0);
                return;
            }

            // �e�O���[�v���Ƃ̎x�������z�����߂�
            int gentlemen = 0;
            int men = 0;
            int women = 0;

            //  �ЂƂ̃O���[�v�̏ꍇ�̏���...
            if ((nofMen == 0)&&(nofWomen == 0))
            {
                // �X�|���T�[�����̏ꍇ...1�~�P�ʂł������芄�芨
                gentlemen = calculateSingleCategory(nofGentlemen, totalValue, 1);
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }
            if ((nofGentlemen == 0)&&(nofWomen == 0))
            {
                // �j�������̏ꍇ...100�~�P�ʂ̊��芨
                men = calculateSingleCategory(nofMen, totalValue, 100);
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }
            if ((nofGentlemen == 0)&&(nofMen == 0))
            {
                // ���������̏ꍇ...10�~�P�ʂł̊��芨
                women = calculateSingleCategory(nofWomen, totalValue, 10);
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }

            //  ���芨���Z���W�b�N... ���\�ނ��Ⴍ���Ⴉ���B�B�B

            // �X�|���T�[�̎x�����z�����肷��
            gentlemen = calculateGentlemen(totalValue, nofGentlemen, nofMen, nofWomen);

            // �c����z��j���Ə����ł�肩��
            int restValue = totalValue - gentlemen * nofGentlemen;
            if (restValue <= 0)
            {
                // �x���z�����肵��... ���^�[������
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }

            // �c����z�� �j���̐l���~�P�O�O�O�~��菬������΁A�����͕���Ȃ�
            women = calculateWomen(restValue, nofGentlemen, nofMen, nofWomen);

            // �j���̎x���z���v�Z���� (500�~�P�ʂ�)
            restValue = restValue - nofWomen * women;
            men = calculateSingleCategory(nofMen, restValue, 500);
            if ((nofMen > 0)&&(men < 1000)&&(nofWomen > 0))
            {
                // �j���̂ق������z�����Ȃ��Ȃ�ꍇ... 500�~�P�ʂł̒j�����z�̊��芨�Ƃ���
                restValue = totalValue - gentlemen * nofGentlemen;
                men = calculateSingleCategory((nofMen + nofWomen), restValue, 500);
                women = men;
            }

            if ((nofMen > 0)&&(men > gentlemen)&&(nofGentlemen > 0))
            {
                // �j���̋��z���X�|���T�[�̋��z������ꍇ...�X�|���T�[�̋��z�����Z���čČv�Z
                gentlemen = gentlemen + 5000;  // 5000�~�����Z
                restValue = totalValue - gentlemen * nofGentlemen - women * nofWomen;
                men = calculateSingleCategory(nofMen, restValue, 500);               
                if ((nofMen > 0)&&(men < 1000)&&(nofWomen > 0))
                {
                    // �Čv�Z��A�j���̂ق������z�����Ȃ��Ȃ�ꍇ... 500�~�P�ʂł̒j�����z�̊��芨�Ƃ���
                    restValue = totalValue - gentlemen * nofGentlemen;
                    men = calculateSingleCategory((nofMen + nofWomen), restValue, 500);
                    women = men;
                }
            }

            // �x�������ʂ�\������
            updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
        }
        catch (Exception ex)
        {
            //
        }
    }

    
    /**
     *  �X�|���T�[�̎x���z�����肷��
     * @param payment       �x�����K�v�̂���z
     * @param nofGentlemen  �X�|���T�[�̐l��
     * @param nofMen        �j���̐l��
     * @param nofWomen      �����̐l��
     * @return
     */
    private int calculateGentlemen(int payment, int nofGentlemen, int nofMen, int nofWomen)
    {
        if (nofGentlemen == 0)
        {
            return (0);
        }
        
        int totalPerson = nofGentlemen + nofMen + nofWomen;
        int gentlemen = 0;
        int percentage = (nofGentlemen * 100) / totalPerson;
        if (percentage > 60)
        {
            // �X�|���T�[�䗦�� 60%�𒴂����ꍇ�ɂ́A�X�|���T�[�����Ŋ��芨����
            gentlemen = calculateSingleCategory(nofGentlemen, payment, 1000);
            //updatePayment(nofGentlemen, gentlemen, nofMen, 0, nofWomen, 0);
            return (gentlemen);
        }
        
        int value = 0;
        if (percentage > 30)
        {
            // �X�|���T�[�̊����� 30% �𒴂��Ă����ꍇ�ɂ́A�X�|���T�[��2/3���x����
            value = payment * 2 / 3;
            gentlemen = calculateSingleCategory(nofGentlemen, value, 1000);
        }
        else
        {
            // �X�|���T�[�̊����� 30% �����������ꍇ�ɂ́A10000�~�ȏ�A5000�~�P�ʂŎx����
            value = payment * (percentage * 2) / 100;
            if ((value / nofGentlemen) > 10000)
            {
                gentlemen = calculateSingleCategory(nofGentlemen, value, 5000);
            }
            else
            {
                gentlemen = 10000;  // ��{��10000�~�I
            }
        }
        return (gentlemen);
    }

    /**
     *  �����̎x���z�����肷��
     * @param payment       �x�����K�v�̂���z
     * @param nofGentlemen  �X�|���T�[�̐l��
     * @param nofMen        �j���̐l��
     * @param nofWomen      �����̐l��
     * @return
     */
    private int calculateWomen(int restValue, int nofGentlemen, int nofMen, int nofWomen)
    {
        int women = 0;
        if (restValue > nofMen * 1000)
        {
            // �����͂R���̂P���x���x���� (1000�~�P�ʂŁA�Q���l������������Čv�Z)
            int percentage = nofWomen * 100 / (nofWomen + nofMen + nofGentlemen);
            int value = restValue * percentage / 300;
            women = calculateSingleCategory(nofWomen, value, 1000);            
        }
        return (women);
    }
    
    
    /**
     *   ���z�̕\��
     * @param nofGentlemen  �X�|���T�[�̐l��
     * @param gentlemen     �X�|���T�[�̋��z
     * @param nofMen        �j���̐l��
     * @param men           �j���̋��z
     * @param nofWomen      �����̐l��
     * @param women         �����̋��z
     */
    private void updatePayment(int nofGentlemen, int gentlemen, int nofMen, int men, int nofWomen, int women)
    {
        // �X�|���T�[�̋��z�\��
        String data = "" + gentlemen;
        setValueToTextView(R.id.GentlemenMoney, data);

        // �j���̋��z�\��
        data = "" + men;
        setValueToTextView(R.id.MenMoney, data);

        // �����̋��z�\��
        data = "" + women;
        setValueToTextView(R.id.WomenMoney, data);
        
        // ���z�̋��z�\��
        int total = gentlemen * nofGentlemen + men * nofMen + women * nofWomen;
        data = "" + total;
        setValueToTextView(R.id.totalAmountValue, data);

        // ���܂�̋��z
        int mod   = total - totalValue;
        if (mod > 0)
        {
            // ���܂肪����Ε\������
            data = parent.getString(R.string.modStart) + mod + parent.getString(R.string.modEnd);
            setValueToTextView(R.id.additionalInfoTotalAmount, data);
        }
        else
        {
            // ���܂�̕\���̈���N���A����
            data = "";
            setValueToTextView(R.id.additionalInfoTotalAmount, data);                
        }

        if ((nofGentlemen > 0)||(nofMen > 0)||(nofWomen > 0))
        {
            // �u�v�Z���܂����v���b�Z�[�W��\������
            showMessage(parent.getString(R.string.info_updatePayment));
        }
    }
 
    /**
     *  �x�����z���w�肳�ꂽ�P�ʂŐ؂�グ�Ċ��芨����
     * @param person �l��
     * @param payment �x�������z
     * @param unit    �ۂ߂�P��
     * @return
     */
    private int calculateSingleCategory(int person, int payment, int unit)
    {
        // ���������A�ی��ŃK�[�h��������
        if ((person <= 0)||(payment < 0))
        {
            return (0);
        }

        // MEMO: ����Z�����Ƃ��̏����_���������܂炳�Ȃ��悤�ɂ���
        //      (���̏����͂���Ȃ�...�����H)
        int value = payment * 100 / person; // 10�{�����l���v�Z
        int modular = value % 100;
        if (modular > 0)
        {
            value = value + (100 - modular);  // �������肳����
        }
        value = value / 100;

        //  �v�Z���ʂ��w�肳�ꂽ�P�ʂł܂�߂�(���グ����)
        if (unit <= 0)
        {
            unit = 1;
        }
        modular = value % unit;
        if (modular > 0)
        {
            value = value + (unit - modular);  // �������肷��
        }
        return (value);
    }

    /**
     *  �X�|���T�[�̎x�������z��ύX����Ƃ��A�{���ɕύX���邩�m�F���s������
     * @param number �X�|���T�[�̐l��
     * @param current ���݂̎x�������z
     * @param change  �ύX����x�������z
     * @return  true:�ύX���� ,  false:�ύX���~
     */    
    private boolean confirmToChangeGentlemenPayment(int number, int current, int change)
    {
    	return (true);    // �ύX��������
    }

    /**
     *  �j���̎x�������z��ύX����Ƃ��A�{���ɕύX���邩�m�F���s������
     * @param number �j���̐l��
     * @param current ���݂̎x�������z
     * @param change  �ύX����x�������z
     * @return  true:�ύX���� ,  false:�ύX���~
     */    
    private boolean confirmToChangeMenPayment(int number, int current, int change)
    {
        return (true);    // �ύX��������    	
    }

    /**
     *   �x�������z�����b�Z�[�W�\������
     */
    private void showTotalPaymentData()
    {
        String outputData = parent.getString(R.string.info_payment);
        outputData = outputData + totalValue + parent.getString(R.string.Yen);
        outputData = outputData + parent.getString(R.string.info_postfix);
        showMessage(outputData);
    }
    
    /**
     *  ���b�Z�[�W��\������
     * @param outputData  �\�����郁�b�Z�[�W
     */
    private void showMessage(String outputData)
    {
        // ������ƃ��b�Z�[�W��\������
        Toast.makeText(parent, outputData, Toast.LENGTH_SHORT).show();
    }
}

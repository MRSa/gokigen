package jp.sourceforge.gokigen.warikan;

import jp.sourceforge.gokigen.warikan.R;
import android.app.Activity;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  メイン画面の表示を更新する人
 * @author MRSa
 *
 */
public class MainScreenUpdater
{
    private Activity parent    = null;
    private int     totalValue = 0;
    
    /**
     *  コンストラクタ
     * @param argument
     */
    public MainScreenUpdater(Activity argument)
    {
        super();
        parent = argument;
    }

    /**
     *   画面表示の更新
     */
    public void updateScreen()
    {
         // 
    }

    /**
     *  表示データの更新指示を受信した！
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode < 0)
        {
            //  指定された値が負なら、なにもしない (異常値)
            return;
        }

        boolean needCalculate = false;
        String stringData = "";
        switch (requestCode)
        {
          case R.id.totalPaymentData:
            // 支払い総額の変更処理
            needCalculate = setPaymentValue(resultCode);
            break;

          case R.id.chkAddCharge:
            // チップ加算をした支払い総額の変更処理
            updateTotalValueWithServiceCharge(resultCode);
            showTotalPaymentData();
            needCalculate = true;
            break;
              
          case R.id.numberOfGentlemen:
          case R.id.numberOfMen:
          case R.id.numberOfWomen:
            // 値を設定する
            stringData = stringData + resultCode;
            setValueToTextView(requestCode, stringData);
            needCalculate = true;
            break;
            
          case R.id.GentlemenMoney:
          case R.id.MenMoney:
          case R.id.WomenMoney:
            // 支払額が補正された場合...
            modifyPayment(requestCode, resultCode);
            needCalculate = false;
            break;

          default:
            // do nothing!
            break;
        }
        if (needCalculate == true)
        {
            // わりかん計算を行い、画面表示を更新する。
            calculatePayment();
        }
    }

    /**
     *  チェックボックスがクリアされたときの処理
     * @param id
     */
    public void clearedCheckBox(int id)
    {
        if (id == R.id.chkAddCharge)
        {
            // チップ加算のチェックがはずされた！
            clearAddChargeCheckBox();
            return;
        }
    }

    /**
     *  TextViewにString型の値を設定する
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
     *  TextViewから整数型の値を取得する
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
     *  『チップ加算』のチェックがはずされたときの処理
     */
    private void clearAddChargeCheckBox()
    {
        try
        {
            // チェックがはずされたときの処理
            String data = "";
            setValueToTextView(R.id.serviceCharge, data);

            // 支払い総額を更新する (チップ加算をはずす)
            totalValue = getIntValueFromTextView(R.id.totalPaymentData);
            if (totalValue < 0)
            {
                totalValue = 0;
            }
            // メッセージの表示
            showTotalPaymentData();
            
            // 金額の再計算を行い、画面を再表示する
            calculatePayment();
        }
        catch (Exception ex)
        {
            //
        }
    }
    
    /**
     *  支払い金額を設定する
     * @param value
     */
    private boolean setPaymentValue(int value)
    {
        boolean isUpdated = false;
        try
        {
            //  設定された値を「支払金額」欄に表示する
            String data = "" + value;
            setValueToTextView(R.id.totalPaymentData, data);
            
            // 支払い総額を設定する
            totalValue = value;

            //  支払い総額が変更になったときは、「チップ加算」はクリアする
            //  （チェックボックスの状態更新イベントで、表示データは消す）
            CheckBox chargeCheck = (CheckBox) parent.findViewById(R.id.chkAddCharge);
            if (chargeCheck.isChecked() == false)
            {
                // チェックボックスがついていない...再計算する
                isUpdated = true;

                // 「チップ加算」の領域に表示しているデータを消去する(保険)
                data = "";
                setValueToTextView(R.id.serviceCharge, data);

                // 合計金額を表示                
                showTotalPaymentData();
            }
            else
            {
                // チェックボックスの状態変化イベント受信時に再計算する
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
     *  チップを含んだ支払総額を計算し、画面表示を更新する
     * @param value
     */
    private void updateTotalValueWithServiceCharge(int value)
    {
        String   outputData  = "";
        try
        {
            // チップの割合を計算し、支払い総額を求める。
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

            // 支払総額を画面表示
            outputData = outputData + "(" + totalValue + unit + " )";
            setValueToTextView(R.id.serviceCharge, outputData);
        }
        catch (Exception ex)
        {
            //
        }
    }

    
    /**
     *  支払い額が変わったので、それぞれの支払額を補正してから、表示する
     *  
     *  @param id 支払額を変更した箇所
     *  @param value 支払額の変更後の値
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
                // 人数がゼロ人...計算せずに終了する (ありえないはず)
                return;
            }
            
            int gentlemen = getIntValueFromTextView(R.id.GentlemenMoney);
            int men = getIntValueFromTextView(R.id.MenMoney);
            int women = getIntValueFromTextView(R.id.WomenMoney);

            int restValue = totalValue;
            if (id == R.id.WomenMoney)
            {
                // 女性の金額が変更となった場合...
                if (((value - men) <= 300)&&((value - men) >= -300))
                {
                    // Menと Womenの支払い額が誤差範囲に入った場合...500円単位の割り勘にする
                    restValue = totalValue - nofGentlemen * gentlemen;

                    men = calculateSingleCategory((nofMen + nofWomen), restValue, 500);
                    women = men;
                    updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                   }
                else
                {
                    // 女性の金額を入力した値に置き換える
                    women = value;
                    int paymentValue = totalValue - women * nofWomen;

                    // スポンサーの支払い額を決定する
                    gentlemen = calculateGentlemen(paymentValue, nofGentlemen, nofMen, 0);
 
                    // 男性の支払額を計算する (100円単位で)
                    restValue = paymentValue - nofGentlemen * gentlemen;
                    men = calculateSingleCategory(nofMen, restValue, 100);
                    if ((nofMen > 0)&&(men < 1000)&&(nofWomen > 0))
                    {
                        // 男性のほうが支払い金額が少なくなる場合... 500円単位での男女同額の割り勘とする
                        restValue = paymentValue - gentlemen * nofGentlemen;
                        men = calculateSingleCategory((nofMen + nofWomen), restValue, 500);
                        women = men;
                    }

                    // 支払い結果を表示する
                    updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                }
                return;
            }
            if (id == R.id.GentlemenMoney)
            {
                // スポンサーの金額を変更しようとしたとき、本当に変更するか、確認を求める。
                boolean check = confirmToChangeGentlemenPayment(nofGentlemen, gentlemen, value);
                if (check == false)
                {
                    // 支払い額の変更を中止する
                    return;
                }

            	// スポンサーの金額を入力した値に置き換える
                gentlemen = value;
                restValue= totalValue - gentlemen * nofGentlemen;
                if (restValue <= 0)
                {
                    // 支払額が決定した... リターンする
                    updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                    return;
                }

                // 女性の支払い金額を計算する
                women = calculateWomen(restValue, nofGentlemen, nofMen, nofWomen);

                // 男性の支払額を計算する (500円単位で)
                restValue = restValue - nofWomen * women;
                men = calculateSingleCategory(nofMen, restValue, 500);
                if ((nofMen > 0)&&(men < 1000)&&(nofWomen > 0))
                {
                    // 男性のほうが金額が少なくなる場合... 500円単位での男女同額の割り勘とする
                    restValue = totalValue - gentlemen * nofGentlemen;
                    men = calculateSingleCategory((nofMen + nofWomen), restValue, 500);
                    women = men;
                }
                // 支払額が決定した... リターンする
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }

            if (id == R.id.MenMoney)
            {
                // 男性の金額を変更したとき、本当に変更するか、確認を求める。
                boolean check = confirmToChangeMenPayment(nofGentlemen, gentlemen, value);
                if (check == false)
                {
                    // 支払い額の変更を中止する
                    return;
                }

                // 男性の支払額を変更する
                men = value;
                restValue = totalValue - gentlemen * nofGentlemen - men * nofMen;
                if (restValue > 0)
                {
                    // 女性の支払い金額を計算する
                    women = calculateSingleCategory(nofWomen, restValue, 1000);
                }

                // 支払額が決定した... リターンする
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
     *  人数と支払い総額から、それぞれの支払い額を計算し、表示する
     */
    private void calculatePayment()
    {
        if (totalValue <= 0)
        {
            // 総額が入力されていなかったので、なにもせずに終了する
            return;
        }

        try
        {
            // それぞれの人数を取得する
            int nofGentlemen = getIntValueFromTextView(R.id.numberOfGentlemen);
            int nofMen = getIntValueFromTextView(R.id.numberOfMen);
            int nofWomen = getIntValueFromTextView(R.id.numberOfWomen);

            if ((nofGentlemen <= 0)&&(nofMen <= 0)&&(nofWomen <= 0))
            {
                // 人数がゼロ人...計算せずに終了する
                updatePayment(0, 0, 0, 0, 0, 0);
                return;
            }

            // 各グループごとの支払い金額を求める
            int gentlemen = 0;
            int men = 0;
            int women = 0;

            //  ひとつのグループの場合の処理...
            if ((nofMen == 0)&&(nofWomen == 0))
            {
                // スポンサーだけの場合...1円単位できっちり割り勘
                gentlemen = calculateSingleCategory(nofGentlemen, totalValue, 1);
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }
            if ((nofGentlemen == 0)&&(nofWomen == 0))
            {
                // 男性だけの場合...100円単位の割り勘
                men = calculateSingleCategory(nofMen, totalValue, 100);
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }
            if ((nofGentlemen == 0)&&(nofMen == 0))
            {
                // 女性だけの場合...10円単位での割り勘
                women = calculateSingleCategory(nofWomen, totalValue, 10);
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }

            //  割り勘演算ロジック... 結構むちゃくちゃかも。。。

            // スポンサーの支払い額を決定する
            gentlemen = calculateGentlemen(totalValue, nofGentlemen, nofMen, nofWomen);

            // 残り金額を男性と女性でわりかん
            int restValue = totalValue - gentlemen * nofGentlemen;
            if (restValue <= 0)
            {
                // 支払額が決定した... リターンする
                updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
                return;
            }

            // 残り金額が 男性の人数×１０００円より小さければ、女性は払わない
            women = calculateWomen(restValue, nofGentlemen, nofMen, nofWomen);

            // 男性の支払額を計算する (500円単位で)
            restValue = restValue - nofWomen * women;
            men = calculateSingleCategory(nofMen, restValue, 500);
            if ((nofMen > 0)&&(men < 1000)&&(nofWomen > 0))
            {
                // 男性のほうが金額が少なくなる場合... 500円単位での男女同額の割り勘とする
                restValue = totalValue - gentlemen * nofGentlemen;
                men = calculateSingleCategory((nofMen + nofWomen), restValue, 500);
                women = men;
            }

            if ((nofMen > 0)&&(men > gentlemen)&&(nofGentlemen > 0))
            {
                // 男性の金額がスポンサーの金額を上回る場合...スポンサーの金額を加算して再計算
                gentlemen = gentlemen + 5000;  // 5000円を加算
                restValue = totalValue - gentlemen * nofGentlemen - women * nofWomen;
                men = calculateSingleCategory(nofMen, restValue, 500);               
                if ((nofMen > 0)&&(men < 1000)&&(nofWomen > 0))
                {
                    // 再計算後、男性のほうが金額が少なくなる場合... 500円単位での男女同額の割り勘とする
                    restValue = totalValue - gentlemen * nofGentlemen;
                    men = calculateSingleCategory((nofMen + nofWomen), restValue, 500);
                    women = men;
                }
            }

            // 支払い結果を表示する
            updatePayment(nofGentlemen, gentlemen, nofMen, men, nofWomen, women);
        }
        catch (Exception ex)
        {
            //
        }
    }

    
    /**
     *  スポンサーの支払額を決定する
     * @param payment       支払う必要のある額
     * @param nofGentlemen  スポンサーの人数
     * @param nofMen        男性の人数
     * @param nofWomen      女性の人数
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
            // スポンサー比率が 60%を超えた場合には、スポンサーだけで割り勘する
            gentlemen = calculateSingleCategory(nofGentlemen, payment, 1000);
            //updatePayment(nofGentlemen, gentlemen, nofMen, 0, nofWomen, 0);
            return (gentlemen);
        }
        
        int value = 0;
        if (percentage > 30)
        {
            // スポンサーの割合が 30% を超えていた場合には、スポンサーが2/3を支払う
            value = payment * 2 / 3;
            gentlemen = calculateSingleCategory(nofGentlemen, value, 1000);
        }
        else
        {
            // スポンサーの割合が 30% よりも小さい場合には、10000円以上、5000円単位で支払う
            value = payment * (percentage * 2) / 100;
            if ((value / nofGentlemen) > 10000)
            {
                gentlemen = calculateSingleCategory(nofGentlemen, value, 5000);
            }
            else
            {
                gentlemen = 10000;  // 基本は10000円！
            }
        }
        return (gentlemen);
    }

    /**
     *  女性の支払額を決定する
     * @param payment       支払う必要のある額
     * @param nofGentlemen  スポンサーの人数
     * @param nofMen        男性の人数
     * @param nofWomen      女性の人数
     * @return
     */
    private int calculateWomen(int restValue, int nofGentlemen, int nofMen, int nofWomen)
    {
        int women = 0;
        if (restValue > nofMen * 1000)
        {
            // 女性は３分の１程度を支払う (1000円単位で、参加人数比も加味して計算)
            int percentage = nofWomen * 100 / (nofWomen + nofMen + nofGentlemen);
            int value = restValue * percentage / 300;
            women = calculateSingleCategory(nofWomen, value, 1000);            
        }
        return (women);
    }
    
    
    /**
     *   金額の表示
     * @param nofGentlemen  スポンサーの人数
     * @param gentlemen     スポンサーの金額
     * @param nofMen        男性の人数
     * @param men           男性の金額
     * @param nofWomen      女性の人数
     * @param women         女性の金額
     */
    private void updatePayment(int nofGentlemen, int gentlemen, int nofMen, int men, int nofWomen, int women)
    {
        // スポンサーの金額表示
        String data = "" + gentlemen;
        setValueToTextView(R.id.GentlemenMoney, data);

        // 男性の金額表示
        data = "" + men;
        setValueToTextView(R.id.MenMoney, data);

        // 女性の金額表示
        data = "" + women;
        setValueToTextView(R.id.WomenMoney, data);
        
        // 総額の金額表示
        int total = gentlemen * nofGentlemen + men * nofMen + women * nofWomen;
        data = "" + total;
        setValueToTextView(R.id.totalAmountValue, data);

        // あまりの金額
        int mod   = total - totalValue;
        if (mod > 0)
        {
            // あまりがあれば表示する
            data = parent.getString(R.string.modStart) + mod + parent.getString(R.string.modEnd);
            setValueToTextView(R.id.additionalInfoTotalAmount, data);
        }
        else
        {
            // あまりの表示領域をクリアする
            data = "";
            setValueToTextView(R.id.additionalInfoTotalAmount, data);                
        }

        if ((nofGentlemen > 0)||(nofMen > 0)||(nofWomen > 0))
        {
            // 「計算しました」メッセージを表示する
            showMessage(parent.getString(R.string.info_updatePayment));
        }
    }
 
    /**
     *  支払い額を指定された単位で切り上げて割り勘する
     * @param person 人数
     * @param payment 支払い総額
     * @param unit    丸める単位
     * @return
     */
    private int calculateSingleCategory(int person, int payment, int unit)
    {
        // いちおう、保険でガードをかける
        if ((person <= 0)||(payment < 0))
        {
            return (0);
        }

        // MEMO: 割り算したときの小数点部分をあまらさないようにする
        //      (この処理はいらない...かも？)
        int value = payment * 100 / person; // 10倍した値を計算
        int modular = value % 100;
        if (modular > 0)
        {
            value = value + (100 - modular);  // 桁あがりさせる
        }
        value = value / 100;

        //  計算結果を指定された単位でまるめる(桁上げする)
        if (unit <= 0)
        {
            unit = 1;
        }
        modular = value % unit;
        if (modular > 0)
        {
            value = value + (unit - modular);  // 桁あがりする
        }
        return (value);
    }

    /**
     *  スポンサーの支払い金額を変更するとき、本当に変更するか確認を行う処理
     * @param number スポンサーの人数
     * @param current 現在の支払い金額
     * @param change  変更する支払い金額
     * @return  true:変更許可 ,  false:変更中止
     */    
    private boolean confirmToChangeGentlemenPayment(int number, int current, int change)
    {
    	return (true);    // 変更を許可する
    }

    /**
     *  男性の支払い金額を変更するとき、本当に変更するか確認を行う処理
     * @param number 男性の人数
     * @param current 現在の支払い金額
     * @param change  変更する支払い金額
     * @return  true:変更許可 ,  false:変更中止
     */    
    private boolean confirmToChangeMenPayment(int number, int current, int change)
    {
        return (true);    // 変更を許可する    	
    }

    /**
     *   支払い金額をメッセージ表示する
     */
    private void showTotalPaymentData()
    {
        String outputData = parent.getString(R.string.info_payment);
        outputData = outputData + totalValue + parent.getString(R.string.Yen);
        outputData = outputData + parent.getString(R.string.info_postfix);
        showMessage(outputData);
    }
    
    /**
     *  メッセージを表示する
     * @param outputData  表示するメッセージ
     */
    private void showMessage(String outputData)
    {
        // もわっとメッセージを表示する
        Toast.makeText(parent, outputData, Toast.LENGTH_SHORT).show();
    }
}

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
 *   メイン画面のイベントを処理するクラス
 * @author MRSa
 *
 */
public class MainScreenListener  implements OnClickListener, OnCheckedChangeListener
{
    private Activity parent = null;            // 親分
    private MainScreenUpdater updater = null;  // 子分

    /**
     *  コンストラクタ
     * @param argument 呼び出しもと（Activityクラス）
     */
    public MainScreenListener(Activity argument, MainScreenUpdater screenUpdater)
    {
        super();
        parent = argument;
        updater = screenUpdater;
    }

    /**
     *   ボタンが押されたときの処理
     */
    public void onClick(View v)
    {
         showTenKey(v.getId());
    }
    
    /**
     *   チェックボックスが変更となったときの処理
     *   （OFF ⇒ ONのときにはテンキーを表示する）
     */
    public void onCheckedChanged(CompoundButton v, boolean isChecked)
    {
        int id = v.getId();
        if (isChecked == false)
        {
            // チェックがクリアされたときの処理
            updater.clearedCheckBox(id);
            return;
        }
        
        if (id == R.id.chkAddCharge)
        {
            // 『チップ加算』のチェックがついたときの処理
            showTenKey(id);
            return;
        }
    }

    /**
     *  テンキー画面を表示する処理
     * @param buttonId  表示するトリガとなったオブジェクトのID
     */
    private void showTenKey(int buttonId)
    {
        try
        {
            // 編集指示を送ることで、テンキーを呼び出してみる
            int resId = decideInformationMessageId(buttonId);
            int initialValue = decideInitialValue(buttonId);
            Intent editIntent = new Intent(parent, jp.sourceforge.gokigen.warikan.InputTenKey.class);
            editIntent.putExtra(InputTenKey.INPUT_INFORMATION, (int) resId);
            editIntent.putExtra(InputTenKey.INITIAL_VALUE, (int) initialValue);
            parent.startActivityForResult(editIntent, buttonId);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        }
    }

    /**
     *  入力されたボタンに対応したリソース(表示すべきメッセージ)のリソースIDを応答する
     * @param buttonId
     * @return
     */
    private int decideInformationMessageId(int buttonId)
    {
        int resId = InputTenKey.NO_INFORMATION_MESSAGE;
        
        // 支払い金額を入力する場合...
        if (buttonId == R.id.totalPaymentData)
        {
            resId = R.string.payment_Message;
            return (resId);
        }

        // 『チップ加算』で加算する場合...
        if (buttonId == R.id.chkAddCharge)
        {
            resId = R.string.serviceCharge_Message;
            return (resId);
        }

        // 『スポンサー』の人数を入力する場合...
        if (buttonId == R.id.numberOfGentlemen)
        {
            resId = R.string.numberOfGentlemen_Message;
            return (resId);
        }

        // 『男性』の人数を入力する場合...
        if (buttonId == R.id.numberOfMen)
        {
            resId = R.string.numberOfMen_Message;
            return (resId);
        }

        // 『女性』の人数を入力する場合...
        if (buttonId == R.id.numberOfWomen)
        {
            resId = R.string.numberOfWomen_Message;
            return (resId);
        }
        
        // 『スポンサー』の支払額を変更する場合...
        if (buttonId == R.id.GentlemenMoney)
        {
            resId = R.string.paymentGentlemen_Message;
            return (resId);
        }

        // 『男性』の支払額を変更する場合...
        if (buttonId == R.id.MenMoney)
        {
            resId = R.string.paymentMen_Message;
            return (resId);
        }

        // 『女性』の支払額を変更する場合...
        if (buttonId == R.id.WomenMoney)
        {
            resId = R.string.paymentWomen_Message;
            return (resId);
        }
        return (resId);
    }

    /**
     *   入力されたボタンに対応した初期データ値を応答する
     * @param buttonId
     * @return
     */
    private int decideInitialValue(int buttonId)
    {
        int value = 0;
        
        try
        {
            // 『チップ加算』で加算する場合...
            if (buttonId == R.id.chkAddCharge)
            {
                // とりあえず、直値で15を指定しておこう
                value = 15;
                return (value);
            }

            // 支払い金額 or 人数を入力する場合...
            if ((buttonId == R.id.totalPaymentData)||
                (buttonId == R.id.numberOfGentlemen)||
                (buttonId == R.id.numberOfMen)||
                (buttonId == R.id.numberOfWomen)||
                (buttonId == R.id.GentlemenMoney)||
                (buttonId == R.id.MenMoney)||
                (buttonId == R.id.WomenMoney))
            {
                // 現在設定されている値を取り出して初期値にする
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

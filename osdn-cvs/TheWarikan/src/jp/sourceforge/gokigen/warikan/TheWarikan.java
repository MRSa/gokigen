package jp.sourceforge.gokigen.warikan;

import jp.sourceforge.gokigen.warikan.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.CheckBox;

/**
 *  TheWarikanのメイン画面
 * @author MRSa
 *
 */
public class TheWarikan extends Activity
{
    MainScreenUpdater updater = null;    // 画面表示更新クラス
    MainScreenListener listener = null;  // イベントリスナクラス
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /**  画面情報表示クラスの生成 **/
        updater = new MainScreenUpdater((Activity) this);        
        
        /**  画面オブジェクトに対し、イベントを処理するクラスを設定する   **/
        /**  (Qtでいうところの、Signal-Slot 接続みたいなものかな...)      **/
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
     *  画面が裏に回ったときの処理
     */
    @Override
    public void onPause()
    {
        super.onPause();
    }
    
    /**
     *  画面が表に出てきたときの処理
     */
    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            // 画面表示内容の更新を行うよう、画面情報表示クラスに依頼する
            updater.updateScreen();
        }
        catch (Exception ex)
        {
            // なにもしない
        }
    }
    
    /**
     *  子画面から応答をもらったときの処理
     *  （実処理は 画面情報表示クラスの生成で行う。）
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            // 画面表示処理を画面情報表示クラスに依頼する
            updater.onActivityResult(requestCode, resultCode, data);
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
        }
    }
}

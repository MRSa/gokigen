package jp.osdn.gokigen.aira01b.preference;

import android.support.v7.preference.Preference;

import jp.osdn.gokigen.aira01b.IChangeScene;

class PreferenceScreenArbitrator implements Preference.OnPreferenceClickListener
{
    private final IChangeScene changeScene;
    private final IPreferenceIntentCaller intentCaller;

    /**
     *   コンストラクタ
     *
     */
    PreferenceScreenArbitrator(IChangeScene changeScene, IPreferenceIntentCaller intentCaller)
    {
        this.changeScene = changeScene;
        this.intentCaller = intentCaller;
    }

    /**
     *   クラスの準備
     *
     */
    void prepare()
    {
        // 何もしない
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        if (!preference.hasKey())
        {
            return (false);
        }

        boolean ret = false;
        String key = preference.getKey();
        if (key.contains(ICameraPropertyAccessor.PLAYBACK_CAMERA))
        {
            // カメラ内画像再生画面を表示する
            changeScene.changeSceneToPlaybackCamera();
            ret = true;
        }
        else if (key.contains(ICameraPropertyAccessor.PLAYBACK_PHONE))
        {
            // 本体の画像再生画面を表示する
            changeScene.changeSceneToPlaybackPhone();
            ret = true;
        }
        else if (key.contains(ICameraPropertyAccessor.SELECT_SAMPLE_IMAGE))
        {
            // 表示する作例を選択する画面を表示する
            intentCaller.selectSampleImageFromGallery();
            ret = true;
        }
        return (ret);
    }

}
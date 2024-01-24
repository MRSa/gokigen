package jp.osdn.gokigen.aira01b.preference;

import android.support.v7.preference.Preference;

import jp.osdn.gokigen.aira01b.IChangeScene;

public class PreferenceScreenArbitrator implements Preference.OnPreferenceClickListener
{
    private final IChangeScene changeScene;

    /**
     *   コンストラクタ
     *
     */
    public PreferenceScreenArbitrator(IChangeScene changeScene)
    {
        this.changeScene = changeScene;

    }

    /**
     *   クラスの準備
     *
     */
    public void prepare()
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
        return (ret);
    }
}

package jp.osdn.gokigen.aira01b;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.co.olympus.camerakit.OLYCamera;

/**
 *  ConnectingFragment
 *
 */
public class ConnectingFragment extends Fragment
{
    private TextView connectingTextView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_connecting_view, container, false);
        String versionText = getString(R.string.sdk_version) + " " + OLYCamera.getVersion();
        TextView sdkVersionTextView = (TextView)view.findViewById(R.id.sdkVersionTextView);
        connectingTextView = (TextView)view.findViewById(R.id.connectingStatusTextView);
        sdkVersionTextView.setText(versionText);
        setHasOptionsMenu(true);

        return (view);
    }

    /**
     *  メッセージを表示する
     *
     * @param message  表示するメッセージ
     */
    public void setInformationText(String message)
    {
        if (connectingTextView != null)
        {
            connectingTextView.setText(message);
        }
    }

    /**
     *   （タイトルを表示する場合には）表示する
     *
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        ActionBar bar = activity.getSupportActionBar();
        if (bar != null)
        {
            bar.setTitle(getString(R.string.app_name));
        }
    }
}

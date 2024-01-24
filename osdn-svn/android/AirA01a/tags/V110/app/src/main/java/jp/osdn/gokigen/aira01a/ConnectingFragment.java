package jp.osdn.gokigen.aira01a;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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
 *  (ほぼOLYMPUS imagecapturesampleのサンプルコードそのまま)
 *
 */
public class ConnectingFragment extends Fragment
{
    private final String TAG = this.toString();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_connecting_view, container, false);
        String versionText = getString(R.string.sdk_version) + " " + OLYCamera.getVersion();
        TextView sdkVersionTextView = (TextView)view.findViewById(R.id.sdkVersionTextView);
        sdkVersionTextView.setText(versionText);
        setHasOptionsMenu(true);

        return (view);
    }

    /**
     *
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.app_name));
    }

}

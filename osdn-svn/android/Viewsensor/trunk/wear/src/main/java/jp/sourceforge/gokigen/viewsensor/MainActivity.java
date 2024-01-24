package jp.sourceforge.gokigen.viewsensor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

/**
 *
 */
public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener

{
    private static final String TAG = "MainActivity";
    private MainListener mListener = null;
    private GoogleApiClient mGoogleApiClient = null;

    /**
     *
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mListener = new MainListener(this);

        // このアプリでは画面をOFFしないようにする
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 表示画面のレイアウトを行う
        setContentView(R.layout.activity_main);
        if (!hasGps())
        {
            // GPS機能が搭載されていない場合...
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "This hardware doesn't have GPS.");
            }
            // Fall back to functionality that does not use location or
            // warn the user that location function is not available.
        }
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnApplyWindowInsetsListener(mListener);
        stub.setOnLayoutInflatedListener(mListener);

        // Google Play Services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     *  画面が裏に回ったときの処理
     */
    @Override
    public void onPause()
    {
        Log.v(TAG, "onPause()");
        super.onPause();
        mListener.onPause();
        if (mGoogleApiClient.isConnected())
        {
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        mGoogleApiClient.disconnect();
    }

    /**
     *  画面が表に出てきたときの処理
     */
    @Override
    public void onResume()
    {
        super.onResume();
        mListener.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        /* ここで状態を保存 */
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        /* ここで保存した状態を読み出して設定 */
    }

    /**
     *   終了時の処理
     */
    @Override
    protected void onDestroy()
    {
        Log.v(TAG, "onDestroy()");
        mListener.onDestroy();
        mListener = null;
        super.onDestroy();
    }

    /**
     *   コールバック
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mListener.onActivityResult(requestCode, resultCode, data) == false)
        {
            // その他のIntent受信結果処理
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onConnected() " + bundle);
        }
        // Now you can use the Data Layer API
        //Wearable.DataApi.addListener(mGoogleApiClient, this);
/*
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_INTERVAL_MS);
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, this)
                .setResultCallback(new ResultCallback() {

                    @Override
                    public void onResult(Status status) {
                        if (status.getStatus().isSuccess()) {
                            if (Log.isLoggable(TAG, Log.DEBUG)) {
                                Log.d(TAG, "Successfully requested location updates");
                            }
                        } else {
                            Log.e(TAG,
                                    "Failed in requesting location updates, "
                                            + "status code: "
                                            + status.getStatusCode()
                                            + ", message: "
                                            + status.getStatusMessage());
                        }
                    }
                });
*/
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onConnectionSuspended() " + cause);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onConnectionFailed() " + connectionResult);
        }
    }

    /**
     *
     *
     * @return true GPS搭載, false GPS非搭載
     */
    private boolean hasGps()
    {
        return (getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS));
    }
}

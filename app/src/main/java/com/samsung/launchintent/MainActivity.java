package com.samsung.launchintent;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "LaunchIntent";

    public static final String PACKAGE_SI = "com.sec.android.app.sbrowser";
    public static final String PACKAGE_SI_BETA = "com.sec.android.app.sbrowser.beta";

    public static final String SI_CUSTOM_TABS_CONNECTION =
            "android.support.customtabs.action.CustomTabsService";

    public static final String PACKAGE_SI_GEARVR = "com.sec.android.app.svrbrowser";
    public static final String ACTIVITY_SI_GEARVR = PACKAGE_SI_GEARVR+".UnityPlayerNativeActivity";
    // can get rid of 'Native'?

    public static final String PACKAGE_VRSHELL = "com.oculus.vrshell";
    public static final String ACTIVITY_VRSHELL = PACKAGE_VRSHELL+".MainActivity";

    public static final String PACKAGE_OCULUS_BROWSER = "com.oculus.browser";

    public static final String DEFAULT_URL = "https://webvr.info/samples/";


    public static CustomTabsServiceConnection mServiceConnection;

    private ServiceConnectionCallback mConnectionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Log information on the received intent.
        Intent i = getIntent();
        Log.d(TAG, "getAction() " + i.getAction());
        Log.d(TAG, "getType() " + i.getType());
        Log.d(TAG, "getDataString() " + i.getDataString());
        Log.d(TAG, "getStringExtra(EXTRA_TEXT) " + i.getStringExtra(Intent.EXTRA_TEXT));

        final EditText urlText = (EditText) findViewById(R.id.urlText);

        urlText.setText(DEFAULT_URL);

        Button siBtn = (Button) findViewById(R.id.siLaunch);
        Button sicustomBtn = (Button) findViewById(R.id.sicustomLaunch);
        Button oculusBtn = (Button) findViewById(R.id.oculusLaunch);

        siBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String url = urlText.getText().toString();
                Intent intent = getSIIntent(url);
                launchActivity(intent);
            }
        });

        sicustomBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String url = urlText.getText().toString();
                Intent intent = getSICustomTabIntent(url);
                launchActivity(intent);
            }
        });

        oculusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String url = urlText.getText().toString();
                Intent intent = getOculusIntent(url);
                launchActivity(intent);
            }
        });

        // setup Samsung custom tab behavior
        mServiceConnection = new ServiceConnection(null);

        Intent serviceIntent = new Intent(SI_CUSTOM_TABS_CONNECTION);
        serviceIntent.setPackage(PACKAGE_SI);
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE |
                Context.BIND_WAIVE_PRIORITY);



        if (!isPackageInstalled(PACKAGE_SI_GEARVR))
            siBtn.setEnabled(false);
        if (!isPackageInstalled(PACKAGE_VRSHELL) || !isPackageInstalled(PACKAGE_OCULUS_BROWSER))
            oculusBtn.setEnabled(false);

    }

    public Intent getSIIntent(String uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setClassName(PACKAGE_SI_GEARVR, ACTIVITY_SI_GEARVR);

        intent.putExtra("isValid", "valid");
        intent.putExtra("currentURL", uri);

        return intent;
    }

    public Intent getOculusIntent(String uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setClassName(PACKAGE_VRSHELL, ACTIVITY_VRSHELL);
        intent.setData(Uri.parse("apk://"+PACKAGE_OCULUS_BROWSER));

        intent.putExtra("uri", uri);

        return intent;
    }



    public Intent getSICustomTabIntent(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        // Extra used to match the session.
        final String EXTRA_SESSION = "android.support.customtabs.extra.SESSION";
        Bundle extras = new Bundle();
        extras.putBinder(EXTRA_SESSION, null);
        intent.putExtras(extras);
        intent.setData(Uri.parse(uri));
        intent.setPackage(PACKAGE_SI);

        final String CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
        int color = Color.parseColor("#6141e3");
        intent.putExtra(CUSTOM_TABS_TOOLBAR_COLOR, color);

        return intent;
    }

    public boolean launchActivity(Intent intent) {
        Log.d(TAG, "Launching activity: " + intent.getPackage());
        startActivity(intent);
        return true;
    }

    public boolean isPackageInstalled(String pkg) {
        PackageManager pm=getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(pkg, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

}

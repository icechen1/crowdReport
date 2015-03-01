package com.icechen1.crowdreport;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // New login using the provider and update the token cache.
        CrowdReportApplication.getInstance().mClient.setContext(this);
        CrowdReportApplication.getInstance().mClient.login(MobileServiceAuthenticationProvider.Google,
                new UserAuthenticationCallback() {
                    @Override
                    public void onCompleted(MobileServiceUser user,
                                            Exception exception, ServiceFilterResponse response) {

                        synchronized(CrowdReportApplication.getInstance().mAuthenticationLock)
                        {
                            if (exception == null) {
                                CrowdReportApplication.getInstance().cacheUserToken(CrowdReportApplication.getInstance().mClient.getCurrentUser());
                                CrowdReportApplication.getInstance().createTable();
                            } else {
                                CrowdReportApplication.getInstance().createAndShowDialog(exception.getMessage(), "Login Error");
                                finish();
                            }
                            CrowdReportApplication.getInstance().bAuthenticating = false;
                            CrowdReportApplication.getInstance().mAuthenticationLock.notifyAll();
                            finish();
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

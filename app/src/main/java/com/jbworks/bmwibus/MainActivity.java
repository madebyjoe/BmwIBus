package com.jbworks.bmwibus;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    /*
    Names:
    BMW IBUS
    Android to BMW IBUS
    AndroidIBus
    AndroidUsbIBus
    BMW Droid
    bmwibus
    iBusRadio
    iBusDroid

    this uses a usb to serial interface to connect to the bmw ibus (only the one I use)

    it needs to essentially be a replacement for the head unit. because the head unit will not be
    accessable once installed.

    It also needs to be able to access these commads from outside the app so it can integrate with
    tasker or other apps like gesture control
     */

    /*
    TASKS

    -USB to Serial

    -IBUS Wrapper

    -Launch on start & Notification status bar

    -Internal Controls

    -External Controls (use shortcuts)

    -Good Frontend UI

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

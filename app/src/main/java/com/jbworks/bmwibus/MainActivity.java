package com.jbworks.bmwibus;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jbworks.bmwibus.ibus.IBusMessageService;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


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

    -Launch on start
    -Notification status bar

    -Internal Controls

    -External Controls (use shortcuts)

    -Good Frontend UI

     */

    /*
    IBUS Struct
    Protocol of the I-bus

    In BMW, the Protocol that the individual control units in the networks communicate according to the following scheme is structured vehicles:

    * Source ID
    Identification of the participant who wants to send a message to a different BUS participants

    * Length
    Length of the entire message (without source ID and length indication itself)

    * Target ID
    Identification of the participant to send the message

    * Data
    Payload the message

    * XOR CRC - checksum
    The checksum is used to verify the message. The recipient of the message calculates the checksum and compares it with the one contained in the message.

    Processes bytes received from serial and parse packets
    ---------------------------------------------
    | Source ID | Length | Dest Id | Data | XOR |
    ---------------------------------------------
                         | ------ Length -------|

    Example
    < steeringwheel 04 radio forward checksum>
    < 50 04 68 3B 01 XOR > (xor = 06)
    < steeringwheel 04 radio previous checksum>
    < 50 04 68 3B 08 XOR > (xor = 0F)

    XOR all the previous bytes for checksum (^)

    uses baud rate of 9600

     */

    /*
    BUTTONS KeyCodes

    Steering Wheel
    Vol +
    Vol -
    Forward
    Back
    R/T
    Voice
    (cruise control - ignore)

    Radio
    btn 1
    btn 2
    btn 3
    btn 4
    btn 5
    btn 6
    btn 7
    btn 8

    Power
    rotary?

    Scan
    Mode

    AM
    FM

    clock
    music settings
    plus?

    left <
    m
    right >
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, IBusMessageService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 01, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle("BMW IBus");
        builder.setContentText("The Service is Running");
        builder.setSubText("Click to Restart");
        builder.setNumber(101);
        builder.setContentIntent(pendingIntent);
        builder.setTicker("Fancy Notification");
        builder.setSmallIcon(android.R.color.transparent);
        builder.setAutoCancel(false);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setOngoing(true);
        Notification notification = builder.build();
        NotificationManager notificationManger =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManger.notify(01, notification);
    }

    @Override
    public void onResume() {
        Log.d("Service", "Service Started from MainActivity");
        Intent service = new Intent(this, IBusMessageService.class);
        startService(service);
        super.onResume();
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

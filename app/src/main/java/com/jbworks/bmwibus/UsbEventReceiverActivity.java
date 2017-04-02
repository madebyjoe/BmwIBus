package com.jbworks.bmwibus;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * Created by joe-work on 4/2/17.
 */
public class UsbEventReceiverActivity extends Activity
{
    public static final String ACTION_USB_DEVICE_ATTACHED = "com.example.ACTION_USB_DEVICE_ATTACHED";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        if (intent != null)
        {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED))
            {
                Parcelable usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                // Create a new intent and put the usb device in as an extra
                Intent broadcastIntent = new Intent(ACTION_USB_DEVICE_ATTACHED);
                broadcastIntent.putExtra(UsbManager.EXTRA_DEVICE, usbDevice);

                // Broadcast this event so we can receive it
                sendBroadcast(broadcastIntent);
            }
        }

        // Close the activity
        finish();
    }
}

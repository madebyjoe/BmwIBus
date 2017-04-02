package com.jbworks.bmwibus.ibus;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.jbworks.bmwibus.MediaController;
import com.jbworks.bmwibus.usbserial.driver.UsbSerialDriver;
import com.jbworks.bmwibus.usbserial.driver.UsbSerialPort;
import com.jbworks.bmwibus.usbserial.driver.UsbSerialProber;
import com.jbworks.bmwibus.usbserial.util.HexDump;
import com.jbworks.bmwibus.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by joe-work on 5/11/15.
 */
public class IBusMessageService extends Service {

    private static final String TAG = IBusMessageService.class.getSimpleName();

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    public class LocalBinder extends Binder {
        IBusMessageService getService() {
            return IBusMessageService.this;
        }
    }

    private boolean mServiceRunning = false;

    private final IBinder mBinder = new LocalBinder();
    private UsbSerialPort sPort;

    private MediaController mediaController;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    updateReceivedData(data);
                }
            };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            Log.i("usb", "permission granted for device " + device);
                            openDevice(device);
                            onDeviceStateChange();
                        }
                    } else {
                        Log.i("usb", "permission denied for device " + device);
                    }
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.d(TAG, "Created Service");
        Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_SHORT).show();
        mServiceRunning = false;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start Cmd Service");
        if(!mServiceRunning) {
            mServiceRunning = true;
            Log.d(TAG, "Starting Serial Setup");
            // Find all available drivers from attached devices.
            UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
            if (!availableDrivers.isEmpty()) {

                UsbSerialDriver firstDriver = availableDrivers.get(0);
                sPort = firstDriver.getPorts().get(0);
                UsbDevice usbDevice = firstDriver.getDevice();
                if (usbManager.hasPermission(usbDevice)) {
                    openDevice(usbDevice);
                    onDeviceStateChange();
                } else {
                    Log.d(TAG, "Asking for permission");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(usbDevice, pendingIntent);
                }

            }

            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(mUsbReceiver, filter);

            mediaController = new MediaController(this);
            Toast.makeText(getApplicationContext(), "Service Working", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Service Started Again", Toast.LENGTH_SHORT).show();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void openDevice(final UsbDevice usbDevice) {
        Log.d(TAG, "Opening Device");
        final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
        if (connection == null) {
            Log.e(TAG, "Opening device failed");
            return;
        }

        try {
            sPort.open(connection);
            sPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
            try {
                sPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            sPort = null;
            return;
        }
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    byte[] buffer = new byte[6];
    int bufferIndex = 0;
    boolean recognizedCmd = false;
    byte[] nextDown = {0x50, 0x04, 0x68, 0x3B, 0x01, 0x06};
    byte[] nextUp = {0x50, 0x04, 0x68, 0x3B, 0x21, 0x26};
    byte[] previousDown = {0x50, 0x04, 0x68, 0x3B, 0x08, 0x0F};
    byte[] previousUp = {0x50, 0x04, 0x68, 0x3B, 0x28, 0x2F};

    private void updateReceivedData(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            String hex = String.format("%02X ", data[i]);
//            Log.i("CMD", "Byte IN: " + hex);
            if (data[i] == 0x50) {
                Log.i("CMD", "Start of index");
                recognizedCmd = true;
                bufferIndex = 0;
            }
            if (recognizedCmd) {
                Log.i("CMD", "Add to buffer: " + hex);
                buffer[bufferIndex] = data[i];
                bufferIndex = bufferIndex + 1;
            }
            if (buffer[0] != 0x00)
                Log.i("CMD", "Print Buffer: " + HexDump.dumpHexString(buffer));

            if (bufferIndex == buffer.length) {
                if (Arrays.equals(buffer, nextDown)) {
                    Log.e("CMD", "NextDown is triggered");
                    mediaController.nextSong();
                    clearArray();
                } else if (Arrays.equals(buffer, nextUp)) {
                    Log.e("CMD", "NextUp is triggered");
                    clearArray();
                } else if (Arrays.equals(buffer, previousDown)) {
                    Log.e("CMD", "Previous Down is triggered");
                    mediaController.previousSong();
                    clearArray();
                } else if (Arrays.equals(buffer, previousUp)) {
                    Log.e("CMD", "Previous Up is triggered");
                    clearArray();
                } else {
                    Log.d("CMD", "Invalid => clear");
                    clearArray();
                }
            }
        }

    }

    private void clearArray() {
        bufferIndex = 0;
        recognizedCmd = false;
        for (int j = 0; j < buffer.length; j++) {
            buffer[j] = 0;
        }
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        stopIoManager();
        unregisterReceiver(mUsbReceiver);
        Toast.makeText(getApplicationContext(), "Service Destroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }


}

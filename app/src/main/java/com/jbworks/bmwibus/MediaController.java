package com.jbworks.bmwibus;

import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;

/**
 * Created by joe-work on 4/1/17.
 */
public class MediaController {

    private Context mContext;

    public MediaController(final Context context) {
        mContext = context;
    }

    public void playPause() {
        musicCommand(KeyEvent.KEYCODE_MEDIA_PLAY);
    }

    public void nextSong() {
        musicCommand(KeyEvent.KEYCODE_MEDIA_NEXT);
    }

    public void previousSong() {
        musicCommand(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
    }

    private void musicCommand(final int keyCode) {
        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mAudioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        mAudioManager.dispatchMediaKeyEvent(upEvent);
    }
}

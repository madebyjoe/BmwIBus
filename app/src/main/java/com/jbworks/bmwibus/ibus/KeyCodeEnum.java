package com.jbworks.bmwibus.ibus;

/**
 * Created by joe-work on 5/11/15.
 */
public enum KeyCodeEnum {
    STEERING_WHEEL_VOL_UP(new byte[]{(byte) 0x32, (byte) 0x11}),
    STEERING_WHEEL_VOL_DOWN(new byte[]{(byte) 0x32, (byte) 0x10}),
    STEERING_WHEEL_TRACK_NEXT(new byte[]{(byte) 0x3B, (byte) 0x01}),
    STEERING_WHEEL_TRACK_PREVIOUS(new byte[]{(byte) 0x3B, (byte) 0x08}),
    STEERING_WHEEL_R_T(new byte[]{(byte) 0x3B, (byte) 0x40}),
    STEERING_WHEEL_SELECT(new byte[]{(byte) 0x3B, (byte) 0x80});

    private byte[] code;

    private KeyCodeEnum(final byte[] values) {
        code = values;
    }

    public byte[] getCode() {
        return code;
    }


}

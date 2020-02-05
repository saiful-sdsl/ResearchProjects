package com.saiful.falldetection;

import android.provider.BaseColumns;

/**
 * Created by Dell PC on 31-01-2017.
 */

public class ContactContract {
    public static final String TABLE_NAME = "ContactList";
    public static final String TABLE_FALL_EVENT = "falleventdata";
    public static final String COLUMN_CONTACT  = "contact";
    public static final String COLUMN_ACCEL  = "accel";
    public static final String COLUMN_GYRO  = "gyro";
    public static final String COLUMN_MAGNETOMETER  = "magnetometer";
    public static final String COLUMN_FALL_STATUS  = "fall_status";
    public static final String _ID = "id";
    public static final class ContactContractList implements BaseColumns{



    }
}

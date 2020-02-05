package com.saiful.falldetection;

public interface IOnSensorValueChange {
    void onMagnetometerValueChange();
    void onAccelerometerChange();
    void onGyroChange();
    void onFallDetected();
    void onFakeFall();
}

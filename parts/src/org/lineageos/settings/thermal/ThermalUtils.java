/*
 * Copyright (C) 2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.thermal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import androidx.preference.PreferenceManager;

import org.lineageos.settings.utils.FileUtils;

public final class ThermalUtils {

    private static final String THERMAL_CONTROL = "thermal_control";

    protected static final int STATE_DEFAULT = 0;
    protected static final int STATE_PERFORMANCE = 1;
    protected static final int STATE_CAMERA = 2;
    protected static final int STATE_DIALER = 3;

    private static final String THERMAL_STATE_DEFAULT = "10";
    private static final String THERMAL_STATE_PERFORMANCE = "20";
    private static final String THERMAL_STATE_CAMERA = "12";
    private static final String THERMAL_STATE_DIALER = "8";

    private static final String THERMAL_PERFORMANCE = "thermal.performance=";
    private static final String THERMAL_CAMERA = "thermal.camera=";
    private static final String THERMAL_DIALER = "thermal.dialer=";

    private static final String THERMAL_SCONFIG = "/sys/class/thermal/thermal_message/sconfig";

    private boolean mTouchModeChanged;

    private Display mDisplay;
    private SharedPreferences mSharedPrefs;

    protected ThermalUtils(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        WindowManager mWindowManager = context.getSystemService(WindowManager.class);
        mDisplay = mWindowManager.getDefaultDisplay();
    }

    public static void startService(Context context) {
        context.startServiceAsUser(new Intent(context, ThermalService.class),
                UserHandle.CURRENT);
    }

    private void writeValue(String profiles) {
        mSharedPrefs.edit().putString(THERMAL_CONTROL, profiles).apply();
    }

    private String getValue() {
        String value = mSharedPrefs.getString(THERMAL_CONTROL, null);

        if (value == null || value.isEmpty()) {
            value = THERMAL_PERFORMANCE + ":" + THERMAL_CAMERA + ":" + THERMAL_DIALER;
            writeValue(value);
        }
        return value;
    }

    protected void writePackage(String packageName, int mode) {
        String value = getValue();
        value = value.replace(packageName + ",", "");
        String[] modes = value.split(":");
        String finalString;

        switch (mode) {
            case STATE_PERFORMANCE:
                modes[0] = modes[0] + packageName + ",";
                break;
            case STATE_CAMERA:
                modes[1] = modes[1] + packageName + ",";
                break;
            case STATE_DIALER:
                modes[2] = modes[2] + packageName + ",";
                break;
        }

        finalString = modes[0] + ":" + modes[1] + ":" + modes[2];

        writeValue(finalString);
    }

    protected int getStateForPackage(String packageName) {
        String value = getValue();
        String[] modes = value.split(":");
        int state = STATE_DEFAULT;
        if (modes[0].contains(packageName + ",")) {
            state = STATE_PERFORMANCE;
        } else if (modes[1].contains(packageName + ",")) {
            state = STATE_CAMERA;
        } else if (modes[2].contains(packageName + ",")) {
            state = STATE_DIALER;
        }

        return state;
    }

    protected void setDefaultThermalProfile() {
        FileUtils.writeLine(THERMAL_SCONFIG, THERMAL_STATE_DEFAULT);
    }

    protected void setThermalProfile(String packageName) {
        String value = getValue();
        String modes[];
        String state = THERMAL_STATE_DEFAULT;

        if (value != null) {
            modes = value.split(":");

            if (modes[0].contains(packageName + ",")) {
                state = THERMAL_STATE_PERFORMANCE;
            } else if (modes[1].contains(packageName + ",")) {
                state = THERMAL_STATE_CAMERA;
            } else if (modes[2].contains(packageName + ",")) {
                state = THERMAL_STATE_DIALER;
            }
        }
        FileUtils.writeLine(THERMAL_SCONFIG, state);

        if (state == THERMAL_STATE_PERFORMANCE) {
            updateTouchModes(packageName);
        } else if (mTouchModeChanged) {
            resetTouchModes();
        }
    }

    protected void setThermalProfileForce(int mode) {
        String state = THERMAL_STATE_DEFAULT;

        switch (mode) {
            case STATE_PERFORMANCE:
                state = THERMAL_STATE_PERFORMANCE;
                break;
            case STATE_CAMERA:
                state = THERMAL_STATE_CAMERA;
                break;
            case STATE_DIALER:
                state = THERMAL_STATE_DIALER;
                break;
        }

        FileUtils.writeLine(THERMAL_SCONFIG, state);
    }

    private void updateTouchModes(String packageName) {
        String values = mSharedPrefs.getString(packageName, null);
        resetTouchModes();

        if (values == null || values.isEmpty()) {
            return;
        }

        String[] value = values.split(",");
        String gameMode = value[Constants.TOUCH_GAME_MODE];
        String touchResponse = value[Constants.TOUCH_RESPONSE];
        String touchSensitivity = value[Constants.TOUCH_SENSITIVITY];
        String touchResistant = value[Constants.TOUCH_RESISTANT];

        FileUtils.writeLine(Constants.FILE_TOUCH_TOLERANCE, touchSensitivity);
        FileUtils.writeLine(Constants.FILE_TOUCH_UP_THRESHOLD, touchResponse);
        FileUtils.writeLine(Constants.FILE_TOUCH_EDGE_FILTER, touchResistant);
        SystemProperties.set(Constants.PROP_TOUCH_GAME_MODE, gameMode);

        mTouchModeChanged = true;
    }

    protected void resetTouchModes() {
        if (!mTouchModeChanged) {
            return;
        }

        FileUtils.writeLine(Constants.FILE_TOUCH_TOLERANCE, Constants.DEFAULT_TOUCH_TOLERANCE);
        FileUtils.writeLine(Constants.FILE_TOUCH_UP_THRESHOLD, Constants.DEFAULT_TOUCH_UP_THRESHOLD);
        FileUtils.writeLine(Constants.FILE_TOUCH_EDGE_FILTER, Constants.DEFAULT_TOUCH_EDGE_FILTER);
        SystemProperties.set(Constants.PROP_TOUCH_GAME_MODE, "0");

        mTouchModeChanged = false;
    }
}

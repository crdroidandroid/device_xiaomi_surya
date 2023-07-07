/**
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

public class Constants {

    public static final int TOUCH_GAME_MODE = 0;
    public static final int TOUCH_RESPONSE = 1;
    public static final int TOUCH_SENSITIVITY = 2;
    public static final int TOUCH_RESISTANT = 3;

    public static final String PROP_TOUCH_GAME_MODE = "sys.perf_profile";
    public static final String FILE_TOUCH_UP_THRESHOLD = "/proc/nvt_sensitivity_switch";
    public static final String FILE_TOUCH_TOLERANCE = "/proc/nvt_pf_switch";
    public static final String FILE_TOUCH_EDGE_FILTER = "/proc/nvt_er_range_switch";

    public static final String DEFAULT_TOUCH_UP_THRESHOLD = "3";
    public static final String DEFAULT_TOUCH_TOLERANCE = "0";
    public static final String DEFAULT_TOUCH_EDGE_FILTER = "2";

    public static final String PREF_TOUCH_GAME_MODE = "touch_game_mode";
    public static final String PREF_TOUCH_RESISTANT = "touch_resistant";
    public static final String PREF_TOUCH_RESPONSE = "touch_response";
    public static final String PREF_TOUCH_SENSITIVITY = "touch_sensitivity";
}


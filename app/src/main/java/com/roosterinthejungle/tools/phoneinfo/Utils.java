/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.roosterinthejungle.tools.phoneinfo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Common methods.
 *
 * Created by hhhung on September 4, 2015.
 */
public class Utils {
    /**
     * Convert dp to pixel
     *
     * @param dp
     * @param context
     * @return pixels number
     */
    public static int dp2Pixel(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Read file and return content of the file as string
     *
     * @param filename
     * @return file content
     */
    public static String readFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            StringBuilder sb = new StringBuilder();


            while ((line = br.readLine()) != null) {

                sb.append(line + "\n");

            }

            if (br != null) {
                br.close();
            }

            return sb.toString().trim();
        } catch (IOException e) {
        }

        return "";
    }

    /**
     * Format bytes to human readable
     *
     * @param bytes
     * @param decimals
     * @return human-readable string
     */
    public static String formatByte(long bytes, int decimals) {
        DecimalFormat df;
        if (decimals == 0) {
            df = new DecimalFormat("0");
        } else {
            StringBuilder sb = new StringBuilder(decimals);
            sb.append("0.");

            for (int i = 0; i < decimals; i++) {
                sb.append("0");
            }

            df = new DecimalFormat(sb.toString());
        }

        if (bytes >= 1073741824) {
            return df.format((float) bytes / 1073741824) + " GB";
        } else if (bytes >= 1048576) {
            return df.format((float) bytes / 1048576) + " MB";
        } else if (bytes >= 1024) {
            return df.format((float) bytes / 1024) + " KB";
        } else {
            return "" + bytes;
        }
    }

    /**
     * Convert ip int to string
     *
     * @param ip
     * @return string represent ip address
     */
    public static String toIpv4(int ip) {
        return String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
    }

    /**
     * Start activity
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    /**
     * Check if app has specific permission granted
     *
     * @param context
     * @param name
     * @return
     */
    public static boolean checkPermissionGranted(Context context, String name) {

        int res = context.checkCallingOrSelfPermission(name);
        return res == PackageManager.PERMISSION_GRANTED;
    }
}

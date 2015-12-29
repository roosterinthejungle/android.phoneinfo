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

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.roosterinthejungle.tools.phoneinfo.pages.BasePage;
import com.roosterinthejungle.tools.phoneinfo.pages.GeneralPage;
import com.roosterinthejungle.tools.phoneinfo.pages.HardwarePage;
import com.roosterinthejungle.tools.phoneinfo.pages.SoftwarePage;
import com.roosterinthejungle.tools.phoneinfo.pages.StatusPage;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Main activity. Use appcompat.
 *
 * Created by hhhung on September 4, 2015.
 */
public class MainActivity extends AppCompatActivity {
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private GlRenderer mGlRenderer = new GlRenderer();

    public GlRenderer getGlRenderer() {
        return mGlRenderer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check permission first
        boolean readPhoneStatePermissionGranted = Utils.checkPermissionGranted(getBaseContext(), "android.permission.READ_PHONE_STATE");
        boolean cameraPermissionGranted = Utils.checkPermissionGranted(getBaseContext(), "android.permission.CAMERA");
        if (!readPhoneStatePermissionGranted || !cameraPermissionGranted) {
            Toast.makeText(getApplicationContext(), "Please grant all permission required (Phone & Camera)", Toast.LENGTH_LONG).show();
            finish();

            return;
        }

        setContentView(R.layout.activity_main);

        // init gl surface
        GLSurfaceView sv = (GLSurfaceView) findViewById(R.id.glSurfaceView);
        sv.setRenderer(mGlRenderer);

        // init tabs
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });

        ActionBar actionBar = getSupportActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        for (int i = 0; i < 4; i++) {
            actionBar.addTab(actionBar.newTab().setText(mPagerAdapter.getPageTitle(i)).setTabListener(tabListener));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((GLSurfaceView) findViewById(R.id.glSurfaceView)).onResume();
    }

    public void onPause() {
        super.onPause();

        ((GLSurfaceView) findViewById(R.id.glSurfaceView)).onPause();
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getPageId(int position) {
            // TODO for debug
            //position = 1;
            switch (position) {
                case 0:
                    return R.string.tab_id_general;
                case 1:
                    return R.string.tab_id_status;
                case 2:
                    return R.string.tab_id_software;
                case 3:
                    return R.string.tab_id_hardware;
            }

            return R.string.tab_id_general;
        }

        @Override
        public Fragment getItem(int position) {
            int tabId = getPageId(position);

            BasePage page = null;

            switch (tabId) {
                case R.string.tab_id_general:
                    page = (BasePage) Fragment.instantiate(getApplicationContext(), GeneralPage.class.getName());
                    break;
                case R.string.tab_id_status:
                    page = (BasePage) Fragment.instantiate(getApplicationContext(), StatusPage.class.getName());
                    break;
                case R.string.tab_id_software:
                    page = (BasePage) Fragment.instantiate(getApplicationContext(), SoftwarePage.class.getName());
                    break;
                case R.string.tab_id_hardware:
                    page = (BasePage) Fragment.instantiate(getApplicationContext(), HardwarePage.class.getName());
                    break;
            }

            return page;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int tabId = getPageId(position);

            switch (tabId) {
                case R.string.tab_id_general:
                    return getString(R.string.tab_title_general);
                case R.string.tab_id_status:
                    return getString(R.string.tab_title_status);
                case R.string.tab_id_software:
                    return getString(R.string.tab_title_software);
                case R.string.tab_id_hardware:
                    return getString(R.string.tab_title_hardware);
            }

            return null;
        }
    }

    public class GlRenderer implements GLSurfaceView.Renderer {
        private String mName;
        private String mVendor;
        private String mVersion;

        public String getName() {
            return mName;
        }

        public String getVendor() {
            return mVendor;
        }

        public String getVersion() {
            return mVersion;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mName = gl.glGetString(GL10.GL_RENDERER);
            mVendor = gl.glGetString(GL10.GL_VENDOR);
            mVersion = gl.glGetString(GL10.GL_VERSION);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }
    }
}

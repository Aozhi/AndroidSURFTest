/*
 * Copyright (C) 2011 The Android Open Source Project
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
package com.example.benny.androidsurftest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;


    double lastFingerDis=0;
    //记录两指同时放在屏幕上时，中心点的横坐标值
    float centerPointX;
    //记录两指同时放在屏幕上时，中心点的纵坐标值
    float centerPointY;
    int currentStatus;

    int STATUS_INIT = 1;
    int STATUS_ZOOM_OUT = 2;
    int STATUS_ZOOM_IN = 3;
    float scaledRatio=0.1f;



    public MyGLSurfaceView(Context context) {
        super(context);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

   // private final float TRACKBALL_SCALE_FACTOR = 36.0f;

    private final float TOUCH_SCALE_FACTOR = 180.0f/320;
    private float mPreviousX;
    private float mPreviousY;
/*
    @Override public boolean onTrackballEvent(MotionEvent event) {
        mRenderer.setAngleX  (mRenderer.getAngleX() +event.getX() * TRACKBALL_SCALE_FACTOR);
        mRenderer.setAngleY(mRenderer.getAngleY() + event.getY() * TRACKBALL_SCALE_FACTOR);
        requestRender();
        return true;
    }
*/
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, we are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();


        int  pointerCount = e.getPointerCount(); // 获得触控点个数

            switch (e.getAction()) {

                case MotionEvent.ACTION_POINTER_DOWN:



                    if (e.getPointerCount() == 2) {
                        // 当有两个手指按在屏幕上时，计算两指之间的距离
                        lastFingerDis = distanceBetweenFingers(e);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:

                    if(pointerCount==1)
                    {


                        float dx = x - mPreviousX;
                        float dy = y - mPreviousY;
                        mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
                        mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
         /*
                        float dx = x - mPreviousX;
                        float dy = y - mPreviousY;

                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1;
                    }

*/


                  //  mRenderer.setAngleY(
                       //     mRenderer.getAngleY() +
                                //    ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320


                    requestRender();
                    break;




                    }else if(e.getPointerCount() == 2) {


                        // 有两个手指按在屏幕上移动时，为缩放状态

                        double fingerDis = distanceBetweenFingers(e);
                        if (fingerDis > lastFingerDis) {
                            currentStatus = STATUS_ZOOM_OUT;
                        } else {
                            currentStatus = STATUS_ZOOM_IN;
                        }
                        if (currentStatus == STATUS_ZOOM_OUT )
                            //(float) (fingerDis / lastFingerDis);
                          scaledRatio +=0.01;



                        if(currentStatus== STATUS_ZOOM_IN)
                            scaledRatio -=0.01;

                        mRenderer.setScale(scaledRatio);
                            requestRender();

                            lastFingerDis = fingerDis;

                        break;
                    }else if(e.getPointerCount() == 3){//如果是三个触点移动的话 ，以此类推

                        ;;//后续使用
                    }


                case MotionEvent.ACTION_POINTER_UP:
                    if (pointerCount == 2) {
                        lastFingerDis=0;

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (pointerCount == 2) {
                        lastFingerDis=0;

                    }
                    break;

            }




        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    // 计算两个手指之间的距离。

    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }



}

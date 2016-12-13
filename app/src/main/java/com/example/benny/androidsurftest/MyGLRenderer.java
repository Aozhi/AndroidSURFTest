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

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Point mPoint;
    public float mAngleX;
    public float mAngleY;
    private  float mScale=0.1f;

    //private float mAngleX;
    //private float mAngleY;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mPoint = new Point();
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        // Draw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();   // reset the matrix to its default state

        // When using GL_MODELVIEW, you must set the view point
        GLU.gluLookAt(gl, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

       // gl.glEnable(GL10.GL_LIGHTING);

        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        //long time = SystemClock.uptimeMillis() % 4000L;
        //float angle = 0.090f * ((int) time);

        gl.glScalef(mScale,mScale,mScale);
        gl.glRotatef(mAngleY, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(mAngleX, 0.0f, 1.0f, 0.0f);


       // gl.glRotatef(mAngle, 0.0f, 0.0f, 1.0f);
        //gl.glRotatef(mAngleX, 0.0f, 1.0f, 0.0f);
        //gl.glRotatef(mAngleY, 1.0f, 0.0f, 0.0f);
        // Draw triangle
        mPoint.draw(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Adjust the viewport based on geometry changes
        // such as screen rotations
        gl.glViewport(0, 0, width, height);

        // make adjustments for screen ratio
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
        gl.glLoadIdentity();                        // reset the matrix to its default state
        gl.glFrustumf(-ratio, ratio, -1, 1, 1.0f, 7);  // apply the projection matrix


        gl.glEnable(GL10.GL_DEPTH_TEST);        //启用深度测试
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */

    public float getAngleX() {
        return mAngleX;
    }
    public float getAngleY() {
        return mAngleY;
    }
     public  float getScale(){return  mScale;}
    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngleX(float anglex) {
        mAngleX = anglex;
    }
    public void setAngleY(float angley) {
        mAngleY = angley;
    }

/*
    public float getAngleX(){return mAngleX;}
    public float getAngleY(){return mAngleY;}
    public void setAngleX(float angle){mAngleX=angle;}
    public void setAngleY(float angle){mAngleY=angle;}
*/
    public  void setScale(float scale) { mScale = scale;}
}
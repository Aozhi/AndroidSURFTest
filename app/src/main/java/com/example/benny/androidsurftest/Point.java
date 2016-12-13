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

import org.opencv.core.MatOfPoint3f;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Point {

    private final FloatBuffer vertexBuffer;
    int MAX_PT=100000;//最大能读取的点云数
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    MatOfPoint3f pt3;

    float []allpoints=new float[MAX_PT*3];
    int index=0;

    float color[] = { 1.0f, 0.0f, 0.0f, 0.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Point() {


        pt3=new MatOfPoint3f(MainActivity.rec3d);
        for (int i=0;i<pt3.toList().size();i++)
        {

                allpoints[index]=(float) pt3.toList().get(i).x;
                allpoints[index+1]=(float)pt3.toList().get(i).y;
                allpoints[index+2]=(float)pt3.toList().get(i).z;





                float a=  allpoints[index]*  allpoints[index];
                float b=  allpoints[index+1]*  allpoints[index+1];

                float c=  allpoints[index+2]*  allpoints[index+2];
                double abc=Math.sqrt(a+b+c);
                allpoints[index]=(float)(pt3.toList().get(i).x/abc);
                allpoints[index+1]=(float)(pt3.toList().get(i).y/abc);
                allpoints[index+2]=(float)(pt3.toList().get(i).z/abc);


                index=index+3;

        }

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                allpoints.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(allpoints);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
    }


    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param gl - The OpenGL ES context in which to draw this shape.
     */
    public void draw(GL10 gl) {
        // Since this shape uses vertex arrays, enable them
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // draw the shape
        gl.glColor4f(       // set color:
                color[0], color[1],
                color[2], color[3]);
        gl.glVertexPointer( // point to vertex data:
                COORDS_PER_VERTEX,
                GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawArrays(    // draw shape:
                GL10.GL_POINTS, 0,
                allpoints.length / COORDS_PER_VERTEX);

        gl.glEnable(GL10.GL_POINT_SIZE);
        gl.glPointSize(3);
        // Disable vertex array drawing to avoid
        // conflicts with shapes that don't use it
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}

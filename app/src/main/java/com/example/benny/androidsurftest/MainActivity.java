package com.example.benny.androidsurftest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MatOfPoint3f rec3d;
    private GLSurfaceView mGLView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        OpenCVLoader.initDebug();
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this,mLoaderCallback))
        {
            Toast.makeText(MainActivity.this, "init failture!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Mat> imageList= new ArrayList<Mat>();


//        Mat img1 = Imgcodecs.imread("/mnt/sdcard/Test/P1000965.jpg");
//        Mat img2 = Imgcodecs.imread("/mnt/sdcard/Test/P1000966.jpg");
//        Mat img3 = Imgcodecs.imread("/mnt/sdcard/Test/P1000967.jpg");

//        Mat img1 = Imgcodecs.imread("/storage/sdcard0/Test/P1000965.jpg");
//        Mat img2 = Imgcodecs.imread("/storage/sdcard0/Test/P1000966.jpg");
//        Mat img3 = Imgcodecs.imread("/storage/sdcard0/Test/P1000967.jpg");
//        Mat img4 = Imgcodecs.imread("/storage/sdcard0/Test/P1000968.jpg");
//        Mat img5 = Imgcodecs.imread("/storage/sdcard0/Test/P1000969.jpg");
//        Mat img6 = Imgcodecs.imread("/storage/sdcard0/Test/P1000970.jpg");
//        Mat img7 = Imgcodecs.imread("/storage/sdcard0/Test/P1000971.jpg");

//        Mat img1 = Imgcodecs.imread("/storage/sdcard0/Test/image000.jpg");
//        Mat img2 = Imgcodecs.imread("/storage/sdcard0/Test/image001.jpg");
//        Mat img3 = Imgcodecs.imread("/storage/sdcard0/Test/image002.jpg");
//        Mat img4 = Imgcodecs.imread("/storage/sdcard0/Test/image003.jpg");
//        Mat img5 = Imgcodecs.imread("/storage/sdcard0/Test/image004.jpg");
//        Mat img6 = Imgcodecs.imread("/storage/sdcard0/Test/image005.jpg");
//        Mat img7 = Imgcodecs.imread("/storage/sdcard0/Test/image006.jpg");
//        Mat img8 = Imgcodecs.imread("/storage/sdcard0/Test/image007.jpg");
//        Mat img9 = Imgcodecs.imread("/storage/sdcard0/Test/image008.jpg");
//        Mat img10 = Imgcodecs.imread("/storage/sdcard0/Test/image009.jpg");
//        Mat img11 = Imgcodecs.imread("/storage/sdcard0/Test/image010.jpg");
//        Mat img12 = Imgcodecs.imread("/storage/sdcard0/Test/image011.jpg");
//        Mat img13 = Imgcodecs.imread("/storage/sdcard0/Test/image012.jpg");
//        Mat img14 = Imgcodecs.imread("/storage/sdcard0/Test/image013.jpg");
//        Mat img15 = Imgcodecs.imread("/storage/sdcard0/Test/image014.jpg");
//        Mat img16 = Imgcodecs.imread("/storage/sdcard0/Test/image015.jpg");
//        Mat img17 = Imgcodecs.imread("/storage/sdcard0/Test/image016.jpg");
//        Mat img18 = Imgcodecs.imread("/storage/sdcard0/Test/image017.jpg");
//        Mat img19 = Imgcodecs.imread("/storage/sdcard0/Test/image018.jpg");
//        Mat img20 = Imgcodecs.imread("/storage/sdcard0/Test/image019.jpg");
//        Mat img21 = Imgcodecs.imread("/storage/sdcard0/Test/image020.jpg");

        Mat img1= Imgcodecs.imread(Environment.getExternalStorageDirectory().getPath()+"/Test/0001.png");
        Mat img2= Imgcodecs.imread(Environment.getExternalStorageDirectory().getPath()+"/Test/0002.png");
        Mat img3 = Imgcodecs.imread(Environment.getExternalStorageDirectory().getPath()+"/Test/0003.png");


        Mat K = Mat.eye(3, 3, CvType.CV_32FC1);
        float data[]={2759.48f, 0, 300.00f,0, 2764.16f, 240.00f,0, 0, 1};
//        float data[] = {2500.00f, 0, 384.00f, 0, 2500.00f, 512.00f, 0, 0, 1};
//        float data[] = {477.1153f, 0, 317.3166f, 0, 478.4056f, 241.0606f, 0, 0, 1};


        for (int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                K.put(i,j,data[i*3+j]);

        imageList.add(img1);
        imageList.add(img2);
        imageList.add(img3);


//        imageList.add(img4);
//        imageList.add(img5);
//        imageList.add(img6);
//        imageList.add(img7);

//        imageList.add(img8);
//        imageList.add(img9);
//        imageList.add(img10);
//        imageList.add(img11);
//
//        imageList.add(img12);
//        imageList.add(img13);
//        imageList.add(img14);
//        imageList.add(img15);
//
//        imageList.add(img16);
//        imageList.add(img17);
//        imageList.add(img18);
//        imageList.add(img19);
//
//        imageList.add(img20);
//        imageList.add(img21);


        rec3d = new MatOfPoint3f();
        StructureFromMotion sfm = new StructureFromMotion();

        //test key points
        //ArrayList<MatOfKeyPoint> keypointstest = new ArrayList<>();
        //ArrayList<Mat> descriptorstest = new ArrayList<>();
        //ArrayList<MatOfDMatch> matchestest = new ArrayList<>();


        //sfm.extractFeatures(imageList, keypointstest, descriptorstest);
        //sfm.matchFeatures(descriptorstest, matchestest);
        //writeKeyPoints("/storage/sdcard0/Test/keypoints10.txt", keypointstest.get(0));
        //writeKeyPoints("/storage/sdcard0/Test/keypoints20.txt", keypointstest.get(1));
        //writeKeyPoints("/storage/sdcard0/Test/keypoints30.txt", keypointstest.get(2));

        //MatOfPoint3f pointstest = new MatOfPoint3f();
        //ArrayList<MatOfInt> correspondstructidxtest = new ArrayList<>();

        //test matched key points
        //MatOfPoint2f p1test = new MatOfPoint2f();
        //MatOfPoint2f p2test = new MatOfPoint2f();
        //sfm.getMatchedPoints(keypointstest.get(0), keypointstest.get(1), matchestest.get(0), p1test, p2test);
        //write2DPoints("/storage/sdcard0/Test/matchedpoints10.txt", p1test);
        //write2DPoints("/storage/sdcard0/Test/matchedpoints20.txt", p2test);

        //test mask
        //Mat Rtest = new Mat();
        //Mat Ttest = new Mat();
        //Mat masktest = new Mat();
        //sfm.computeRT(data, p1test, p2test, Rtest, Ttest, masktest);
        //writeMask("/storage/sdcard0/Test/masktest0.txt", masktest);
        rec3d = sfm.multipleViewRec(data, K, imageList);
        //write3DPoints("/storage/sdcard0/Test/pointCloudHead8.txt",rec3d);
        //write3DPoints(Environment.getExternalStorageDirectory().getPath()+"/pointCloudHead8.txt",rec3d);
        setContentView(mGLView);
        //setContentView(R.layout.activity_main);

    }

    static{
        System.loadLibrary("opencv_java3");
    }

    //write mask numbers to sd card of the mobile phone
    //this is a function for results testing
   public static void writeMask(String filename, Mat mask){
       try{
           FileWriter fileWriter = new FileWriter(filename);
           for (int i = 0; i < mask.rows(); i++){
               try {

                   fileWriter.write(String.valueOf(mask.get(i,0)[0]));
                   fileWriter.write("\n");
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }

           fileWriter.close();

       }catch (IOException e){
           e.printStackTrace();
       }
   }

    //write 3d point cloud to sd card of the mobile phone
    //this is a function for results testing
    public static void write3DPoints(String filename, MatOfPoint3f point3f){

        try{
            FileWriter fileWriter = new FileWriter(filename);
            for (int i = 0; i < point3f.toList().size(); i++){
                try {

                    fileWriter.write(String.valueOf(point3f.toList().get(i).x));
                    fileWriter.write(" ");
                    fileWriter.write(String.valueOf(point3f.toList().get(i).y));
                    fileWriter.write(" ");
                    fileWriter.write(String.valueOf(point3f.toList().get(i).z));
                    fileWriter.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            fileWriter.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //write 2d feature points to sd card of the mobile phone
    //this is a function for results testing
    public static void writeKeyPoints(String filename, MatOfKeyPoint keypoints){

        try{
            FileWriter fileWriter = new FileWriter(filename);
            for (int i = 0; i < keypoints.toList().size(); i++){
                try {

                    fileWriter.write(String.valueOf(keypoints.toList().get(i).pt));
                    fileWriter.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            fileWriter.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    //write 2d matched feature points to sd card of the mobile phone
    //this is a function for results testing
    public static void write2DPoints(String filename, MatOfPoint2f matchedpoints){

        try{
            FileWriter fileWriter = new FileWriter(filename);
            for (int i = 0; i < matchedpoints.toList().size(); i++){
                try {

                    fileWriter.write(String.valueOf(matchedpoints.toList().get(i).x));
                    fileWriter.write(" ");
                    fileWriter.write(String.valueOf(matchedpoints.toList().get(i).y));
                    fileWriter.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            fileWriter.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }


    //write corresponding indices into a file
    //this is a function of results testing
    public static void writeCSIdx(String filename, MatOfInt inputint){

        try{
            FileWriter fileWriter = new FileWriter(filename);
            for (int i = 0; i < inputint.toList().size(); i++){
                try {

                    fileWriter.write(String.valueOf(inputint.toList().get(i)));
                    fileWriter.write(" ");

                    if (i != 0 && i % 10 == 0){
                        fileWriter.write("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            fileWriter.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");//armeabi-v7a armeabi
                }
                break;
                default:
                {
                    super.onManagerConnected(status);
                    Log.i("OpenCV", "OpenCV can't successfully load");
                }
                break;
            }
        }
    };
}

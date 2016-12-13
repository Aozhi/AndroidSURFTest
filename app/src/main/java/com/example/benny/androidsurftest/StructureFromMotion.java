package com.example.benny.androidsurftest;

/**
 * Created by Benny on 10/26/16.
 */

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.hconcat;

public class StructureFromMotion {

    public void extractFeatures(List<Mat> imageForAll,
                                ArrayList<MatOfKeyPoint> keypointsForAll,
                                ArrayList<Mat> descriptorsForAll){

        keypointsForAll.clear();
        descriptorsForAll.clear();

        for(int i = 0; i < imageForAll.size(); i++){

            FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);

            DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SURF);

            //Define an instance of each image
            Mat image = imageForAll.get(i);

            //check if the image is empty
            if(image.empty())continue;

            //Define keypoints stored in a Mat
            MatOfKeyPoint keypoints = new MatOfKeyPoint();

            //Define descriptors
            Mat descriptors = new Mat();

            //Detect SIFT points and compute the descriptors
            detector.detect(image, keypoints);
            extractor.compute(image, keypoints, descriptors);

            if(keypoints.toList().size() <= 10)continue;

            //Save the keypoints and features
            keypointsForAll.add(keypoints);
            descriptorsForAll.add(descriptors);

        }



    }



    public void matchFeatures(Mat query, Mat train, MatOfDMatch matches){

        List<MatOfDMatch> knnmatches = new ArrayList<MatOfDMatch>();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2);
        matcher.knnMatch(query, train, knnmatches, 2);

        //Define a minimal distance, so that the
        //Ratio Test can be applied
        //float minDist = Float.MAX_VALUE;
        double minDist = Double.MAX_VALUE;
        for(int i = 0; i < knnmatches.size(); i++){

            //Ratio Test
            if(knnmatches.get(i).toArray()[0].distance > 0.6 * knnmatches.get(i).toArray()[1].distance)continue;
            float dist = knnmatches.get(i).toArray()[0].distance;
            if(dist < minDist)minDist = dist;

        }

        matches.toList().clear();
        ArrayList<DMatch> matcheslist = new ArrayList<>();
        for(int i = 0;i < knnmatches.size();i++){
            if(
                    knnmatches.get(i).toArray()[0].distance>0.6*knnmatches.get(i).toArray()[1].distance||
                            knnmatches.get(i).toArray()[0].distance > 5 * Math.max(minDist, 10.0)

                    )continue;

            matcheslist.add(knnmatches.get(i).toArray()[0]);
        }
        matches.fromList(matcheslist);

    }

    public void matchFeatures(ArrayList<Mat> descriptorsForAll, ArrayList<MatOfDMatch> matchesForAll){


        matchesForAll.clear();
        for (int i = 0; i < descriptorsForAll.size() - 1; i++){

            MatOfDMatch matches = new MatOfDMatch();
            matchFeatures(descriptorsForAll.get(i), descriptorsForAll.get(i + 1), matches);
            matchesForAll.add(matches);
        }

    }

    public void computeRT(float d[],
                             MatOfPoint2f p1,
                             MatOfPoint2f p2,
                             Mat R,
                             Mat T,
                             Mat mask

    ){
        //obtain focal length and the priciple point from the camera matrix
        double focallength = 0.5 * (d[0] + d[4]);
        Point principlepoint = new Point(d[2],d[5]);

        //compute essential matrix with image correspondences using RANSAC
        Mat E = Calib3d.findEssentialMat(p1, p2, focallength, principlepoint, Calib3d.RANSAC, 0.99, 1.0, mask);
        //Mat maskcopy = new Mat();
        //mask.copyTo(maskcopy);
        //Calib3d.recoverPose(E, p1, p2, R, T, focallength, principlepoint, mask);
        Calib3d.recoverPose(E, p1, p2, R, T, focallength, principlepoint);


    }




    public void getMatchedPoints(MatOfKeyPoint p1,
                                 MatOfKeyPoint p2,
                                 MatOfDMatch matches,
                                 MatOfPoint2f outp1,
                                 MatOfPoint2f outp2){

        outp1.toList().clear();
        outp2.toList().clear();

        List<Point> outp1temp = new ArrayList<>();
        List<Point> outp2temp = new ArrayList<>();


        for (int i = 0; i < matches.toList().size(); i++){

            outp1temp.add(p1.toList().get(matches.toList().get(i).queryIdx).pt);
            outp2temp.add(p2.toList().get(matches.toList().get(i).trainIdx).pt);

        }

        outp1.fromList(outp1temp);
        outp2.fromList(outp2temp);

    }

    public void twoViewRect(Mat K, Mat R1, Mat T1, Mat R2, Mat T2, MatOfPoint2f p1, MatOfPoint2f p2, MatOfPoint3f structure){

        //triangulatePoints only support float
        Mat proj1 = new Mat(3, 4, CvType.CV_32FC1);
        Mat proj2 = new Mat(3, 4, CvType.CV_32FC1);

        R1.convertTo(R1, CvType.CV_32FC1);
        T1.convertTo(T1, CvType.CV_32FC1);

        R2.convertTo(R2, CvType.CV_32FC1);
        T2.convertTo(T2, CvType.CV_32FC1);

        List<Mat> l1 = new ArrayList<>();
        List<Mat> l2 = new ArrayList<>();

        l1.add(R1);
        l1.add(T1);

        l2.add(R2);
        l2.add(T2);

        hconcat(l1, proj1);
        hconcat(l2, proj2);

        Mat fK = new Mat();
        K.convertTo(fK, CvType.CV_32FC1);

        Mat projection1 = new Mat();
        Mat projection2 = new Mat();

        Core.gemm(fK, proj1, 1, proj1, 0, projection1);
        Core.gemm(fK, proj2, 1, proj2, 0, projection2);



        Mat s = new Mat();
        Calib3d.triangulatePoints(projection1, projection2, p1, p2, s);

        structure.toList().clear();

        List<Point3> pointsList = new ArrayList<>();
        for (int i = 0; i < s.cols(); i++){

            Mat col = s.col(i);
            double ele0[] = col.get(0, 0);
            double ele1[] = col.get(1, 0);
            double ele2[] = col.get(2, 0);
            double ele3[] = col.get(3, 0);

            double e1 = ele0[0]/ele3[0];
            double e2 = ele1[0]/ele3[0];
            double e3 = ele2[0]/ele3[0];


            Point3 point = new Point3(e1, e2, e3);
            pointsList.add(point);
        }

        structure.fromList(pointsList);

    }

    public MatOfPoint2f maskoutPoints(MatOfPoint2f p, Mat mask){

        List<Point> pfrom = new ArrayList<>();
        MatOfPoint2f pto = new MatOfPoint2f();

        for (int i = 0; i < mask.rows(); i++){
            if(mask.get(i,0)[0] > 0)pfrom.add(p.toList().get(i));
        }

        pto.fromList(pfrom);
        return pto;

    }

    public void initStructure(float data[],
                              Mat K,
                              ArrayList<MatOfKeyPoint> keypointsForAll,
                              ArrayList<MatOfDMatch> matchesForAll,
                              MatOfPoint3f structure,
                              ArrayList<MatOfInt> correspondStructIdx,
                              ArrayList<Mat> rotations,
                              ArrayList<Mat> translations){

        MatOfPoint2f p1 = new MatOfPoint2f();
        MatOfPoint2f p2 = new MatOfPoint2f();
        Mat R = new Mat();
        Mat T = new Mat();
        Mat mask = new Mat();
        getMatchedPoints(keypointsForAll.get(0), keypointsForAll.get(1), matchesForAll.get(0), p1, p2);
        computeRT(data, p1, p2, R, T, mask);
        p1 = maskoutPoints(p1, mask);
        p2 = maskoutPoints(p2, mask);

        Mat R0 = Mat.eye(3, 3, CvType.CV_64FC1);
        Mat T0 = Mat.zeros(3, 1, CvType.CV_64FC1);

        twoViewRect(K, R0, T0, R, T, p1, p2, structure);

        rotations.add(R0);
        rotations.add(R);
        translations.add(T0);
        translations.add(T);

        correspondStructIdx.clear();
        for(int i = 0; i < keypointsForAll.size(); i++){

            int j = keypointsForAll.get(i).toList().size();
            Mat CSIM = new Mat(j, 1, CvType.CV_32S, Scalar.all(-1));
            MatOfInt CSI = new MatOfInt(CSIM);
            correspondStructIdx.add(CSI);
        }

        int idx = 0;
        MatOfDMatch matches = matchesForAll.get(0);
        for(int i = 0; i < matches.toList().size(); i++){

            if(mask.get(i,0)[0] == 0.0)continue;
            correspondStructIdx.get(0).put((matches.toArray()[i].queryIdx), 0, idx);
            correspondStructIdx.get(1).put((matches.toArray()[i].trainIdx), 0, idx);
            ++idx;
        }

    }

    public void getObjAndImgPoints(MatOfDMatch matches,
                                   MatOfInt structIdices,
                                   MatOfPoint3f structure,
                                   MatOfKeyPoint keypoints,
                                   MatOfPoint3f objpoints,
                                   MatOfPoint2f imgpoints

    ){
        objpoints.toList().clear();
        imgpoints.toList().clear();

        ArrayList<Point3> objectpoints = new ArrayList<>();
        ArrayList<Point> imagepoints = new ArrayList<>();
        for(int i = 0; i < matches.toList().size(); i++){
            int queryidx = matches.toList().get(i).queryIdx;
            int trainidx = matches.toList().get(i).trainIdx;

            int structidx = structIdices.toList().get(queryidx);
            if (structidx < 0)continue;

            objectpoints.add(structure.toList().get(structidx));
            imagepoints.add(keypoints.toList().get(trainidx).pt);
        }
        objpoints.fromList(objectpoints);
        imgpoints.fromList(imagepoints);

    }

    public void fusionStructure(MatOfDMatch matches,
                                MatOfInt structIndices,
                                MatOfInt nextStructIndices,
                                MatOfPoint3f structure,
                                MatOfPoint3f nextStructure){


        //List<Point3> structuretemp = new ArrayList<>();
        List<Point3> structurecopy = new ArrayList<>();
        structurecopy.addAll(structure.toList());

        for(int i = 0; i < matches.toList().size(); i++){

            int queryidx = matches.toList().get(i).queryIdx;
            int trainidx = matches.toList().get(i).trainIdx;

            int structidx = structIndices.toList().get(queryidx);
            if(structidx >= 0){
                //nextStructIndices.toArray()[trainidx] = structidx;
                nextStructIndices.put(trainidx, 0, structidx);
                continue;
            }

            structurecopy.add(nextStructure.toList().get(i));
            nextStructIndices.put(trainidx, 0, structurecopy.size() - 1);
            structIndices.put(queryidx, 0, structurecopy.size()-1);
        }

        structure.fromList(structurecopy);

    }

    public MatOfPoint3f multipleViewRec(float []data, Mat K, List<Mat> imageForall){

        MatOfPoint3f pointcloud = new MatOfPoint3f();
        ArrayList<MatOfKeyPoint> keypointsForAll = new ArrayList<>();
        ArrayList<Mat> descriptorsForAll = new ArrayList<>();
        ArrayList<MatOfDMatch> matchesForAll = new ArrayList<>();
        ArrayList<MatOfInt> correspondStructIdx = new ArrayList<>();
        ArrayList<Mat> roataions = new ArrayList<>();
        ArrayList<Mat> translations = new ArrayList<>();

        //extra all feature points
        extractFeatures(imageForall, keypointsForAll, descriptorsForAll);

        //match feature pairwise
        matchFeatures(descriptorsForAll, matchesForAll);

        //Initialize the structure of first two images
        initStructure(data, K, keypointsForAll, matchesForAll, pointcloud, correspondStructIdx, roataions, translations);


        //reconstruction of the rest of images
        //by the incremental method

        for (int i = 1; i < matchesForAll.size(); i++){

             MatOfPoint3f objectpoints = new MatOfPoint3f();
             MatOfPoint2f imagepoints = new MatOfPoint2f();

             Mat r = new Mat();
             Mat R = new Mat();
             Mat T = new Mat();

            //get structure of the ith image and the correspongding image points
            getObjAndImgPoints(matchesForAll.get(i),
                    correspondStructIdx.get(i),
                    pointcloud,
                    keypointsForAll.get(i+1),
                    objectpoints,
                    imagepoints);

            //if(objectpoints.toList().size() == 0 || imagepoints.toList().size() == 0)continue;

            //solve R and T
            Calib3d.solvePnPRansac(objectpoints, imagepoints, K, new MatOfDouble(), r, T);
            //Change roatation vectors into roatation matrix
            Calib3d.Rodrigues(r, R);

            roataions.add(R);
            translations.add(T);
            //Save the R and T


            MatOfPoint2f p3 = new MatOfPoint2f();
            MatOfPoint2f p4 = new MatOfPoint2f();
            getMatchedPoints(keypointsForAll.get(i), keypointsForAll.get(i+1), matchesForAll.get(i), p3, p4);

            //3d reconstruction according to the previous derived R and T
            MatOfPoint3f nextStructre = new MatOfPoint3f();
            twoViewRect(K, roataions.get(i), translations.get(i), R, T, p3, p4, nextStructre);

            fusionStructure(matchesForAll.get(i),
                            correspondStructIdx.get(i),
                            correspondStructIdx.get(i+1),
                            pointcloud,
                            nextStructre);

        }

        return pointcloud;
    }



}

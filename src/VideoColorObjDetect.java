import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.util.ArrayList;
import java.util.List;

public class VideoColorObjDetect {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat src = new Mat();
        Mat dst = new Mat();
        Mat backgroundFrame;
        Mat hsvFrame = new Mat();
        Mat blurFrame = new Mat();

        Mat mask1 = new Mat();
        Mat mask2 = new Mat();

        Scalar redLower = new Scalar(0, 50, 0);
        Scalar redUpper = new Scalar(10, 255, 255);

//        Scalar redLower = new Scalar(4,80,40);
//        Scalar redUpper = new Scalar(10,200,255);

        Scalar redLower2 = new Scalar(170,50,0);
        Scalar redUpper2 = new Scalar(180,255,255);

        Mat dilateElem = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(15,15));
        Mat erodeElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(15,15));

        VideoCapture videoCapture = new VideoCapture(0);
        if(!videoCapture.isOpened()) return;

        // 2^x // fix exposure // EXP_TIME = 2^(-EXP_VAL) // 0 to -13
        videoCapture.set(Videoio.CAP_PROP_EXPOSURE, -1);
//        videoCapture.set(Videoio.CAP_PROP_AUTO_EXPOSURE, 1);

        double frameW = videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        double frameH = videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);

        System.out.println(frameW + "x" + frameH);

        backgroundFrame = new Mat(new Size(frameW,frameH), CvType.CV_8UC3,new Scalar(128,0,0));

        int key=0;
        while(key != 27){
            if(!videoCapture.read(src)) break;

            Imgproc.blur(src, blurFrame, new Size(9,9));

            Imgproc.cvtColor(blurFrame, hsvFrame, Imgproc.COLOR_BGR2HSV);

            Core.inRange(hsvFrame, redLower, redUpper, mask1);

            Imgproc.erode(mask1, mask1,erodeElem);
            Imgproc.dilate(mask1, mask1,dilateElem);

            HighGui.imshow("Mask1", mask1);

            Mat res1 = new Mat();
            Mat res2 = new Mat();

            Core.bitwise_not(mask1,mask2);
            Core.copyTo(src, res1, mask2);
            Core.copyTo(backgroundFrame, res2, mask1);

            Core.add(res1, res2, dst);

            HighGui.imshow("res1", res1);
            HighGui.imshow("res2", res2);
            HighGui.imshow("dst", dst);

//            List<Mat> hsvComponents = new ArrayList<>();
//            Core.split(hsvFrame, hsvComponents);
//
//            HighGui.imshow("H", hsvComponents.get(0));
//            HighGui.imshow("S", hsvComponents.get(1));
//            HighGui.imshow("V", hsvComponents.get(2));

//            HighGui.imshow("HSV Frame", hsvFrame);
            HighGui.imshow("src Frame", src);

            key = HighGui.waitKey(20);
        }
        HighGui.destroyAllWindows();
        System.exit(0);
    }
}

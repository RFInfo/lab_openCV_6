import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class VideoObjColor2Detect {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat src = new Mat();
        Mat dst = new Mat();
        Mat bgFrame = new Mat();
        Mat hsvFrame = new Mat();
        Mat blurFrame = new Mat();

        Mat mask1 = new Mat();
        Mat mask2 = new Mat();

        Mat range1 = new Mat();
        Mat range2 = new Mat();

        Scalar redLower1 = new Scalar(0,50,0);
        Scalar redUpper1 = new Scalar(10,255,255);

        Scalar redLower2 = new Scalar(150,30,0);
        Scalar redUpper2 = new Scalar(180,255,255);

        Mat dilateElem = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(15,15));
        Mat erodeElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(15,15));

        VideoCapture capture = new VideoCapture(0);

        if(!capture.isOpened()) return;

//        capture.read(bgFrame);

//        capture.set(Videoio.CAP_PROP_EXPOSURE, -2);

        int key = 0;
        while(key != 27){
            if(!capture.read(src)) break;

            bgFrame = new Mat(src.size(), CvType.CV_8UC3,new Scalar(255,0,0));

            Imgproc.blur(src,blurFrame, new Size(9,9));
            Imgproc.cvtColor(blurFrame, hsvFrame,Imgproc.COLOR_BGR2HSV);

//            List<Mat> hsvComponents = new ArrayList<>();
//            Core.split(hsvFrame,hsvComponents);
//
//            HighGui.imshow("H", hsvComponents.get(0));
//            HighGui.imshow("S", hsvComponents.get(1));
//            HighGui.imshow("V", hsvComponents.get(2));

            Core.inRange(hsvFrame,redLower1,redUpper1,range1);
            Core.inRange(hsvFrame,redLower2,redUpper2,range2);

            Core.bitwise_or(range1,range2,mask1);

            Imgproc.erode(mask1,mask1,erodeElem);
            Imgproc.dilate(mask1,mask1,dilateElem);

            Core.bitwise_not(mask1, mask2);

            Mat res1 = new Mat();
            Mat res2 = new Mat();

            Core.copyTo(src, res1, mask2);
            Core.copyTo(bgFrame,res2, mask1);

            Core.add(res1,res2,dst);

            HighGui.imshow("Src", src);
//            HighGui.imshow("HSV", hsvFrame);
            HighGui.imshow("Mask1", mask1);
            HighGui.imshow("Range1", range1);
            HighGui.imshow("Range2", range2);
            HighGui.imshow("Dst", dst);


            key = HighGui.waitKey(20);
        }

        HighGui.destroyAllWindows();
        System.exit(0);
    }
}

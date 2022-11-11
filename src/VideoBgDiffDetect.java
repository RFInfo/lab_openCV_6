import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class VideoBgDiffDetect {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat src = new Mat();
        Mat dst = new Mat();
        Mat bgFrame = new Mat();

        Mat diff = new Mat();
        Mat graySrc = new Mat();
        Mat grayBg = new Mat();
        Mat thresholdDiff = new Mat();


        Mat mask1 = new Mat();
        Mat mask2 = new Mat();

        Mat dilateElem = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(15,15));
        Mat erodeElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(15,15));

        VideoCapture capture = new VideoCapture(0);

        if(!capture.isOpened()) return;

        capture.set(Videoio.CAP_PROP_EXPOSURE, -2);

        // first frame for background
        capture.read(bgFrame);
        Imgproc.cvtColor(bgFrame, grayBg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayBg, grayBg,new Size(21,21),0);



        int key = 0;
        while(key != 27){
            if(!capture.read(src)) break;

            dst = new Mat(src.size(),CvType.CV_8UC3, new Scalar(255,0,0));

            Imgproc.cvtColor(src, graySrc, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(graySrc, graySrc,new Size(21,21),0);

            Core.absdiff(graySrc, grayBg, diff);

            Imgproc.GaussianBlur(diff, diff,new Size(21,21),0);
            Imgproc.threshold(diff,thresholdDiff, 10, 255, Imgproc.THRESH_BINARY);

            Imgproc.dilate(thresholdDiff,thresholdDiff,dilateElem);

            Core.copyTo(src,dst,thresholdDiff);

            HighGui.imshow("Src", src);
            HighGui.imshow("Diff", diff);
            HighGui.imshow("Threshold", thresholdDiff);
            HighGui.imshow("Dst", dst);

            key = HighGui.waitKey(20);
        }

        HighGui.destroyAllWindows();
        System.exit(0);
    }
}

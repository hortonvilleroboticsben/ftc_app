package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.media.Image;
import android.os.Environment;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import boofcv.alg.enhance.EnhanceImageOps;
import org.jcodec.movtool.streaming.tracks.ToAACTrack;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import boofcv.alg.InputSanityCheck;
import boofcv.alg.distort.DistortImageOps;
import boofcv.alg.filter.binary.BinaryImageOps;
//import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
//import boofcv.alg.filter.blur.GBlurImageOps;
//import boofcv.alg.misc.ImageMiscOps;
//import boofcv.alg.sfm.DepthSparse3D;
import boofcv.alg.filter.binary.impl.ImplBinaryImageOps;
import boofcv.alg.filter.binary.impl.ImplBinaryImageOps_MT;
import boofcv.alg.interpolate.InterpolationType;
import boofcv.alg.misc.ImageMiscOps;
import boofcv.android.ConvertBitmap;
//import boofcv.io.image.ConvertBufferedImage;
//import boofcv.struct.image.GrayF32;
import boofcv.concurrency.BoofConcurrency;
import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;
//import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;


public class ImageAnalyzer {
    public static final int height = 669 ;
    public static final int width = 473;
    public static final int threshold = 180;

    public Bitmap bitmap;



    public int analyze(Context c, byte[] bytes, TextureView imageView, TextView textView, TextView widthTV, TextView heightTV) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;



        bitmap = imageView.getBitmap();

        Canvas canvas = new Canvas(bitmap);


/*//load source bitmap and prepare destination bitmap
        Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable., options);
        Bitmap result = Bitmap.createBitmap(pic.getWidth(), pic.getHeight(),  Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(result);

//first convert bitmap to grey scale:
        bitmapPaint.setColorFilter(new ColorMatrixColorFilter(createGreyMatrix()));
        c.drawBitmap(pic, 0, 0, bitmapPaint);

//then convert the resulting bitmap to black and white using threshold matrix
        bitmapPaint.setColorFilter(new ColorMatrixColorFilter(createThresholdMatrix(128)));
        c.drawBitmap(result, 0, 0, bitmapPaint);
        bitmapPaint.setColorFilter(null);
        otherCanvas.drawBitmap(result, null, new Rect(x, y, x + size, y + size), bitmapPaint);*/


        Toast.makeText(c, "Receive Bitmap", Toast.LENGTH_LONG).show();
        if (bytes!=null){
            Toast.makeText(c, "Bytes has a value", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(c, "Bytes is equal to null", Toast.LENGTH_LONG).show();
        }

        //convert bytes to bitmap then i guess
        //ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        // BufferedImage bImage2 = ImageIO.read(bis);
        //Bitmap image = ImageIO.read(new File(fp ));

        //File storage = Environment.getExternalStorageDirectory();
        //File file = new File(storage, filepath)

        //Bitmap image = BitmapFactory.decodeFile;
        //Main norm = new Main();
        //BufferedImage b =  normalizeImgC(image);

        //bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //Toast.makeText(c, "Width: " + bitmap.getWidth() + "\nHeight: " + bitmap.getHeight(), Toast.LENGTH_LONG).show();




        Bitmap croppedImg = cropImg(bitmap, c);

        //saveImg(croppedImg, new File("C:\\Users\\Sam\\Pictures\\testImgCrop.jpg"));
//        saveImg(blurImg(croppedImg), new File("C:\\Users\\Sam\\Pictures\\testImgCrop.jpg"));


        //return -1;
        return blurImg(bitmap, c, textView, widthTV, heightTV);
    }
    /*public static void saveImg(BufferedImage image, File filePath){
        try {
            if(!filePath.exists()) filePath.createNewFile();
            ImageIO.write(image, "jpg", filePath);//code playground
        } catch (IOException e) {
            System.out.println(e);
        }
    }*/
    public static Bitmap cropImg(Bitmap image, Context context){
        Bitmap croppedImg = image;
        //Bitmap croppedImg = image.getSubimage(1000, 1350, width, height);
        //saveImg(croppedImg, new File("C:\\Users\\Sam\\Pictures\\testImgCrop.jpg"));
        //save(croppedImg, context);
        return croppedImg;
    }

    public static int blurImg(Bitmap bitmap, Context context, TextView textView, TextView widthTV, TextView heightTV){

        int x = 50;
        int y = 80;


        Planar<GrayU8> layers = ConvertBitmap.bitmapToPlanar(bitmap, null, GrayU8.class, null);
        //DistortImageOps.rotate(layers, layers, BorderType.ZERO, InterpolationType.POLYNOMIAL4, 270);
        //for (int i = 0; i<layers.getNumBands();i++) ImageMiscOps.rotateCW(layers.getBand(i), layers.getBand(i));
        ConvertBitmap.planarToBitmap(layers, bitmap, null);
        //Planar<GrayU8> layers = ConvertBufferedImage.convertFromPlanar(bufferedImage, null, true, GrayU8.class);
        GrayU8 blueLayer = layers.getBand(2);
        GrayU8 redLayer = layers.getBand(0);

        //System.out.println("Width: " + blueLayer.getWidth() + " Height: " + blueLayer.getHeight());

        //GrayU8 blueSubImage = blueLayer.subimage(x, y, x+width, y+height);


        /*Planar<GrayU8> blueImage = new Planar<>(GrayU8.class, 3);
        bufferedImageFromLayers(blueImage, blueLayer, "blueImgLayer");*/

        /*Planar<GrayU8> redImage = new Planar<>(GrayU8.class, 3);
        bufferedImageFromLayers(redImage, redLayer, "redImgLayer");*/
        /*blueImage.setWidth(blueSubImage.getWidth());
        blueImage.setHeight(blueSubImage.getHeight());

        blueImage.setBand(0,blueSubImage);
        blueImage.setBand(1,blueSubImage);
        blueImage.setBand(2,blueSubImage);

        BufferedImage bufferedImgBlue = ConvertBufferedImage.convertTo(blueSubImage, null, true);
        saveImg(bufferedImgBlue, new File("C:\\Users\\Sam\\Pictures\\blueImgLayer.jpg"));*/

        //System.out.println("Width: " + blueLayer.getWidth() + " Height: " + blueLayer.getHeight());


        GrayU8  blueThresh = new GrayU8(width, height),
                redThresh = new GrayU8(width, height),
                dilated = new GrayU8(width , height),
                eroded = new GrayU8(width, height);
        //System.out.println(blueSubImage.getWidth() + " "+ blueSubImage.getHeight());

        /*
         *
         * crashes the app when ever i run the bluethresh, redthresh and overall
         *
         * */
        //int threshold = 140;
        //blueThresh = ThresholdImageOps.threshold(blueLayer, blueThresh, threshold, true);
        //redThresh = ThresholdImageOps.threshold(redLayer, redThresh, threshold, false);

        //blueThresh = threshold(blueLayer);
        //redThresh = threshold(redLayer);

        //GrayU8 overall = new GrayU8(width, height);
        int xAverages = 0;
        int count = 0;
        for (int p = 0; p < width; p++) {
            for (int e = 0; e < height; e++) {
                //textView.setText("asdlfjalsdjfk;asdfjasdlkfj");


                // get pixel color
                int pixel = bitmap.getPixel(p, e);
                int A = Color.alpha(pixel);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
                int average = A+R+B;
                // use 128 as threshold, above -> white, below -> black
                if (gray > threshold) {
                    count++;
                    xAverages += p;
                    gray = 255;
                }else
                    gray = 0;
                // set new pixel color to output bitmap
                bitmap.setPixel(p, e, Color.argb(A, gray, gray, gray));
            }
        }



        xAverages = xAverages / count;


        textView.setText(xAverages+"");


        save(bitmap, context);
        if(xAverages >= 0 && xAverages < 735) {
            //Toast.makeText(context, "2", Toast.LENGTH_LONG).show();
            return 2;
        }
        if(xAverages >= 735 && xAverages < 1122) {
            //Toast.makeText(context, "3", Toast.LENGTH_LONG).show();
            return 3;
        }
        if(xAverages >= 1123 && xAverages <= width){
            //Toast.makeText(context, "1", Toast.LENGTH_LONG).show();
            return 1;
        }
        return -1;
    }
    public static void save(Bitmap bitmap, Context context){
        OutputStream outputStream = null;
        //Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        try (OutputStream out = new FileOutputStream(getOutputMediaFile())){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (Exception e){
            Toast.makeText(context, e+"", Toast.LENGTH_LONG).show();
        }

    }
    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraCaptureAppIATests");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + "__"+threshold+ ".jpg");
    }
}



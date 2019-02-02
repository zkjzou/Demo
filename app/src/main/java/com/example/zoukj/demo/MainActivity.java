package com.example.zoukj.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;


public class MainActivity extends AppCompatActivity {
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }
    private ImageView iv;
    private ImageView temp;
    private Button choosePhoto;
    private Button save;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    private SeekBar sb;
    private SeekBar sc;
    private SeekBar ss;
    private int brightnessFinal;
    private float contrastFinal;
    private float saturationFinal;
    Bitmap finalImage;
    Bitmap originalImage;
    Bitmap bitmap;
    public MainActivity() {
    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.imageView2);
        choosePhoto = (Button) findViewById(R.id.button2);
        save=(Button) findViewById(R.id.save);
        sb=findViewById(R.id.bar_bright);
        sc=findViewById(R.id.bar_contrast);
        ss=findViewById(R.id.bar_saturation);

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveImageToGallery();
            }
        });
        sb.setMax(200);
        sb.setProgress(100);

        sc.setMax(20);
        sc.setProgress(0);

        ss.setMax(30);
        ss.setProgress(10);
        sb.setOnSeekBarChangeListener(new barListner());
        ss.setOnSeekBarChangeListener(new barListner());
        sc.setOnSeekBarChangeListener(new barListner());
    }
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode==RESULT_OK&& requestCode ==PICK_IMAGE){

            imageUri = data.getData();
            sb.setProgress(100);
            sc.setProgress(0);
            ss.setProgress(10);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            iv.setImageBitmap(originalImage);
        }
    }
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        iv.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        iv.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        iv.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }
    private void saveImageToGallery(){
        MediaStore.Images.Media.insertImage(getContentResolver(),finalImage,System.currentTimeMillis()+"profile.jpg",null);
    }

    private class barListner implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            final Bitmap bitmap = originalImage.copy(Bitmap.Config.ARGB_8888, true);

            Filter myFilter = new Filter();
            myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
            myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
            myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
            finalImage = myFilter.processFilter(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (seekBar.getId() == R.id.bar_bright) {
                // brightness values are b/w -100 to +100
                onBrightnessChanged(progress - 100);
            }

            if (seekBar.getId() == R.id.bar_contrast) {
                // converting int value to float
                // contrast values are b/w 1.0f - 3.0f
                // progress = progress > 10 ? progress : 10;
                progress += 10;
                float floatVal = .10f * progress;
                onContrastChanged(floatVal);
            }

            if (seekBar.getId() == R.id.bar_saturation) {
                // converting int value to float
                // saturation values are b/w 0.0f - 3.0f
                float floatVal = .10f * progress;
                onSaturationChanged(floatVal);
            }
        }
    }
}

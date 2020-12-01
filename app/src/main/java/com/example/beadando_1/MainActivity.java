package com.example.beadando_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Sensor mySensor;
    private SensorManager sensorManager;
    private Random random;
    private int rollThreshold;
    private ImageView img;
    private boolean isItDND;
    private boolean isShakeable;
    private List<Sample> sampleArray;
    private int sampleMaxIndex;
    private int lastNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_FASTEST);
        rollThreshold = 10;
        random = new Random();
        img = (ImageView) findViewById(R.id.imgDice);
        isShakeable = true;
        sampleMaxIndex = 60;
        sampleArray = new LinkedList<>();
        lastNumber = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] < rollThreshold && event.values[0] > -rollThreshold &&
                event.values[1] < rollThreshold && event.values[1] > -rollThreshold &&
                event.values[2] < rollThreshold && event.values[2] > -rollThreshold) {
            sampleArray.add(new Sample(event.values[0], event.values[1], event.values[2]));
            if (sampleArray.size() == sampleMaxIndex) {
                isShakeable = true;
                sampleArray.clear();
            }

        }
        if ((event.values[0] > rollThreshold || event.values[0] < -rollThreshold ||
                event.values[1] > rollThreshold || event.values[1] < -rollThreshold ||
                event.values[2] > rollThreshold || event.values[2] < -rollThreshold) && !isItDND) {
            int roll = 0;
            sampleArray.clear();
            if (isShakeable) {
                roll = generateNextNumber();
                if (roll == 5) {
                    isItDND = true;
                }
                MediaPlayer mediaPlayer = generateSound(roll);
                mediaPlayer.start();
                setImage("dice_", roll);
                isShakeable = false;
            }
        }
    }

    public int generateNextNumber() {
        int number = random.nextInt(6);
        while (number == lastNumber) {
            number = random.nextInt(6);
        }
        lastNumber = number;
        return number;
    }

    public void setImage(String prefix, int number) {
        String image = prefix + number;
        img.setImageDrawable(getResources().getDrawable(getResourceID(image, "drawable", getApplicationContext())));
    }


    public MediaPlayer generateSound(int number) {
        if (number != 5) {
            return MediaPlayer.create(MainActivity.this, R.raw.sound);
        } else {
            return MediaPlayer.create(MainActivity.this, R.raw.sound2);
        }
    }

    protected final static int getResourceID(final String resName, final String resType, final Context ctx) {
        final int ResourceID = ctx.getResources().getIdentifier(resName, resType, ctx.getApplicationInfo().packageName);
        if (ResourceID == 0) {
            throw new IllegalArgumentException("No resource string found with name " + resName);
        } else {
            return ResourceID;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class Sample {
        public float x;
        public float y;
        public float z;
        public boolean isEmpty;

        public Sample(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.isEmpty = false;
        }
    }
}
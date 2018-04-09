package com.arestides.cat.careartraining;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QuizChooser extends AppCompatActivity {

    int numInts = 1;

    String[] allIntervals = {"u", "m2", "M2", "m3", "M3", "tt", "p4", "p5", "m6", "M6", "m7", "M7", "o"};
    String[] notes = {"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"};
    ArrayList<String> allNotes = new ArrayList<>();

    int numOctaves = 1;
    int startOctave = 2;

    Map<String, Integer> intNamestoValues = new HashMap<>();

    TextView availIntsTextView;
    Button startOrStopButton;

    MediaPlayer notePlayer = new MediaPlayer();
    MediaPlayer answerPlayer = new MediaPlayer();

    ArrayList<String> availInts = new ArrayList<>();

    int delay = 2000;

    static boolean running = true;

    public void sleep (int delayMillis){
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void playAnswer (String interval){
        AssetFileDescriptor afd = null;

        answerPlayer.stop();
        answerPlayer.reset();

        try {
            afd = getAssets().openFd(interval + ".mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            answerPlayer.setDataSource(afd);
            answerPlayer.prepare();
            answerPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void playNote (String note){
        AssetFileDescriptor afd = null;

        notePlayer.stop();
        notePlayer.reset();

        try {
            afd = getAssets().openFd(note + ".mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            notePlayer.setDataSource(afd);
            notePlayer.prepare();
            notePlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void playNotes (String firstNote, String secondNote){
        playNote(firstNote);
        sleep(1000);
        playNote(secondNote);
    }

    public void runIntervalPlayer (){
        new Thread(new Runnable() {
            public void run() {
                while (running){
                    String interval = availInts.get(new Random().nextInt(availInts.size()));

                    int intVal = intNamestoValues.get(interval);
                    //This prevents an ArrayOutOfBounds, by only allowing notes to be selected
                    //when their interval note will be in the arraylist.
                    int firstNoteIndex = new Random().nextInt(allNotes.size() - intVal);
                    String firstNote = allNotes.get(firstNoteIndex);
                    String secondNote = allNotes.get(firstNoteIndex + intVal);

                    playNotes(firstNote, secondNote);

                    sleep(1000);

                    playAnswer(interval);

                    sleep (1000);

                }
            }
        }).start();
    }

    private void initVarsandElements () {
        availIntsTextView = findViewById(R.id.availIntsDisplay);
        startOrStopButton = findViewById(R.id.startOrStop);

        for (int i=0; i<allIntervals.length; i++ ){
            intNamestoValues.put(allIntervals[i], i);
        }

        for (int i = 0; i<numOctaves; i++) {
            for (String note : notes){
                allNotes.add(note + (i + startOctave));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_chooser);

        initVarsandElements();
        Log.d("DEBUG", Arrays.toString(allNotes.toArray()));
        updateAvailInts();
        runIntervalPlayer();
    }


    public void updateAvailInts(){
        String s = "";
        //faking immutability here, sue me
        availInts.clear();
        for (int i = 0; i < numInts; i++){
            s = s + ", " + allIntervals[i];
            availInts.add(allIntervals[i]);
        }
        availIntsTextView.setText(s);
    }

    public void decAvailIntervals (View v){
        if (numInts > 1) {
            numInts--;
            updateAvailInts();
        }
    }

    public void incAvailIntervals (View v){
        if (numInts < 12) {
            numInts++;
            updateAvailInts();
        }
    }

    public void startOrStop (View v){
        if (running) {
            running = false;
            startOrStopButton.setText("START");
        } else {
            running = true;
            startOrStopButton.setText("STOP");
        }
        //This is ugly. Need to come up with a way to have the interval player constantly checking
        //do we wrap the existing while in a while true?
        runIntervalPlayer();
    }

}

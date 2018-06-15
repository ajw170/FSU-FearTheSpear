package com.cop4656.fearthespear;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.media.MediaPlayer.OnCompletionListener;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements Runnable {

    static final String CURRENT_SONG = "CurrentSong";
    static final String CURRENT_POSITION = "CurrentPosition";
    static final String IS_PLAYING = "IsPlaying";

    private Button song1Button;
    private Button song2Button;
    private Button song3Button;
    private Button song4Button;
    private Button song5Button;
    private Button song6Button;
    private Button song7Button;
    private Button pauseButton;
    private Button stopButton;
    private Button playButton;

    private ProgressBar progressBar;

    private int currentSong;
    private int currentPosition;
    private boolean wasStopped;

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    //The media player that will play the files
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //set the button labels
        song1Button = (Button) findViewById(R.id.buttonSong1);
        song2Button = (Button) findViewById(R.id.buttonSong2);
        song3Button = (Button) findViewById(R.id.buttonSong3);
        song4Button = (Button) findViewById(R.id.buttonSong4);
        song5Button = (Button) findViewById(R.id.buttonSong5);
        song6Button = (Button) findViewById(R.id.buttonSong6);
        song7Button = (Button) findViewById(R.id.buttonSong7);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        playButton = (Button) findViewById(R.id.playButton);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        //set onclick listeners
        song1Button.setOnClickListener(buttonListener);
        song2Button.setOnClickListener(buttonListener);
        song3Button.setOnClickListener(buttonListener);
        song4Button.setOnClickListener(buttonListener);
        song5Button.setOnClickListener(buttonListener);
        song6Button.setOnClickListener(buttonListener);
        song7Button.setOnClickListener(buttonListener);
        pauseButton.setOnClickListener(buttonListener);
        stopButton.setOnClickListener(buttonListener);
        playButton.setOnClickListener(buttonListener);

        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        executor.scheduleAtFixedRate(this,0,1,TimeUnit.SECONDS);

        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        progressBar.setProgress(0);

        if (savedInstanceState != null)
        {
            //determine if a song was in fact playing
            currentSong = savedInstanceState.getInt(CURRENT_SONG);
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION);

            if (currentPosition == 0 && currentSong == 0 && !savedInstanceState.getBoolean(IS_PLAYING))
            {
                return;
            }
            if (savedInstanceState.getBoolean(IS_PLAYING))
            {
                mp = MediaPlayer.create(this,currentSong);
                mp.seekTo(currentPosition);
                play();
            }
            else //it's paused on a song ( i think)
            {
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(true);
                mp = MediaPlayer.create(this,currentSong);
                mp.seekTo(currentPosition);
                mp.setOnCompletionListener(onCompletionListener);

            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outstate)
    {
        int position;
        int song;
        boolean isPlaying;
        if (mp != null) {
            position = mp.getCurrentPosition();
            song = currentSong;
            isPlaying = mp.isPlaying();
        }
        else {
            position = 0;
            song = 0;
            isPlaying = false;
        }
        outstate.putInt(CURRENT_POSITION,position);
        outstate.putInt(CURRENT_SONG,song);
        outstate.putBoolean(IS_PLAYING,isPlaying);
        super.onSaveInstanceState(outstate);
    }

    private final OnClickListener buttonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //determine which button was pressed
            Button pressedButton = ((Button)v);

            if (pressedButton == song1Button)
            {
                loadClip(R.raw.war_chant);
                play();

            }
            else if (pressedButton == song2Button)
            {
                loadClip(R.raw.fsu_fight_song);
                play();
            }
            else if (pressedButton == song3Button)
            {
                loadClip(R.raw.victory_song);
                play();
            }
            else if (pressedButton == song4Button)
            {
                loadClip(R.raw.gold_and_garnett);
                play();
            }
            else if (pressedButton == song5Button)
            {
                loadClip(R.raw.fsu_cheer);
                play();
            }
            else if (pressedButton == song6Button)
            {
                loadClip(R.raw.fourth_quarter_fanfare);
                play();
            }
            else if (pressedButton == song7Button)
            {
                loadClip(R.raw.seminole_uprising);
                play();
            }
            else if (pressedButton == pauseButton)
            {
                pause();
            }
            else if (pressedButton == stopButton)
            {
                stop();
            }
            else if (pressedButton == playButton)
            {
                play();
            }
            else
            {
                Log.e("ButtonPress","A button was pressed but not handled.");
            }
        }
    };

    private final OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            stop();
        }
    };

    @Override
    public void onStop()
    {
        super.onStop();
        if (mp != null)
            stop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mp != null) {
            stop(); //stop music if app is closed or minimized
            progressBar.setProgress(0);
        }
        executor.shutdown();
    }

    private void setup()
    {
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    private void loadClip(int songReference)
    {
        try
        {
            //release media player if it already exists
            if (mp != null)
            {
                //release if the reference isnt null
                mp.release();
            }
            mp = MediaPlayer.create(this,songReference);
            currentSong = songReference;
            mp.setOnCompletionListener(onCompletionListener);
            //play();
        }
        catch (Throwable t)
        {
            //get rid of player

            mp.release();
        }
    }

    private void pause()
    {
        mp.pause();
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void play()
    {
        mp.start();
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    private void stop()
    {
        if (mp != null)
        {
            mp.stop();
            progressBar.setProgress(0);
        }
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        try {
            mp.prepare();
            mp.seekTo(0);
            playButton.setEnabled(true);
        }
        catch (Throwable t)
        {
            //get rid of player completely
            mp.release();
        }

    }

    @Override
    public void run()
    {
        if (progressBar.getProgress() < 100) {

            int duration = 0;
            int position = 0;
            float tempProgress = 0;
            int progress = 0;
            if (mp != null)
            {
                duration = mp.getDuration();
                String d = Integer.toString(duration);
                Log.i("Duration is",d);
                position = mp.getCurrentPosition();
                String p = Integer.toString(position);
                Log.i("Position is",p);
                tempProgress = (float) position / (duration);
                tempProgress *= 100;
                String pr = Float.toString(tempProgress);
                Log.i("Progress is",pr);
                String s = Float.toString(tempProgress);
                Log.i("message",s);
                progress = (int) tempProgress;
                String pri = Integer.toString(progress);
                Log.i("final",pri);
                progressBar.setProgress(progress);
            }
        }

    }

}

package com.mistershorr.synthesizer;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SynthesizerActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonA;
    private Button buttonBb;
    private Button buttonB;
    private Button buttonScale;
    private Button buttonLoadAndPlay;
    private SoundPool soundPool;
    private int noteA;
    private int noteBb;
    private int noteB;
    private int noteC,noteF,noteG;
    private Map<Integer, Integer> noteMap;
    private Song loadedSong;

    public static final float DEFAULT_VOLUME = 1.0f;
    public static final float DEFAULT_RATE = 1.0f;
    public static final int DEFAULT_PRIORITY = 1;
    public static final int WHOLE_NOTE = 250; // in ms


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synthesizer);

        wireWidgets();
        setListeners();
        initializeSoundPool();
        initializeNoteMap();
        loadSong();
    }

    private void initializeNoteMap() {
        noteMap = new HashMap<>();
        // in a map, you store a key:value pair
        // the key is the buttonId, the value is the noteId
        noteMap.put(R.id.button_synth_a, noteA);
        noteMap.put(R.id.button_synth_bb, noteBb);
        noteMap.put(R.id.button_synth_b, noteB);
        // repeat for all your notes you want individual buttons
    }

    private void initializeSoundPool() {
        soundPool = new SoundPool(10,
                AudioManager.STREAM_MUSIC, 0);
        noteA = soundPool.load(this, R.raw.scalea,1);
        noteBb = soundPool.load(this, R.raw.scalebb,1);
        noteB = soundPool.load(this, R.raw.scaleb,1);
        noteC = soundPool.load(this, R.raw.scalec,1);
        noteF = soundPool.load(this, R.raw.scalef,1);
        noteG = soundPool.load(this, R.raw.scaleg,1);
    }

    private void setListeners() {
        KeyboardNoteListener noteListener = new KeyboardNoteListener();
        buttonA.setOnClickListener(noteListener);
        buttonBb.setOnClickListener(noteListener);
        buttonB.setOnClickListener(noteListener);

        // challenge buttons still use the Activity's implementation
        buttonScale.setOnClickListener(this);
        buttonLoadAndPlay.setOnClickListener(this);
    }

    private void wireWidgets() {
        buttonA = findViewById(R.id.button_synth_a);
        buttonBb = findViewById(R.id.button_synth_bb);
        buttonB = findViewById(R.id.button_synth_b);
        buttonScale = findViewById(R.id.button_synth_scale);
        buttonLoadAndPlay = findViewById(R.id.button_synth_loadsong);
    }

    @Override
    public void onClick(View view) {
        // one method to handle the clicks of all the buttons
        // but don't forget to tell the buttons who is doing
        // the listening.

        switch(view.getId()) {
//            case R.id.button_synth_a:
//                soundPool.play(noteA, 1.0f, 1.0f,
//                        1, 0, 1.0f);
//                break;
//            case R.id.button_synth_bb:
//                soundPool.play(noteBb, 1.0f, 1.0f,
//                        1, 0, 1.0f);
//                break;
//            case R.id.button_synth_b:
//                soundPool.play(noteB, 1.0f, 1.0f,
//                        1, 0, 1.0f);
//                break;
            case R.id.button_synth_scale:
                playScale();
                break;
            case R.id.button_synth_loadsong:
                playSong(loadedSong);
                break;
        }
    }

    private void playScale() {
        // play all the notes one at a time with a delay in between
        Song scale = new Song();

        scale.add(new Note(noteA));
        scale.add(new Note(noteBb));
        scale.add(new Note(noteB, Note.WHOLE_NOTE*2));
        scale.add(new Note(noteBb));
        scale.add(new Note(noteA));
        scale.add(new Note(noteA));

        playSong(scale);
    }



    private void playSong(Song song) {
        for(Note note : song.getNotes()) {
            playNote(note);
            delay(note.getDelay());
        }
    }


    private void delay(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playNote(int note, int loop) {
        soundPool.play(note, DEFAULT_VOLUME, DEFAULT_VOLUME, DEFAULT_PRIORITY,
                loop, DEFAULT_RATE);
    }

    private void playNote(int note) {
        playNote(note, 0);
    }

    private void playNote(Note note) {
        playNote(note.getNoteId(), 0);
    }

    private void playNote(List<Note> chord) {
        for(Note note : chord) {
            playNote(note);
        }
    }

    private void loadSong() {
        InputStream stream = getResources().openRawResource(R.raw.song);
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        loadedSong = new Song();
        try {
            while ((line = br.readLine()) != null) {
                loadedSong.add(convertStringToNote(line));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Note convertStringToNote(String noteString) {
        int noteId = 0;
        int delay = WHOLE_NOTE;
        if(noteString.indexOf(" ") > 0) {
           delay = Integer.parseInt(noteString.substring(noteString.indexOf(" ") + 1));
           noteString = noteString.substring(0, noteString.indexOf(" "));
        }

        switch(noteString) {
            case "a" :
                noteId = noteA;
                break;
            case "b" :
                noteId = noteB;
                break;
            case "c" :
                noteId = noteC;
                break;
            case "f" :
                noteId = noteF;
                break;
            case "g" :
                noteId = noteG;
                break;
        }

        return new Note(noteId, delay);
    }

    // make an inner class to handle the button clicks
    // for the individual notes
    private class KeyboardNoteListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            // get the id of the button that was clicked
            int id = view.getId();
            // use the map to figure out what note to play
            int note = noteMap.get(id);
            // play the note
            playNote(note);
        }
    }
}









package de.hfkbremen.algorithmiccliches.examples;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;

import javax.swing.*;
import java.util.ArrayList;

/**
 * http://en.wikipedia.org/wiki/Fft
 */
public class SketchFFTSimple extends PApplet {

    private static float mCurrentTime = 0.0f;
    private Minim mMinim;
    private AudioPlayer mPlayer;
    private FFT mFFT;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void stop() {
        mPlayer.pause();
        mMinim.stop();
        super.stop();
    }

    public void setup() {

        JFileChooser mfileChooser = new JFileChooser();
        mfileChooser.showOpenDialog(frame);
        String mFileName = mfileChooser.getSelectedFile().getAbsolutePath();

        mMinim = new Minim(this);
        mPlayer = mMinim.loadFile(mFileName, 1024); // make sure the sketch can find the audio
        mPlayer.play();
        mFFT = new FFT(mPlayer.bufferSize(), mPlayer.sampleRate());
        mFFT.logAverages(55, 4);
    }

    public void draw() {
        mFFT.forward(mPlayer.left);
        mCurrentTime = (float) mPlayer.position() / (float) mPlayer.length();

        ArrayList<Float> mBands = new ArrayList<Float>(mFFT.avgSize()); // @todo revisit the diff between ```.getBand
        // (i)```and ```.getAvg(i)```
        for (int i = 0; i < mFFT.avgSize(); i++) {
            mBands.add(i, mFFT.getBand(i));
        }

        /* draw bands */
        background(255);
        for (int i = 0; i < mFFT.avgSize(); i++) {
            final float x = width * (float) i / mFFT.avgSize();
            final float myHeight = mFFT.getAvg(i) * 16;
            fill(0);
            stroke(0);
            rect(x, height, width / mFFT.avgSize(), -myHeight);
        }
        line(0, height / 2.0f, (float) mCurrentTime * width, height / 2.0f);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchFFTSimple.class.getName()});
    }
}

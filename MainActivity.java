package edu.illinois.cs.cs125.additivesynthesizer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.ImpulseOscillator;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.TriangleOscillator;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import jsyn.devices.android.JSynAndroidAudioDevice;


public class MainActivity extends AppCompatActivity{
    Spinner Spinner1;
    Spinner Spinner2;
    ToggleButton Toggle;
    SeekBar Bar;
    double[] mode;
    double[] major = {130.8, 146.8, 164.8, 174.6, 196.0, 220.0, 246.9, 261.6, 293.7, 329.6, 349.2, 392.0, 440.0, 493.9, 523.3};
    double[] naturalMinor = {130.8, 146.8, 155.6, 174.6, 196.0, 207.7, 233.1, 261.6, 293.7, 311.1, 349.2, 392.0, 415.3, 466.2, 523.3};
    double[] dorian = {130.8, 146.8, 155.6, 174.6, 196.0, 220.0, 233.1, 261.6, 293.7, 311.1, 349.2, 392.0, 440.0, 466.2, 523.3};
    double[] mixolydian = {130.8, 146.8, 164.8, 174.6, 196.0, 220.0, 233.1, 261.6, 293.7, 329.6, 349.2, 392.0, 440.0, 466.2, 523.3};
    int period;
    int wave;

    SineOscillator Sine = new SineOscillator();
    ImpulseOscillator Impulse = new ImpulseOscillator();
    SawtoothOscillator Sawtooth = new SawtoothOscillator();
    TriangleOscillator Triangle = new TriangleOscillator();
    LineOut LineOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Synthesizer synth = JSyn.createSynthesizer(new JSynAndroidAudioDevice());


        /**
         * Spinner 1
         */
        Spinner1 = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.mode_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner1.setAdapter(adapter1);
        Spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    mode = major;
                } else if(position == 1) {
                    mode = naturalMinor;
                } else if(position == 2) {
                    mode = dorian;
                } else if(position == 3) {
                    mode = mixolydian;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toggle.setEnabled(false);
            }
        });


        /**
         * Spinner 2
         */
        Spinner2 = (Spinner)findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.waveform_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner2.setAdapter(adapter2);
        Spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    wave = 0;

                } else if(position == 1) {
                    wave = 1;

                } else if(position == 2) {
                    wave = 2;

                } else if(position == 3) {
                    wave = 3;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toggle.setEnabled(false);
            }
        });

        /**
         * Seek Bar
         */
        Bar = (SeekBar)findViewById(R.id.seekBar);
        Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                    if (i == 0) {
                        period = 250;
                    } else if (i == 1) {
                        period = 167;
                    } else if (i == 2) {
                        period = 125;
                    } else if (i == 3) {
                        period = 100;
                    } else if (i == 4) {
                        period = 83;
                    }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });



        class PitchRNG {
            private double[] Mode;
            PitchRNG(double[] input) {
                Mode = input;
            }
            public double freqGen() {
                Random rand = new Random();
                int  n = rand.nextInt(14);
                return Mode[n];
            }
        }


        /**
         * Toggle Button
         */
        Toggle = (ToggleButton)findViewById(R.id.toggleButton);
        Toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        if(wave == 0) {
                            Sine.frequency.set(new PitchRNG(mode).freqGen());
                        } else if(wave == 1) {
                            Impulse.frequency.set(new PitchRNG(mode).freqGen());
                        } else if(wave == 2) {
                            Sawtooth.frequency.set(new PitchRNG(mode).freqGen());
                        } else if(wave == 3) {
                            Triangle.frequency.set(new PitchRNG(mode).freqGen());
                        }
                    }
                };
                timer.scheduleAtFixedRate(tt, 0, period);

                if(Toggle.isChecked()) {
                    Spinner1.setEnabled(false);
                    Spinner2.setEnabled(false);
                    Bar.setEnabled(false);


                    if(wave == 0) {
                        synth.add(Sine = new SineOscillator());
                        synth.add(LineOut = new LineOut());

                        Sine.output.connect(0,LineOut.input,0);
                        Sine.output.connect(0,LineOut.input,1);
                        synth.start();
                        LineOut.start();

                    } else if(wave == 1) {
                        synth.add(Impulse = new ImpulseOscillator());
                        synth.add(LineOut = new LineOut());

                        Impulse.output.connect(0,LineOut.input,0);
                        Impulse.output.connect(0,LineOut.input,1);
                        synth.start();
                        LineOut.start();

                    } else if(wave == 2) {
                        synth.add(Sawtooth = new SawtoothOscillator());
                        synth.add(LineOut = new LineOut());

                        Sawtooth.frequency.set(new PitchRNG(mode).freqGen());
                        Sawtooth.output.connect(0,LineOut.input,0);
                        Sawtooth.output.connect(0,LineOut.input,1);
                        synth.start();
                        LineOut.start();

                    } else if(wave == 3) {
                        synth.add(Triangle = new TriangleOscillator());
                        synth.add(LineOut = new LineOut());

                        Triangle.frequency.set(new PitchRNG(mode).freqGen());
                        Triangle.output.connect(0,LineOut.input,0);
                        Triangle.output.connect(0,LineOut.input,1);
                        synth.start();
                        LineOut.start();
                    }
                } else {
                    Spinner1.setEnabled(true);
                    Spinner2.setEnabled(true);
                    Bar.setEnabled(true);

                    synth.stop();
                    LineOut.stop();
                    timer.cancel();
                    tt.cancel();
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        });
    }



}

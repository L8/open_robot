package com.openrobot.common;

// spiritplumber@gmail.com
//THIS IS CURRENTLY BEING OPTIMIZED

import java.util.Arrays;
import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.SystemClock;

public class AudioSerialOutMono {
       // idea from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html as modified by Steve Pomeroy <steve@staticfree.info>

       private  static Thread audiothread = null;
       private  static AudioTrack audiotrk = null;
       private  static byte generatedSnd[] = null;

       // set that can be edited externally
       public static int max_sampleRate = 48000;
       public static int min_sampleRate = 4000;
       public static int new_baudRate = 4800; // assumes N,8,1 right now
       public static int new_sampleRate = 48000; // min 4000 max 48000
       public static int new_characterdelay = 0; // in audio frames, so depends on the sample rate. Useful to work with some microcontrollers.

       // set that is actually used: this is so they get upadted all in one go (safer)
       private static int baudRate = 4800;
       private static int sampleRate = 48000;
       private static int characterdelay = 0;

       public static LinkedList<byte[]> playque = new LinkedList<byte[]>();
       public static boolean active = false;



       public static void UpdateParameters(boolean AutoSampleRate){
               baudRate = new_baudRate; // we're not forcing standard baud rates here specifically because we want to allow odd ones
               if (AutoSampleRate == true)
               {
                       new_sampleRate = new_baudRate;
                       while(new_sampleRate <= (max_sampleRate-1))
                       {
                               new_sampleRate *=2;//+= new_baudRate;
                       }
                       new_sampleRate/=2;
               }


               if (new_sampleRate > max_sampleRate)
                       new_sampleRate = max_sampleRate;
               if (new_sampleRate < min_sampleRate)
                       new_sampleRate = min_sampleRate;

               sampleRate = new_sampleRate; // min 4000 max 48000
               if (new_characterdelay < 0)
                       new_characterdelay = 0;
               characterdelay = new_characterdelay;
               minbufsize=AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_8BIT);
       }

       public static void output(String sendthis)
       {
               playque.add(SerialDAC(sendthis.getBytes()));
               audiothread.interrupt();
       }
       public static void output(byte[] sendthis)
       {
               playque.add(SerialDAC(sendthis));
               audiothread.interrupt();
       }


       private static int bytesinframe=10+characterdelay;
       private static int i=0; // counter
       private static int j=0; // counter
       private static int k=0; // counter
       private static int m=0; // counter
       private static int n=sampleRate / baudRate;
       private static boolean[] bits;
       private static byte[] waveform;
       private static final byte l=3; // intentional jitter used to prevent the DAC from flattening the waveform prematurely
       private static final byte logichigh = (byte) (-128);
       private static final byte logiclow = (byte)  (64);

       public static byte[] SerialDAC(byte[] sendme)
       {
               bytesinframe=10+characterdelay;
               j=0; // counter
               m=0; // counter
               n=sampleRate / baudRate;
               bits = new boolean[sendme.length*bytesinframe];
               waveform = new byte[(sendme.length*bytesinframe*sampleRate /
baudRate)]; // 8 bit, no parity, 1 stop
               //Arrays.fill(waveform, (byte) 0);
               Arrays.fill(bits, true); // slight opti to decide what to do with stop bits

               // generate bit array first: makes it easier to understand what's going on
               for (i=0;i<sendme.length;++i)
               {
                       m=i*bytesinframe;
                       bits[m]=false;
                       bits[++m]=((sendme[i]&1)==1);//==0)?false:true;
                       bits[++m]=((sendme[i]&2)==2);//==0)?false:true;
                       bits[++m]=((sendme[i]&4)==4);//==0)?false:true;
                       bits[++m]=((sendme[i]&8)==8);//==0)?false:true;
                       bits[++m]=((sendme[i]&16)==16);//==0)?false:true;
                       bits[++m]=((sendme[i]&32)==32);//==0)?false:true;
                       bits[++m]=((sendme[i]&64)==64);//==0)?false:true;
                       bits[++m]=((sendme[i]&128)==128);//==0)?false:true;
                       // cheaper to prefill to true
                       // now we need a stop bit, BUT we want to be able to add more (character delay) to play-nice with some microcontrollers such as the Picaxe or BS1 that need it in order to do decimal conversion natively.
                       //                      for(k=0;k<bytesinframe-9;k++)
                       //                              bits[++m]=true;
               }

                       // now generate the actual waveform using l to wiggle the DAC and prevent it from zeroing out
                       for (i=0;i<bits.length;i++)
                       {
                               for (k=0;k<n;k++)
                               {
                                       waveform[j++]=(bits[i])?((byte) (logichigh+(j&l))):((byte)
(logiclow-(j&l)));
                                       //if (bits[i])
                                       //      waveform[j]=  (byte) (logichigh+(j&l)); // the +l / -l is to fool the DAC into not having a flat waveform, which it might reject
                                       //else
                                       //      waveform[j]=  (byte) (logiclow-(j&l)); // the +l / -l is to fool the DAC into not having a flat waveform, which it might reject
                                       //++j;
                               }
                       }

               bits=null;
               return waveform;
       }

       // essentially a constructor, but i prefer to do a manual call.
       public static void activate() {
               UpdateParameters(true);

               // Use a new tread as this can take a while
               audiothread = new Thread(new Runnable() {
                       public void run() {
                               playSound();
                       }
               }
               );
               audiothread.start();
               while(active == false) // wait for the thread to actually turn on
                       SystemClock.sleep(50);
       }

       public static void deactivate() // allows the audio thread to die
       {
               active=false;
               audiothread.interrupt();
       }

       public static boolean isPlaying()
       {
               try{return audiotrk.getPlaybackHeadPosition() <
(generatedSnd.length);}catch(Exception e){return false;}
       }

       private static int minbufsize;
       private static int length;

       private static void playSound(){
               active = true;
               while(active)
               {
                       try {Thread.sleep(Long.MAX_VALUE);} catch (InterruptedException e) {
                               while (playque.isEmpty() == false)
                               {
                                       if (audiotrk != null)
                                       {
                                               if (generatedSnd != null)
                                               {
                                                       while (audiotrk.getPlaybackHeadPosition() < (generatedSnd.length))
                                                               SystemClock.sleep(50);  // let existing sample finish first: this can probably be set to a smarter number using the information above
                                               }
                                               audiotrk.release();
                                       }
                                       UpdateParameters(false); // might as well do it at every iteration, it's cheap
                                       generatedSnd = playque.poll();
                                       length = generatedSnd.length;
                                       if (minbufsize<length)
                                               minbufsize=length;
                                       audiotrk = new AudioTrack(AudioManager.STREAM_MUSIC,
                                                       sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                                       AudioFormat.ENCODING_PCM_8BIT, minbufsize,
                                                       AudioTrack.MODE_STATIC);
                                       audiotrk.setStereoVolume(2,2);
                                       audiotrk.write(generatedSnd, 0, length);
                                       audiotrk.play();
                               }
                       }
               }
       }
}
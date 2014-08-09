package com.nothing.c.voice;

//��������Ƶ����Ĵ��룺

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


/** 
* Title:        VoiceChat 
* Description: �����Ƶ���������� 
* Copyright:    Copyright (c) 2001 
* Company: 
* @author        
* @version 1.0 
*/


class Playback implements Runnable {

       final int bufSize = 16384; 
       SourceDataLine line; 
       Thread thread; 
       Socket s;
       String cfid;

       Playback(String cfid, Socket s){//������ ȡ��socket�Ի������������ 
    	   this.cfid = cfid;
    	   this.s=s; 
       } 
       public void start() {

           thread = new Thread(this); 
           thread.setName("Playback"); 
           thread.start(); 
       }

       public void stop() { 
           thread = null; 
       }

       public void run() {

           AudioFormat format =new AudioFormat(8000,16,2,true,true);//AudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian�� 
           BufferedInputStream playbackInputStream;

           try { 
             playbackInputStream=new BufferedInputStream(new AudioInputStream(s.getInputStream(),format,2147483647));//��װ����Ƶ�����������������Ǿ���ѹ�������ڴ˼��׽�ѹ�� 
           } 
           catch (IOException ex) { 
               return; 
           }

           DataLine.Info info = new DataLine.Info(SourceDataLine.class,format);

           try { 
               line = (SourceDataLine) AudioSystem.getLine(info); 
               line.open(format, bufSize); 
           } catch (LineUnavailableException ex) { 
               return; 
           }

           byte[] data = new byte[1024];//�˴�����Ĵ�С��ʵʱ�Թ�ϵ���󣬿ɸ���������е��� 
           int numBytesRead = 0; 
           line.start();
           ManageVoiceLine.addVoiceServerLine(cfid, line);

           while (thread != null) { 
              try{ 
                 numBytesRead = playbackInputStream.read(data); 
                 line.write(data, 0,numBytesRead); 
              } catch (IOException e) { 
                   break; 
               } 
           }

           if (thread != null) { 
               line.drain(); 
           }

           line.stop(); 
           line.close(); 
           line = null; 
       } 
}

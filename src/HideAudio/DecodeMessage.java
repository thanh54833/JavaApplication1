/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HideAudio;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import static HideAudio.EncodeMessage.makeAudio;


/**
 *
 * @author thanh
 */
public class DecodeMessage {
    
    public static void main (String [] args) throws FileNotFoundException {
                Scanner scan = new Scanner(System.in);
                int ma = 40000;
 
                System.out.print("--Decode an audio file--");
         
                System.out.println();
 
                        System.out.print("File name that you want to decode: ");
                        String audioToDecode = scan.next();
                        
                        if (!audioToDecode.endsWith(".wav")) 
                                audioToDecode += ".wav";
 
                        System.out.print("Audio file that is the decoder key: ");
                        String audioKey = scan.next();
                      
                        if (!audioKey.endsWith(".wav"))
                                audioKey += ".wav";
 
                        double[] audioEncoded = StdAudio.read(audioToDecode);
                        double[] key = StdAudio.read(audioKey);
                        char [] contents = new char[ma];
                       
 
                        int contentCounter = 0; 
                        for (int i = 0; i < key.length; i++) {
                                if (!((audioEncoded[i] - key[i] == 0))) { 
                                        contents[contentCounter] = (char) (Math.round((float) (10000 * ( audioEncoded[i] - key[i] )))); 
                                        contentCounter++;
                                }
                        }
                       
                        System.out.print("Text file to write the data to : ");
                        String outputName = scan.next();
                        if (!outputName.endsWith(".txt"))
                                outputName += ".txt";
                               
                        
                        BufferedWriter writer = null;
                        try {
                                writer = new BufferedWriter(new FileWriter(outputName));
                        } catch (IOException e1) {
                                e1.printStackTrace();
                        }
                        try {
                                for (int i = 0; i < contentCounter+1; i++) {
                                         writer.write(contents[i]);
                                }
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                        try {
                                writer.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                
               
                StdAudio.close();
                System.out.println("<<End>>");
 
 
        }
    
       public static double[] getAudioFile(String input) {
                double[] audioFile;
               
                if (input.equals("1")) {
                        audioFile = makeAudio(); 
                        System.out.print("Please enter a name for the generated audio clip : ");
                        Scanner scan = new Scanner(System.in);
                        String name = scan.next();
                        if (!name.endsWith(".wav"))
                                name += ".wav";
                        StdAudio.save(name, audioFile);
                }
                else {
                        if (!input.endsWith(".wav")) 
                                input += ".wav";
 
                        audioFile = StdAudio.read(input); 
                }
               
                return audioFile;
        }
       
 
    
}

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


/**
 *
 * @author thanh
 */
public class EncodeMessage {
    
        public static void main (String [] args) throws FileNotFoundException {
                Scanner scan = new Scanner(System.in);
                int ma = 40000;
                
                System.out.println("--Encode a text file into an audio file--");
               
                char [] contents = new char[ma];          
                System.out.print("File name that you want to encode : ");
                String textToEncode = scan.next(); 
                if (!textToEncode.endsWith(".txt"))  
                                textToEncode += ".txt";        
 
                File file = new File(textToEncode);
                Scanner s = new Scanner(file);
                s.useDelimiter("\\Z");
                if(s.hasNext()) {
                contents = s.next().toCharArray();
                }              
                System.out.println("Audio file name that you want the text encoded into :");
                double[] audioFile = getAudioFile(scan.next());
                int audioCounter = 0; 
                for (int i = 0; i < contents.length; i++) {
                                while ((audioFile[i+audioCounter] + (double)(contents[i])/10000.0) >= 1.0) {audioCounter++;}
                                audioFile[i+audioCounter] += ((double)(contents[i]))/10000.0;                          
                }                                              
                System.out.print("Name for the output audio file with the text encoded in it :");
                String outAudio = scan.next();
                if (!outAudio.endsWith(".wav")) 
                          outAudio += ".wav";
                       
                StdAudio.save(outAudio, audioFile);
                scan.close();
                          
                StdAudio.close();
                System.out.println("<<End>>");
        }
 
        
        public static double[] makeAudio () {
                double[] audio = new double[5*44100];
                int i;
                for (i=0; i < audio.length; i++)
                        audio[i] = 2*Math.random() - 1; 
 
                return audio;
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

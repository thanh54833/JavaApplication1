package HideAudio;
import java.applet.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;
 

public final class StdAudio {
 
    /**
     *  The sample rate - 44,100 Hz for CD quality audio.
     */
    public static final int SAMPLE_RATE = 44100;
 
    private static final int BYTES_PER_SAMPLE = 2;                
    private static final int BITS_PER_SAMPLE = 16;             
    private static final double MAX_16_BIT = Short.MAX_VALUE;    
    private static final int SAMPLE_BUFFER_SIZE = 4096;
 
 
    private static SourceDataLine line;  
    private static byte[] buffer;         
    private static int bufferSize = 0;   
 
   
    private StdAudio() { }
 
   
  
    static { init(); }
 
    // open up an audio stream
    private static void init() {
        try {
          
            AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
 
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);
           
            
            buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE/3];
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
 
        
        line.start();
    }
 
 
    
    public static void close() {
        line.drain();
        line.stop();
    }
   
   
    public static void play(double in) {
 
      
        if (in < -1.0) in = -1.0;
        if (in > +1.0) in = +1.0;
 
        short s = (short) (MAX_16_BIT * in);
        buffer[bufferSize++] = (byte) s;
        buffer[bufferSize++] = (byte) (s >> 8);   // little Endian
      
        if (bufferSize >= buffer.length) {
            line.write(buffer, 0, buffer.length);
            bufferSize = 0;
        }
    }
 
   
    public static void play(double[] input) {
        for (int i = 0; i < input.length; i++) {
            play(input[i]);
        }
    }
 
    public static double[] read(String filename) {
        byte[] data = readByte(filename);
        int N = data.length;
        double[] d = new double[N/2];
        for (int i = 0; i < N/2; i++) {
            d[i] = ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / ((double) MAX_16_BIT);
        }
        return d;
    }
 
 
 
 
    public static void play(String filename) {
        URL url = null;
        try {
            File file = new File(filename);
            if (file.canRead()) url = file.toURI().toURL();
        }
        catch (MalformedURLException e) { e.printStackTrace(); }
        // URL url = StdAudio.class.getResource(filename);
        if (url == null) throw new RuntimeException("audio " + filename + " not found");
        AudioClip clip = Applet.newAudioClip(url);
        clip.play();
    }
 
  
    public static void loop(String filename) {
        URL url = null;
        try {
            File file = new File(filename);
            if (file.canRead()) url = file.toURI().toURL();
        }
        catch (MalformedURLException e) { e.printStackTrace(); }
        // URL url = StdAudio.class.getResource(filename);
        if (url == null) throw new RuntimeException("audio " + filename + " not found");
        AudioClip clip = Applet.newAudioClip(url);
        clip.loop();
    }
 
 
   
    private static byte[] readByte(String filename) {
        byte[] data = null;
        AudioInputStream ais = null;
        try {
            // try to read from file
            File file = new File(filename);
            if (file.exists()) {
                ais = AudioSystem.getAudioInputStream(file);
                data = new byte[ais.available()];
                ais.read(data);
            }
 
            // try to read from URL
            else {
                URL url = StdAudio.class.getResource(filename);
                ais = AudioSystem.getAudioInputStream(url);
                data = new byte[ais.available()];
                ais.read(data);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Could not read " + filename);
        }
 
        return data;
    }
 
 
 
  
    public static void save(String filename, double[] input) {
 
        // assumes 44,100 samples per second
        // use 16-bit audio, mono, signed PCM, little Endian
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        byte[] data = new byte[2 * input.length];
        for (int i = 0; i < input.length; i++) {
            int temp = (short) (input[i] * MAX_16_BIT);
            data[2*i + 0] = (byte) temp;
            data[2*i + 1] = (byte) (temp >> 8);
        }
 
        // now save the file
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            AudioInputStream ais = new AudioInputStream(bais, format, input.length);
            if (filename.endsWith(".wav") || filename.endsWith(".WAV")) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
            }
            else if (filename.endsWith(".au") || filename.endsWith(".AU")) {
                AudioSystem.write(ais, AudioFileFormat.Type.AU, new File(filename));
            }
            else {
                throw new RuntimeException("File format not supported: " + filename);
            }
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
 
 
 
 
   /***********************************************************************
    * sample test client
    ***********************************************************************/
 
    // create a note (sine wave) of the given frequency (Hz), for the given
    // duration (seconds) scaled to the given volume (amplitude)
    private static double[] note(double hz, double duration, double amplitude) {
        int N = (int) (StdAudio.SAMPLE_RATE * duration);
        double[] a = new double[N+1];
        for (int i = 0; i <= N; i++)
            a[i] = amplitude * Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
        return a;
    }
 
    /**
     * Test client - play an A major scale to standard audio.
     */
    public static void main(String[] args) {
       
        // 440 Hz for 1 sec
        double freq = 440.0;
        for (int i = 0; i <= StdAudio.SAMPLE_RATE; i++) {
            StdAudio.play(0.5 * Math.sin(2*Math.PI * freq * i / StdAudio.SAMPLE_RATE));
        }
       
        // scale increments
        int[] steps = { 0, 2, 4, 5, 7, 9, 11, 12 };
        for (int i = 0; i < steps.length; i++) {
            double hz = 440.0 * Math.pow(2, steps[i] / 12.0);
            StdAudio.play(note(hz, 1.0, 0.5));
        }
 
 
        // need to call this in non-interactive stuff so the program doesn't terminate
        // until all the sound leaves the speaker.
        StdAudio.close();
 
        // need to terminate a Java program with sound
        System.exit(0);
    }
}

package concurrenrLogger;

import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    public FileWriter fw;
    public Writer(String outputFile)  throws  IOException{
        fw = new FileWriter(outputFile, true);
    }
    public synchronized void write(Object content) {
        try {
            fw.write(content.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

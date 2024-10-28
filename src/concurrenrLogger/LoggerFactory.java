package concurrenrLogger;

import java.io.IOException;

public class LoggerFactory {
    public static String filePath = "log.txt";
    public static Logger logger;
    public static synchronized Logger getLogger() throws IOException {
        if(logger == null) {
            logger = new Logger(filePath);
        }
        return logger;
    }
}

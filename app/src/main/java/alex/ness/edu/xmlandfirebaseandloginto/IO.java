package alex.ness.edu.xmlandfirebaseandloginto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Android2017 on 6/2/2017.
 */

class IO {
    public static String read(InputStream in) throws IOException {
        return read(in, "utf-8");
    }

    public static String read(InputStream in, String charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
        StringBuilder builder = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null){
                builder.append(line);
            }
        }
        finally {
            reader.close();
        }
        return builder.toString();
    }
}

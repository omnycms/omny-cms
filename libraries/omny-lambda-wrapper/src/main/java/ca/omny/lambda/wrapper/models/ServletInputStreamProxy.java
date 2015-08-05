
package ca.omny.lambda.wrapper.models;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletInputStream;

public class ServletInputStreamProxy extends ServletInputStream {

    private final InputStream stream;

    public ServletInputStreamProxy(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public int read() throws IOException {
        return this.stream.read();
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.stream.close();
    }

}

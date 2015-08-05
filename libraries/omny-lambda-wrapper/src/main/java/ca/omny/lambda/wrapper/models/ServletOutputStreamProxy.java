
package ca.omny.lambda.wrapper.models;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

public class ServletOutputStreamProxy extends ServletOutputStream {

    private final OutputStream stream;

    public ServletOutputStreamProxy(OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.stream.close();
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
    }

}

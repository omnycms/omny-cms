/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.omny.lambda.wrapper.models;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author al
 */
public class StringBuilderOutputStream extends OutputStream {
    private StringBuilder sb = new StringBuilder();
    
    @Override
    public void write(int b) throws IOException {
        this.sb.append((char) b );
    }
    
    @Override
    public void write(byte[] b) throws IOException {
        this.write(b,0,b.length);
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for(int i=off; i<len; i++) {
            this.write((int)b[i]);
        }
    }

    @Override
    public String toString(){
        return this.sb.toString();
    }
}

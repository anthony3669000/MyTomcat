package com.melo;

import com.melo.bio.Server;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        new Server().start();
    }
}

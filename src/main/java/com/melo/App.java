package com.melo;

import com.melo.netty.Server;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        new Server().start();
    }
}

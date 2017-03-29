package org.anarres.ipmi.protocol.util;

import java.io.PrintStream;

public class ByteArrayPrinter {
	public static void print(byte[] bytes, PrintStream ps) {
        System.out.println("wireLength = " + bytes.length);
        for(byte b : bytes) {
        	System.out.printf("0x%02x, ", b);
        }
        System.out.println();
	}
}

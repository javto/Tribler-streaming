package com.example.fibonacci;

public class FibLib {
	public static long fibJR(long n) {
		return n <= 0? 0 : n==1? 1 : fibJR(n-1) + fibJR(n-2);
	}
	public native static long javamain(long n, String string);
	
	static {
		System.loadLibrary("com_example_fibonacci_FibLib");
	}
}

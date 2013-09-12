#include "com_example_fibonacci_FibLib.h"
#include "fib.h"

long fibexam(long n){
	return n <= 0? 0 : n==1? 1 : fibexam(n-1) + fibexam(n-2);
}

JNIEXPORT jlong JNICALL Java_com_example_fibonacci_FibLib_javamain
(JNIEnv *env, jclass clazz, jlong argc, jcharArray argv) {
	long n = argc;
	char argv_interface[] = "burp";

	return fibext(n);
	//testmain(argc_interface, &argv_interface);
}

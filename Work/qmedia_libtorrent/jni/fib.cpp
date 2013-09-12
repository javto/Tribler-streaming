#include "fib.h"

long fibext(long n){
	return n <= 0? 0 : n==1? 1 : fibext(n-1) + fibext(n-2);
}

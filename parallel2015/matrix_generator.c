#include<time.h>
#include<stdlib.h>
#include <stdio.h>


int main(int argc, char *argv[]) {
    srand(time(NULL));
    size_t dimensions = atoi(argv[1]);
    int i;
    int j;
    int real = rand();
    int imag = rand();
    printf("%zu\n", dimensions);
    for (i = 0; i < dimensions; ++i) {
        for (j = 0; j < dimensions - 1; ++j) {
            real = rand();
            imag = rand();
            printf("%d %d ", real, imag);
        }
        real = rand();
        imag = rand();
        printf("%d %d\n", real, imag);
    }
}


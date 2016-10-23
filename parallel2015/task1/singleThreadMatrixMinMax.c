#include <stdio.h>
#include <omp.h>
#include <stdlib.h>

struct coord {
    size_t x;
    size_t y;
};

struct complex {
    float im;
    float re;
    struct coord position;
};

float length(struct complex number) {
    return number.im * number.im + number.re * number.re;
}

int main() {
    size_t dimensions;
    scanf("%zu", &dimensions);
    size_t threads = 10;
    struct complex *matrix = calloc(sizeof(struct complex), dimensions * dimensions);
    size_t i;
    size_t j;
    float real;
    float imaginary;
    struct complex temp;
    for (i = 0; i < dimensions; ++i) {
        for (j = 0; j < dimensions; ++j) {
            scanf("%f %f", &temp.re, &temp.im);
            temp.position.x = i;
            temp.position.y = j;
            matrix[i * dimensions + j] = temp;
        }
    }

    struct complex max_complex = matrix[0];
    struct complex min_complex = matrix[0];
    double begin, end;
    begin = omp_get_wtime();
    for (i = 0; i < dimensions * dimensions; ++i) {
        if (length(matrix[i]) > length(max_complex)) {
            max_complex = matrix[i];
        }
        if (length(matrix[i]) < length(min_complex)) {
            min_complex = matrix[i];
        }
    }
    end = omp_get_wtime();



    printf("max complex number %.2f+i*%.2f position x:%zu y:%zu \n", max_complex.re, max_complex.re,
           max_complex.position.x, max_complex.position.y);

    printf("min complex number %.2f+i*%.2f position x:%zu, y:%zu \n", min_complex.re, min_complex.im,
           min_complex.position.x,
           min_complex.position.y);
    printf("execution time: %f\n",end - begin);
    free(matrix);
}


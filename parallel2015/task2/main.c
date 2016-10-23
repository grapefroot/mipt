#include <stdio.h>
#include <omp.h>
#include <stdlib.h>
#include <time.h>

struct coordinates {
    size_t x;
    size_t y;
};

struct complex {
    float re;
    float im;
    struct coordinates position;
};

float length(const struct complex number) {
    return number.re * number.re + number.im * number.im;
}

int main(int argc, char *argv[]) {

    size_t n_threads = 4;
    if(argc == 2) {
        n_threads = (size_t) atoi(argv[1]);
    }
    omp_set_num_threads((int) n_threads);
    size_t dimensions;
    scanf("%zu", &dimensions);
    struct complex **matrix = calloc(sizeof(struct complex *), dimensions);
    size_t i;
    for (i = 0; i < dimensions; ++i) {
        matrix[i] = calloc(sizeof(struct complex), dimensions);
    }

    struct complex temp;
    for (i = 0; i < dimensions; ++i) {
        size_t j;
        for (j = 0; j < dimensions; ++j
                ) {
            scanf("%f %f", &temp.re, &temp.im);
            temp.position.x = i;
            temp.position.y = j;
            matrix[i][j] = temp;
        }
    }


    struct complex max_complex = matrix[0][0];
    struct complex min_complex = matrix[0][0];
    double time_spent;
    struct complex *reduced_min = calloc(sizeof(struct complex), dimensions);
    struct complex *reduced_max = calloc(sizeof(struct complex), dimensions);

    size_t j;
    double begin, end;
    begin = omp_get_wtime();
#pragma omp parallel for schedule(static) private(j)
    for (i = 0; i < dimensions; i++) {
        for (j = 0; j < dimensions; j++) {
            if (length(matrix[i][j]) > length(max_complex)) {
                max_complex = matrix[i][j];
            }

            if (length(matrix[i][j]) < length(min_complex)) {
                min_complex = matrix[i][j];
            }
            reduced_max[i] = max_complex;
            reduced_min[i] = min_complex;
        }
    }
    end = omp_get_wtime();

    for (i = 0; i < dimensions; ++i) {
        if (length(reduced_min[i]) < length(min_complex)) {
            min_complex = reduced_min[i];
        }
    }

    for (i = 0; i < dimensions; ++i) {
        if (length(reduced_max[i]) > length(max_complex)) {
            max_complex = reduced_max[i];
        }
    }

    time_spent = (end - begin);

    printf("max complex number %.2f+i*%.2f position x:%zu y:%zu \n", max_complex.re, max_complex.im,
           max_complex.position.x, max_complex.position.y);

    printf("min complex number %.2f+i*%.2f position x:%zu, y:%zu \n", min_complex.re, min_complex.im,
           min_complex.position.x,
           min_complex.position.y);
    printf("execution time: %f\n", time_spent);

    for (i = 0; i < dimensions; ++i) {
        free(matrix[i]);
    }
    free(matrix);

    return 0;
}
#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>
#include <omp.h>

struct coordinate_point {
    size_t x;
    size_t y;
};

struct complex {
    float re;
    float im;
    struct coordinate_point position;
};


struct row_vector {
    size_t length;
    size_t row_number;
    struct complex *numbers;
    struct complex max_number;
    struct complex min_number;
    size_t first_row;
    size_t last_row;
    struct complex **matrix_ptr;

};

float length(struct complex number) {
    return number.im * number.im + number.re * number.re;
}

static void *thread_func(void *arg) {
    struct row_vector *row = (struct row_vector *) arg;
    size_t i;
    size_t j;
    struct complex max = row->matrix_ptr[row->first_row][0];
    struct complex min = row->matrix_ptr[row->first_row][0];
    for (j = row->first_row; j <= row->last_row; ++j) {
        for (i = 0; i < row->length; ++i) {
            if (length(row->matrix_ptr[j][i]) > length(max)) {
                max = row->matrix_ptr[j][i];
            }
            if (length(row->matrix_ptr[j][i]) < length(min)) {
                min = row->matrix_ptr[j][i];
            }
        }
    }
    row->max_number = max;
    row->min_number = min;
    return (void *) row;
}

int main(int argc, char *argv[]) {
    size_t n_threads = 4;
    if (argc == 2) {
        n_threads = (size_t) atoi(argv[1]);
    }

    size_t dimensions;
    scanf("%zu", &dimensions);
    struct complex **matrix = calloc(sizeof(struct complex *), dimensions);
    size_t i;
    size_t j;
    for (i = 0; i < dimensions; ++i) {
        matrix[i] = calloc(sizeof(struct complex), dimensions);
    }

    struct complex temp;
    for (i = 0; i < dimensions; ++i) {
        for (j = 0; j < dimensions; ++j) {
            scanf("%f %f", &temp.re, &temp.im);
            temp.position.x = i;
            temp.position.y = j;
            matrix[i][j] = temp;
        }
    }

    struct row_vector *temp_args = calloc(sizeof(struct row_vector), n_threads);
    pthread_t* a = calloc(sizeof(pthread_t), n_threads);


    size_t reminder = 0;
    if (dimensions < n_threads) {
        n_threads = dimensions;
    } else {
        reminder = dimensions % n_threads;
    }

    size_t submatrix_size = dimensions / n_threads;
    double begin, end;
    begin = omp_get_wtime();
    for (i = 0; i < n_threads; ++i) {
        temp_args[i].length = dimensions;
        temp_args[i].max_number.position.x = i;
        temp_args[i].max_number.position.y = 0;
        temp_args[i].min_number.position.x = i;
        temp_args[i].min_number.position.y = 0;
        temp_args[i].first_row = i*submatrix_size;
        temp_args[i].matrix_ptr = matrix;
        temp_args[i].last_row = (i + 1)*submatrix_size - 1;
        pthread_create(&(a[i]), NULL, thread_func, (void *) &temp_args[i]);
    }

#ifdef DEBUG
    for(i = 0; i < dimensions; ++i) {
        for(j = 0; j < dimensions; ++j) {
            printf("%.2f+%.2fi ", matrix[i][j].re, matrix[i][j].im);
        }
        printf("\n");
    }
#endif

    struct complex max_complex = matrix[0][0];
    struct complex min_complex = matrix[0][0];

    if (reminder != 0) {
        for (i = dimensions - i - 1; i < dimensions; ++i) {
            for (j = 0; j < dimensions; ++j) {
                if (length(matrix[i][j]) > length(max_complex)) {
                    max_complex = matrix[i][j];
                }

                if (length(matrix[i][j]) < length(min_complex)) {
                    min_complex = matrix[i][j];
                }
            }
        }
    }

    for (i = 0; i < n_threads; ++i) {
        pthread_join(a[i], NULL);
        if (length(temp_args[i].max_number) > length(max_complex)) {
            max_complex = temp_args[i].max_number;
        }
        if (length(temp_args[i].min_number) < length(min_complex)) {
            min_complex = temp_args[i].min_number;
        }
    }
    end = omp_get_wtime();
    free(temp_args);


    printf("max complex number %.2f+i*%.2f position x:%zu y:%zu \n", max_complex.re, max_complex.re,
           max_complex.position.x, max_complex.position.y);

    printf("min complex number %.2f+i*%.2f position x:%zu, y:%zu \n", min_complex.re, min_complex.im,
           min_complex.position.x,
           min_complex.position.y);
    printf("execution time: %f\n", end - begin);

    /*
     clean up: free memory and close file descriptors
     */

    for (i = 0; i < dimensions; ++i) {
        free(matrix[i]);
    }
    free(matrix);
    free(a);

    return 0;
}

#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

struct complex {
    double re;
    double im;
    int x;
    int y;
};


double length(struct complex v1) {
    return v1.re * v1.re + v1.im * v1.im;
}

int main(int argc, char **argv) {
    size_t dimensions;
    size_t i, j;
    scanf("%zu", &dimensions);
    struct complex *matrix = calloc(sizeof(struct complex), dimensions * dimensions);

    struct complex temp;
    for (i = 0; i < dimensions; ++i) {
        for (j = 0; j < dimensions; ++j) {
            scanf("%lf", &temp.re);
            scanf("%lf", &temp.im);
            temp.x = (int) i;
            temp.y = (int) j;
            matrix[i * dimensions + j] = temp;
        }
    }

    int counter, size;
    double begin, end;
    begin = omp_get_wtime();
    MPI_Init(&argc, &argv);
    MPI_Datatype complex_t;
    MPI_Datatype type[4] = {MPI_DOUBLE, MPI_DOUBLE, MPI_INT, MPI_INT};
    int blocklen[4] = {1, 1, 1, 1};
    //*because readability is our main concern*//*
    MPI_Aint disp[4];
    MPI_Type_create_struct(4, blocklen, disp, type, &complex_t);
    MPI_Type_commit(&complex_t);
    MPI_Comm_rank(MPI_COMM_WORLD, &counter);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    printf("%d %d", counter, size);
    struct complex thread_min = matrix[0];
    thread_min.x = counter;
    thread_min.y = 0;
    struct complex thread_max = matrix[0];
    thread_max.x = counter;
    thread_max.y = 0;
    for (i = (size_t) counter; i < dimensions; i += size) {
        for (j = 0; j < dimensions; ++j) {
            if (length(matrix[i * dimensions + j]) < length(thread_min)) {
                thread_min = matrix[i * dimensions + j];
            }
            if (length(matrix[i * dimensions + j]) > length(thread_max)) {
                thread_max = matrix[i * dimensions + j];
            }
        }
    }
    if (counter != 0) {
        MPI_Send(&thread_min, 1, complex_t, 0, 0, MPI_COMM_WORLD);
        MPI_Send(&thread_max, 1, complex_t, 0, 0, MPI_COMM_WORLD);
    }
    if (counter == 0) {
        struct complex min = thread_min;
        struct complex max = thread_max;
        for (i = 1; i < size; ++i) {
            MPI_Recv(&thread_min, 1, complex_t, 0, 0, MPI_COMM_WORLD, NULL);
            MPI_Recv(&thread_max, 1, complex_t, 0, 0, MPI_COMM_WORLD, NULL);
            printf("%.2f+i*%.2f", thread_min.re, thread_min.im);
            printf("%.2f+i*%.2f", thread_max.re, thread_max.im);
            if (length(thread_min) < length(min)) {
                min = thread_min;
            }
            if (length(thread_max) > length(max)) {
                max = thread_max;
            }
        }
        printf("max complex number %.2f+i*%.2f position x:%d y:%d \n", max.re, max.im,
               max.x, max.y);

        printf("min complex number %.2f+i*%.2f position x:%d, y:%d \n", min.re, min.im,
               min.x,
               min.y);
    }
    MPI_Finalize();
    end = omp_get_wtime();
    printf("execution time: %f\n", end - begin);
    free(matrix);
    return 0;
}


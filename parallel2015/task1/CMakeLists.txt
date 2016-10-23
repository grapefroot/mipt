cmake_minimum_required(VERSION 3.2.2)
project(matrix_max)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -pthread -fopenmp")

set(SOURCE_FILES
    main.c)

add_executable(task1 ${SOURCE_FILES})

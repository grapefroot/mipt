cmake_minimum_required(VERSION 3.2)
project(matrix_max_task_2)

set(CMAKE_C_FLAGS "${CMAKE_CXX_FLAGS} -std=c89 -fopenmp -pthread")


set(SOURCE_FILES main.c)
add_executable(task2 ${SOURCE_FILES})


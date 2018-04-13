//
// Created by barco on 2018/3/27.
//

#ifndef CONVARTER_MAPKEY_H
#define CONVARTER_MAPKEY_H

#include <cstdint>

typedef struct {
    int32_t x_div16;
    int32_t z_div16;
    uint32_t dimension;
} mapkey_t;

#endif //CONVARTER_MAPKEY_H

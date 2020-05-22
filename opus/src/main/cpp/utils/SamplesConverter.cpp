//
// Created by Loboda Alexey on 22.05.2020.
//

#include "SamplesConverter.h"
#include "logger.h"

std::vector<uint8_t> SamplesConverter::convert(short **array, int length) {
    std::vector<uint8_t> result;
    if (!array) return result;

    for (int i = 0; i < length; ++i) {
        uint8_t hi = (uint8_t) ((*array)[i] & 0xFF);
        uint8_t low = (uint8_t) (((*array)[i] >> 8) & 0xFF);
        result.push_back(hi);
        result.push_back(low);
    }
    return result;
}

std::vector<short> SamplesConverter::convert(uint8_t **array, int length) {
    std::vector<short> result;
    if (!array) return result;

    for (int i = 0; i < length; ++i) {
        short val = ((*array)[i] | ((i + 1 < length ? (*array)[i + 1] : 0) << 8));
        result.push_back(val);
    }
    return result;
}
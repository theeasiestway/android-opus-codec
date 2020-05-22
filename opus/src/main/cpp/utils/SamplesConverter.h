//
// Created by Loboda Alexey on 22.05.2020.
//

#ifndef OPUS_SAMPLESCONVERTER_H
#define OPUS_SAMPLESCONVERTER_H

#include <cstdint>
#include <vector>

class SamplesConverter {

public:
    static std::vector<uint8_t> convert(short **array, int length);
    static std::vector<short> convert(uint8_t **array, int length);
};

#endif //OPUS_SAMPLESCONVERTER_H
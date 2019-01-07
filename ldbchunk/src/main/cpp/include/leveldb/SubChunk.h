//
// Created by barco on 2018/12/24.
//

#ifndef CONVARTER_SUBCHUNK_H
#define CONVARTER_SUBCHUNK_H

#include <cstdint>
#include <string>
#include "slice.h"

#define DEBUG_SUBCHUNK

class SubChunk {
public:

    virtual ~SubChunk() {};

    virtual uint16_t getBlock(unsigned char x, unsigned char y, unsigned char z) { return 0; };

    virtual void setBlock(unsigned char x, unsigned char y, unsigned char z, uint16_t block) {};

    virtual void setBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer,
                           uint16_t block) {};

    virtual uint16_t
    getBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer) {
        return 0;
    }

    virtual leveldb::Slice save() { return NULL; };
};

class OldSubChunk : public SubChunk {
private:
    unsigned char storage[6144];
public:
    OldSubChunk(std::string buf);

    ~OldSubChunk();

    uint16_t getBlock(unsigned char x, unsigned char y, unsigned char z) override;

    void setBlock(unsigned char x, unsigned char y, unsigned char z, uint16_t block) override;

    leveldb::Slice save() override;
};

struct BlockStorage {
    uint8_t blen;//Length per block
    uint16_t types;//Types of blocks
    uint16_t *palette;//Palette
    uint32_t *storage;//Storage
};

class PalettedSubChunk : public SubChunk {
private:

    static const int32_t msk[15];

    static char pattern_name[10];

    static char pattern_val[6];

    bool hasMultiStorage;

    BlockStorage storages[2];

    const char *loadStorage(const char *ptr, const char *max, int which);

    void generateStorage(uint8_t which, uint16_t initTypes);

    size_t countStorageSize(int which);

    char *saveStorage(char *ptr, const char *max, int which);

    uint16_t getBlockCode(unsigned char x, unsigned char y, unsigned char z, uint8_t which);

    void
    setBlockCode(unsigned char x, unsigned char y, unsigned char z, uint16_t rec, uint8_t which);

public:
    PalettedSubChunk(const std::string &buf, bool hasMultiStorage);

    PalettedSubChunk(uint16_t initTypes, bool hasMultiStorage);

    ~PalettedSubChunk();

    uint16_t getBlock(unsigned char x, unsigned char y, unsigned char z) override;

    void setBlock(unsigned char x, unsigned char y, unsigned char z, uint16_t block) override;

    void setBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer,
                   uint16_t block) override;

    uint16_t
    getBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer) override;

    leveldb::Slice save() override;

};

#endif //CONVARTER_SUBCHUNK_H

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

    virtual unsigned char getTile(unsigned char x, unsigned char y, unsigned char z) { return 0; };

    virtual unsigned char getData(unsigned char x, unsigned char y, unsigned char z) { return 0; };

    virtual void setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id,
                         unsigned char data) {};

    virtual void setData(unsigned char x, unsigned char y, unsigned char z, unsigned char data) {};

    virtual leveldb::Slice save() { return NULL; };
};

class OldSubChunk : public SubChunk {
public:
    OldSubChunk(std::string buf);

    ~OldSubChunk();

    unsigned char getTile(unsigned char x, unsigned char y, unsigned char z) override;

    unsigned char getData(unsigned char x, unsigned char y, unsigned char z) override;

    void setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id,
                 unsigned char data) override;

    void setData(unsigned char x, unsigned char y, unsigned char z, unsigned char data) override;
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

    size_t countStorageSize(int which);

    char *saveStorage(char *ptr, const char *max, int which);

    uint16_t getBlockCode(unsigned char x, unsigned char y, unsigned char z, uint8_t which);

    void
    setBlockCode(unsigned char x, unsigned char y, unsigned char z, uint16_t rec, uint8_t which);

public:
    PalettedSubChunk(const std::string &buf, bool hasMultiStorage);

    PalettedSubChunk(bool hasMultiStorage);

    ~PalettedSubChunk();

    unsigned char getTile(unsigned char x, unsigned char y, unsigned char z) override;

    unsigned char getData(unsigned char x, unsigned char y, unsigned char z) override;

    void setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id,
                 unsigned char data) override;

    void setData(unsigned char x, unsigned char y, unsigned char z, unsigned char data) override;

    leveldb::Slice save() override;

};

#endif //CONVARTER_SUBCHUNK_H

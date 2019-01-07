//
// Created by barco on 2018/3/23.
//

#ifndef CONVARTER_CHUNK_H
#define CONVARTER_CHUNK_H

#include <leveldb/db.h>
#include <vector>
#include <leveldb/SubChunk.h>
#include "mapkey.h"
#include "debug_conf.h"
#include "qstr.h"

//Get or set subchunk versions from flags.
#define GET_SUBCHUNK_VER(x) ((flags[x]>>8)&0xff)
#define SET_SUBCHUNK_VER(x, y) (flags[x]=(flags[x]&0xff00ffff)|(((uint16_t)y)<<8))

//Get or set if modified of a subchunk.
#define IS_SUBCHUNK_MODIFIED(x) (flags[x]&0b1)
#define SET_SUBCHUNK_MODIFIED(x) (flags[x]|=0b1)
#define UNSET_SUBCHUNK_MODIFIED(x) (flags[x]&=0b11111111111111111111111111111110)

class World;

class Chunk {
protected:

    //Key of this chunk, NOT key in database.
    mapkey_t key;

    //World the chunk belongs to.
    World *world;

public:
    //Standard interface as a chunk.

    Chunk(World *world, mapkey_t key);

    virtual ~Chunk() {};

    virtual uint16_t getBlock(unsigned char x, unsigned char y, unsigned char z) { return 0; };

    virtual void setBlock(unsigned char x, unsigned char y, unsigned char z, uint16_t block) {};

    virtual uint16_t
    getBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer) { return 0; };

    virtual void setBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer,
                           uint16_t block) {};

    void generateFlatLayers(qustr *layers);

    void chflat(qustr old, qustr nwe);

    virtual void save() {};
};

//Later to be found out not being used at all.
typedef uint16_t bits;

class PocketChunk : public Chunk {
public:

    PocketChunk(World *world, mapkey_t key);

    ~PocketChunk() {};

};

//Introduced in MCPE 1.x
class BedrockChunk : public Chunk {
private:

    //Constants.

    static const int32_t msk[];

    static const char pattern_name[];

    static const char pattern_val[];

    //Member vars.

    //Flags containing subchunk versions high 8 bits and modified mark low 1 bit.
    uint16_t flags[16];

    //Subchunks.
    SubChunk *subchunks[16];

    //Internal functions.

    ////Init.

    void loadSubchunks(unsigned char top);

    //////Load a subchunk.
    //////Returns if subchunk's loaded or generated.
    bool loadSubchunk(unsigned char which);

public:

    //Ya.

    BedrockChunk(World *world, mapkey_t key);

    ~BedrockChunk();

    //Standard interface as a chunk.

    ////Public Getters.

    //////getBlock.
    uint16_t getBlock(unsigned char x, unsigned char y, unsigned char z) override;

    ////Public Setters.

    //////setBlock(5).
    void setBlock(unsigned char x, unsigned char y, unsigned char z, uint16_t block) override;

    uint16_t
    getBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer) override;

    void setBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer,
                   uint16_t block) override;

    ////Saving and Deinit.

    //////Save entrance, saves subchunks if modified.
    void save() override;

    //Test entrance, useless.
    const char *test();
};

#endif //CONVARTER_CHUNK_H

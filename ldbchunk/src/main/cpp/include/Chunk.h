//
// Created by barco on 2018/3/23.
//

#ifndef CONVARTER_CHUNK_H
#define CONVARTER_CHUNK_H

#include <leveldb/db.h>
#include <vector>
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

class Chunk {
protected:

    //Key of this chunk, NOT key in database.
    mapkey_t key;

    //Database we're using.
    leveldb::DB *database;

    //ReadOptions we're using.
    leveldb::ReadOptions &roptions;

public:
    //Standard interface as a chunk.

    Chunk(leveldb::DB *database, mapkey_t key, leveldb::ReadOptions &readOptions);

    virtual ~Chunk() {};

    virtual unsigned char getTile(unsigned char x, unsigned char y, unsigned char z) { return 0; };

    virtual unsigned char getData(unsigned char x, unsigned char y, unsigned char z) { return 0; };

    //One should always use 5 args setTile instead, but this also works.
    virtual void setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id) {};

    virtual void setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id,
                         unsigned char data) {};

    virtual void setData(unsigned char x, unsigned char y, unsigned char z, unsigned char data) {};

    void generateFlatLayers(qustr *layers);

    virtual void save() {};
};

//Later to be found out not being used at all.
typedef uint16_t bits;

class PocketChunk : public Chunk {
public:

    PocketChunk(leveldb::DB *database, mapkey_t key, leveldb::ReadOptions &readOptions);

    ~PocketChunk() {};

    unsigned char getTile(unsigned char x, unsigned char y, unsigned char z) override;

    unsigned char getData(unsigned char x, unsigned char y, unsigned char z) override;

    void setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id) override;

    void setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id,
                 unsigned char data) override;

    void setData(unsigned char x, unsigned char y, unsigned char z, unsigned char data) override;

    void save() override;

};

//Holds info of a paletted subchunk.
//This and the below structs are only used for a bedrock version chunk along with the union below.
struct PalettedSubChunk {
    uint16_t len_block;
    uint16_t cnt_types;
    uint32_t *sticks;
    uint16_t *palette;
};

//Holds info of an aligned subchunk.
struct AlignedSubChunk {
    unsigned char ids[4096];
    unsigned char datas[2048];
};

union SubChunk {//A subchunk is either paletted or aligned.
    struct PalettedSubChunk *paletted;
    struct AlignedChunk *aligned;
};

//Introduced in MCPE 1.x which's called a Bedrock Edition no longer PE.
class BedrockChunk : public Chunk {
private:

    //Constants.

    static const int32_t msk[];

    static const char pattern_name[];

    static const char pattern_val[];

    //Member vars.

    //The prefix of any subchunks within.
    char karrbase[9];

    //Flags containing subchunk versions high 8 bits and modified mark low 1 bit.
    uint16_t flags[16];

    //Subchunks.
    union SubChunk subchunks[16];

    //Internal functions.

    ////Init.

    void loadSubchunks(unsigned char top);

    //////Load a subchunk.
    //////Returns if subchunk's loaded or generated.
    bool loadSubchunk(unsigned char which);

    //////For paletted format.
    void doLoadPalettedSubchunk(const unsigned char which, const char *buffer);

    //////For aligned format.
    void doLoadAlignedSubchunk(const unsigned char which, const char *buffer);

    ////Getters.

    //////Getter for paletted format, gets both block id and data.
    uint16_t getPaletteRecord(unsigned char x, unsigned char y, unsigned char z);

    ////Setters.

    //////Setter for paletted format, set both block id and data.
    void setPalettedRecord(unsigned char x, unsigned char y, unsigned char z, uint16_t rec);

    ////Saving and Deinit.

    //////Saves a paletted subchunk.
    void savePalettedSubchunk(unsigned char which);

    //////Saves an aligned subchunk.
    void saveAlignedSubchunk(unsigned char which);

public:

    //Ya.

    BedrockChunk(leveldb::DB *database, mapkey_t key, leveldb::ReadOptions &readOptions);

    ~BedrockChunk();

    //Standard interface as a chunk.

    ////Public Getters.

    //////getTile.
    unsigned char getTile(unsigned char x, unsigned char y, unsigned char z) override;

    //////getData.
    unsigned char getData(unsigned char x, unsigned char y, unsigned char z) override;

    ////Public Setters.

    //////setTile(4).
    void setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id) override;


    //////setTile(5).
    void setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id,
                 unsigned char data) override;

    //////setData.
    void setData(unsigned char x, unsigned char y, unsigned char z, unsigned char data) override;

    ////Saving and Deinit.

    //////Save entrance, saves subchunks if modified.
    void save() override;

    //Test entrance, useless.
    const char *test();
};

#endif //CONVARTER_CHUNK_H

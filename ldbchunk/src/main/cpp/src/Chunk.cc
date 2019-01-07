//
// Created by barco on 2018/3/23.
//


#include <leveldb/db.h>
#include "Chunk.h"
#include "mapkey.h"
#include <ldbchunkjni.h>
#include "qstr.h"
//#include <BlockNames.h>
#include <stdlib.h>
#include <mapkey.h>
#include "World.h"

#ifdef LOG_CHUNK_LOADSAVE
#define LOGE_LS(x, ...) LOGE(CAT("BedrockChunk: ", x), ##__VA_ARGS__);
#else
#define LOGE_LS(x, ...)
#endif

#ifdef LOG_CHUNK_OPERATION
#define LOGE_OP(x, ...) LOGE(CAT("BedrockChunk: ", x), ##__VA_ARGS__);
#else
#define LOGE_OP(x, ...)
#endif

//Init constants.

const int32_t BedrockChunk::msk[] = {0b1, 0b11, 0b111, 0b1111, 0b11111, 0b111111, 0b1111111,
                                     0b11111111,
                                     0b111111111, 0b1111111111, 0b11111111111,
                                     0b111111111111,
                                     0b1111111111111, 0b11111111111111, 0b11111111111111};

const char BedrockChunk::pattern_name[] = {0x0a, 0x00, 0x00, 0x08, 0x04, 0x00, 'n', 'a', 'm',
                                           'e'};

const char BedrockChunk::pattern_val[] = {0x02, 0x03, 0x00, 'v', 'a', 'l'};

////////////////////////////////////////////////////////////////////////////////////////////////////
//

Chunk::Chunk(World *world, mapkey_t key) : world(
    world), key(key) {}

void Chunk::generateFlatLayers(qustr *layers) {
    LOGE_LS("Generating layers.");
    unsigned char y = 0;

    //For each layer item.
    for (int i = 0, lim = layers->length; i < lim; i += 3) {
        uint16_t block = layers->str[i];
        block <<= 8;
        block |= layers->str[i + 1];

        //For each y in the layer.
        for (int j = 0, lim2 = layers->str[i + 2]; j < lim2; j++) {

            //For each x and z.
            for (unsigned char x = 0; x < 16; x++) {
                for (unsigned char z = 0; z < 16; z++) {
                    setBlock(x, y, z, block);
                }
            }
            y++;
        }
    }

    LOGE_LS("Generated layers.");
}

bool isSameBlock(uint16_t blk1, uint16_t blk2) {
    if (blk1 == blk2)return true;
    uint16_t id1 = blk1 >> 8;
    uint16_t id2 = blk2 >> 8;
    if (id1 == 2 && id2 == 3)return true;//Dirt und grass
    if (id1 == 3 && id2 == 2)return true;
    if (id1 == 8 && id2 == 9)return true;//Water and water
    if (id1 == 9 && id2 == 8)return true;
    if (id1 == 10 && id2 == 11)return true;//Lava and java
    return id1 == 11 && id2 == 10;
}

void Chunk::chflat(qustr old, qustr nwe) {
    //if (0 == 0)return;
    LOGE_LS("Changing layers.");
    unsigned char y = 0;

    //Unfold layers.

    //First decompress (kind of) && count their sizes.
    unsigned char sold = 0, snew = 0;
    uint16_t iold[128], inew[128];
    for (int i = 0; i < old.length; i += 3) {
        unsigned char am = old.str[i + 2];
        uint16_t id = old.str[i];
        unsigned char data = old.str[i + 1];
        for (int j = 0; j < am; j++) {
            iold[sold + j] = (id << 8) | data;
        }
        sold += am;
    }
    for (int i = 0; i < nwe.length; i += 3) {
        unsigned char am = nwe.str[i + 2];
        uint16_t id = nwe.str[i];
        unsigned char data = nwe.str[i + 1];
        for (int j = 0; j < am; j++) {
            inew[snew + j] = (id << 8) | data;
        }
        snew += am;
    }
//    unsigned char s[nwe.length + 1];
//    for (int i = 0; i < nwe.length; i++) {
//        s[i] = nwe.str[i] + '0';
//    }
//    s[nwe.length] = '\0';
//    LOGE_LS("nwe==%s", s);
//    unsigned char t[old.length + 1];
//    for (int i = 0; i < old.length; i++) {
//        t[i] = old.str[i] + '0';
//    }
//    t[old.length] = '\0';
//    LOGE_LS("old==%s", t);
    //return;

    uint16_t block;
    //For the common part
    unsigned char scnt = (sold < snew) ? sold : snew;
    for (unsigned char i = 0; i < scnt; i++) {
        for (unsigned char x = 0; x < 16; x++) {
            for (unsigned char z = 0; z < 16; z++) {
                block = getBlock(x, i, z);
                if (isSameBlock(block, iold[i])) {
                    setBlock(x, i, z, inew[i]);
                } //else
            }
        }
    }

    //If old layers higher, have their head cut.
    for (unsigned char i = scnt; i < sold; i++) {
        for (unsigned char x = 0; x < 16; x++) {
            for (unsigned char z = 0; z < 16; z++) {
                block = getBlock(x, i, z);
                if (isSameBlock(block, iold[i])) {
                    setBlock(x, i, z, 0);
                }
            }
        }
    }

    //If new layers higher, replace only airs.
    for (unsigned char i = scnt; i < snew; i++) {
        for (unsigned char x = 0; x < 16; x++) {
            for (unsigned char z = 0; z < 16; z++) {
                block = getBlock(x, i, z);
                if (block == 0) {
                    setBlock(x, i, z, inew[i]);
                }
            }
        }
    }
}


////////////////////////////////////////////////////////////////////////////////////////////////////
//

PocketChunk::PocketChunk(World *world, mapkey_t key)
    : Chunk(world, key) {
    LOGE("ERROR: Tell rbq2012 to implement support for Pocket Chunks.");
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//

////////
//Inits

BedrockChunk::BedrockChunk(World *world, mapkey_t key)
    : Chunk(world, key) {
    std::string str;
    memset(subchunks, 0, sizeof(subchunks));
    memset(flags, 0, sizeof(flags));
}

bool BedrockChunk::loadSubchunk(unsigned char which) {
    LOGE_LS("Loading subchunk %d", which)
    LDBKEY_SUBCHUNK(this->key, which)
    leveldb::Slice slice(key, 0 == this->key.dimension ? 10 : 14);
    std::string val;
    bool hit = world->readFromDb(slice, &val);
    if (hit) {//Found, detect version.
        switch (val[0]) {
            case 0://Aligned subchunk.
            case 2://All
            case 3://The
            case 4://Same
            case 5://Really
            case 6://Wierd
            case 7://Ahhh
                SET_SUBCHUNK_VER(which, 0);
                //doLoadAlignedSubchunk(which, value);
                break;
            case 8://Current paletted format
                SET_SUBCHUNK_VER(which, 8);
                subchunks[which] = new PalettedSubChunk(val, true);
                break;
            case 1: //Previous paletted version
                SET_SUBCHUNK_VER(which, 1);
                subchunks[which] = new PalettedSubChunk(val, false);
                //doLoadPalettedSubchunk(which, value);
                break;
            default:
                //Unsupported format.
                break;
        }
        return true;
    } else {
        subchunks[which] = new PalettedSubChunk(1, true);
        SET_SUBCHUNK_VER(which, 8);
        return false;
    }
}

void BedrockChunk::loadSubchunks(unsigned char top) {
    //"If we gonna create a subchunk all subchunks below have to exist."
    //THIS IS RUMOR!!!!!!
    //THIS IS RUMOR!!!!!!
    //THIS IS RUMOR!!!!!!
    //Your Lord the Holy Cat hath been lied to!
    //0000000000000000010000002d
    //0000000000000000010000002f00
    //0000000000000000010000002f01
    //0000000000000000010000002f05
    //0000000000000000010000002f06
    //0000000000000000010000002f07
    //00000000000000000100000036
    //00000000000000000100000076
    //Dump a map to see.
    //Leave this for people to laugh at.
    if (loadSubchunk(top))return;
    SET_SUBCHUNK_MODIFIED(top);
    //for (unsigned char i = static_cast<unsigned char>(top - 1); i != 0xff; i--) {
    //if (GET_SUBCHUNK_VER(i) != 0)break;//Means all those below or equal to i exists.
    //if (!this->loadSubchunk(i))SET_SUBCHUNK_MODIFIED(i);//Generated subchunks shall be saved.
    //}
}

////////
//Get & set

uint16_t BedrockChunk::getBlock(unsigned char x, unsigned char y, unsigned char z) {
    unsigned char sub = y >> 4;
    if (GET_SUBCHUNK_VER(sub) == 0)loadSubchunks(sub);
    return subchunks[sub]->getBlock(x, static_cast<unsigned char>(y & 0xf), z);
}

void BedrockChunk::setBlock(unsigned char x, unsigned char y, unsigned char z, uint16_t block) {
    unsigned char sub = y >> 4;
    if (GET_SUBCHUNK_VER(sub) == 0)loadSubchunks(sub);
    SET_SUBCHUNK_MODIFIED(sub);
    subchunks[sub]->setBlock(x, static_cast<unsigned char>(y & 0xf), z, block);
}

uint16_t
BedrockChunk::getBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer) {
    unsigned char sub = y >> 4;
    if (GET_SUBCHUNK_VER(sub) == 0)loadSubchunks(sub);
    return subchunks[sub]->getBlock3(x, static_cast<unsigned char>(y & 0xf), z, layer);
}

void BedrockChunk::setBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer,
                             uint16_t block) {
    unsigned char sub = y >> 4;
    if (GET_SUBCHUNK_VER(sub) == 0)loadSubchunks(sub);
    SET_SUBCHUNK_MODIFIED(sub);
    subchunks[sub]->setBlock3(x, static_cast<unsigned char>(y & 0xf), z, layer, block);
}

////////
//Save & Deinit

void BedrockChunk::save() {
    for (char i = 0; i < 16; i++) {
        if (IS_SUBCHUNK_MODIFIED(i) == 1) {
            LOGE_LS("Saving subchunk %d", i)
            leveldb::Slice val = subchunks[i]->save();
            LDBKEY_SUBCHUNK(this->key, i)

            //Save it.
            world->writeToDb(leveldb::Slice(key, 0 == this->key.dimension ? 10 : 14), val);
            delete[] val.data();
            UNSET_SUBCHUNK_MODIFIED(i);
        }
    }
}

BedrockChunk::~BedrockChunk() {
    for (int i = 0; i < 16; i++) {
        delete subchunks[i];
    }
}

////////
//Test
const char *BedrockChunk::test() {
    return "";
}

//
////////////////////////////////////////////////////////////////////////////////////////////////////

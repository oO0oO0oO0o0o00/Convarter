//
// Created by barco on 2018/12/26.
//

#include <string>
#include <SubChunk.h>
#include <stdlib.h>
#include <string.h>
#include <qstr.h>
#include <BlockNames.h>
#include <Chunk.h>

////////////////
//

OldSubChunk::OldSubChunk(std::string buf) {
#ifdef DEBUG_SUBCHUNK
    if (buf.length() < 6145) {//(16*16*16*1.5+1)
        //
    }
#endif
    const char *ptr = buf.c_str();
    ptr++;
    memcpy(storage, ptr, 6144);
}

OldSubChunk::~OldSubChunk() {
}

#define OSUB_INDEX\
    unsigned int index=x;\
    index<<=4;\
    index|=z;\
    index<<=4;\
    index|=y;

uint16_t OldSubChunk::getBlock(unsigned char x, unsigned char y, unsigned char z) {
    OSUB_INDEX
    uint16_t ret = storage[index];
    ret <<= 8;
    uint16_t data = storage[4096 + (index >> 1)];
    if (index & 1)data &= 0x0f;
    else data >>= 4;
    ret |= data;
    return ret;
}

void OldSubChunk::setBlock(unsigned char x, unsigned char y, unsigned char z, uint16_t block) {
    OSUB_INDEX
    uint16_t t = block >> 8;
    storage[index] = static_cast<unsigned char>(t);
    unsigned char ch = storage[4096 + (index >> 1)];
    unsigned char ci = static_cast<unsigned char>(block & 0x0f);
    if (index & 1) {
        ch &= 0xf0;
    } else {
        ch &= 0x0f;
        ci <<= 4;
    }
    ch |= ci;
    storage[4096 + (index >> 1)] = ch;
}

leveldb::Slice OldSubChunk::save() {
    char *buf = new char[6145];
    buf[0] = 0;
    memcpy(buf + 1, storage, 6144);
    return leveldb::Slice(buf, 6145);
}

////////////////
//

char PalettedSubChunk::pattern_name[] = {0x0a, 0x00, 0x00, 0x08, 0x04, 0x00, 'n', 'a', 'm',
                                         'e'};
char PalettedSubChunk::pattern_val[] = {0x02, 0x03, 0x00, 'v', 'a', 'l'};

const int32_t PalettedSubChunk::msk[] = {0b1, 0b11, 0b111, 0b1111, 0b11111, 0b111111, 0b1111111,
                                         0b11111111,
                                         0b111111111, 0b1111111111, 0b11111111111,
                                         0b111111111111,
                                         0b1111111111111, 0b11111111111111, 0b11111111111111};

PalettedSubChunk::PalettedSubChunk(const std::string &buf, bool hasMultiStorage) {

    this->hasMultiStorage = hasMultiStorage;
    storages[0].storage = nullptr;
    storages[0].palette = nullptr;
    storages[1].storage = nullptr;
    storages[1].palette = nullptr;

    const char *cbuf = buf.c_str();
    const char *ptr = cbuf + 1;
    if (hasMultiStorage) {
        unsigned char count = *((const unsigned char *) ptr);
#ifdef DEBUG_SUBCHUNK
        if (count > 2 || count == 0) {
            //
        }
#endif
        ptr++;
        ptr = loadStorage(ptr, cbuf + buf.length(), 0);
#ifdef DEBUG_SUBCHUNK
        if (count > 1) {
            loadStorage(ptr, cbuf + buf.length(), 1);
        }
#endif
    } else {
        loadStorage(ptr, cbuf + buf.length(), 0);
    }
}

PalettedSubChunk::PalettedSubChunk(uint16_t initTypes, bool hasMultiStorage) {
    this->hasMultiStorage = hasMultiStorage;
    storages[1].storage = nullptr;
    storages[1].palette = nullptr;
    generateStorage(0, initTypes);
}

PalettedSubChunk::~PalettedSubChunk() {
    delete[] storages[0].storage;
    delete[] storages[0].palette;
    delete[] storages[1].storage;
    delete[] storages[1].palette;
}

const char *PalettedSubChunk::loadStorage(const char *ptr, const char *max, int which) {

#ifdef DEBUG_SUBCHUNK
    //lowest bit should be 0.
    if (((*ptr) & 1) != 0 || max - ptr < 2) {
        //
    }
#endif

    //Length of each block, in bits.
    storages[which].blen = static_cast<uint8_t>(*ptr >> 1);

    ptr++;

    //How many uint32s do it need to store all blocks?
    div_t res = div(4096, (32 / storages[which].blen));
    size_t bufsize = static_cast<size_t>(res.quot);
    if (res.rem != 0)bufsize++;

#ifdef DEBUG_SUBCHUNK
    if (max - ptr < static_cast<int>((bufsize << 2))) {
        //
    }
#endif

    //Copy 'em up. bufsize is 4-bytes long and memcpy requires count of bytes so x4.
    storages[which].storage = new uint32_t[bufsize];
    memcpy(storages[which].storage, ptr, bufsize << 2);

    //Move the pointer to the end of uint32s.
    ptr += bufsize << 2;

    //Here records how many types of blocks are in this subchunk.
    storages[which].types = *(uint16_t *) ptr;
    ptr += 4;

    storages[which].palette = new uint16_t[storages[which].types];

    for (uint16_t i = 0; i < storages[which].types; i++) {
#ifdef  DEBUG_SUBCHUNK
        if (max - ptr < static_cast<int>(sizeof(pattern_name))) {
            //
        }
        if (memcmp(ptr, pattern_name, sizeof(pattern_name)) != 0) {
            //Something has gone wrong.
        }
#endif
        ptr += sizeof(pattern_name);
        qstr name;
#ifdef  DEBUG_SUBCHUNK
        if (max - ptr < 2) {
            //
        }
#endif
        name.length = *(uint16_t *) ptr;
        ptr += 2;
#ifdef DEBUG_SUBCHUNK
        if (max - ptr < static_cast<int>(name.length)) {
            //
        }
#endif
        name.str = new char[name.length];
        memcpy(name.str, ptr, name.length);
        ptr += name.length;
#ifdef DEBUG_SUBCHUNK
        if (max - ptr < static_cast<int>(sizeof(pattern_val)) + 3) {
            //
        }
        if (memcmp(ptr, pattern_val, sizeof(pattern_val)) != 0) {
            //
        }
#endif
        ptr += sizeof(pattern_val);
        storages[which].palette[i] = BlockNames::resolve(name);
        delete[] name.str;
        storages[which].palette[i] <<= 8;
        storages[which].palette[i] |= *ptr;
        ptr += 3;
    }
    return ptr;
}

void PalettedSubChunk::generateStorage(uint8_t which, uint16_t initTypes) {
    BlockStorage &thiz = storages[which];
    uint8_t initLen = 1;
    if (initTypes < 1)initTypes = 1;
#ifdef DEBUG_SUBCHUNK
    if (initTypes > 128) {
        //
    }
#endif
    for (uint16_t tmp = 2; tmp < initTypes; tmp <<= 1) {
        initLen++;
    }
    thiz.types = initTypes;
    thiz.blen = initLen;
    div_t res = div(4096, 32 / initLen);
    thiz.storage = new uint32_t[res.quot + (res.rem ? 1 : 0)]{0};
    thiz.palette = new uint16_t[initTypes]{0};
}

size_t PalettedSubChunk::countStorageSize(int which) {
    BlockStorage &thiz = storages[which];
    if (thiz.storage == nullptr)return 0;
    size_t size = 5;//1x blen + 4x palette items count
    div_t res = div(4096, 32 / thiz.blen);
    size += res.quot << 2;
    if (res.rem != 0)size += 4;
    for (int i = 0; i < thiz.types; i++) {
        size += sizeof(pattern_name);
        size += sizeof(pattern_val);
        size += 15;
        //len(len(name.val))  len("minecraft:")  len(val.val)
        //2 +                 10 +                3         = 15
        char *name = BlockNames::get(thiz.palette[i] >> 8);

        //We put non-null flag at name[31] value 0xff and length at name[30].
        //Null blocks (including all customized blox) will become Air...
        //if (name[31] != static_cast<char>(0xff)) name = BlockNames::names[0];
        size += name[30];
    }
    return size;
}

char *PalettedSubChunk::saveStorage(char *ptr, const char *max, int which) {
    BlockStorage &thiz = storages[which];
    if (thiz.storage == nullptr)return ptr;
#ifdef DEBUG_SUBCHUNK
    if (max - ptr < 2) {
        //
    }
#endif
    *ptr = thiz.blen << 1;
    ptr++;
    div_t res = div(4096, 32 / thiz.blen);
    int meow = res.quot;
    if (res.rem != 0)meow++;
    meow = meow << 2;
#ifdef DEBUG_SUBCHUNK
    if (max - ptr < meow) {
        //
    }
#endif
    memcpy(ptr, thiz.storage, static_cast<size_t>(meow));
    ptr += meow;
#ifdef DEBUG_SUBCHUNK
    if (max - ptr < 4) {
        //
    }
#endif
    *((uint16_t *) ptr) = thiz.types;
    ptr += 2;
    *((uint16_t *) ptr) = 0;
    ptr += 2;

    //Copy palette. Complicated!
    for (int i = 0; i < thiz.types; i++) {

        //name.caption
#ifdef DEBUG_SUBCHUNK
        if (max - ptr < static_cast<int>(sizeof(pattern_name))) {
            //
        }
#endif
        memcpy(ptr, pattern_name, sizeof(pattern_name));
        ptr += sizeof(pattern_name);

        //name.val, starts with "minecraft:", we have to add it.

        //Get it from names by global id.
        char *name = BlockNames::get(thiz.palette[i] >> 8);
        //if (name[31] != static_cast<char>(0xff)) name = BlockNames::names[0];

        //Length.
#ifdef DEBUG_SUBCHUNK
        if (max - ptr < name[30] + 12) {
            //
        }
#endif
        *((uint16_t *) ptr) = static_cast<uint16_t>(name[30] + 10);
        ptr += 2;

        //Prefix. Assuming always "minecraft:" by not considering about e.g. "thaumcraft:xxx"
        //Forget about it JE mods would never come to BE.
        memcpy(ptr, "minecraft:", 10);
        ptr += 10;

        //Name itself;
        memcpy(ptr, name, static_cast<size_t>(name[30]));
        ptr += name[30];

        //Value's caption.
#ifdef DEBUG_SUBCHUNK
        if (max - ptr < static_cast<int>(sizeof(pattern_val)) + 3) {
            //
        }
#endif
        memcpy(ptr, pattern_val, sizeof(pattern_val));
        ptr += sizeof(pattern_val);

        //Value's value. 3 bytes long, 1 byte valid...
        ptr[0] = static_cast<char>(thiz.palette[i]);
        ptr[1] = 0;
        ptr[2] = 0;

        ptr += 3;
    }
    return ptr;
}

uint16_t
PalettedSubChunk::getBlockCode(unsigned char x, unsigned char y, unsigned char z, uint8_t which) {

    //If there's only one storage than getBlockCode or other storages can just return 0.
    if (which == 0 && storages[which].storage == nullptr)return 0;

    BlockStorage &thiz = storages[which];

    //Get the index among all blocks.
    int index = x;
    index <<= 4;
    index |= z;
    index <<= 4;
    index |= y;

    //How many blox can each stick hold.
    int capa = (32 / thiz.blen);

    //Stick that hold this block.
    uint32_t stick = *(thiz.storage + (index / capa));

    //The bits for this block is index in palette.
    //No need care about endian but not very efficiency, i guess?
    uint32_t ind = (stick >> (index % capa * thiz.blen)) & msk[thiz.blen - 1];

    //Return the record.
    return thiz.palette[ind];
}

void
PalettedSubChunk::setBlockCode(unsigned char x, unsigned char y, unsigned char z, uint16_t rec,
                               uint8_t which) {

#ifdef DEBUG_SUBCHUNK
    bool restarted = false;
#endif

    BlockStorage &thiz = storages[which];

    //Non-zero storage may not exist, generate then.
    if (which != 0 && thiz.storage == nullptr)generateStorage(which, 2);

    //On some cases we restart.
    restart:

    //Do we already have it in palette?
    for (int i = 0; i < thiz.types; i++) {
        if (thiz.palette[i] == rec) {
            //Just set it.

            //Capacity per stick
            int capa = 32 / thiz.blen;

            //Index of record
            int index = x;
            index <<= 4;
            index |= z;
            index <<= 4;
            index |= y;

            uint32_t *ptr = thiz.storage + (index / capa);

            int shift = index % capa * thiz.blen;
            *ptr &= ~(msk[thiz.blen - 1] << shift);
            *ptr |= i << shift;
            return;
            //That's all.
        }
    }

#ifdef DEBUG_SUBCHUNK
    if (restarted) {
        //Shouldn't reach here!
    }
#endif

    //Extend the palette.
    uint16_t *paletteOld = thiz.palette;
    thiz.palette = new uint16_t[thiz.types + 1];
    //amount of chars = amount of shorts << 1.
    memcpy(thiz.palette, paletteOld, thiz.types << 1);
    delete[] paletteOld;
    thiz.palette[thiz.types] = rec;
    thiz.types++;

    //If codec capacity is full (2,4,8,16...) we have to rebuild a larger one.
    if ((((uint16_t) 1) << thiz.blen) == thiz.types - 1) {
        //Sadly we need to expand whole subchunk...
        uint32_t *storageOld = thiz.storage;
        uint8_t blenOld = thiz.blen;
        thiz.blen = blenOld + (uint8_t) 1;
        int capa_new = 32 / thiz.blen;
        int capa_old = 32 / blenOld;
        div_t res = div(4096, capa_new);
        int sticks = res.quot;
        if (res.rem != 0)sticks++;
        thiz.storage = new uint32_t[sticks];
        //TODO remove this line
        memset(thiz.storage, 0, static_cast<size_t>(sticks << 2));

        for (int i = 0, hold = 0, hnew = 0, mold = 0, mnew = 0; i < 4096; i++) {
            uint32_t idold =
                (storageOld[hold] >> (mold * blenOld)) & msk[blenOld - 1];
            idold <<= mnew * thiz.blen;
            thiz.storage[hnew] |= idold;
            mold++;
            mnew++;
            if (mold == capa_old) {
                mold = 0;
                hold++;
            }
            if (mnew == capa_new) {
                mnew = 0;
                hnew++;
            }
        }
        delete[] storageOld;
    }

    //Just restart the function.
#ifdef DEBUG_SUBCHUNK
    restarted = true;
#endif
    goto restart;
}

uint16_t PalettedSubChunk::getBlock(unsigned char x, unsigned char y, unsigned char z) {
    return getBlockCode(x, y, z, 0);
}

void PalettedSubChunk::setBlock(unsigned char x, unsigned char y, unsigned char z, uint16_t block) {
    setBlockCode(x, y, z, block, 0);
}

void
PalettedSubChunk::setBlock3(unsigned char x, unsigned char y, unsigned char z, unsigned char layer,
                            uint16_t block) {
    if (layer != 0 && layer != 1)return;
    setBlockCode(x, y, z, block, layer);
}

uint16_t PalettedSubChunk::getBlock3(unsigned char x, unsigned char y, unsigned char z,
                                     unsigned char layer) {
    if (layer != 0 && layer != 1)return 0;
    return getBlockCode(x, y, z, layer);
}

leveldb::Slice PalettedSubChunk::save() {
    size_t size;
    char *buf, *ptr, *max;
    size = hasMultiStorage ?
           2 + countStorageSize(0) + countStorageSize(1) : 1 + countStorageSize(0);
    buf = new char[size];
    ptr = buf;
    max = buf + size;
    *ptr = static_cast<char>(hasMultiStorage ? 0x8 : 0x1);
    ptr++;
    if (hasMultiStorage) {
        *ptr = static_cast<char>((storages[1].storage == nullptr) ? 1 : 2);
        ptr++;
        ptr = saveStorage(ptr, max, 0);
        saveStorage(ptr, max, 1);
    } else {
        saveStorage(ptr, max, 0);
    }
    return leveldb::Slice(buf, size);
}

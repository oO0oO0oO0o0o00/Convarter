//
// Created by barco on 2018/3/23.
//


#include <leveldb/db.h>
#include <Chunk.h>
#include <mapkey.h>
#include <ldbchunkjni.h>
#include <qstr.h>
#include <BlockNames.h>
#include <stdlib.h>

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

Chunk::Chunk(leveldb::DB *database, mapkey_t key, leveldb::ReadOptions &readOptions)
    : database(database), key(key), roptions(readOptions) {
}

void Chunk::generateFlatLayers(qustr *layers) {
    LOGE_LS("Generating layers.");
    unsigned char y = 0;

    //For each layer item.
    for (int i = 0, lim = layers->length; i < lim; i += 3) {
        unsigned char id = layers->str[i];
        unsigned char data = layers->str[i + 1];

        //For each y in the layer.
        for (int j = 0, lim2 = layers->str[i + 2]; j < lim2; j++) {

            //For each x and z.
            for (unsigned char x = 0; x < 16; x++) {
                for (unsigned char z = 0; z < 16; z++) {
                    setTile(x, y, z, id, data);
                }
            }
            y++;
        }
    }

    LOGE_LS("Generated layers.");
}

bool isSameBlock(unsigned char id1, unsigned char data1, unsigned char id2, unsigned char data2) {
    if (id1 == id2 && data1 == data2)return true;
    if (id1 == 2 && id2 == 3)return true;//Dirt und grass
    if (id1 == 3 && id2 == 2)return true;
    if (id1 == 8 && id2 == 9)return true;//Water and water
    if (id1 == 9 && id2 == 8)return true;
    if (id1 == 10 && id2 == 11)return true;//Lava and java
    if (id1 == 11 && id2 == 10)return true;
    return false;
}

void Chunk::chflat(qustr old, qustr nwe) {
    //if (0 == 0)return;
    LOGE_LS("Changing layers.");
    unsigned char y = 0;

    //Unfold layers.

    //First decompress (kind of) && count their sizes.
    unsigned char sold = 0, snew = 0;
    unsigned char iold[128], inew[128], dold[128], dnew[128];
    for (int i = 0; i < old.length; i += 3) {
        unsigned char am = old.str[i + 2];
        unsigned char id = old.str[i];
        unsigned char data = old.str[i + 1];
        for (int j = 0; j < am; j++) {
            iold[sold + j] = id;
            dold[sold + j] = data;
        }
        sold += am;
    }
    for (int i = 0; i < nwe.length; i += 3) {
        unsigned char am = nwe.str[i + 2];
        unsigned char id = nwe.str[i];
        unsigned char data = nwe.str[i + 1];
        for (int j = 0; j < am; j++) {
            inew[snew + j] = id;
            dnew[snew + j] = data;
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

    //For the common part
    //LOGE_LS("asdf: %d,%d", sold, snew);
    unsigned char scnt = (sold < snew) ? sold : snew;
    for (unsigned char i = 0; i < scnt; i++) {
        for (unsigned char x = 0; x < 16; x++) {
            for (unsigned char z = 0; z < 16; z++) {
                unsigned char id = getTile(x, i, z);
                if (isSameBlock(id, getData(x, i, z), iold[i], dold[i])) {
                    setTile(x, i, z, inew[i], dnew[i]);
                } //else
                //LOGE_LS("EEE %d,%d,%d,%d,%d", x, i, z, id, iold[i]);
            }
        }
    }

    //If old layers higher, have their head cut.
    for (unsigned char i = scnt; i < sold; i++) {
        for (unsigned char x = 0; x < 16; x++) {
            for (unsigned char z = 0; z < 16; z++) {
                unsigned char id = getTile(x, i, z);
                if (isSameBlock(id, getData(x, i, z), iold[i], dold[i])) {
                    setTile(x, i, z, 0, 0);
                }
            }
        }
    }

    //If new layers higher, replace only airs.
    for (unsigned char i = scnt; i < snew; i++) {
        for (unsigned char x = 0; x < 16; x++) {
            for (unsigned char z = 0; z < 16; z++) {
                unsigned char id = getTile(x, i, z);
                if (id == 0) {
                    setTile(x, i, z, inew[i], dnew[i]);
                }
            }
        }
    }
}


////////////////////////////////////////////////////////////////////////////////////////////////////
//

PocketChunk::PocketChunk(leveldb::DB *database, mapkey_t key, leveldb::ReadOptions &readOptions)
    : Chunk(database, key, readOptions) {
    LOGE("ERROR: Tell rbq2012 to implement support for Pocket Chunks.");
}

unsigned char PocketChunk::getTile(unsigned char x, unsigned char y, unsigned char z) {
    return 0;
}

unsigned char PocketChunk::getData(unsigned char x, unsigned char y, unsigned char z) {
    return 0;
}

void PocketChunk::setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id) {
    //
}

void PocketChunk::setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id,
                          unsigned char data) {
    //
}

void PocketChunk::setData(unsigned char x, unsigned char y, unsigned char z, unsigned char data) {
    //
}

void PocketChunk::save() {
    //
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//

////////
//Inits

BedrockChunk::BedrockChunk(leveldb::DB *database, mapkey_t key, leveldb::ReadOptions &readOptions)
    : Chunk(database, key, readOptions) {
    std::string str;
    *(uint32_t *) karrbase = key.x_div16;
    *(uint32_t *) (karrbase + 4) = key.z_div16;
    karrbase[8] = 47;
    memset(subchunks, 0, sizeof(subchunks));
    memset(flags, 0, sizeof(flags));
}

bool BedrockChunk::loadSubchunk(unsigned char which) {
    LOGE_LS("Loading subchunk %d", which);
    char key[10];
    memcpy(key, karrbase, sizeof(karrbase));
    key[9] = which;
    leveldb::Slice slice(key, 10);
    std::string val;
    leveldb::Status status = database->Get(roptions, slice, &val);
    const char *value = val.c_str();
    if (status.ok()) {//Found, detect version.

        //Asuming it's 1, changing later.
        SET_SUBCHUNK_VER(which, 1);

        switch (value[0]) {
            case 0://Aligned subchunk.
                SET_SUBCHUNK_VER(which, 0);
                doLoadAlignedSubchunk(which, value);
                break;
            case 8://Current paletted format
                SET_SUBCHUNK_VER(which, 8);
                value++;
            case 1: //Previous paletted version
                doLoadPalettedSubchunk(which, value);
                break;
            default:
                //Unsupported format.
                break;
        }
        return true;
    } else if (status.IsNotFound()) {
        PalettedSubChunk *sch = new PalettedSubChunk;
        subchunks[which].paletted = sch;
        sch->len_block = 1;
        sch->cnt_types = 1;
        sch->palette = new uint16_t[1];
        sch->palette[0] = 0;
        sch->sticks = new uint32_t[128];
        memset(sch->sticks, 0, 128 * 4);
        SET_SUBCHUNK_VER(which, 8);
        return false;
    } else {//Db error
        return false;
    }
}

void BedrockChunk::loadSubchunks(unsigned char top) {
    //If we gonna create a subchunk all subchunks below have to exist.
    //Mojang said so.
    if (loadSubchunk(top))return;
    for (unsigned char i = top - 1; i != 0xff; i--) {
        if (GET_SUBCHUNK_VER(i) != 0)break;//Means all those below or equal to i exists.
        if (!this->loadSubchunk(i))SET_SUBCHUNK_MODIFIED(i);//Generated subchunks shall be saved.
    }
}

void BedrockChunk::doLoadPalettedSubchunk(const unsigned char which, const char *buffer) {
    PalettedSubChunk *sch = new PalettedSubChunk{};
    subchunks->paletted = sch;

    //The second byte records length of each block, in bits.
    sch->len_block = buffer[1] >> 1;

    //How many uint32s do it need to store all blocks?
    div_t res = div(4096, (32 / sch->len_block));
    int cnt_sticks = res.quot;
    if (res.rem != 0)cnt_sticks++;

    LOGE_LS("%d sticks there are.", cnt_sticks);

    //Copy 'em up, the source will be cleared after running out of this function.
    sch->sticks = new uint32_t[cnt_sticks];
    memcpy(sch->sticks, buffer + 2, cnt_sticks << 2);

    //Move the pointer to the end of uint32s.
    buffer += (cnt_sticks << 2) + 2;

    //Here records how many types of blocks are in this subchunk.
    //Though Mojang use an 32-bits int, there're at most 256*16 types, 16 bits enough.
    sch->cnt_types = *(uint16_t *) buffer;
    LOGE_LS("blox: %d", sch->cnt_types);
    buffer += 4;

    sch->palette = new uint16_t[sch->cnt_types];

    for (uint16_t i = 0; i < sch->cnt_types; i++) {
        if (memcmp(buffer, pattern_name, sizeof(pattern_name)) != 0) {
            //Something has gone wrong.
        }
        buffer += sizeof(pattern_name);
        qstr name;
        name.length = *(uint16_t *) buffer;
        buffer += 2;
        name.str = new char[name.length];
        memcpy(name.str, buffer, name.length);
        buffer += name.length;
        if (memcmp(buffer, pattern_val, sizeof(pattern_val)) != 0) {
            //
        }
        buffer += sizeof(pattern_val);
        sch->palette[i] = BlockNames::resolve(name);
        sch->palette[i] <<= 8;
        sch->palette[i] |= *buffer;
        buffer += 3;
    }
    subchunks[which].paletted = sch;
}

void BedrockChunk::doLoadAlignedSubchunk(const unsigned char which, const char *buffer) {
    LOGE("ERROR: Tell rbq2012 to implement support for Aligned subchunks!");
}

////////
//Get & set

unsigned char BedrockChunk::getTile(unsigned char x, unsigned char y, unsigned char z) {
    unsigned char sub = y >> 4;
    if (GET_SUBCHUNK_VER(sub) == 0)loadSubchunks(sub);
    switch (GET_SUBCHUNK_VER(sub)) {
        case 0:
            return 0;
        case 1:
        case 8:
        default:
            return getPaletteRecord(x, y, z) >> 8;
    }
}

unsigned char BedrockChunk::getData(unsigned char x, unsigned char y, unsigned char z) {
    unsigned char sub = y >> 4;
    if (GET_SUBCHUNK_VER(sub) == 0)loadSubchunks(sub);
    switch (GET_SUBCHUNK_VER(sub)) {
        case 0:
            return 0;
        case 1:
        case 8:
        default:
            return getPaletteRecord(x, y, z);
    }
}

void BedrockChunk::setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id) {
    setTile(x, y, z, id, 0);
}

void BedrockChunk::setTile(unsigned char x, unsigned char y, unsigned char z, unsigned char id,
                           unsigned char data) {
    unsigned char sub = y >> 4;
    if (GET_SUBCHUNK_VER(sub) == 0)loadSubchunks(sub);
    switch (GET_SUBCHUNK_VER(sub)) {
        case 0:
            return;
        case 1:
        case 8:
        default: {
            uint16_t rec = id;
            rec <<= 8;
            rec |= data;
            setPalettedRecord(x, y, z, rec);
        }
            return;
    }
}

void BedrockChunk::setData(unsigned char x, unsigned char y, unsigned char z, unsigned char data) {
    unsigned char sub = y >> 4;
    if (GET_SUBCHUNK_VER(sub) == 0)loadSubchunks(sub);
    switch (GET_SUBCHUNK_VER(sub)) {
        case 0:
            return;
        case 1:
        case 8:
        default:
            setPalettedRecord(x, y, z, (getPaletteRecord(x, y, z) & ((uint16_t) 0xff00)) | data);
            return;
    }
}

uint16_t BedrockChunk::getPaletteRecord(unsigned char x, unsigned char y, unsigned char z) {

    //Get the structure.
    PalettedSubChunk *sch = subchunks[y >> 4].paletted;

    //Y is relative to subchunk now.
    y &= 0xf;

    //Get the index among all blocks.
    uint16_t index = x;
    index <<= 4;
    index |= z;
    index <<= 4;
    index |= y;

    //How many blox can each stick hold.
    uint16_t capa = (32 / sch->len_block);

    //Stick that hold this block.
    uint32_t *ptr = sch->sticks + (index / capa);
    uint32_t stick = *ptr;

    //The bits for this block is index in palette.
    uint32_t ind = (stick >> (index % capa * sch->len_block)) & msk[sch->len_block - 1];

    //Return the record.
    return sch->palette[ind];
}

void
BedrockChunk::setPalettedRecord(unsigned char x, unsigned char y, unsigned char z, uint16_t rec) {

    //Get the structure and mark it modified.
    int which = y >> 4;
    SET_SUBCHUNK_MODIFIED(which);
    LOGE_OP("set at (%d %d %d)", x, y, z);
    //if (true)return;
    //Y is now relative.
    y &= 0xf;

    //On some cases we restart.
    restart:
    PalettedSubChunk *sch = subchunks[which].paletted;

    //Do we have it in palette?
    for (int i = 0; i < sch->cnt_types; i++) {
        if (sch->palette[i] != rec)continue;
        {
            //Just set it.
            int capa = 32 / sch->len_block;

            uint16_t index = x;
            index <<= 4;
            index |= z;
            index <<= 4;
            index |= y;

            uint32_t h = index / capa;

            int shift = index % capa * sch->len_block;
            sch->sticks[h] &= ~(msk[sch->len_block - 1] << shift);
            sch->sticks[h] |= i << shift;
        }
        //That's all.
        return;
    }

    //Since it's not, see if palette is full (2,4,8,16...).
    uint16_t lim = 1;
    lim <<= sch->len_block;
    if (lim == sch->cnt_types) {
        //Sadly we need to expand whole subchunk...
        PalettedSubChunk *subnew = new PalettedSubChunk;
        //LOGE("Subchunk expanded from %d-bits per block to %d.", sch->len_block, sch->len_block + 1);
        lim >>= 1;
        subnew->cnt_types = sch->cnt_types + 1;
        subnew->len_block = sch->len_block + 1;
        subnew->palette = new uint16_t[subnew->cnt_types];
        memcpy(subnew->palette, sch->palette, sch->cnt_types << 1);
        subnew->palette[sch->cnt_types] = rec;
        int capa_new = 32 / subnew->len_block;
        int capa_old = 32 / sch->len_block;
        div_t res = div(4096, capa_new);
        uint16_t cnt_sticks = res.quot;
        if (res.rem != 0)cnt_sticks++;
        subnew->sticks = new uint32_t[cnt_sticks];
        memset(subnew->sticks, 0, cnt_sticks << 2);

        //Lots of works!! Wondering why Mojang have to use palettes!
        for (int i = 0, hold = 0, hnew = 0, mold = 0, mnew = 0; i < 4096; i++) {
            uint32_t idold =
                (sch->sticks[hold] >> (mold * sch->len_block)) & msk[sch->len_block - 1];
            idold <<= mnew * subnew->len_block;
            subnew->sticks[hnew] |= idold;
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
        delete sch;//Can we?
        subchunks[which].paletted = subnew;
    } else {
        //Then we only have to expand die palette.
        uint16_t *pl = new uint16_t[sch->cnt_types + 1];
        memcpy(pl, sch->palette, sch->cnt_types << 1);
        pl[sch->cnt_types] = rec;
        delete sch->palette;
        sch->palette = pl;
        sch->cnt_types++;
        //LOGE("Palette expanded to %d.", sch->cnt_types);
    }

    //Just restart die function.
    goto restart;
}

////////
//Save & Deinit

void BedrockChunk::save() {
    for (int i = 0; i < 16; i++) {
        if (IS_SUBCHUNK_MODIFIED(i) == 1) {
            LOGE_LS("Saving subchunk %d", i);
            switch (GET_SUBCHUNK_VER(i)) {
                case 4:
                    saveAlignedSubchunk(i);
                    break;
                default:
                    savePalettedSubchunk(i);
            }
            UNSET_SUBCHUNK_MODIFIED(i);
        }
    }
}

void BedrockChunk::savePalettedSubchunk(unsigned char which) {
    //std::string pal;

    //Subchunk we uses.
    PalettedSubChunk *sch = subchunks[which].paletted;

    //This records size.
    //Initial value 6=  1           +1          +4
    //                  subversion  2nd val     int32 blox types count
    int size_all = 6;

    //Subversion 08 01 uses 2 bytes.
    if (GET_SUBCHUNK_VER(which) == 8) size_all++;

    //Capacity of each stick (int32).
    int capa = 32 / sch->len_block;

    //How many sticks are there.
    div_t res = div(4096, capa);
    int cnt_sticks = res.quot;
    if (res.rem != 0)cnt_sticks++;

    //Add size of sticks to counter.
    size_all += cnt_sticks * 4;

    //Count length of palette.
    LOGE_LS("%d palette items will be saved.", sch->cnt_types);
    for (int i = 0; i < sch->cnt_types; i++) {
        size_all += sizeof(pattern_name);
        size_all += sizeof(pattern_val);
        size_all += 15;
        //len(len(name.val))  len("minecraft:")  len(val.val)
        //2 +                 10 +                3         = 15
        char *name = BlockNames::names[sch->palette[i] >> 8];

        //We put non-null flag at name[31] value 0xff and length at name[30].
        if (name[31] != 0xff) name = BlockNames::names[0];
        size_all += name[30];
    }

    LOGE_LS("subsize %d", size_all);

    //"all" points to beginning of buffer, "ptr" points to "current" position.
    char *all = new char[size_all];
    char *ptr = all;

    //For subversion 0x0801.
    if (GET_SUBCHUNK_VER(which) == 8) {
        *ptr = 8;
        ptr++;
    }

    //First 2 bytes, well maybe 2nd and 3rd actually, but whatever.
    ptr[0] = 1;
    ptr[1] = sch->len_block << 1;
    ptr += 2;

    //Sticks are copied directly.
    memcpy(ptr, sch->sticks, cnt_sticks << 2);
    ptr += cnt_sticks << 2;

    //Block types count. We use uint16 while Mojang uses uint32.
    ((uint16_t *) ptr)[0] = sch->cnt_types;
    ((uint16_t *) ptr)[1] = 0;
    ptr += 4;

    //Copy palette. Complicated!
    for (int i = 0; i < sch->cnt_types; i++) {

        //name.caption
        memcpy(ptr, pattern_name, sizeof(pattern_name));
        ptr += sizeof(pattern_name);

        //name.val, starts with "minecraft:", we have to add it.

        //Get it from names by global id.
        char *name = BlockNames::names[sch->palette[i] >> 8];
        if (name[31] != 0xff) name = BlockNames::names[0];

        //Length of it. We use char while Mojang use uint16.
        ptr[0] = name[30] + 10;
        ptr[1] = 0;
        ptr += 2;

        //Prefix.
        memcpy(ptr, "minecraft:", 10);
        ptr += 10;

        //Name itself;
        memcpy(ptr, name, name[30]);
        ptr += name[30];

        //Value's caption.
        memcpy(ptr, pattern_val, sizeof(pattern_val));
        ptr += sizeof(pattern_val);

        //Value's value. 3 bytes long, 1 byte valid...
        ptr[0] = sch->palette[i];
        ptr[1] = 0;
        ptr[2] = 0;

        ptr += 3;
    }

    //Key.
    char key[10];
    memcpy(key, karrbase, 9);
    key[9] = which;

    //Save it.
    database->Put(leveldb::WriteOptions(), leveldb::Slice(key, 10), leveldb::Slice(all, size_all));
}

void BedrockChunk::saveAlignedSubchunk(unsigned char which) {
    //Not implemented yet.
}

BedrockChunk::~BedrockChunk() {

}

////////
//Test
const char *BedrockChunk::test() {
    return "";
}

//
////////////////////////////////////////////////////////////////////////////////////////////////////

//
// Created by barco on 2018/3/22.
//

#include <SavDb.h>
#include <leveldb/filter_policy.h>
#include <leveldb/cache.h>
#include <leveldb/zlib_compressor.h>
#include <qstr.h>
#include <mapkey.h>
#include <ldbchunkjni.h>
#include <BlockNames.h>
#include "leveldb/decompress_allocator.h"

#ifdef LOG_SAVDB_LOADSAVE
#define LOGE_LS(x, ...) LOGE(CAT("SavDb: ", x), ##__VA_ARGS__);
#else
#define LOGE_LS(x, ...)
#endif

#ifdef LOG_SAVDB_OPERATION
#define LOGE_OP(x, ...) LOGE(CAT("SavDb: ", x), ##__VA_ARGS__);
#else
#define LOGE_OP(x, ...)
#endif

#ifdef LOG_SAVDB_LRU
#define LOGE_LR(x, ...) LOGE(CAT("SavDb: ", x), ##__VA_ARGS__);
#else
#define LOGE_LR(x, ...)
#endif

////////////////////////////////////////////////////////////////////////////////////////////////////
//Static stuff

static inline uint16_t *initCrc16() {
    unsigned short Crc;
    unsigned short *table = new unsigned short[256];
    for (int i = 0; i < 256; i++) {
        Crc = i;
        for (int j = 0; j < 8; j++) {
            if (Crc & 0x1)
                Crc = (Crc >> 1) ^ 0xA001;
            else
                Crc >>= 1;
        }
        table[i] = Crc;
    }
    return table;
}

uint16_t *SavDb::crc16table = initCrc16();

uint16_t SavDb::getCrc16(mapkey_t *key) {
    unsigned short Crc = 0x0000;
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key))];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 1)];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 2)];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 3)];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 4)];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 5)];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 6)];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 7)];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 8)];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 9)];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 10)];
    Crc = (Crc >> 8) ^ crc16table[(Crc & 0xFF) ^ *(((unsigned char *) key) + 11)];
    return Crc;
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//

void NullLogger::Logv(const char *, va_list) {}

////////////////////////////////////////////////////////////////////////////////////////////////////
//

SavDb::SavDb(const char *path, char version) {
    leveldb::Options options;
    options.filter_policy = leveldb::NewBloomFilterPolicy(10);
    options.block_cache = leveldb::NewLRUCache(40 * 1024 * 1024);
    options.write_buffer_size = 4 * 1024 * 1024;
    options.info_log = new NullLogger();
    options.compressors[0] = new leveldb::ZlibCompressorRaw(-1);
    options.compressors[1] = new leveldb::ZlibCompressor();
    options.create_if_missing = true;

    readOptions.decompress_allocator = new leveldb::DecompressAllocator();

    status = leveldb::DB::Open(options, path, &database);

    if (!status.ok())return;
    for (int i = 0; i < 65536; i++) {
        chunks[i] = nullptr;
    }
    lru = nullptr;
    mru = nullptr;
    num_chunks = 0;
    num_maxchunks = 128;
    this->version = version;
}

SavDb::~SavDb() {
    flush();
    delete database;
    if (layers.str != nullptr)delete layers.str;
}

////////
//Getters & Setters

byte SavDb::getTile(int32_t x, int32_t y, int32_t z, uint32_t dim) {
    mapkey_t key = TO_MAPKEY(x, z, dim);
    chunk_li *item = getChunkItem(key);
    byte ret = item->val->getTile(x & 0xf, y, z & 0xf);
    makeNewest(item);
    LOGE_OP("getTile at (%d,%d,%d)", x, y, z);
    return ret;
}

void SavDb::setTile(int32_t x, int32_t y, int32_t z, uint32_t dim, byte id, byte data) {
    mapkey_t key = TO_MAPKEY(x, z, dim);
    chunk_li *item = getChunkItem(key);
    item->val->setTile(x & 0xf, y, z & 0xf, id, data);
    makeNewest(item);
    item->flag = CHUNK_LI_DIRTY;
    LOGE_OP("setTile at (%d,%d,%d)", x, y, z);
}

byte SavDb::getData(int32_t x, int32_t y, int32_t z, uint32_t dim) {
    mapkey_t key = TO_MAPKEY(x, z, dim);
    chunk_li *item = getChunkItem(key);
    byte ret = item->val->getData(x & 0xf, y, z & 0xf);
    makeNewest(item);
    LOGE_OP("getData at (%d,%d,%d)", x, y, z);
    return ret;
}

void SavDb::setData(int32_t x, int32_t y, int32_t z, uint32_t dim, byte data) {
    mapkey_t key = TO_MAPKEY(x, z, dim);
    chunk_li *item = getChunkItem(key);
    item->val->setData(x & 0xf, y, z & 0xf, data);
    makeNewest(item);
    item->flag = CHUNK_LI_DIRTY;
    LOGE_OP("setData at (%d,%d,%d)", x, y, z);
}

chunk_li *SavDb::getChunkItem(mapkey_t key) {
    LOGE_OP("Aquiring chunk (%d,%d)", key.x_div16, key.z_div16);
    uint16_t crc = getCrc16(&key);
    for (chunk_li *i = chunks[crc]; i != nullptr; i = i->next) {
        if (memcmp(&key, &i->key, sizeof(mapkey_t)) != 0)continue;
        return i;
    }
    return loadChunk(key);
}

////////
//Load & Save

chunk_li *SavDb::loadChunk(mapkey_t key) {
    if (num_chunks >= num_maxchunks) releaseLeastRecentUsedChunk();
    LOGE_LS("Loading chunk (%d,%d)", key.x_div16, key.z_div16);
    char key_db[9];
    *(int32_t *) key_db = key.x_div16;
    *(int32_t *) (key_db + 4) = key.z_div16;
    key_db[8] = 118;
    std::string str;
    char ver;
    leveldb::Slice skey(key_db, 9);
    status = database->Get(readOptions, skey, &str);
    if (status.ok()) {
        if (str[0] <= 4)ver = VERSION_POCKET;
        else ver = VERSION_BEDROCK;
    } else if (status.IsNotFound()) {
        //We have to generate version and other data for a new chunk;
        ver = version;
        char buf[4]{version, 0, 0, 0};
        database->Put(leveldb::WriteOptions(), skey, leveldb::Slice(buf, 1));
        key_db[8] = 54;
        buf[0] = 2;
        database->Put(leveldb::WriteOptions(), skey, leveldb::Slice(buf, 4));
    } else {
        throw -23301;
    }
    Chunk *chunk;
    switch (ver) {
        case 4:
            chunk = new PocketChunk(database, key, readOptions);
            break;
        case 7:
        default:
            chunk = new BedrockChunk(database, key, readOptions);
    }
    chunk_li *li = new chunk_li;
    li->key = key;
    li->val = chunk;
    if (layers.length != 0 && status.IsNotFound()) {
        chunk->generateFlatLayers(&layers);
        li->flag = CHUNK_LI_DIRTY;
    }
    int crc = getCrc16(&key);
    chunk_li *h = chunks[crc];
    if (h == nullptr)chunks[crc] = li;
    else {
        while (h->next != nullptr)h = h->next;
        h->next = li;
    }
    chunk_lru_li *wrapper = new chunk_lru_li{li, mru, nullptr};
    li->sorter = wrapper;
    if (mru != nullptr)mru->next = wrapper;
    else lru = wrapper;
    mru = wrapper;
    num_chunks++;
    LOGE_LS("Loaded chunk. Now %d in total.", num_chunks);
    return li;
}

void SavDb::save() {
    for (chunk_lru_li *item = lru; item != nullptr; item = item->next) {
        chunk_li *ch = item->item;
        LOGE_LS("Saving chunk (%d,%d)", ch->key.x_div16, ch->key.z_div16);
        if (ch->flag == CHUNK_LI_CLEAN)continue;
        ch->val->save();
        ch->flag = CHUNK_LI_CLEAN;
        LOGE_LS("Saved chunk.");
    }
}

////////
//Others

const char *SavDb::test() {
    //loadChunk(mapkey_t{85, 0, 0});
    for (int i = 0, x = 1360, z = 0; i < 256; i++) {
        char *name = BlockNames::names[i];
        if (name[31] != 0xff)continue;
        setTile(x, 12, z, 0, i, 0);
        z++;
        if (z == 160000) {
            z = 0;
            x++;
        }
    }
    flush();
    return "000";
}

void SavDb::setLayers(unsigned int length, unsigned char *data) {
    layers.length = length;
    layers.str = new unsigned char[length];
    memcpy(layers.str, data, length);
}

void SavDb::releaseLeastRecentUsedChunk() {

    LOGE_LR("Releasing lru chunk.");

    //Save it.
    chunk_li *li = lru->item;
    if (li->flag == CHUNK_LI_DIRTY) {
        LOGE_LS("Saving lru chunk (%d,%d)", li->key.x_div16, li->key.z_div16);
        li->val->save();
        LOGE_LS("Saved chunk.");
    }
#ifdef LOG_SAVDB_LOADSAVE
    else
        LOGE_LS("No need to save lru chunk (%d,%d)", li->key.x_div16, li->key.z_div16);
#endif

    //Delete chunk.
    delete li->val;

    //Delete li.
    uint16_t crc = getCrc16(&li->key);
    for (chunk_li *i = chunks[crc], *prev = nullptr; i != nullptr; i = i->next) {
        if (memcmp(&i->key, &li->key, sizeof(mapkey_t)) == 0) {
            if (prev == nullptr) {
                chunks[crc] = li->next;
            } else {
                prev->next = li->next;
            }
            break;
        }
        prev = i;
    }
    delete li;

    //Delete lru_li.
    chunk_lru_li *next = lru->next;
    delete lru;
    lru = next;
    lru->prev = nullptr;

    //Done.
    num_chunks--;
    LOGE_LR("Done. Now there're %d chunks.", num_chunks);
}

void SavDb::makeNewest(chunk_li *li) {
    chunk_lru_li *wrapper = li->sorter;
    if (mru == wrapper) {
        LOGE_OP("Current chunk already newest.");
        return;
    }
    if (lru == wrapper)lru = wrapper->next;
    if (wrapper->prev != nullptr) wrapper->prev->next = wrapper->next;
    if (wrapper->next != nullptr) wrapper->next->prev = wrapper->prev;
    wrapper->prev = mru;
    mru->next = wrapper;
    wrapper->next = nullptr;
    mru = wrapper;
    LOGE_OP("Chunk made newest.");
}

void SavDb::flush() {
    LOGE_LS("Flushing cached chunks...");
    for (chunk_lru_li *i = lru, *j; i != nullptr; i = j) {
        if (i->item->flag == CHUNK_LI_DIRTY) {
            LOGE_LS("Saving chunk (%d,%d).", i->item->key.x_div16, i->item->key.z_div16);
            i->item->val->save();
            LOGE_LS("Saved chunk.");
        }
        j = i->next;
        delete i->item->val;
        delete i->item;
        delete i;
    }
    lru = nullptr;
    mru = nullptr;
    num_chunks = 0;
    memset(chunks, 0, sizeof(chunks));
    LOGE_LS("Flusing done.");
}

void SavDb::setMaxChunksCount(uint16_t limit) {
    num_maxchunks = limit;
    LOGE_LR("Current limit of cached chunks count is %d.", limit);
}

void SavDb::changeFlatLayers(unsigned int length, unsigned char layers[]) {
    leveldb::Iterator *iter = database->NewIterator(readOptions);
    iter->SeekToFirst();
    qustr qlayers = qustr{length, layers};
    while (iter->Valid()) {
        leveldb::Slice key = iter->key();
        if ((key.size() == 9) && key[8] == 118) {
            const char *kch = key.ToString().c_str();
            mapkey_t mkey = mapkey_t{((int32_t *) kch)[0], ((int32_t *) kch)[1], 0};
            chunk_li *li = loadChunk(mkey);
            li->val->chflat(this->layers, qlayers);
            li->flag = CHUNK_LI_DIRTY;
            //releaseLeastRecentUsedChunk();
        }
        iter->Next();
    }
}

leveldb::Iterator *SavDb::iterator() {
    return database->NewIterator(readOptions);
}

qstr SavDb::getRaw(qstr key) {
    std::string val;
    database->Get(readOptions, leveldb::Slice(key.str, key.length), &val);
    unsigned int vlen = val.length();
    char *buf = new char[vlen];
    memcpy(buf, val.c_str(), vlen);
    qstr a{vlen, buf};
    return a;
}

void SavDb::putRaw(qstr key, qstr value) {
    database->Put(leveldb::WriteOptions(),
                  leveldb::Slice(key.str, key.length),
                  leveldb::Slice(value.str, value.length));
}

//
////////////////////////////////////////////////////////////////////////////////////////////////////

//
// Created by barco on 2018/3/22.
//

#ifndef CONVARTER_SAVDB_H
#define CONVARTER_SAVDB_H

#include "map"
#include <leveldb/db.h>
#include "leveldb/env.h"
#include "Chunk.h"
#include "qstr.h"
#include "mapkey.h"

#define VERSION_POCKET 4
#define VERSION_BEDROCK 7

typedef unsigned char byte;

class NullLogger : public leveldb::Logger {
public:
    void Logv(const char *, va_list) override;
};

#define CHUNK_LI_CLEAN 0b0
#define CHUNK_LI_DIRTY 0b1

#define TO_MAPKEY(x, z, dim) mapkey_t{x >> 4, z >> 4, dim}

struct chunk_li;
struct chunk_lru_li;

struct chunk_li {
    uint32_t flag = CHUNK_LI_CLEAN;
    mapkey_t key;
    Chunk *val;
    chunk_li *next = nullptr;
    chunk_lru_li *sorter;
};

struct chunk_lru_li {
    chunk_li *item;
    chunk_lru_li *prev;
    chunk_lru_li *next;
};

class SavDb {
private:

    //Static Stuff.

    ////What is crc? Google it!
    static uint16_t *crc16table;

    ////Calculate the crc16 value of a certain map key.
    static uint16_t getCrc16(mapkey_t *key);

    char *logstr;

    //Database IO related

    ////Associated leveldb database.
    leveldb::DB *database;

    ////Standard read option.
    leveldb::ReadOptions readOptions;

    ////Saves a previous Status returned by leveldb for test perpose.
    leveldb::Status status;

    //Cache related.

    ////Count of cached chunks.
    uint16_t num_chunks;

    uint16_t num_maxchunks;

    ////Hash array of chunks, linked listed in case of conflicts.
    chunk_li *chunks[65536];

    ////Lru list of chunks, oldest first.
    //I won't tell you here lru stands for least recent used.
    chunk_lru_li *lru;

    ////Tail of lru list.
    chunk_lru_li *mru;

    //Other things.

    ////Flat world layers, null if non-flat. In a format you may not understand.
    qustr layers = {0, nullptr};

    ////Well, version. 4 for Pocket and 7 for Bedrock.
    char version;

    //Internal functions.

    ////Release a chunk when there're too many of.
    void releaseLeastRecentUsedChunk();

    ////Make a chunk the most recent used.
    inline void makeNewest(chunk_li *li);

    ////Load a chunk from database.
    chunk_li *loadChunk(mapkey_t key);

    ////Get a chunk_li from cache, load if necessary.
    chunk_li *getChunkItem(mapkey_t key);

public:

    //You know.

    SavDb(const char *path, unsigned int version = 7);

    ~SavDb();

    //Standard interface Block-Launcher-styled.

    byte getTile(int32_t x, int32_t y, int32_t z, uint32_t dim);

    void setTile(int32_t x, int32_t y, int32_t z, uint32_t dim, byte id);

    void setTile(int32_t x, int32_t y, int32_t z, uint32_t dim, byte id, byte data);

    byte getData(int32_t x, int32_t y, int32_t z, uint32_t dim);

    void setData(int32_t x, int32_t y, int32_t z, uint32_t dim, byte data);

    //Others.

    void setMaxChunksCount(uint16_t limit);

    const char *test();

    ////Save all chunks to disk.
    void save();

    ////Flush all chunks to disk. Cache cleared.
    void flush();

    void setLayers(unsigned int length, unsigned char *data);
};

#endif //CONVARTER_SAVDB_H

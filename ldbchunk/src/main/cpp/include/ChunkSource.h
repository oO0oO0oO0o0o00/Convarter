//
// Created by barco on 2019/1/13.
//

#ifndef CONVARTER_CHUNKSOURCE_H
#define CONVARTER_CHUNKSOURCE_H

#include <leveldb/db.h>
#include "Chunk.h"

class ChunkSource {
private:
    ////Db path
    char *path;

    ////Associated leveldb database.
    leveldb::DB *database;

    ////Standard read option.
    leveldb::ReadOptions readOptions;

    ////Well, version. 4 for Pocket and 7 for Bedrock.
    char storageVersion;

    char subchunkVersion;
public:

    ChunkSource(const char *path, char storageVersion, char subchunkVersion);

    int openDb();

    void closeDb();

    Chunk *getOrCreateChunk(mapkey_t mapkey);

    bool readFromDb(leveldb::Slice key, std::string *value);

    void writeToDb(leveldb::Slice key, leveldb::Slice value);

    leveldb::Iterator *iterator();

    void voidChunk(mapkey_t mapkey);
};

#endif //CONVARTER_CHUNKSOURCE_H

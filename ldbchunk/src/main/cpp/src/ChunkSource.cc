//
// Created by barco on 2019/1/13.
//

#include <Chunk.h>
#include <ChunkSource.h>
#include <leveldb/filter_policy.h>
#include <leveldb/cache.h>
#include <leveldb/zlib_compressor.h>
#include <decompress_allocator.h>
#include <World.h>

ChunkSource::ChunkSource(const char *path, char storageVersion, char subchunkVersion) {
    readOptions.decompress_allocator = new leveldb::DecompressAllocator();
    database = nullptr;
    this->storageVersion = storageVersion;
    this->subchunkVersion = subchunkVersion;
    this->path = new char[strlen(path) + 1];
    strcpy(this->path, path);
}

int ChunkSource::openDb() {
    if (database)return 0;
    leveldb::Options options;
    options.filter_policy = leveldb::NewBloomFilterPolicy(10);
    options.block_cache = leveldb::NewLRUCache(40 * 1024 * 1024);
    options.write_buffer_size = 4 * 1024 * 1024;
    options.info_log = new NullLogger();
    options.compressors[0] = new leveldb::ZlibCompressorRaw(-1);
    options.compressors[1] = new leveldb::ZlibCompressor();
    options.create_if_missing = true;

    leveldb::Status status = leveldb::DB::Open(options, path, &database);

    if (!status.ok())return status.code();

    return 0;
}

void ChunkSource::closeDb() {
    delete database;
}

Chunk *ChunkSource::getOrCreateChunk(mapkey_t mapkey) {
    LDBKEY_VERSION(mapkey)
    std::string str;
    char ver;
    leveldb::Slice skey(key_db, mapkey.dimension == 0 ? 9 : 13);
    leveldb::Status status = database->Get(readOptions, skey, &str);
    if (status.ok()) {
        if (str[0] <= 4)ver = VERSION_POCKET;
        else ver = VERSION_BEDROCK;
    } else if (status.IsNotFound()) {
        //We have to generate version and other data for a new chunk;
        ver = storageVersion;
        char buf[4]{storageVersion, 0, 0, 0};
        database->Put(leveldb::WriteOptions(), skey, leveldb::Slice(buf, 1));
        key_db[mapkey.dimension == 0 ? 8 : 12] = 54;
        buf[0] = 2;
        database->Put(leveldb::WriteOptions(), skey, leveldb::Slice(buf, 4));
    } else {
        throw 233;
    }
    Chunk *chunk;
    switch (ver) {
        case 4:
            chunk = new PocketChunk(this, mapkey);
            break;
        case 7:
        default:
            chunk = new BedrockChunk(this, mapkey);
    }
    return chunk;
}

bool ChunkSource::readFromDb(leveldb::Slice key, std::string *value) {
    leveldb::Status status = database->Get(readOptions, key, value);
    if (status.ok())return true;
    if (status.IsNotFound())return false;
    throw status.ToString();
}

void ChunkSource::writeToDb(leveldb::Slice key, leveldb::Slice value) {
    leveldb::Status status = database->Put(leveldb::WriteOptions(), key, value);
    if (!status.ok())throw status.ToString();
}

leveldb::Iterator *ChunkSource::iterator() {
    return database->NewIterator(readOptions);
}

void ChunkSource::voidChunk(mapkey_t mkey) {
    LDBKEY_VERSION(mkey)
    leveldb::Slice skey(key_db, mkey.dimension == 0 ? 9 : 13);
    char buf[4]{storageVersion, 0, 0, 0};
    database->Put(leveldb::WriteOptions(), skey, leveldb::Slice(buf, 1));
    key_db[mkey.dimension == 0 ? 8 : 12] = 54;
    buf[0] = 2;
    database->Put(leveldb::WriteOptions(), skey, leveldb::Slice(buf, 4));
    LDBKEY_SUBCHUNK(mkey, 0)
    for (unsigned char i = 0; i <= 15; i++) {
        key[mkey.dimension == 0 ? 9 : 13] = i;
        database->Delete(leveldb::WriteOptions(),
                         leveldb::Slice(key, mkey.dimension == 0 ? 10 : 14));
    }
}

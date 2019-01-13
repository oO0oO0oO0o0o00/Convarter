//
// Created by barco on 2018/3/22.
//

#include <jni.h>
#include <ldbchunkjni.h>
#include <qstr.h>
#include <macros.h>
#include <ChunkSource.h>

#define PTR_TO_CHUNK Chunk *chunk = reinterpret_cast<Chunk *>(ptr);

static void
nativeChSetBlock(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z,
                 jint block) {
    PTR_TO_CHUNK
    if (chunk != nullptr)
        chunk->setBlock(static_cast<unsigned char>(x), static_cast<unsigned char>(y),
                        static_cast<unsigned char>(z), static_cast<uint16_t>(block));
}

static jint
nativeChGetBlock(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z) {
    PTR_TO_CHUNK
    if (chunk != nullptr)
        return chunk->getBlock(static_cast<unsigned char>(x), static_cast<unsigned char>(y),
                               static_cast<unsigned char>(z));
    return 0;
}

static void
nativeChSetBlock3(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z,
                  jint layer,
                  jint block) {
    PTR_TO_CHUNK
    if (chunk != nullptr)
        chunk->setBlock3(static_cast<unsigned char>(x), static_cast<unsigned char>(y),
                         static_cast<unsigned char>(z), static_cast<unsigned char>(layer),
                         static_cast<uint16_t>(block));
}

static jint
nativeChGetBlock3(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z,
                  jint layer) {
    PTR_TO_CHUNK
    if (chunk != nullptr)
        return chunk->getBlock3(
            static_cast<unsigned char>(x), static_cast<unsigned char>(y),
            static_cast<unsigned char>(z), static_cast<unsigned char>(layer));
    return 0;
}

static int nativeChSave(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    PTR_TO_CHUNK
    if (chunk != nullptr) return chunk->save();
    return 0;
}

static int nativeChSaveTo(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jlong chsptr) {
    PTR_TO_CHUNK
    ChunkSource *chunkSource = reinterpret_cast<ChunkSource *>(chsptr);
    if (chunk != nullptr && chunkSource != nullptr) return chunk->saveTo(chunkSource);
    return 0;
}

static int nativeChClose(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    PTR_TO_CHUNK
    if (chunk != nullptr) {
        int ret = chunk->save();
        delete chunk;
        return ret;
    }
    return 0;
}

static void nativeChDiscard(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    PTR_TO_CHUNK
    if (chunk != nullptr) delete chunk;
}

static JNINativeMethod sMethods[] =
    {
        {"nativeChSetBlock",  "(JIIII)V",  (void *) nativeChSetBlock},
        {"nativeChGetBlock",  "(JIII)I",   (void *) nativeChGetBlock},
        {"nativeChSetBlock3", "(JIIIII)V", (void *) nativeChSetBlock3},
        {"nativeChGetBlock3", "(JIIII)I",  (void *) nativeChGetBlock3},
        {"nativeChSave",      "(J)I",      (void *) nativeChSave},
        {"nativeChSaveTo",    "(JJ)I",     (void *) nativeChSaveTo},
        {"nativeChClose",     "(J)I",      (void *) nativeChClose},
        {"nativeChDiscard",   "(J)V",      (void *) nativeChDiscard}
    };

int register_rbq2012_ldbchunk_Chunk(JNIEnv *env) {
    jclass clazz = env->FindClass("rbq2012/ldbchunk/Chunk");
    if (!clazz) {
        LOGE("Can't find class rbq2012.ldbchunk.Chunk");
        return 0;
    }

    return env->RegisterNatives(clazz, sMethods, NELEM(sMethods));
}

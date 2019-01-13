//
// Created by barco on 2018/3/22.
//

#include <jni.h>
#include <ldbchunkjni.h>
#include <qstr.h>
#include <macros.h>
#include <ChunkSource.h>

#define PTR_TO_CHUNKSOURCE ChunkSource *source = reinterpret_cast<ChunkSource *>(ptr);

static jlong nativeChBegin(JNIEnv *env, jclass clazz UNUSED, jstring dbpath, jint storageVersion,
                           jint subchunkVersion) {
    const char *path = env->GetStringUTFChars(dbpath, 0);
    ChunkSource *source = new ChunkSource(path, static_cast<char>(storageVersion),
                                          static_cast<char>(subchunkVersion));
    env->ReleaseStringUTFChars(dbpath, path);
    return reinterpret_cast<jlong>(source);
}

static jint nativeChOpenDb(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    PTR_TO_CHUNKSOURCE
    return source->openDb();
}

static void nativeChCloseDb(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    PTR_TO_CHUNKSOURCE
    source->closeDb();
}

static void nativeChEnd(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    PTR_TO_CHUNKSOURCE
    delete source;
}

static jlong
nativeChGetOrCreateChunk(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint xdiv16,
                         jint zdiv16,
                         jint dim) {
    PTR_TO_CHUNKSOURCE
    return reinterpret_cast<jlong>(source->getOrCreateChunk(mapkey_t{xdiv16, zdiv16, dim}));
}

static jbyteArray nativeChGetRaw(JNIEnv *env, jclass clazz UNUSED, jlong ptr, jbyteArray key) {
    PTR_TO_CHUNKSOURCE
    jbyte *buf = env->GetByteArrayElements(key, 0);
    std::string val;
    try {
        bool res = source->readFromDb(
            leveldb::Slice(reinterpret_cast<char *>(buf),
                           static_cast<size_t>(env->GetArrayLength(key))),
            &val);
        env->ReleaseByteArrayElements(key, buf, JNI_ABORT);
        if (res) {
            jbyteArray jba = env->NewByteArray((jsize) val.length());
            env->SetByteArrayRegion(jba, 0, (jsize) val.length(), (jbyte *) val.c_str());
            return jba;
        }
        return nullptr;
    } catch (std::string msg) {
        env->ReleaseByteArrayElements(key, buf, JNI_ABORT);
        env->ThrowNew(env->FindClass("java/io/IOException"), msg.c_str());
        return nullptr;
    }
}

static void
nativeChPutRaw(JNIEnv *env, jclass clazz UNUSED, jlong ptr, jbyteArray key, jbyteArray value) {
    PTR_TO_CHUNKSOURCE
    jbyte *kb, *vb;
    kb = env->GetByteArrayElements(key, 0);
    vb = env->GetByteArrayElements(value, 0);
    try {
        source->writeToDb(
            leveldb::Slice(reinterpret_cast<char *>(kb),
                           static_cast<size_t>(env->GetArrayLength(key))),
            leveldb::Slice(reinterpret_cast<char *>(vb),
                           static_cast<size_t>(env->GetArrayLength(value)))
        );
    } catch (std::string msg) {
        env->ReleaseByteArrayElements(key, kb, JNI_ABORT);
        env->ReleaseByteArrayElements(value, vb, JNI_ABORT);
        env->ThrowNew(env->FindClass("java/io/IOException"), msg.c_str());
        return;
    }
    env->ReleaseByteArrayElements(key, kb, JNI_ABORT);
    env->ReleaseByteArrayElements(value, vb, JNI_ABORT);
}

static jlong nativeChIterator(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    PTR_TO_CHUNKSOURCE
    return reinterpret_cast<jlong>(source->iterator());
}

static void
nativeChVoidChunk(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint xdiv16, jint zdiv16,
                  jint dim) {
    PTR_TO_CHUNKSOURCE
    if (source != nullptr)source->voidChunk(mapkey_t{xdiv16, zdiv16, dim});
}

static JNINativeMethod sMethods[] =
    {
        {"nativeChBegin",            "(Ljava/lang/String;)J", (void *) nativeChBegin},
        {"nativeChOpenDb",           "(J)I",                  (void *) nativeChOpenDb},
        {"nativeChCloseDb",          "(J)V",                  (void *) nativeChCloseDb},
        {"nativeChEnd",              "(J)V",                  (void *) nativeChEnd},
        {"nativeChGetOrCreateChunk", "(JIII)J",               (void *) nativeChGetOrCreateChunk},
        {"nativeChGetRaw",           "(J[B)[B",               (void *) nativeChGetRaw},
        {"nativeChPutRaw",           "(J[B[B)V",              (void *) nativeChPutRaw},
        {"nativeChIterator",         "(J)J",                  (void *) nativeChIterator},
        {"nativeChVoidChunk",        "(JIII)V",               (void *) nativeChVoidChunk}
    };

int register_rbq2012_ldbchunk_ChunkSource(JNIEnv *env) {
    jclass clazz = env->FindClass("rbq2012/ldbchunk/ChunkSource");
    if (!clazz) {
        LOGE("Can't find class rbq2012.ldbchunk.ChunkSource");
        return 0;
    }

    return env->RegisterNatives(clazz, sMethods, NELEM(sMethods));
}

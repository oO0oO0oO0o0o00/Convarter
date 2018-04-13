LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ldbchunkjni
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_CPP_EXTENSION := .cc
LOCAL_CFLAGS := -std=gnu++0x
LOCAL_SRC_FILES := rbq2012_ldbchunk_DB.cc\
                    rbq2012_ldbchunk_Names.cc\
                    ldbchunkjni.cc\
                    sav/SavDb.cc\
                    sav/Chunk.cc\
                    sav/BlockNames.cc
LOCAL_LDLIBS +=  -llog -ldl $(LOCAL_PATH)/../../../../libs/$(TARGET_ARCH_ABI)/libleveldb.so
LOCAL_CPPFLAGS  += -std=c++11
LOCAL_CPP_FEATURES += exceptions

include $(BUILD_SHARED_LIBRARY)

####################################################################################################
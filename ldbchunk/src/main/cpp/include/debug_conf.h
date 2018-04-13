//
// Created by barco on 2018/4/12.
//

#ifndef CONVARTER_DEBUG_CONF_H
#define CONVARTER_DEBUG_CONF_H

//Chunk
//#define LOG_CHUNK_OPEARTIONS
//#define LOG_CHUNK_LOADSAVE

//SavDb
//#define LOG_SAVDB_OPERATIONS
#define LOG_SAVDB_LOADSAVE
#define LOG_SAVDB_LRU

#ifdef LOG_CHUNK_OPEARTIONS
#define LOG_CHUNK_LOADSAVE
#endif

#ifdef LOG_SAVDB_OPERATIONS
#define LOG_SAVDB_LOADSAVE
#define LOG_SAVDB_LRU
#endif

#define CAT(x, y) x y

#endif //CONVARTER_DEBUG_CONF_H

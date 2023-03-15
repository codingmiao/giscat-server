/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.util;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;
import cn.com.enersun.mywebgis.mywebgisservice.common.exception.OtherException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rocksdb.*;
import org.rocksdb.util.SizeUnit;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 文件缓存，基于RocksDB
 *
 * @author liuyu
 * @date 2023/3/7
 */
public class FileCache implements Closeable {


    private static Options buildDefaultRocksDbOptions() {
        Options options = new Options();
        final Filter bloomFilter = new BloomFilter(10);
        final Statistics stats = new Statistics();
        final RateLimiter rateLimiter = new RateLimiter(10000000, 10000, 10);

        options.setCreateIfMissing(true)
                .setStatistics(stats)
                .setWriteBufferSize(8 * SizeUnit.KB)
                .setMaxWriteBufferNumber(3)
                .setMaxBackgroundJobs(10)
                .setCompressionType(CompressionType.SNAPPY_COMPRESSION)
                .setCompactionStyle(CompactionStyle.UNIVERSAL);

        final BlockBasedTableConfig table_options = new BlockBasedTableConfig();
        Cache cache = new LRUCache(512, 6);
        table_options.setBlockCache(cache)
                .setFilterPolicy(bloomFilter)
                .setBlockSizeDeviation(5)
                .setBlockRestartInterval(10)
                .setCacheIndexAndFilterBlocks(true)
                .setBlockCacheCompressed(new LRUCache(512, 10));
        options.setTableFormatConfig(table_options);
        options.setRateLimiter(rateLimiter);
        return options;
    }

    private static void clearDb(RocksDB db) {
        try (WriteOptions writeOpt = new WriteOptions(); WriteBatch batch = new WriteBatch()) {
            try {
                RocksIterator iter = db.newIterator();
                for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                    db.delete(iter.key());
                }
                db.write(writeOpt, batch);
            } catch (RocksDBException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private interface Impl {
        void put(byte[] bytesKey, byte[] bytesValue);

        byte[] get(byte[] bytesKey);

        void remove(byte[] bytesKey);

        void clear();

        void close() throws IOException;
    }

    private final class TimeOutImpl implements Impl {
        private final @NotNull RocksDB timeDb;
        private final @NotNull RocksDB db;
        private final String fileDir;
        private final long cacheTimeOut;

        public TimeOutImpl(String fileDir, @Nullable Options options, long cacheTimeOut) {
            if (cacheTimeOut == 0) {
                throw new ConfigException("cacheTimeOut不能为0");
            }
            this.fileDir = fileDir;
            this.cacheTimeOut = cacheTimeOut;
            if (null == options) {
                options = buildDefaultRocksDbOptions();
            }
            try {
                db = RocksDB.open(options, fileDir + "/value");
                timeDb = RocksDB.open(options, fileDir + "/time");
            } catch (RocksDBException e) {
                throw new OtherException("RocksDB初始化异常", e);
            } finally {
                options.close();
            }
        }

        @Override
        public void put(byte[] bytesKey, byte[] bytesValue) {
            byte[] cacheTimeBytes = long2Bytes(RoughTimeUtil.getTimestamp() + cacheTimeOut);
            try {
                db.put(bytesKey, bytesValue);
                timeDb.put(bytesKey, cacheTimeBytes);
            } catch (RocksDBException e) {
                throwRocksDBException(e);
            }
        }

        @Override
        public byte[] get(byte[] bytesKey) {
            long now = RoughTimeUtil.getTimestamp();
            try {
                byte[] cacheTimeBytes = timeDb.get(bytesKey);
                if (null == cacheTimeBytes) {
                    return null;
                }
                long cacheTime = bytes2Long(cacheTimeBytes);
                if (cacheTime < now) {
                    return null;
                }
                return db.get(bytesKey);
            } catch (RocksDBException e) {
                throwRocksDBException(e);
            }
            return null;
        }

        @Override
        public void remove(byte[] bytesKey) {
            try {
                timeDb.delete(bytesKey);
            } catch (RocksDBException e) {
                throwRocksDBException(e);
            }
        }

        @Override
        public void clear() {
            clearDb(timeDb);
            clearDb(db);
        }

        @Override
        public void close() throws IOException {
            timeDb.close();
            db.close();
        }

        private void throwRocksDBException(RocksDBException e) {
            throw new OtherException("操作RocksDB出错: " + fileDir, e);
        }

    }

    private final class NotTimeOutImpl implements Impl {
        private final @NotNull RocksDB db;
        private final String fileDir;

        public NotTimeOutImpl(String fileDir, @Nullable Options options) {
            this.fileDir = fileDir;
            if (null == options) {
                options = buildDefaultRocksDbOptions();
            }
            try {
                db = RocksDB.open(options, fileDir + "/value");
            } catch (RocksDBException e) {
                throw new OtherException("RocksDB初始化异常", e);
            } finally {
                options.close();
            }
        }

        @Override
        public void put(byte[] bytesKey, byte[] bytesValue) {
            try {
                db.put(bytesKey, bytesValue);
            } catch (RocksDBException e) {
                throwRocksDBException(e);
            }
        }

        @Override
        public byte[] get(byte[] bytesKey) {
            try {
                return db.get(bytesKey);
            } catch (RocksDBException e) {
                throwRocksDBException(e);
            }
            return null;
        }

        @Override
        public void remove(byte[] bytesKey) {
            try {
                db.delete(bytesKey);
            } catch (RocksDBException e) {
                throwRocksDBException(e);
            }
        }

        @Override
        public void clear() {
            clearDb(db);
        }

        @Override
        public void close() throws IOException {
            db.close();
        }

        private void throwRocksDBException(RocksDBException e) {
            throw new OtherException("操作RocksDB出错: " + fileDir, e);
        }
    }


    private final Impl impl;

    /**
     * 用自定义参数构建缓存
     *
     * @param fileDir      缓存文件夹路径
     * @param options      RocksDB参数 为空则用默认值
     * @param cacheTimeOut 缓存在多少毫秒后失效 为负表示缓存永不失效 不允许为0
     */
    public FileCache(String fileDir, @Nullable Options options, long cacheTimeOut) {
        if (0 == cacheTimeOut) {
            throw new ConfigException("FileCache.cacheTimeOut不能为0");
        }
        if (cacheTimeOut < 0) {
            impl = new NotTimeOutImpl(fileDir, options);
        } else {
            impl = new TimeOutImpl(fileDir, options, cacheTimeOut);
        }
    }

    public void put(byte[] bytesKey, byte[] bytesValue) {
        impl.put(bytesKey, bytesValue);
    }

    public byte[] get(byte[] bytesKey) {
        return impl.get(bytesKey);
    }

    public void remove(byte[] bytesKey) {
        impl.remove(bytesKey);
    }

    public void clear() {
        impl.clear();
    }

    @Override
    public void close() throws IOException {
        impl.close();
    }

    public static byte[] string2Bytes(@NotNull String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static @NotNull String bytes2String(byte @NotNull [] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] long2Bytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static long bytes2Long(byte @NotNull [] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }
}

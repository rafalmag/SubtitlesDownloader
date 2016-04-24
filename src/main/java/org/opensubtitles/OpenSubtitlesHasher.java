package org.opensubtitles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * Hash code is based on Media Player Classic. In natural language it
 * calculates: size + 64bit checksum of the first and last 64k (even if they
 * overlap because the file is smaller than 128k).
 */
public class OpenSubtitlesHasher {

    /**
     * Size of the chunks that will be hashed in bytes (64 KB)
     */
    private static final int HASH_CHUNK_SIZE = 64 * 1024;

    public static String computeHash(File file) throws IOException {
        long size = file.length();
        long chunkSizeForFile = Math.min(HASH_CHUNK_SIZE, size);

        try (FileInputStream fileInputStream = new FileInputStream(file);
             FileChannel fileChannel = fileInputStream.getChannel()) {
            long head = computeHashForChunk(fileChannel.map(MapMode.READ_ONLY, 0, chunkSizeForFile));
            long tail = computeHashForChunk(fileChannel.map(MapMode.READ_ONLY,
                    Math.max(size - HASH_CHUNK_SIZE, 0), chunkSizeForFile));
            return String.format("%016x", size + head + tail);
        }
    }

    private static long computeHashForChunk(ByteBuffer buffer) {
        LongBuffer longBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
        long hash = 0;

        while (longBuffer.hasRemaining()) {
            hash += longBuffer.get();
        }
        return hash;
    }

}
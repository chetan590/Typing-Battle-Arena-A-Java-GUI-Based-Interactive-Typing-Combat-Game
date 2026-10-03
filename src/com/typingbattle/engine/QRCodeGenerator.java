package com.typingbattle.engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

/**
 * Pure Java standard QR Code (ISO/IEC 18004) generator with Byte Mode encoding
 * and Reed-Solomon error correction over GF(256).
 * Produces crisp, high-resolution scannable QR Code images without external dependencies.
 */
public class QRCodeGenerator {

    // Galois Field GF(256) tables with primitive polynomial 0x11D (x^8 + x^4 + x^3 + x^2 + 1)
    private static final int[] EXP = new int[512];
    private static final int[] LOG = new int[256];

    static {
        int x = 1;
        for (int i = 0; i < 255; i++) {
            EXP[i] = x;
            LOG[x] = i;
            x <<= 1;
            if ((x & 0x100) != 0) {
                x ^= 0x11D;
            }
        }
        for (int i = 255; i < 512; i++) {
            EXP[i] = EXP[i - 255];
        }
    }

    private static int gfMul(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return EXP[LOG[a] + LOG[b]];
    }

    /**
     * Generates a scannable QR code image for the specified text/URL.
     * Selects appropriate QR version based on payload length.
     */
    public static BufferedImage generateQRCode(String content, int targetSize) {
        byte[] payload = content.getBytes(StandardCharsets.UTF_8);

        // Version selection:
        // Version 3 (29x29): holds up to 53 bytes (Level L)
        // Version 4 (33x33): holds up to 78 bytes (Level L)
        // Version 5 (37x37): holds up to 106 bytes (Level L)
        int version;
        int totalDataCodewords;
        int ecCodewords;
        int alignmentPatternPos;

        if (payload.length <= 50) {
            version = 3;
            totalDataCodewords = 55;
            ecCodewords = 15;
            alignmentPatternPos = 22;
        } else if (payload.length <= 75) {
            version = 4;
            totalDataCodewords = 80;
            ecCodewords = 20;
            alignmentPatternPos = 26;
        } else {
            version = 5;
            totalDataCodewords = 108;
            ecCodewords = 26;
            alignmentPatternPos = 30;
        }

        int matrixSize = 17 + 4 * version;
        boolean[][] matrix = new boolean[matrixSize][matrixSize];
        boolean[][] isFunction = new boolean[matrixSize][matrixSize];

        // 1. Encode Bitstream
        byte[] dataCodewords = encodeData(payload, totalDataCodewords);

        // 2. Compute Reed-Solomon Error Correction
        byte[] ecBytes = computeReedSolomon(dataCodewords, ecCodewords);

        // 3. Assemble all codewords
        byte[] allCodewords = new byte[dataCodewords.length + ecBytes.length];
        System.arraycopy(dataCodewords, 0, allCodewords, 0, dataCodewords.length);
        System.arraycopy(ecBytes, 0, allCodewords, dataCodewords.length, ecBytes.length);

        // 4. Place Function Patterns into matrix
        placeFinder(matrix, isFunction, 0, 0);
        placeFinder(matrix, isFunction, matrixSize - 7, 0);
        placeFinder(matrix, isFunction, 0, matrixSize - 7);

        // Timing patterns
        for (int i = 8; i < matrixSize - 8; i++) {
            boolean bit = (i % 2 == 0);
            matrix[6][i] = bit;
            isFunction[6][i] = true;
            matrix[i][6] = bit;
            isFunction[i][6] = true;
        }

        // Alignment pattern
        if (version >= 2) {
            placeAlignment(matrix, isFunction, alignmentPatternPos, alignmentPatternPos);
        }

        // Dark module
        matrix[4 * version + 9][8] = true;
        isFunction[4 * version + 9][8] = true;

        // Reserve format info areas
        reserveFormatAreas(isFunction, matrixSize);

        // 5. Place Data Bits in zig-zag pattern with Mask 0 ((row + col) % 2 == 0)
        placeDataBits(matrix, isFunction, allCodewords, matrixSize);

        // 6. Write Format Info (Level L, Mask 0 -> 15-bit format codeword 0x77C4)
        writeFormatInfo(matrix, matrixSize);

        // 7. Render Matrix to BufferedImage with quiet zone
        int quietZone = 4;
        int fullSize = matrixSize + quietZone * 2;
        int scale = Math.max(1, targetSize / fullSize);
        int finalPixelSize = fullSize * scale;

        BufferedImage img = new BufferedImage(finalPixelSize, finalPixelSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, finalPixelSize, finalPixelSize);

        g2.setColor(Color.BLACK);
        for (int r = 0; r < matrixSize; r++) {
            for (int c = 0; c < matrixSize; c++) {
                if (matrix[r][c]) {
                    g2.fillRect((c + quietZone) * scale, (r + quietZone) * scale, scale, scale);
                }
            }
        }
        g2.dispose();
        return img;
    }

    private static byte[] encodeData(byte[] payload, int totalCodewords) {
        BitBuffer bb = new BitBuffer();
        // Mode indicator for 8-bit Byte Mode: 0100
        bb.append(4, 4);
        // Character count indicator: 8 bits for versions 1-9
        bb.append(payload.length, 8);
        for (byte b : payload) {
            bb.append(b & 0xFF, 8);
        }
        // Terminator: up to 4 zero bits
        int remaining = totalCodewords * 8 - bb.bitLength();
        int term = Math.min(4, Math.max(0, remaining));
        bb.append(0, term);

        // Pad to byte boundary
        while (bb.bitLength() % 8 != 0) {
            bb.append(0, 1);
        }

        // Pad bytes: alternating 0xEC and 0x11
        byte[] padBytes = {(byte) 0xEC, (byte) 0x11};
        int padIdx = 0;
        while (bb.byteLength() < totalCodewords) {
            bb.append(padBytes[padIdx % 2] & 0xFF, 8);
            padIdx++;
        }

        return bb.getBytes(totalCodewords);
    }

    private static byte[] computeReedSolomon(byte[] data, int ecCount) {
        int[] gen = {1};
        for (int i = 0; i < ecCount; i++) {
            int[] factor = {1, EXP[i]};
            gen = polyMul(gen, factor);
        }

        int[] result = new int[data.length + ecCount];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] & 0xFF;
        }

        for (int i = 0; i < data.length; i++) {
            int coef = result[i];
            if (coef != 0) {
                for (int j = 0; j < gen.length; j++) {
                    result[i + j] ^= gfMul(gen[j], coef);
                }
            }
        }

        byte[] ec = new byte[ecCount];
        for (int i = 0; i < ecCount; i++) {
            ec[i] = (byte) result[data.length + i];
        }
        return ec;
    }

    private static int[] polyMul(int[] a, int[] b) {
        int[] res = new int[a.length + b.length - 1];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                res[i + j] ^= gfMul(a[i], b[j]);
            }
        }
        return res;
    }

    private static void placeFinder(boolean[][] m, boolean[][] fn, int row, int col) {
        for (int r = -1; r <= 7; r++) {
            for (int c = -1; c <= 7; c++) {
                int mr = row + r;
                int mc = col + c;
                if (mr >= 0 && mr < m.length && mc >= 0 && mc < m.length) {
                    fn[mr][mc] = true;
                    if (r >= 0 && r <= 6 && c >= 0 && c <= 6) {
                        m[mr][mc] = (r == 0 || r == 6 || c == 0 || c == 6 || (r >= 2 && r <= 4 && c >= 2 && c <= 4));
                    } else {
                        m[mr][mc] = false; // separator
                    }
                }
            }
        }
    }

    private static void placeAlignment(boolean[][] m, boolean[][] fn, int rCenter, int cCenter) {
        for (int r = -2; r <= 2; r++) {
            for (int c = -2; c <= 2; c++) {
                int mr = rCenter + r;
                int mc = cCenter + c;
                if (!fn[mr][mc]) {
                    fn[mr][mc] = true;
                    m[mr][mc] = (Math.abs(r) == 2 || Math.abs(c) == 2 || (r == 0 && c == 0));
                }
            }
        }
    }

    private static void reserveFormatAreas(boolean[][] fn, int size) {
        for (int i = 0; i <= 8; i++) {
            fn[8][i] = true;
            fn[i][8] = true;
        }
        for (int i = size - 8; i < size; i++) {
            fn[8][i] = true;
            fn[i][8] = true;
        }
    }

    private static void placeDataBits(boolean[][] m, boolean[][] fn, byte[] data, int size) {
        int bitIdx = 0;
        int totalBits = data.length * 8;
        int col = size - 1;
        boolean upward = true;

        while (col > 0) {
            if (col == 6) col--; // Skip vertical timing column

            for (int step = 0; step < size; step++) {
                int row = upward ? (size - 1 - step) : step;
                for (int c = 0; c < 2; c++) {
                    int curCol = col - c;
                    if (!fn[row][curCol]) {
                        boolean bit = false;
                        if (bitIdx < totalBits) {
                            int bytePos = bitIdx / 8;
                            int bitOffset = 7 - (bitIdx % 8);
                            bit = ((data[bytePos] >>> bitOffset) & 1) != 0;
                            bitIdx++;
                        }
                        // Apply Mask 0: (row + col) % 2 == 0
                        boolean maskBit = ((row + curCol) % 2 == 0);
                        m[row][curCol] = bit ^ maskBit;
                    }
                }
            }
            col -= 2;
            upward = !upward;
        }
    }

    private static void writeFormatInfo(boolean[][] m, int size) {
        // Format info for Error Correction L (01) and Mask Pattern 000 (0): 0x77C4 (15 bits)
        int format = 0x77C4;
        for (int i = 0; i < 15; i++) {
            boolean bit = ((format >>> (14 - i)) & 1) != 0;
            // Around top-left
            if (i <= 5) {
                m[8][i] = bit;
            } else if (i == 6) {
                m[8][7] = bit;
            } else if (i == 7) {
                m[8][8] = bit;
            } else if (i == 8) {
                m[7][8] = bit;
            } else {
                m[14 - i][8] = bit;
            }

            // Along top-right and bottom-left
            if (i < 7) {
                m[size - 1 - i][8] = bit;
            } else {
                m[8][size - 15 + i] = bit;
            }
        }
    }

    private static class BitBuffer {
        private final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        private int currentByte = 0;
        private int numBits = 0;
        private int totalBits = 0;

        public void append(int value, int bits) {
            for (int i = bits - 1; i >= 0; i--) {
                int bit = (value >>> i) & 1;
                currentByte = (currentByte << 1) | bit;
                numBits++;
                totalBits++;
                if (numBits == 8) {
                    bytes.write(currentByte);
                    currentByte = 0;
                    numBits = 0;
                }
            }
        }

        public int bitLength() { return totalBits; }
        public int byteLength() { return (totalBits + 7) / 8; }

        public byte[] getBytes(int requiredLength) {
            if (numBits > 0) {
                currentByte <<= (8 - numBits);
                bytes.write(currentByte);
                numBits = 0;
            }
            byte[] out = new byte[requiredLength];
            byte[] src = bytes.toByteArray();
            System.arraycopy(src, 0, out, 0, Math.min(src.length, requiredLength));
            return out;
        }
    }
}

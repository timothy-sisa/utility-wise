package com.timothysisa.utilitywise.ui.addreading

/**
 * Returns the power of two to pass as `inSampleSize` so that a [width] by [height] image
 * decodes to at least [targetWidth] by [targetHeight]. Returns 1 when no size is positive.
 */
fun photoSampleSize(width: Int, height: Int, targetWidth: Int, targetHeight: Int): Int {
    if (minOf(width, height, targetWidth, targetHeight) <= 0) return 1
    var sampleSize = 1
    while (width / (sampleSize * 2) >= targetWidth && height / (sampleSize * 2) >= targetHeight) sampleSize *= 2
    return sampleSize
}

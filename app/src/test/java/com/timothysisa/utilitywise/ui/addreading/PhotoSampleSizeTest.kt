package com.timothysisa.utilitywise.ui.addreading

import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoSampleSizeTest {

    @Test
    fun imageSmallerThanTargetIsNotScaled() {
        assertEquals(1, photoSampleSize(300, 200, 400, 400))
    }

    @Test
    fun imageExactlyDoubleTheTargetHalves() {
        assertEquals(2, photoSampleSize(800, 800, 400, 400))
    }

    @Test
    fun largeCameraPhotoStaysAtLeastTargetSize() {
        // 4 gives 1000 by 750; 8 would give 500 by 375, under the target height.
        assertEquals(4, photoSampleSize(4000, 3000, 400, 400))
    }

    @Test
    fun zeroOrNegativeSizesReturnOne() {
        assertEquals(1, photoSampleSize(0, 3000, 400, 400))
        assertEquals(1, photoSampleSize(4000, -1, 400, 400))
        assertEquals(1, photoSampleSize(4000, 3000, 0, 400))
        assertEquals(1, photoSampleSize(4000, 3000, 400, -5))
    }
}

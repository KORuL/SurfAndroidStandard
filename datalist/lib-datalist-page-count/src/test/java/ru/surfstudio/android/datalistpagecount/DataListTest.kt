package ru.surfstudio.android.datalistpagecount

import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import ru.surfstudio.android.datalistpagecount.domain.datalist.DataList
import ru.surfstudio.android.datalistpagecount.util.emptyDataListOf
import ru.surfstudio.android.logger.Logger
import ru.surfstudio.android.logger.logging_strategies.impl.test.TestLoggingStrategy


class DataListTest {

    companion object {

        private val testLoggingStrategy = TestLoggingStrategy()

        @BeforeClass
        @JvmStatic
        fun setUp() {
            Logger.addLoggingStrategy(testLoggingStrategy)
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            Logger.removeLoggingStrategy(testLoggingStrategy)
        }
    }

    @Test
    fun checkNormalMerge() {
        val list1 = DataList(arrayListOf(1, 2, 3, 4, 5), 0, 5)
        val list2 = DataList(arrayListOf(6, 7, 8, 8, 10), 1, 5)

        list1.merge(list2)

        val expected = DataList(arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 8, 10), 0, 2, 5)
        Assert.assertEquals(expected, list1)
    }

    @Test
    fun mergePagesWithGapBetweenPages() {
        val list1 = DataList(arrayListOf(1, 2, 3, 4, 5), 0, 5)
        val list2 = DataList(arrayListOf(6, 7, 8, 8, 10), 2, 5)

        list1.merge(list2)

        val expected = DataList(arrayListOf(1, 2, 3, 4, 5), 0, 1, 5)
        Assert.assertEquals(expected, list1)
    }

    @Test
    fun mergeNotFromFirstPage() {
        val list1 = DataList(arrayListOf(1, 2, 3, 4, 5), 4, 5)
        val list2 = DataList(arrayListOf(6, 7, 8, 8, 10), 5, 5)

        list1.merge(list2)

        val expected = DataList(arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 8, 10), 4, 2, 5)
        Assert.assertEquals(expected, list1)
    }

    @Test
    fun mergeWithEmptyList() {
        val list1 = DataList(arrayListOf(1, 2, 3, 4, 5), 0, 5)
        val list2 = DataList(arrayListOf<Int>(), 2, 5)

        list1.merge(list2)

        val expected = DataList(arrayListOf(1, 2, 3, 4, 5), 0, 1, 5)
        Assert.assertEquals(expected, list1)
    }

    @Test
    fun mergeEmptyListWithNonEmpty() {
        val list1 = DataList.empty<Int>()
        val list2 = DataList(arrayListOf(1, 2, 3, 4, 5), 0, 5)

        list1.merge(list2)

        val expected = DataList(arrayListOf(1, 2, 3, 4, 5), 0, 1, 5)
        Assert.assertEquals(expected, list1)
    }

    @Test
    fun mergeWithPreviousPage() {
        val list1 = DataList(arrayListOf(1, 2, 3, 4, 5), 2, 5)
        val list2 = DataList(arrayListOf(6, 7, 8, 9, 10), 1, 5)

        list1.merge(list2)

        val expected = DataList(arrayListOf(6, 7, 8, 9, 10, 1, 2, 3, 4, 5), 1, 2, 5)
        Assert.assertEquals(expected, list1)
    }

    @Test
    fun mergeWithCollision() {
        val list1 = DataList(arrayListOf(1, 2, 3, 4, 5), 0, 5, 1)
        val list2 = DataList(arrayListOf(6, 7, 8, 9, 10), 1, 5, 1)

        list1.merge(list2)

        val expected = DataList(arrayListOf(1, 6, 7, 8, 9, 10), 0, 6, 1)
        Assert.assertEquals(expected, list1)
    }


    @Test(expected = IllegalArgumentException::class)
    fun mergeWithError() {
        val list1 = DataList(arrayListOf(1, 2, 3, 4, 5), 2, 5)
        val list2 = DataList(arrayListOf(6, 7, 8, 8, 10), 1, 1)

        list1.merge(list2)
    }

}
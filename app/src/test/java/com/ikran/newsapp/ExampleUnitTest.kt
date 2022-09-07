package com.ikran.newsapp

import com.ikran.newsapp.repository.NewsRepository
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.framework.concurrency.FixedInterval
import io.kotest.framework.concurrency.eventually
import io.kotest.matchers.shouldBe

/*import org.junit.Test

import org.junit.Assert.**/

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    /*@Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }*/
}

class MyTests : StringSpec({
    ("length should return size of string") {
        "hello".length shouldBe 5
    }
})

// @Tags("SlowTest")
class NetworkTransactionTests : ShouldSpec({
    val newsRepo = NewsRepository()

    should("Should make repo transaction complete") {
        eventually({
            duration = 5000
            interval = FixedInterval(1000)
        }) {
            newsRepo.getTopNews("in", 1) shouldBe "COMPLETE"
        }
    }
})

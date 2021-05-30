import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.dsl.module
import org.koin.test.KoinTestRule

import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock

import org.mockito.BDDMockito.given
import org.mockito.Mockito

class TestMain :KoinTest {
    private val db by inject<Database>()

    /** Start Koin container
     * KoinApplication has not been started
     */
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        //printLogger(Level.DEBUG)
        
        //Let Koin Container generate instances
        modules(module {
            // generate Config instance as singleton instance
            single { Config() } 
            //generate Config insntace as singleton instance
            //Let Koin Container pass appropriate instance as constructor
            single { DatabaseImpl(get()) }  
        })
    }

    /**
     * Initialize Mock Provider. Mock can be defined in 2 ways
     *   1. declareMock<..>{ }
     *   2. 
     */
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun getMessage() {
        //declare inserting mock into Database class
        declareMock<Database> {
            given(connect()).will({println("Mock OK")})
        }

        var result = "Hello world"
        assertEquals("Hello world", result)
        db.connect()
    }

}
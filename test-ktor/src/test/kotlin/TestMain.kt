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
import org.koin.core.Koin
class TestMain :KoinTest {
    private val db by inject<Database>()

    /** Start Koin container
     * KoinApplication has not been started
     */
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        //Let Koin Container generate instances
        modules(module {
            // generate Config instance as singleton instance
            single { Config() } 
            //generate Config insntace as singleton instance
            //Let Koin Container pass appropriate instance as constructor
            single { DatabaseMock(get()) as Database}  
        })
    }

    /**
     * Initialize Mock Provider. The Provider can produce modules in 2 ways
     *   1. declareMock<..>{ } --- declare & instantiate module
     *   2. declare{  } --- declare only. you need get<ComponentX>() to retrieve component from container
     */
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun testConnection() {
        println( "db class = %s".format(db))
        //declare inserting mock into Database class
        val dbmock = declareMock<Database> {
            given(connect()).will({println("Mock Database class execute connect() --- OK")})
        }
        println( "db class = %s".format(dbmock))
        db.connect()
        dbmock.connect()
    }

    @Test
    fun `TEST コンバータのテスト`() {
        println("This is converter test")
        assertEquals(true, true)
    }

}
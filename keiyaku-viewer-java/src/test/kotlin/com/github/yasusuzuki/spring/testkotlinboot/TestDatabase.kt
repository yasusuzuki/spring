package com.github.yasusuzuki.spring.testkotlinboot

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach
import java.sql.*

class TestDatabase {
    lateinit var con : Connection 

    @BeforeEach
    fun init() {
        //Class.forName("com.ibm.db2.jcc.DB2Driver")
        Class.forName("org.hsqldb.jdbc.JDBCDriver");

        try {
            //con = DriverManager.getConnection("jdbc:db2://localhost:50000/DB", "user", "pass");
            con = DriverManager.getConnection("jdbc:hsqldb:mem:employees", "vinod", "vinod");
            var stmt = con.createStatement();
            stmt.execute("CREATE TABLE employee (id INT NOT NULL, name VARCHAR(50) NOT NULL,"
                    + "email VARCHAR(50) NOT NULL, PRIMARY KEY (id))");
            con.commit();

            stmt.executeUpdate(
                "INSERT INTO employee VALUES (1001,'Vinod Kumar Kashyap', 'vinod@javacodegeeks.com')");
            stmt.executeUpdate("INSERT INTO employee VALUES (1002,'Dhwani Kashyap', 'dhwani@javacodegeeks.com')");
            stmt.executeUpdate("INSERT INTO employee VALUES (1003,'Asmi Kashyap', 'asmi@javacodegeeks.com')");
            con.commit();
        } catch ( e: Exception ) {
            e.printStackTrace()
        }
    }
    
    @Test
    fun testDatabase() {
        val expected = listOf(1001,1002,1003)
        var result = mutableListOf<Int>()
        var stmt : Statement
        var rt : ResultSet;
        try {
            stmt = con.createStatement();
            rt = stmt.executeQuery("select * from employee ");

            while (rt.next()) {
                result.add( rt.getInt("id") );
            }
        } catch (e : SQLException) {
            e.printStackTrace()
        }
        assertEquals(expected, result)
    }
}

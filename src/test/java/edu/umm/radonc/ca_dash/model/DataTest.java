/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mmcgrath
 */
public class DataTest {
    
    public DataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getNumber method, of class Data.
     */
    @org.junit.Test
    public void testGetNumber() {
        System.out.println("getNumber");
        Data instance = new Data();
        Integer expResult = null;
        Integer result = instance.getNumber();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setNumber method, of class Data.
     */
    @org.junit.Test
    public void testSetNumber() {
        System.out.println("setNumber");
        Integer number = null;
        Data instance = new Data();
        instance.setNumber(number);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getId method, of class Data.
     */
    @org.junit.Test
    public void testGetId() {
        System.out.println("getId");
        Data instance = new Data();
        Integer expResult = null;
        Integer result = instance.getId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setId method, of class Data.
     */
    @org.junit.Test
    public void testSetId() {
        System.out.println("setId");
        Integer id = null;
        Data instance = new Data();
        instance.setId(id);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getStuff method, of class Data.
     */
    @org.junit.Test
    public void testGetStuff() {
        System.out.println("getStuff");
        Data instance = new Data();
        String expResult = "";
        String result = instance.getStuff();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setStuff method, of class Data.
     */
    @org.junit.Test
    public void testSetStuff() {
        System.out.println("setStuff");
        String stuff = "";
        Data instance = new Data();
        instance.setStuff(stuff);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class Data.
     */
    @org.junit.Test
    public void testHashCode() {
        System.out.println("hashCode");
        Data instance = new Data();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Data.
     */
    @org.junit.Test
    public void testEquals() {
        System.out.println("equals");
        Object object = null;
        Data instance = new Data();
        boolean expResult = false;
        boolean result = instance.equals(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Data.
     */
    @org.junit.Test
    public void testToString() {
        System.out.println("toString");
        Data instance = new Data();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

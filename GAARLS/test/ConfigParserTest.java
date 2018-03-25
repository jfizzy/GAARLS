/*
 * Authors: 	Peter Ebear, David Engel, Evan Loughlin, James MacIsaac, Shane Sims
 * Project: 	Genetic Algorithm based Association Rule Learning System
 * Course: 	CPSC 599.44 - Machine Learning - Winter 2018
 * Professor:	Dr. Jörg Denzinger
 * University:	University of Calgary
 */

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Ebear, David Engel, Evan Loughlin, James MacIsaac, Shane Sims
 */
public class ConfigParserTest {
    
    Parser p;
    ConfigParameters cp;

    public ConfigParserTest() {
        p = new Parser();
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

    @Test
    public void testConfigParamInit() {
        ArrayList<Integer> al = new ArrayList<>();
        al.add(0);
        cp = new ConfigParameters(0,0,0,0,0,0,0,0,0,0,0,0,al);
        assertTrue(cp.numGenerations == 0);
    }
}

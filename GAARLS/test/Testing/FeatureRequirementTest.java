package Testing;

/*
 * Authors: 	Peter Ebear, David Engel, Evan Loughlin, James MacIsaac, Shane Sims
 * Project: 	Genetic Algorithm based Association Rule Learning System
 * Course: 	CPSC 599.44 - Machine Learning - Winter 2018
 * Professor:	Dr. Jörg Denzinger
 * University:	University of Calgary
 */
import RuleManager.FeatureRequirement;
import RuleManager.FeatureRequirement.InvalidFeatReqException;
import RuleManager.Rule;

import RuleManager.FeatureRequirement;
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
public class FeatureRequirementTest {

    public FeatureRequirementTest() {
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
    public void testNewValidFeatureRequirements() {
        try {
            FeatureRequirement fr1 = new FeatureRequirement(0, 0, 10.5f, 4.5f); // IGNORE, 10.5, 4.5 -- GOOD
            //check proper construction
            assertEquals(fr1.getParticipation(), 0); // check IGNORE
            assertEquals(fr1.getUpperBound(), 10.5f, 0.0f);
            assertEquals(fr1.getLowerBound(), 4.5f, 0.0f);

            // try again
            FeatureRequirement fr2 = new FeatureRequirement(0, 1, 3.738f, -2.5f); // ANTECEDENT, 3.738, -2.5 -- GOOD
            assertEquals(fr2.getParticipation(), 1); // check ANTECEDENT
            assertEquals(fr2.getUpperBound(), 3.738f, 0.0f);
            assertEquals(fr2.getLowerBound(), -2.5f, 0.0f);

            // another
            FeatureRequirement fr3 = new FeatureRequirement(0, 2, 3.45f, 0f); // CONSEQUENT, 3.45, 0 -- GOOD
            assertEquals(fr3.getParticipation(), 2); // check CONSEQUENT
            assertEquals(fr3.getUpperBound(), 3.45f, 0.0f);
            assertEquals(fr3.getLowerBound(), 0f, 0.0f);

        } catch (InvalidFeatReqException ifre) { // fail if there was an issue here
            fail(ifre.getMessage());
        }
    }

    @Test
    public void testInvalidFeatureRequirements() {
        try {
            FeatureRequirement fr1 = new FeatureRequirement(0, 0, 4.5f, 10.5f); // 10.5 < 4.5 -- BAD!!!
            fail("Successfully created invalid upper and lower and ignore");
        } catch (InvalidFeatReqException ifre) {
            assertTrue(true);
        }
        try {
            FeatureRequirement fr2 = new FeatureRequirement(0, 1, -3.5f, 0.0f); // 0 < -3.5 -- BAD!!!
            fail("Created invalid upper and lower and antecedent");
        } catch (InvalidFeatReqException ifre) {
            assertTrue(true);
        }
        try {
            FeatureRequirement fr3 = new FeatureRequirement(0, 2, -6.0f, -3.45f); // -3.45 < -6 -- BAD!!!
            fail("Created invalid upper and lower and consequent");
        } catch (InvalidFeatReqException ifre) {
            assertTrue(true);
        }
        try {
            FeatureRequirement fr4 = new FeatureRequirement(0, 15, 5.0f, 0.0f); // pFlag = 15 -- bad but will default to IGNORE
            assertEquals(fr4.getParticipation(), 0); // check IGNORE
        } catch (Exception e) {
            fail("Did not default to ignore on invalid participation flag");
        }
    }

    @Test
    public void testEvaluateMethod() {
        try {
            FeatureRequirement fr1 = new FeatureRequirement(0, 0, 10.5f, 4.5f);
            assertTrue(fr1.evaluate(5.5f)); // GOOD
            assertFalse(fr1.evaluate(11.5f)); //TOO HIGH
            assertFalse(fr1.evaluate(3.5f)); //TOO LOW
            //test with negatives
            FeatureRequirement fr2 = new FeatureRequirement(0, 1, -3.738f, -20.5f);
            assertTrue(fr2.evaluate(-10.0f)); // GOOD
            assertFalse(fr2.evaluate(-2.0f)); //TOO HIGH
            assertFalse(fr2.evaluate(-40.67f)); //TOO LOW
        } catch (InvalidFeatReqException ifre) {
            fail(ifre.getMessage());
        }
    }
    
    @Test
    public void testCopyMethod(){
        try {
            FeatureRequirement fr1 = new FeatureRequirement(0, 0, 10.5f, 4.5f);
            assertTrue(fr1.evaluate(5.5f)); 
            FeatureRequirement frCopy = fr1.copy();
            assertEquals(frCopy.getParticipation(), 0); // check IGNORE
            assertEquals(frCopy.getUpperBound(), 10.5f, 0.0f);
            assertEquals(frCopy.getLowerBound(), 4.5f, 0.0f);
            //test eval on copy
            assertTrue(frCopy.evaluate(5.5f)); // GOOD
            assertFalse(frCopy.evaluate(11.5f)); //TOO HIGH
            assertFalse(frCopy.evaluate(3.5f)); //TOO LOW
        } catch (InvalidFeatReqException ifre) {
            fail(ifre.getMessage());
        }
    }
}

import Rule.FeatureRequirement;
import Rule.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class FitnessFunctionTests {

    @Test
    public void testHammingDistance() {
        FeatureRequirement[] r1FeatureVector = new FeatureRequirement[23];
        FeatureRequirement[] r2FeatureVector = new FeatureRequirement[23];
        try {
            for (int i = 0; i < 23; i++) {
                r1FeatureVector[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value
                r2FeatureVector[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value

            }
            r1FeatureVector[3] = new FeatureRequirement(1, 1, 0f, 0f, 0f); //set fourth feature as part of A
            r2FeatureVector[1] = new FeatureRequirement(1, 1, 0f, 0f, 0f); //set second feature as part of A
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        FitnessManager theFitnessManager = new FitnessManager(null, null, null);


        Rule rule1 = new Rule(r1FeatureVector);
        Rule rule2 = new Rule(r2FeatureVector);
        assertEquals(theFitnessManager.hammingDistance(rule1,rule2), 2/rule1.getFeatureReqs().length, 0.0); // check that the two differences are caught

    }

    @Test
    public void testExt1() {
        // Make list of "weka rules" with known features to test against
        ArrayList<Rule> theWekaRules = new ArrayList<Rule>();

        FeatureRequirement[] rFeatureVector = new FeatureRequirement[23];
        FeatureRequirement[] r1FeatureVector = new FeatureRequirement[23];
        FeatureRequirement[] r2FeatureVector = new FeatureRequirement[23];
        FeatureRequirement[] r3FeatureVector = new FeatureRequirement[23];
        try {
            for (int i = 0; i < 23; i++) {
                r1FeatureVector[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value
                r2FeatureVector[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value
                r3FeatureVector[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value
                rFeatureVector[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value

            }
            r1FeatureVector[3] = new FeatureRequirement(1, 1, 0f, 0f, 0f); //set fourth feature as part of A
            r1FeatureVector[7] = new FeatureRequirement(1, 2, 0f, 0f, 0f); //set eighth feature as part of C


            r2FeatureVector[1] = new FeatureRequirement(1, 1, 0f, 0f, 0f); //set second feature as part of A
            r2FeatureVector[5] = new FeatureRequirement(1, 2, 0f, 0f, 0f); //set sixth feature as part of C
            r2FeatureVector[9] = new FeatureRequirement(1, 2, 0f, 0f, 0f); //set tenth feature as part of C


            r3FeatureVector[2] = new FeatureRequirement(1, 1, 0f, 0f, 0f); //set third feature as part of A
            r3FeatureVector[13] = new FeatureRequirement(1, 2, 0f, 0f, 0f); //set fourteenth feature as part of C


            rFeatureVector[1] = new FeatureRequirement(1, 1, 0f, 0f, 0f); //set second feature as part of A
            rFeatureVector[8] = new FeatureRequirement(1, 2, 0f, 0f, 0f); //set ninth feature as part of C


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        Rule rule1 = new Rule(r1FeatureVector); //rule1: If f4 -> f8; distance from rule is 4
        Rule rule2 = new Rule(r2FeatureVector); //rule2: If f2 -> f6 ^ f10; distance from rule is 3
        Rule rule3 = new Rule(r3FeatureVector); //rule2: If f3 -> f14; distance from rule is 4
        Rule rule = new Rule(rFeatureVector); //rule: If f2 -> f9

        theWekaRules.add(rule1);
        theWekaRules.add(rule2);
        theWekaRules.add(rule3);

        FitnessManager theFitnessManager = new FitnessManager(null, theWekaRules, null);

        assertEquals((double) theFitnessManager.ext1(rule), (double) (3/rule.getFeatureReqs().length), 0.0);

        theWekaRules.add(rule);
        FitnessManager newFitnessManager = new FitnessManager(null, theWekaRules, null);
        assertEquals(newFitnessManager.ext1(rule), 0, 0.0);

    }

}

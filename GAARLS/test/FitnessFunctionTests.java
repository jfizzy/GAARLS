import Rule.FeatureRequirement;
import Rule.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;


public class FitnessFunctionTests {

    @Test
    public void testBasicFitnessFunction() {

    }

    @Test
    public void testHammingDistance() {
        FeatureRequirement[] r1FeatureVector = new FeatureRequirement[23];
        FeatureRequirement[] r2FeatureVector = new FeatureRequirement[23];
        try {
            for (int i = 0; i < 23; i++) {
                r1FeatureVector[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value
                r2FeatureVector[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value

            }
            r1FeatureVector[3] = new FeatureRequirement(1, 1, 0f, 0f, 0f); // default initial value
            r2FeatureVector[1] = new FeatureRequirement(1, 1, 0f, 0f, 0f); // default initial value
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        Rule rule1 = new Rule(r1FeatureVector);
        Rule rule2 = new Rule(r2FeatureVector);
        assertEquals(FitnessManager.hammingDistance(rule1,rule2), 2); // check that the two differences are caught

    }
}

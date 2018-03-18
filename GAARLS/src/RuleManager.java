import Rule.Rule;
import Rule.FeatureRequirement;
import java.util.ArrayList;
import java.util.Random;


/**
 * Class: RuleManager
 * Intended functionality: Util class that is in charge of mutations, cross-overs and rule generation
 * Feature Owner: Shane, David
 */


public class RuleManager
{
    
    private final int num_features = 23; // TODO set this to LookupTable.NUM_FEATURES
    
    // public methods
    public RuleManager(LookupTable lookupTable)
    {
        mLookupTable = lookupTable;
        Rule.setNumFeatures(lookupTable.NumFeatures);
    }

    /**
     * Creates a new rule with @mutationTemplate as the base rule
     * NOTE: Does not modify the state of @mutationTemplate
     * @param parent
     * @return a mutated rule
     */
    public Rule mutate(Rule parent)
    {
        Rule mutatedRule = parent;


        // mutate

        return mutatedRule;
    }

    /**
     * Creates a new rule with @parent1 and @parent2 as the base rules
     * @param parent1 
     * @param parent2 
     * @return merged rule (child) of @parent1 and @parent2
     */
    public Rule crossover(Rule parent1, Rule parent2)
    {


		// This needs to be tested!

	    Random rand = new Random();
        int pivot = rand.nextInt(num_features-1); // Randomly selected pivot point (index of where the crossover will occur)
		
		FeatureRequirement parent1FeatReqs[] = parent1.getFeatureReqs();
		FeatureRequirement parent2FeatReqs[] = parent2.getFeatureReqs();
		FeatureRequirement childFeatReqs[] = parent1.getFeatureReqs();
        
        for(int i = pivot; i < num_features; i++) // Replace all elements after the pivot with parent 2's genes
				childFeatReqs[i] = parent2FeatReqs[i];
			
        Rule child = new Rule(childFeatReqs);

        return child;

    }

    /**
     * Helper function to generate new rules in the population initialization phase
     * Creates a new rule with randomized requirements found from LookupTable
     * @return random new rule
     */
    public Rule generateRule()
    {
        // NOTE: Current implementation was created as a proof of concept. Needs to be revisited
        // -Peter
        Rule newRule = new Rule();
        Random random = new Random();
        FeatureRequirement[] featureRequirements = newRule.getFeatureReqs();
        for (int i = 0; i < featureRequirements.length; ++i)
        {
            mLookupTable.GenerateRandomValue(i, featureRequirements[i]);
            featureRequirements[i].setParticipation(random.nextInt(3));
        }
        return newRule;
    }

    public String TranslateRule(Rule rule)
    {
        return mLookupTable.TranslateRule(rule);
    }

    // private members
    private LookupTable mLookupTable;
}

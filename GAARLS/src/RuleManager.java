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
    
    private final int num_features;
    
    // public methods
    public RuleManager(LookupTable lookupTable)
    {
        mLookupTable = lookupTable;
        num_features = lookupTable.NumFeatures;
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
        int pivot = rand.nextInt(num_features-2) + 1; // Randomly selected pivot point (index of where the crossover will occur)
		
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
        Random rand = new Random();

        // NOTE: Current implementation was created as a proof of concept. Needs to be revisited
        // -Peter
        Rule newRule = new Rule();
        Random random = new Random();
        FeatureRequirement[] featureRequirements = newRule.getFeatureReqs();
        int size = featureRequirements.length;

        // choose 3 features randomly for generating a random rule

        int antecedent1 = rand.nextInt(size);
        int antecedent2 = rand.nextInt(size);
        while(antecedent1 == antecedent2)
            antecedent2 = rand.nextInt(size);

        int consequent = rand.nextInt(size);
        while(consequent == antecedent1 || consequent == antecedent2)
            consequent = rand.nextInt(size);

        mLookupTable.GenerateRandomValue(antecedent1, featureRequirements[antecedent1]);
        featureRequirements[antecedent1].setParticipation(1);

//        mLookupTable.GenerateRandomValue(antecedent2, featureRequirements[antecedent2]);
//        featureRequirements[antecedent2].setParticipation(1);

        mLookupTable.GenerateRandomValue(consequent, featureRequirements[consequent]);
        featureRequirements[consequent].setParticipation(2);




        return newRule;
    }

    public String TranslateRule(Rule rule)
    {
        return mLookupTable.TranslateRule(rule);
    }

    // private members
    private LookupTable mLookupTable;
}

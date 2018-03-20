import Rule.Rule;
import Rule.FeatureRequirement;
import java.util.Random;


/**
 * Class: RuleManager
 * Intended functionality: Util class that is in charge of mutations, cross-overs and rule generation
 * Feature Owner: Shane, David
 */


public class RuleManager
{
    
    private final int num_features;
    private static Random rand = new Random();

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
        // copy the rule
        Rule mutatedRule = parent.copy();

        // choose a random feature
        int featureId = rand.nextInt(num_features);
        // ensure that we are selecting a new participation value
        int participation = (rand.nextInt(2) + 1 + mutatedRule.getFeatureReq(featureId).getParticipation()) % 3;
        // we only care if this feature is now, or is still participating in the rule
        if (participation != 0) {
            // get a random feature value
            mLookupTable.GenerateRandomValue(featureId, mutatedRule.getFeatureReq(featureId));
        }
        // set the participation
        mutatedRule.getFeatureReq(featureId).setParticipation(participation);

        // return the rule
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

        Rule newRule = new Rule();

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


        //TODO: add
        mLookupTable.GenerateRandomValue(antecedent1, featureRequirements[antecedent1]);
        featureRequirements[antecedent1].setParticipation(1);

        mLookupTable.GenerateRandomValue(antecedent2, featureRequirements[antecedent2]);
        featureRequirements[antecedent2].setParticipation(1);

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

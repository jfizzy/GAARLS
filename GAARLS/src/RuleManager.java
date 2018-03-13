import Rule.Rule;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class: RuleManager
 * Intended functionality: Util class that is in charge of mutations, cross overs and rule generation
 * Feature Owner: Shane, David
 */


public class RuleManager
{
    
    private final int num_features = 23; // TODO set this to LookupTable.NUM_FEATURES
    
    // public methods
    public RuleManager(LookupTable lookupTable)
    {
        mLookupTable = lookupTable;
    }

    /**
     * Creates a new rule with @mutationTemplate as the base rule
     * NOTE: Does not modify the state of @mutationTemplate
     * @param mutationTemplate
     * @return a mutated rule
     */
    public Rule Mutate(Rule mutationTemplate)
    {
        Rule mutatedRule = mutationTemplate.copy();

        // mutate

        return mutatedRule;
    }

    /**
     * Creates a new rule with @parent1 and @parent2 as the base rules
     * @param parent1 
     * @param parent2 
     * @return merged rule (uglyChild) of @parent1 and @parent2
     */
    public Rule Crossover(Rule parent1, Rule parent2)
    {
        Rule uglyChild = new Rule(); // The offspring of parent1 and parent2 after a couple drinks and some bad life choices
        Random rand = new Random();
        int pivot = rand.nextInt(num_features-1); // Randomly selected pivot point (index of where the crossover will occur)
        
        for(int i = 0; i < pivot; i++)
            uglyChild.replaceFeatureRequirement(i, parent1.getFeatureReqs()[i]);
        
        for(int i = pivot; i < num_features; i++)
            uglyChild.replaceFeatureRequirement(i, parent2.getFeatureReqs()[i]);
        
        return uglyChild;
    }

    /**
     * Helper function to generate new rules in the population initialization phase
     * Creates a new rule with randomized requirements found from LookupTable
     * @return random new rule
     */
    public Rule GenerateRule()
    {
        Rule newRule = new Rule();
        // for i in range newRule.featureRequirement.size()
        // mLookupTable.GenerateRandomValue(i, newRule.featureRequirement[i]);
        return newRule;
    }

    public String TranslateRule(Rule rule)
    {
        return mLookupTable.TranslateRule(rule);
    }

    // private members
    private LookupTable mLookupTable;
}

import Rule.Rule;
import Rule.FeatureRequirement;
import java.util.ArrayList;


/**
 * Class: RuleManager
 * Intended functionality: Util class that is in charge of mutations, cross overs and rule generation
 * Feature Owner: Shane, David
 */


public class RuleManager
{
    // public methods
    public RuleManager(LookupTable lookupTable)
    {
        mLookupTable = lookupTable;
        Rule.setNumFeatures(lookupTable.NumFeatures);
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
     * @return merged rule of @parent1 and @parent2
     */
    public Rule Crossover(Rule parent1, Rule parent2)
    {
        ArrayList<Boolean> whoGetsWhatList = null; // do some calculations
        return parent1.Merge(parent2, whoGetsWhatList);
    }

    /**
     * Helper function to generate new rules in the population initialization phase
     * Creates a new rule with randomized requirements found from LookupTable
     * @return random new rule
     */
    public Rule GenerateRule()
    {
        // NOTE: Current implementation was created as a proof of concept. Needs to be revisited
        // -Peter
        Rule newRule = new Rule();
        FeatureRequirement[] featureRequirements = newRule.getFeatureReqs();
        for (int i = 0; i < featureRequirements.length; ++i)
        {
            mLookupTable.GenerateRandomValue(i, featureRequirements[i]);
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

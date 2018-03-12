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
    }

    /**
     * Creates a new rule with @mutationTemplate as the base rule
     * NOTE: Does not modify the state of @mutationTemplate
     * @param mutationTemplate
     * @return a mutated rule
     */
    public Rule mutate(Rule mutationTemplate)
    {
        Rule mutatedRule = mutationTemplate.Clone();

        // mutate

        return mutatedRule;
    }

    /**
     * Creates a new rule with @parent1 and @parent2 as the base rules
     * @param parent1
     * @param parent2
     * @return merged rule of @parent1 and @parent2
     */
    public Rule crossover(Rule parent1, Rule parent2)
    {
        ArrayList<Boolean> whoGetsWhatList = null; // do some calculations
        return parent1.Merge(parent2, whoGetsWhatList);
    }

    /**
     * Helper function to generate new rules in the population initialization phase
     * Creates a new rule with randomized requirements found from LookupTable
     * @return random new rule
     */
    public Rule generateRule()
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

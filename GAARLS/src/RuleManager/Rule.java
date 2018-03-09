package RuleManager;

import java.util.ArrayList;

/**
 * Class: Rule
 * Intended functionality: Container class of FeatureRequirements. Container size should be equal to LookupTable.NUM_FEATURES
 * Feature Owner: James
 */


public class Rule
{
    
    // private members
    private FeatureRequirement featureReqs[]; //  
    private final int features = 23; // should probably move this somewhere better
    
    
    
    // public methods
    public Rule()
    {
        this.featureReqs = new FeatureRequirement[this.features];
    }

    /**
     * Helper function to assist with mutation/cross overs
     * @return shallow copy of Rule
     */
    public Rule Clone()
    {
        // does a shallow copy of @mFeatureRequirements using FeatureRequirement.Clone()
        return new Rule();
    }

    /**
     * Potential util function for cross over. Not set in stone so feel free to change
     * @param toMerge parent2.
     *                NOTE: Not modified in function call
     * @param featuresToMerge bool flag list. false = parent1's feature. true = parent2's feature
     * @return
     */
    public Rule Merge(Rule toMerge, ArrayList<Boolean> featuresToMerge)
    {
        // assert @featuresToMerge.size() == mFeatureRequirements.size();
        // does a shallow copy of @mFeatureRequirements using FeatureRequirement.Clone()
        /*
        Rule rule = new Rule();

        for i in range mFeatureRequirements.size()
            if (featuresToMerge[i] == true
                rule.mFeatureRequirements[i] = toMerge.mFeatureRequirement[i].Clone()
            else
                rule.mFeatureRequirements[i] = mFeatureRequirement[i].Clone()

        */

        return new Rule();
    }

    // private methods

    
}

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
    // private members
    private LookupTable mLookupTable;
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

    /** Crossover Pivot: Completes a Crossover using traditional pivot point method.
     * Creates a new rule with @parent1 and @parent2 as the base rules
     * @param parent1
     * @param parent2
     * @return merged rule (child) of @parent1 and @parent2
     */
    public Rule crossover(Rule parent1, Rule parent2)
    {
        int pivot = rand.nextInt(num_features-2) + 1; // Randomly selected pivot point (index of where the crossover will occur)

        FeatureRequirement parent2FeatReqs[] = parent2.getFeatureReqs();
		Rule child = parent1.copy();
        FeatureRequirement childFeatReqs[] = child.getFeatureReqs();

        for(int i = pivot; i < num_features; i++) // Replace all elements after the pivot with parent 2's genes
				childFeatReqs[i] = parent2FeatReqs[i].copy();

        return child;
    }

    /** Crossover Uniform: Completes a crossover using a boolean array, each with 50% chance of being 1 or 0.
     *  Creates a new rule with @parent1 and @parent2 as the base rules
     * @param parent1
     * @param parent2
     * @return merged rule (child) of @parent1 and @parent2
     */
    public Rule crossoverUniform(Rule parent1, Rule parent2)
    {
        ArrayList<Boolean> randomBools = new ArrayList<>();

        // Generate random boolean array list.
        for(int i = 0; i < num_features; i++)
          randomBools.add(rand.nextBoolean());

        Rule child = parent1.merge(parent2, randomBools);
        return child;
    }

    /**
     * Helper function to generate new rules in the population initialization phase
     * Creates a new rule with randomized requirements found from LookupTable
     * @return random new rule
     */
    public Rule generateRule(int numFeatAntecedent, int numFeatConsequent)
    {
        Rule newRule = new Rule();

        FeatureRequirement[] featureRequirements = newRule.getFeatureReqs();
        int size = featureRequirements.length;

        ArrayList<Integer> antecedents = new ArrayList<>();
        ArrayList<Integer> consequents = new ArrayList<>();

        for (int i = 0; i < numFeatAntecedent; ++i) {
            int aIndex;
            do {
                aIndex = rand.nextInt(size);
            } while (antecedents.contains(aIndex));
            antecedents.add(aIndex);
        }

        for (int i = 0; i < numFeatConsequent; ++i) {
            int cIndex;
            do {
                cIndex = rand.nextInt(size);
            } while (consequents.contains(cIndex) || antecedents.contains(cIndex));
            consequents.add(cIndex);
        }

        for (int index : antecedents) {
            mLookupTable.GenerateRandomValue(index, featureRequirements[index]);
            featureRequirements[index].setParticipation(1);
        }

        for (int index : consequents) {
            mLookupTable.GenerateRandomValue(index, featureRequirements[index]);
            featureRequirements[index].setParticipation(2);
        }

        return newRule;
    }

    public static boolean IsValidRule(Rule rule)
    {
        final int antecedentParticipationValue = FeatureRequirement.pFlag.ANTECEDENT.getValue();
        final int consequentParticipationValue = FeatureRequirement.pFlag.CONSEQUENT.getValue();
        boolean antecedentPresent = false, consequentPresent = false;
        for (FeatureRequirement featureRequirement : rule.getFeatureReqs())
        {
            antecedentPresent |= featureRequirement.getParticipation() == antecedentParticipationValue;
            consequentPresent |= featureRequirement.getParticipation() == consequentParticipationValue;
        }
        return antecedentPresent && consequentPresent;
    }

    public String TranslateRule(Rule rule)
    {
        return mLookupTable.TranslateRule(rule);
    }


}

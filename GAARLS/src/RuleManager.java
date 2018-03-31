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
     * Creates a new rule with randomized requirements found from LookupTable, with a random number of
     * clauses
     * @return random new rule
     */
    //TODO: figure out why this is always returning a 0.0 fitness rule
    public Rule generateRuleRandomSize()
    {
        Rule newRule = new Rule();
        int numFeatures = 2 + rand.nextInt(num_features - 3);// -1 is to prevent setting C_CASE, +2 increases chances it is valid
        ArrayList<Integer> featuresInRule = new ArrayList(); // will hold the index of the features already active in the rule
        FeatureRequirement[] featureRequirements = newRule.getFeatureReqs();
        int feature;

        for(int i = 0; i < numFeatures; i ++){
            int participation = rand.nextInt(2) + 1; //participation will be either 1 or 2
            do{
                feature = rand.nextInt(num_features-1);
            } while (featuresInRule.contains(feature));
            featuresInRule.add(feature);
            mLookupTable.GenerateRandomValue(feature, featureRequirements[feature]);
            featureRequirements[feature].setParticipation(participation);
        }

        return newRule;
    }

    /**
     * Helper function to generate simple new rules in the population initialization phase
     * Creates a new rule with randomized requirements found from LookupTable, such that rules have
     * two clauses in the antecedent and one in the consequent.
     * @return random new rule
     */
    public Rule generateRuleThreeFeat()
    {
        Rule newRule = new Rule();

        FeatureRequirement[] featureRequirements = newRule.getFeatureReqs();
        int size = featureRequirements.length;

        // TODO: When no longer hard coding rules, ensure that there is always a valid number of antecedents and consequents
        { // TODO: Remove hard coded rule generation
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

        }
        return newRule;
    }

    /**
     * Helper function to generate new rules in the population initialization phase
     * Creates a new rule with randomized requirements found from LookupTable, with
     * the possibility that all features are activated.
     * @return random new rule
     */
    public Rule generateRuleMaxFeatures()
    {
        Rule newRule = new Rule();
        FeatureRequirement[] featureRequirements = newRule.getFeatureReqs();

        // TODO: When no longer hard coding rules, ensure that there is always a valid number of antecedents and consequents
        for(int i = 0; i < num_features; i++){ // TODO: Remove hard coded rule generation
            // choose 3 features randomly for generating a random rule
            int participation = rand.nextInt(2); // generate random participation value of 0,1, or 2
            featureRequirements[i].setParticipation(participation);
            if (participation != 0){
                mLookupTable.GenerateRandomValue(i, featureRequirements[i]);

            }
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

import Rule.Rule;
import Rule.FeatureRequirement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


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
    private ArrayList<FeatureRequirement> requiredFeatures;

    // public methods
    public RuleManager(LookupTable lookupTable, ArrayList<FeatureRequirement> requiredFeatures)
    {
        mLookupTable = lookupTable;
        num_features = lookupTable.NumFeatures;
        this.requiredFeatures = requiredFeatures;
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
        int featureId;
        boolean isImmutableFeature;
        boolean mutateParticipation;
        boolean isRequiredFeature;
        int wildcards = 0;
        do {
            featureId = rand.nextInt(num_features);
            isImmutableFeature = false;
            mutateParticipation = false;
            isRequiredFeature = false;
            for (FeatureRequirement required : requiredFeatures) {
                if (required.getFeatureID() == featureId) {
                    isRequiredFeature = true;
                    wildcards = getWildcardValue(required);
                    if (required.getParticipation() == -1) {
                        mutateParticipation = true;
                    }
                    if (wildcards == 0 && !mutateParticipation) {
                        isImmutableFeature = true;
                    }
                }
            }
        } while(isImmutableFeature);
        if (isRequiredFeature) {
            if (mutateParticipation) {
                mutatedRule.getFeatureReq(featureId).setParticipation(((mutatedRule.getFeatureReq(featureId).getParticipation() + 2) % 2) + 1);
            }
            if (wildcards > 0) {
                mLookupTable.GenerateRandomValue(featureId, mutatedRule.getFeatureReq(featureId), wildcards);
            }
        } else {
            // ensure that we are selecting a new participation value
            int participation = (rand.nextInt(2) + 1 + mutatedRule.getFeatureReq(featureId).getParticipation()) % 3;
            // we only care if this feature is now, or is still participating in the rule
            if (participation != 0) {
                // get a random feature value
                mLookupTable.GenerateRandomValue(featureId, mutatedRule.getFeatureReq(featureId), 3);
            }
            // set the participation
            mutatedRule.getFeatureReq(featureId).setParticipation(participation);
        }
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
    public Rule generateRuleRandomSize()
    {
        Rule newRule = new Rule();
        int numFeatures = 2 + rand.nextInt(num_features - 3) - requiredFeatures.size();// -1 is to prevent setting C_CASE, +2 increases chances it is valid
        FeatureRequirement[] featureRequirements = newRule.getFeatureReqs();
        ArrayList<Integer> featuresInRule; // will hold the index of the features already active in the rule

        // place required features if we have any
        if (requiredFeatures.size() > 0) {
            featuresInRule = placeRequiredFeatures(featureRequirements);
        } else {
            featuresInRule = new ArrayList();
        }

        int feature;

        for(int i = 0; i < numFeatures; i ++){
            int participation = rand.nextInt(2) + 1; //participation will be either 1 or 2
            do{
                feature = rand.nextInt(num_features-1);
            } while (featuresInRule.contains(feature));
            featuresInRule.add(feature);
            mLookupTable.GenerateRandomValue(feature, featureRequirements[feature], 3);
            featureRequirements[feature].setParticipation(participation);
        }
        return newRule;
    }

    /**
     * Helper function to generate simple new rules in the population initialization phase
     * Creates a new rule with randomized requirements found from LookupTable, such that rules have
     * the specified number of clauses.
     * @param numFeatAntecedent
     * @param numFeatConsequent
     * @return random new rule
     */
    public Rule generateRule(int numFeatAntecedent, int numFeatConsequent)
    {
        Rule newRule = new Rule();

        FeatureRequirement[] featureRequirements = newRule.getFeatureReqs();
        int size = featureRequirements.length;

        ArrayList<Integer> placedAntecedents = new ArrayList<>();
        ArrayList<Integer> placedConsequents = new ArrayList<>();

        if (requiredFeatures.size() > 0) {
            placeRequiredFeaturesAndCount(featureRequirements, placedAntecedents, placedConsequents);
        }

        ArrayList<Integer> antecedents = new ArrayList<>();
        ArrayList<Integer> consequents = new ArrayList<>();

        for (int i = 0; i < numFeatAntecedent - placedAntecedents.size(); ++i) {
            int aIndex;
            do {
                aIndex = rand.nextInt(size);
            } while (antecedents.contains(aIndex) || placedAntecedents.contains(aIndex) || placedConsequents.contains(aIndex));
            antecedents.add(aIndex);
        }

        for (int i = 0; i < numFeatConsequent - placedConsequents.size(); ++i) {
            int cIndex;
            do {
                cIndex = rand.nextInt(size);
            } while (consequents.contains(cIndex) || antecedents.contains(cIndex) || placedAntecedents.contains(cIndex) || placedConsequents.contains(cIndex));
            consequents.add(cIndex);
        }

        for (int index : antecedents) {
            mLookupTable.GenerateRandomValue(index, featureRequirements[index], 3);
            featureRequirements[index].setParticipation(1);
        }

        for (int index : consequents) {
            mLookupTable.GenerateRandomValue(index, featureRequirements[index], 3);
            featureRequirements[index].setParticipation(2);
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
        ArrayList<Integer> featuresInRule;

        if (requiredFeatures.size() > 0) {
            featuresInRule = placeRequiredFeatures(featureRequirements);
        } else {
            featuresInRule = null;
        }

        // TODO: When no longer hard coding rules, ensure that there is always a valid number of antecedents and consequents
        for(int i = 0; i < num_features; i++){ // TODO: Remove hard coded rule generation
            if (featuresInRule == null || !featuresInRule.contains(i)) {
                // choose 3 features randomly for generating a random rule
                int participation = rand.nextInt(3); // generate random participation value of 0,1, or 2
                featureRequirements[i].setParticipation(participation);
                if (participation != 0) {
                    mLookupTable.GenerateRandomValue(i, featureRequirements[i], 3);

                }
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

    private int getWildcardValue(FeatureRequirement required) {
        // wildcards indicates which features we can randomly generate versus which are fixed
        // this is so we only have to hand an integer value through to GenerateRandomValue
        // 0 indicates no wildcards, don't do a generation, our values were hardcoded
        // 1 indicates just the min value is a wildcard
        // 2 indicates just the max value is a wildcard
        // 3 indicates that both are a wildcard
        int wildcards = 0;
        if (Float.compare(required.getLowerBound(), -1f) == 0) {
            wildcards += 1;
        }
        if (Float.compare(required.getUpperBound(), -1f) == 0) {
            wildcards += 2;
        }
        return wildcards;
    }

    private ArrayList<Integer> placeRequiredFeatures(FeatureRequirement[] featureRequirements) {
        ArrayList<Integer> featuresInRule = new ArrayList<>();

        for (FeatureRequirement required : requiredFeatures) {
            int featureID = required.getFeatureID();
            int participation = required.getParticipation();
            // 1 or 2 for wildcard participation
            if (participation == -1) {
                participation = 1 + rand.nextInt(2);
            }
            featureRequirements[featureID].setParticipation(participation);
            int wildcards = getWildcardValue(required);
            // set the bounds here, this way if we need to flip the range values in GenerateRandomRule
            // we already know what our fixed value is (think hardcoded min with wildcard max on a non-wrapping range feature)
            featureRequirements[featureID].setBoundRange(required.getLowerBound(), required.getUpperBound(), 0);
            // only generate if we have wildcards, otherwise we just needed to unpack the required feature
            // into our new rule, which is already done (boundrange and participation are set)
            if (Float.compare(featureRequirements[featureID].getLowerBound(), -1f) == 0 || Float.compare(featureRequirements[featureID].getUpperBound(), -1f) == 0) {
                mLookupTable.GenerateRandomValue(featureID, featureRequirements[featureID], wildcards);
            }
            featuresInRule.add(required.getFeatureID());
        }
        return  featuresInRule;
    }

    private ArrayList<Integer> placeRequiredFeaturesAndCount(FeatureRequirement[] featureRequirements, ArrayList<Integer> placedAntecedents, ArrayList<Integer> placedConsequents) {
        ArrayList<Integer> featuresInRule = new ArrayList<>();

        for (FeatureRequirement required : requiredFeatures) {
            int featureID = required.getFeatureID();
            int participation = required.getParticipation();
            // 1 or 2 for wildcard participation
            if (participation == -1) {
                participation = 1 + rand.nextInt(2);
            }
            if (participation == 1) {
                placedAntecedents.add(featureID);
            } else {
                placedConsequents.add(featureID);
            }
            featureRequirements[featureID].setParticipation(participation);
            int wildcards = getWildcardValue(required);
            // set the bounds here, this way if we need to flip the range values in GenerateRandomRule
            // we already know what our fixed value is (think hardcoded min with wildcard max on a non-wrapping range feature)
            featureRequirements[featureID].setBoundRange(required.getLowerBound(), required.getUpperBound(), 0);
            // only generate if we have wildcards, otherwise we just needed to unpack the required feature
            // into our new rule, which is already done (boundrange and participation are set)
            if (Float.compare(featureRequirements[featureID].getLowerBound(), -1f) == 0 || Float.compare(featureRequirements[featureID].getUpperBound(), -1f) == 0) {
                mLookupTable.GenerateRandomValue(featureID, featureRequirements[featureID], wildcards);
            }
            featuresInRule.add(required.getFeatureID());
        }
        return  featuresInRule;
    }

    public String TranslateRule(Rule rule)
    {
        return mLookupTable.TranslateRule(rule);
    }


}

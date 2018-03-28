
import Rule.*;

import java.util.ArrayList;

/**
 * Class: FitnessManager
 * Intended functionality: Util class that is in charge of all fitness function calculations, using the feature database
 * for value queries
 * Feature Owner: Shane, Evan, David
 */


public class FitnessManager {

    // private members
    private Database theDatabase;
    private ArrayList<Rule> theWekaRules;

    // public functions
    public FitnessManager(Database database, ArrayList<Rule> wekaRules)
    {
        theDatabase = database;
        theWekaRules = wekaRules;
    }

    /**
     * Calculate the fitness of a rule given internal fitness functions
     * @param rule to evaluate
     *             NOTE: information relevant to rule will be cached inside rule at the same time
 *                       will not change functionality of rule
     * @return fitness value of rule
     */
    public float fitnessOf(Rule rule) {
        //TODO: Remove when Mutate, Crossover and Generate rule ensure that a rule is valid at creation time
        //TODO: assign weights
        float w1 = 1.0f;
        float w2 = 1.0f;
        float w3 = 1.0f;

        if (!RuleManager.IsValidRule(rule))
            return 0;

        if(theWekaRules.size() == 0) {
            return fitnessBasic(rule);
        }
        else{
            float fitness = (w1*fitnessBasic(rule) + w2*ext1(rule) + w3*ext2(rule));
            return fitness;
        }
    }

    //Basic version of fitness function:

    /**
     *
     * @param rule
     * @return fitness value between 0.0 and 1.0
     */
    private float fitnessBasic(Rule rule){
        theDatabase.EvaluateRule(rule);                       // Initializes coverage and accuracy values in rule
        float coverage = rule.getCoverage();
        float accuracy = rule.getAccuracy();
        float rangeFitness = rule.getRangeCoverage();

        float fitnessBase = ((coverage + accuracy + rangeFitness)/3.0f);
        return fitnessBase;
    }

    /**
     * This method computes a Hamming distance like measure between two given Rules. In this version
     * we define 'distance' as number of non-identical clauses in the two rules.
     * TODO: extend this by determining % overlap of a given feature domain for each matching participation
     * value, and add this to the distance value.
     *
     * @param r1: first rule
     * @param r2: second rule
     * @return  the number of elements of the featureRequirement vector 'participation' values that are different between the two rules, normalized
     * to value between 0.0 and 1.0
     */

    protected static float hammingDistance(Rule r1, Rule r2){
        int distance = 0;

        FeatureRequirement[] r1FeatureVector = r1.getFeatureReqs();
        FeatureRequirement[] r2FeatureVector = r2.getFeatureReqs();
        for(int i = 0; i < r1FeatureVector.length; i++){
            if(r1FeatureVector[i].getParticipation() != r2FeatureVector[i].getParticipation())
                distance++;
        }
        return (distance/r1.getFeatureReqs().length);
    }

    private float ext1(Rule rule){
        float smallestDistance = rule.getFeatureReqs().length;   // holds the smallest Hamming distance found to exist between rule and anu rule in known rules
        for(Rule wekaRule : theWekaRules){
            float dist = hammingDistance(rule, wekaRule);
            if(dist < smallestDistance) {
                smallestDistance = dist;
            }
        }

        return smallestDistance;
    }

    /**
     *
     * @param rule
     * @return value between 0.0 and 1.0
     */
    private float ext2(Rule rule){
        return rule.getCompleteness();
    }

    public static void main(String[] args){

    }
}

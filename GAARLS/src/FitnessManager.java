
import Rule.*;

import java.util.ArrayList;
import java.util.Scanner;

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
     * @return float fitness value of rule
     */
    public float fitnessOf(Rule rule) {
        //TODO: Remove when Mutate, Crossover and Generate rule ensure that a rule is valid at creation time
        if (!RuleManager.IsValidRule(rule))
            return 0;


        if(theWekaRules.size() == 0) {
            return fitnessBasic(rule);
        }
        else{
            float fitness = fitnessBasic(rule) + ext1(rule) + ext2(rule);
            return fitness;
        }
    }

    //Basic version of fitness function:
    private float fitnessBasic(Rule rule){
        theDatabase.EvaluateRule(rule);                       // Initializes coverage and accuracy values in rule
        float coverage = rule.getCoverage()*100;
        float accuracy = rule.getAccuracy()*100;
        int sizeOfTheDatabase = theDatabase.getNumDataItems();

        float fitnessBase = (((coverage/(float)sizeOfTheDatabase) + accuracy)/2.0f);
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
     * @return  the number of elements of the featureRequirement vector 'participation' values that are different between the two rules
     */

    private int hammingDistance(Rule r1, Rule r2){
        int distance = 0;

        FeatureRequirement[] r1FeatureVector = r1.getFeatureReqs();
        FeatureRequirement[] r2FeatureVector = r2.getFeatureReqs();
        for(int i = 0; i < r1FeatureVector.length; i++){
            if(r1FeatureVector[i].getParticipation() != r2FeatureVector[i].getParticipation())
                distance++;
        }
        return distance;
    }

    private float ext1(Rule rule){
        int smallestDistance = 23;   // holds the smallest Hamming distance found to exist between rule and anu rule in known rules
        for(Rule wekaRule : theWekaRules){
            int dist = hammingDistance(rule, wekaRule);
            if(dist < smallestDistance) {
                smallestDistance = dist;
            }
        }

        return smallestDistance;
    }

    private float ext2(Rule rule){
        float rangeFitness = rule.getRangeCoverage(); // TODO: Hook up to fitness equation
        float completeness = rule.getCompleteness();// TODO: Hook up to fitness equation
        return rangeFitness * completeness;
    }

    public static void main(String[] args){
        //testing ext1
        // TODO: moving this testing to JUnit test

        FeatureRequirement[] r1FeatureVector = new FeatureRequirement[23];
        FeatureRequirement[] r2FeatureVector = new FeatureRequirement[23];
        try {
           for (int i = 0; i < 23; i++) {
               r1FeatureVector[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value
               r2FeatureVector[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value

           }
            r1FeatureVector[3] = new FeatureRequirement(1, 1, 0f, 0f, 0f); // default initial value
            r2FeatureVector[1] = new FeatureRequirement(1, 1, 0f, 0f, 0f); // default initial value
        } catch (FeatureRequirement.InvalidFeatReqException ifre) {
            System.out.println(ifre.getMessage());
        }


        Rule rule1 = new Rule(r1FeatureVector);
        Rule rule2 = new Rule(r2FeatureVector);
        //System.out.println("Hamming distance is: " + hammingDistance(rule1,rule2)); // distance should be 1

    }
}

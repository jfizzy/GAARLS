
import Rule.*;
/**
 * Class: FitnessManager
 * Intended functionality: Util class that is in charge of all fitness function calculations, using the feature database
 * for value queries
 * Feature Owner: Shane, Evan, David
 */


public class FitnessManager {

    // private members
    private Database theDatabase;

    // public functions
    public FitnessManager(Database database)
    {
        theDatabase = database;
    }

    /**
     * Calculate the fitness of a rule given internal fitness functions
     * @param rule to evaluate
     *             NOTE: information relevant to rule will be cached inside rule at the same time
 *                       will not change functionality of rule
     * @return float fitness value of rule
     */
    public float fitnessOf(Rule rule)
    {
        theDatabase.EvaluateRule(rule);                       // Initializes coverage and accuracy values in rule
        //Basic version of fitness function:
        float coverage = rule.getCoverage()*100;
        float accuracy = rule.getAccuracy()*100;

        float rangeFitness = calculateRangeFitness(rule); // TODO: Hook up to fitness equation

        int sizeOfTheDatabase = theDatabase.getNumDataItems();

        float fitnessBase = (((coverage/(float)sizeOfTheDatabase) + accuracy)/2.0f);
        return fitnessBase;
    }

    /**
     * Range fitness is the product of normalized feature values in the rule. ie featureRange1 * featureRange2 * featureRange3
     * @param rule
     * @return
     */
    private float calculateRangeFitness(Rule rule)
    {
        float cumRangeFitness = 1.0f;
        for (FeatureRequirement feature : rule.getFeatureReqs())
        {
            if (feature.getParticipation() != FeatureRequirement.pFlag.IGNORE.getValue())
            {
                float rangeFitness = 1 - feature.getRangeCoverage();
                cumRangeFitness *= rangeFitness;
            }
        }
        return cumRangeFitness;
    }
}

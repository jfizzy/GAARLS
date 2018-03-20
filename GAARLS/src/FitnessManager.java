
import Rule.Rule;
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

        int sizeOfTheDatabase = theDatabase.getNumDataItems();

        float fitnessBase = (((coverage/(float)sizeOfTheDatabase) + accuracy)/2.0f) * 100.0f;
        return fitnessBase;
    }
}


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
        //TODO: Remove when Mutate, Crossover and Generate rule ensure that a rule is valid at creation time
        if (!RuleManager.IsValidRule(rule))
            return 0;

        theDatabase.EvaluateRule(rule);                       // Initializes coverage and accuracy values in rule
        //Basic version of fitness function:
        float coverage = rule.getCoverage()*100;
        float accuracy = rule.getAccuracy()*100;

        float rangeFitness = rule.getRangeCoverage(); // TODO: Hook up to fitness equation
        float completeness = rule.getCompleteness();// TODO: Hook up to fitness equation

        int sizeOfTheDatabase = theDatabase.getNumDataItems();

        float fitnessBase = (((coverage/(float)sizeOfTheDatabase) + accuracy)/2.0f);
        return fitnessBase;
    }

}

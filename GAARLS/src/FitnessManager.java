
import Rule.Rule;

/**
 * Class: FitnessManager
 * Intended functionality: Util class that is in charge of all fitness function calculations, using the feature database
 * for value queries
 * Feature Owner: Shane, Evan, David
 */


public class FitnessManager
{
    // public functions
    public FitnessManager(Database database)
    {
        mDatabase = database;
    }

    /**
     * Calculate the fitness of a rule given internal fitness functions
     * @param rule to evaluate
     *             NOTE: information relevant to rule will be cached inside rule at the same time
 *                       will not change functionality of rule
     * @return normalized value between 0-1 TODO: Shane is this correct?
     */
    public float FitnessOf(Rule rule)
    {
        // get rule to do burpees until it pukes
        return 0; // rule apparently is out of shape
    }

    // private functions

    // private members
    private Database mDatabase;
}

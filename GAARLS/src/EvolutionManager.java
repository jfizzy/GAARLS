import RuleManager.Rule;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class: EvolutionManager
 * Intended functionality: Main work horse class for the evolutionary process of rule learning.
 *
 * External Usage:
 * 1. InitializePopulation()
 * 2. Evolve()
 * 3. PrintToFile()
 *
 * Feature Owner: Shane, David, Evan
 */


public class EvolutionManager
{
    // publid methods
    public EvolutionManager(Database database, LookupTable lookupTable)
    {
        mFitnessManager = new FitnessManager(database);
        mRuleManager = new RuleManager(lookupTable);
        mSortedPopulation = new ArrayList<>();
    }

    /**
     *  Initialization function creating an initial population
     *  @param populationSize: initial population size
     */
    public void IntializePopulation(int populationSize)
    {
        // create @populationSize rules
        // calculate fitness of rule and add to population
    }

    public void Evolve(int forGenerations)
    {
        int numGenerations = 0;
        while (numGenerations < forGenerations)
        {
            // NOTE: Example Flow
            // pick some rules to mutate and crossover
            Rule mutateRule = new Rule(),
                    parent1 = new Rule(),
                    parent2 = new Rule();
            ArrayList<Rule> newRules = new ArrayList<>();
            newRules.add(mRuleManager.Mutate(mutateRule));
            newRules.add(mRuleManager.Crossover(parent1, parent2));

            // evaluate the fitness of the new rules and add to population
            for (int i = 0; i < newRules.size(); ++i)
            {

                mSortedPopulation.add(new Pair<Float, Rule>(mFitnessManager.FitnessOf(newRules.get(i)), newRules.get(i)));
            }
            // sort new population
            mSortedPopulation.sort(new Comparator<Pair<Float, Rule>>()
            {
                @Override
                public int compare(Pair<Float, Rule> o1, Pair<Float, Rule> o2)
                {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });

            // remove some rules
            // ...
            ++numGenerations;
        }
    }

    /**
     * Outputs the evolved populate to file
     * @param filePath
     */
    public void ToFile(String filePath)
    {
        // output to file
        // example for getting a line in the file
        String lineInFile = mRuleManager.TranslateRule(mSortedPopulation.get(0).getValue());
    }

    // private methods

    // private members
    ArrayList<Pair<Float, Rule>> mSortedPopulation; // Pair.key = fitness value Pair.value = rule. NOTE: Data structure is completely open for suggestions

    FitnessManager mFitnessManager;
    RuleManager mRuleManager;
}

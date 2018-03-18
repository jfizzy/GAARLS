import Rule.Rule;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

/**
 * EvolutionManager.java
 *
 * Intended functionality: Main work horse class for the evolutionary process of rule learning.
 *
 * External Usage:
 * 1. InitializePopulation()
 * 2. searchControl()
 * 3. PrintToFile()
 *
 * @author shane.sims.ss@gmail.com
 * @version 9 March 2018
 */


public class EvolutionManager
{

    // private members
    //private ArrayList<Pair<Float, Rule>> state;
    private FitnessManager theFitnessManager;                                             // Has function to evaluate fitness of an individual
    private RuleManager theRuleManager;                                                   // Has functions for crossover and mutation
//  private ArrayList<Rule> knownRules;                                                   // TODO: need this to be passed in from main
    private int crossToMut;
    private int crossoversDone;

    // public methods

    /**
     * 
     * @param database: the dataset file converted to Database structure
     * @param lookupTable: table of allowable feature values
     * @param crossToMut: number of crossover operations to perform between each mutation
     */
    public EvolutionManager(Database database, LookupTable lookupTable, int crossToMut) {
        theFitnessManager = new FitnessManager(database);
        theRuleManager = new RuleManager(lookupTable);
        this.crossToMut = crossToMut;
        crossoversDone = 0;

    }

    /**
     *  Initialization function creating an initial population
     *  @param populationSize: initial population size
     */
    private ArrayList<Pair<Float, Rule>> initializePopulation(int populationSize) {

        ArrayList<Pair<Float, Rule>> state = new ArrayList<>();

        Rule potentialRule;
        Float ruleFitness;
        for(int i = 0; i < populationSize; i++){
            potentialRule = new Rule();
        //  while(knownRules.contains(potentialRule)
        //      potentialRule = new Rule();
            ruleFitness = theFitnessManager.fitnessOf(potentialRule);
            state.add(new Pair<>(ruleFitness, potentialRule));
        }
        return state;
    }

    /**
     *
     * All parameters are tunable
     *
     * @param startSize: size of initial population
     * @param forGenerations: stop condition
     * @param maxPop: max state size
     */
    public void evolve(int startSize, int forGenerations, int maxPop) {
        int numGenerations = 0;
        int cullToSize = maxPop - 100;
        ArrayList<Pair<Float,Rule>> state = this.initializePopulation(startSize);



        System.out.println("Size of initial pop: " + state.size());
        System.out.println("FIrst individual: " + state.get(0));

        while (numGenerations < forGenerations) {
            state = fSelect(state);

            if(state.size() > maxPop) {
                while(state.size() > cullToSize) {
                    state.remove(state.size() - 1);
                }
            }
            numGenerations++;
        }
    }

    /**
     * Create new state from current one by application of genetic operations
     */
    private ArrayList<Pair<Float, Rule>> fSelect(ArrayList<Pair<Float, Rule>> aState){
        ArrayList<Pair<Float, Rule>> nextState = aState;

        // Order population by decreasing order of fitness
        nextState.sort(new Comparator<Pair<Float, Rule>>()
        {
            @Override
            public int compare(Pair<Float, Rule> o1, Pair<Float, Rule> o2)
            {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        // Determine overall fitness score of population, FIT
        Float FIT = 0.0f;
        for(int i = 0; i < nextState.size(); i++){
            FIT += nextState.get(i).getKey();
        }

        // Associate to each individual, an portion of fitnessInterval according to their fitness
        // Note: As spots are determined with floor function, there may be an extra index available at the end
        // of fitness interval. This will hold 0 (null) and thus be allocated to the most fit individual.
        int[] fitnessInterval = new int[(int) Math.ceil(FIT)];
        int spotsAllocated = 0;
        for(int i = 0; i < nextState.size(); i++){
            int spots = (int) Math.floor(nextState.get(i).getKey());
            for(int j = spotsAllocated; j < spots; j++){
                fitnessInterval[j] = i;
                spotsAllocated++;
            }

        }
        // Select individual(s) for genetic operation and call
        Random rand = new Random();

        if(crossoversDone == crossToMut){                   // Do mutation
            crossoversDone = 0;
            int parentIndex = fitnessInterval[rand.nextInt((int) Math.ceil(FIT))];
            Rule parent = nextState.get(parentIndex).getValue();
            Rule child = theRuleManager.mutate(parent);
            Float childFitness = theFitnessManager.fitnessOf(child);
            nextState.add(new Pair<>(childFitness, child));
        }
        else{                                               // Do crossover
            int parent1Index, parent2Index;
            do {
                parent1Index = fitnessInterval[rand.nextInt((int) Math.ceil(FIT))];
                parent2Index = fitnessInterval[rand.nextInt((int) Math.ceil(FIT))];
            } while (parent1Index == parent2Index);
            Rule parent1 = nextState.get(parent1Index).getValue();
            Rule parent2 = nextState.get(parent2Index).getValue();
            Rule child = theRuleManager.crossover(parent1,parent2);
            Float childFitness = theFitnessManager.fitnessOf(child);
            nextState.add(new Pair<>(childFitness, child));
            crossoversDone++;
        }
        return nextState;
    }

    /**
     * Prints population to output file as rules
     * @param filePath
     */
/*    public void toFile(String filePath)
    {
        // output to file
        // example for getting a line in the file
        String lineInFile = theRuleManager.TranslateRule(state.get(0).getValue());
    }

*/

}

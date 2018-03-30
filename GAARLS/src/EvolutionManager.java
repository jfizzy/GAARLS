import Rule.Rule;
import javafx.util.Pair;

import java.io.*;
import java.util.*;

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

    private static Random rand = new Random();

    // private members
    //private ArrayList<Pair<Float, Rule>> state;
    private FitnessManager theFitnessManager;                                             // Has function to evaluate fitness of an individual
    private RuleManager theRuleManager;                                                   // Has functions for crossover and mutation
    private ArrayList<Rule> knownRules;
    private int crossToMut;
    private int crossoversDone;
    private ArrayList<Pair<Float,Rule>> state;                                            // All the current Fitness/Rule pairs.

    // public methods

    /**
     * 
     * @param database: the dataset file converted to Database structure
     * @param lookupTable: table of allowable feature values
     * @param crossToMut: number of crossover operations to perform between each mutation
     */
    public EvolutionManager(Database database, LookupTable lookupTable, ArrayList<Rule> knownRules, ArrayList<Rule> wekaRules, int crossToMut) {
        theFitnessManager = new FitnessManager(database, wekaRules);
        theRuleManager = new RuleManager(lookupTable);
        this.knownRules = knownRules;
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
        float ruleFitness;

        for(int i = 0; i < populationSize; i++){
            do {
                potentialRule = theRuleManager.generateRule();
                ruleFitness = theFitnessManager.fitnessOf(potentialRule);
            } while (ruleFitness == 0 || state.contains(potentialRule) || knownRules.contains(potentialRule));

            if((i > 0) && (i%100 == 0)) {
                System.out.println(i + " total rules added to initial population");
            }

            state.add(new Pair<>(ruleFitness, potentialRule));
        }
        System.out.println(populationSize + " rules added to initial population");

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

        System.out.println("Generating initial population...");
        state = this.initializePopulation(startSize);

        Scanner input = new Scanner(System.in);
        System.out.println("Initial population generated. Press RETURN to begin evolution of rules.");
        System.out.print(input.nextLine());

        while (numGenerations < forGenerations) {
            state = fSelect(state);

            if(state.size() > maxPop) {
                System.out.println("\nPopulation size: " + state.size());
                System.out.println("Max population size exceeded. Trimming 100 individuals...");
                while(state.size() > cullToSize) {
                    state.remove(state.size() - 1);
                }
                System.out.println("\nPopulation size: " + state.size());
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
        nextState.sort(new Comparator<Pair<Float, Rule>>() {
            @Override
            public int compare(Pair<Float, Rule> o1, Pair<Float, Rule> o2)
            {
                return o2.getKey().compareTo(o1.getKey());
            }
        });
        System.out.println("Fitness of fittest: " + nextState.get(0).getKey());
        System.out.println("Fitness of weakest: " + nextState.get(nextState.size()-1).getKey());


        Float FIT = 0.0f;
        for(int i = 0; i < nextState.size(); i++){
            FIT += nextState.get(i).getKey();
        }

        System.out.println("Average rule fitness: " + ((FIT)/((float)nextState.size())));



        // Associate to each individual, an portion of fitnessInterval according to their fitness
        // Note: As spots are determined with floor function, there may be an extra index available at the end
        // of fitness interval. This will hold 0 (null) and thus be allocated to the most fit individual.
        int[] fitnessInterval = new int[(int) Math.ceil(FIT)];
        int spotsAllocated = 0;
        for(int i = 0; i < nextState.size(); i++){
            int spots = (int) Math.ceil(nextState.get(i).getKey());
            int lastSpot = spots + spotsAllocated - 1;
            for(int j = spotsAllocated; j < lastSpot; j++){
                fitnessInterval[j] = i;
                spotsAllocated++;
            }
        }


        // Select individual(s) for genetic operation and call
        if(crossoversDone == crossToMut){                   // Do mutation

            crossoversDone = 0;
            Rule child;
            do {
                int parentIndex = fitnessInterval[rand.nextInt((int) Math.ceil(FIT))];
                Rule parent = nextState.get(parentIndex).getValue();
                child = theRuleManager.mutate(parent);
            } while (!RuleManager.IsValidRule(child) || nextState.contains(child) || knownRules.contains(child));

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
            
            int crossOverAttempts = 0;
            while(!RuleManager.IsValidRule(child) || nextState.contains(child) || knownRules.contains(child)) { //keep going until pivot point produces valid Rule
                child = theRuleManager.crossover(parent1, parent2);
                crossOverAttempts += 1;
                //This will prevent hanging in the event where a unique child cant be made given the state and the parents
                if(crossOverAttempts == 100){
                    crossOverAttempts = 0;
                    System.out.println("Unable to generate unique child from crossover operation. Mutating child.\nConsider increasing mutation rate.");
                    child = theRuleManager.mutate(child);
                }
            }
            
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
    public void toFile(String filePath) {
        float minAccuracy = 0.9f;           // TODO: make this a global parameter, entered at runtime. Alternatively, print some % of top rules
        File f = new File(filePath);

        if(f.exists() && !f.isDirectory()) {

        }
        else{
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try(FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {

            out.println("BEGINNING OF RULE OUTPUT FOR GIVEN RUN");
            for(int i = 0; i < state.size(); i++) {
                Pair individual = state.get(i);
                float individualsAccuracy = (float) individual.getKey();
                if (Float.compare(individualsAccuracy, minAccuracy) >= 0) {
                    String lineInFile = theRuleManager.TranslateRule(state.get(i).getValue());
                    out.println("Rule Accuracy: " + individualsAccuracy + "; " + lineInFile);
                }
            }
            System.out.println("Are first and second rule equal? : " + (state.get(0)).equals(state.get(1)));
            out.println("END OF RULE OUTPUT FOR GIVEN RUN");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

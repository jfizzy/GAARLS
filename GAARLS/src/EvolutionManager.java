import Rule.Rule;
import Rule.RuleRegex;
import javafx.util.Pair;
import Rule.FeatureRequirement;

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
    private ArrayList<RuleRegex> knownRegexs;
    ArrayList<FeatureRequirement> requiredFeatures;

    private ArrayList<Rule> wekaRules;
    private ConfigParameters cp;

    private int maxPopulation;
    private int crossToMute;
    private int initialSize;
    private int individualsToTrim;
    private int maxGenerations;
    private int numFeatAntecedent;
    private int numFeatConsequent;

    private int crossoversDone;
    private ArrayList<Pair<Float,Rule>> state;                                            // All the current Fitness/Rule pairs.

    // public methods

    /**
     * 
     * @param database: the dataset file converted to Database structure
     * @param lookupTable: table of allowable feature values
     * @param cp: set of config parameters
     */
    public EvolutionManager(Database database, LookupTable lookupTable, ArrayList<Rule> knownRules, ArrayList<RuleRegex> knownRegexs, ArrayList<Rule> wekaRules, ConfigParameters cp) {
        this.theFitnessManager = new FitnessManager(database, wekaRules, cp);
        this.knownRules = knownRules;
        this.knownRegexs = knownRegexs;
        this.wekaRules = wekaRules;
        this.maxPopulation = cp.populationMax;
        this.crossToMute = cp.crossToMute;
        this.initialSize = cp.initialPopSize;
        this.individualsToTrim = cp.individualsToTrim;
        this.maxGenerations = cp.numGenerations;
        this.numFeatAntecedent = cp.numFeatAntecedent;
        this.numFeatConsequent = cp.numFeatConsequent;
        this.requiredFeatures = cp.requiredFeatures;
        this.crossoversDone = 0;
        this.theRuleManager = new RuleManager(lookupTable, requiredFeatures);
    }

    /**
     *  Initialization function creating an initial population
     */
    private ArrayList<Pair<Float, Rule>> initializePopulation() {

        ArrayList<Pair<Float, Rule>> state = new ArrayList<>();
        Rule potentialRule;
        float ruleFitness;
        Pair ruleAndFit;
        boolean regexMatch;
        for(int i = 0; i < initialSize; i++){
            do {
                potentialRule = theRuleManager.generateRuleRandomSize();
               //potentialRule = theRuleManager.generateRule(numFeatAntecedent, numFeatConsequent);

                regexMatch = false;

                for (RuleRegex regex : knownRegexs) {
                    if (regex.matches(potentialRule)) {
                        regexMatch = true;
                        break;
                    }
                }

                ruleFitness = theFitnessManager.fitnessOf(potentialRule);
                ruleAndFit = new Pair<>(ruleFitness,potentialRule);
            } while (!RuleManager.IsValidRule(potentialRule) || state.contains(ruleAndFit) || knownRules.contains(potentialRule) || regexMatch);

            if((i > 0) && (i%100 == 0)) {
                System.out.println(i + " total rules added to initial population");
            }

            state.add(ruleAndFit);
        }
        System.out.println(initialSize + " rules added to initial population");

        return state;
    }

    public void evolve() {
        int numGenerations = 0;
        int cullToSize = (maxPopulation - this.individualsToTrim > 0) ? maxPopulation - this.individualsToTrim : (maxPopulation/10);
        // handles a mismatch between max pop and num individuals to trim
        // defauts to 10% of max pop size
        System.out.println("Generating initial population...");
        state = this.initializePopulation();

        Scanner input = new Scanner(System.in);
        System.out.println("Initial population generated. Press RETURN to begin evolution of rules.");
        System.out.print(input.nextLine());

        while (numGenerations < maxGenerations) {
            state = fSelect(state);

            if(state.size() > maxPopulation) {
                System.out.println("\nPopulation size: " + state.size());
                System.out.println("Max population size exceeded. Trimming "+cullToSize+" individuals...");
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
        if(crossoversDone == crossToMute){                   // Do mutation

            crossoversDone = 0;
            Rule child;
            Pair childAndFit;
            boolean regexMatch;
            do {
                int parentIndex = fitnessInterval[rand.nextInt((int) Math.ceil(FIT))];
                Rule parent = nextState.get(parentIndex).getValue();
                child = theRuleManager.mutate(parent);

                regexMatch = false;

                for (RuleRegex regex : knownRegexs) {
                    if (regex.matches(child)) {
                        regexMatch = true;
                        break;
                    }
                }

                Float childFitness = theFitnessManager.fitnessOf(child);
                childAndFit = new Pair<>(childFitness, child);
            } while (!RuleManager.IsValidRule(child) || nextState.contains(childAndFit) || knownRules.contains(child) || regexMatch);

            nextState.add(childAndFit);
        }
        else{                                               // Do crossover

            int parent1Index, parent2Index;
            do {
                parent1Index = fitnessInterval[rand.nextInt((int) Math.ceil(FIT))];
                parent2Index = fitnessInterval[rand.nextInt((int) Math.ceil(FIT))];
 //               System.out.println("index 1: " + parent1Index + "\nindex 2: " + parent2Index);
            } while (parent1Index == parent2Index);

            Rule parent1 = nextState.get(parent1Index).getValue();
            Rule parent2 = nextState.get(parent2Index).getValue();
            Rule child = theRuleManager.crossover(parent1,parent2);
            Float childFitness = theFitnessManager.fitnessOf(child);
            Pair childAndFit = new Pair<>(childFitness, child);
            boolean regexMatch;

            regexMatch = false;

            for (RuleRegex regex : knownRegexs) {
                if (regex.matches(child)) {
                    regexMatch = true;
                    break;
                }
            }

            int crossOverAttempts = 0;
            while(crossOverAttempts < 20 && (!RuleManager.IsValidRule(child) || nextState.contains(childAndFit) || knownRules.contains(child)) || regexMatch) { //keep going until pivot point produces valid Rule
                child = theRuleManager.crossover(parent1, parent2);

                regexMatch = false;

                for (RuleRegex regex : knownRegexs) {
                    if (regex.matches(child)) {
                        regexMatch = true;
                        break;
                    }
                }

                childFitness = theFitnessManager.fitnessOf(child);
                childAndFit = new Pair<>(childFitness, child);
                crossOverAttempts += 1;
            }

            if(crossOverAttempts == 20) { // unable to produce unique child from 100 crossover attempts. Switching to mutation
                do {
                    child = theRuleManager.mutate(child); // keep mutating child until it meets constraints
                    childFitness = theFitnessManager.fitnessOf(child);
                    childAndFit = new Pair<>(childFitness, child);
                                    regexMatch = false;

                    for (RuleRegex regex : knownRegexs) {
                        if (regex.matches(child)) {
                            regexMatch = true;
                            break;
                        }
                    }

                }
                while (!RuleManager.IsValidRule(child) || nextState.contains(childAndFit) || knownRules.contains(child) || regexMatch);//keep going until pivot point produces valid Rule
            }

            nextState.add(childAndFit);
            crossoversDone++;
        }
        return nextState;
    }

    /**
     * Prints population to output file as rules
     * @param filePath
     */
    public void toFile(String filePath, ConfigParameters cp) {
        float minAccuracy = 0.1f;           // TODO: make this a global parameter, entered at runtime. Alternatively, print some % of top rules
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
        String configString = "---Configuration parameters---\n" +
        "Initial Population Size: \t\t"+cp.initialPopSize+"\n" +
        "Number Of Generations: \t\t\t"+cp.numGenerations+"\n" +
        "Maximum Population Size: \t\t"+cp.populationMax+"\n" +
        "Min Coverage: \t\t\t\t"+cp.minCoverage+"\n" +
        "Min Accuracy: \t\t\t\t"+cp.minAccuracy+"\n" +
        "Crossover to Mutation Ratio: \t\t"+cp.crossToMute +"\n" +
        "Base Fitness Weighting: \t\t"+cp.baseFitnessWeight +"\n" +
        "Ext1 Fitness Weighting: \t\t"+cp.ext1FitnessWeight +"\n" +
        "Ext2 Fitness Weighting: \t\t"+cp.ext2FitnessWeight +"\n" +
        "Num Antecedent Features: \t\t"+cp.numFeatAntecedent +"\n" +
        "Num Consequent Features: \t\t"+cp.numFeatConsequent +"\n" +
        "------------------------------";

        try(FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {

            out.println(configString);
            for(int i = 0; i < state.size(); i++) {
                Pair individual = state.get(i);
                Rule ind = (Rule) individual.getValue();
                float individualsAccuracy = ind.getAccuracy()*100;
                float indCov = ind.getCoverage()*100;

                if (Float.compare(individualsAccuracy, minAccuracy) >= 0) {
                    String lineInFile = theRuleManager.TranslateRule(ind);
                    out.println("Rule Accuracy: " + individualsAccuracy + "; Rule Coverage: " + indCov + "\n" + lineInFile);
                }
            }
            out.println("------------------------- RULES END");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

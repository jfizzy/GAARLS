
import java.util.ArrayList;
import java.util.Arrays;
import Rule.Rule;
import Rule.RuleRegex;
import Rule.FeatureRequirement;

/*
    Main driver for the program
 */
public class Main
{
    private static String lookupFilePath = "NCBD_Data_Dictionary.doc";
    private static String dataFilePath = "NCDB_1999_to_2015.csv";
    private static String ruleFilePath = "rules.txt";
    private static String wekaFilePath = "weka.txt";
    private static String configFilePath = "config.txt";

    public static void main(String args[])
    {
        parseArgs(args);
        System.out.println("Welcome to GAARLS :-)");
        System.out.println("Genetic Algorithm-based Association Rule Learning System\n");
        System.out.println("------------------------------------");
        System.out.println("Lookup File:\t\t"+lookupFilePath);
        System.out.println("Data File:\t\t"+dataFilePath);
        System.out.println("Rule File:\t\t"+ruleFilePath);
        System.out.println("WEKA File:\t\t"+wekaFilePath);
        System.out.println("Config File:\t\t"+configFilePath);
        System.out.println("------------------------------------\n");
        
        Parser parser = new Parser();
        
        ConfigParameters cp = parser.parseConfigParameters(configFilePath);
        if (cp == null){
            cp = new ConfigParameters(1000,1000,1300,0.01f,0.01f,10,1f,0f,0f,10,10,null);
            System.out.println("Using Default Config.");
        }
        // desc of default configuration when file is missing
        /*  1000        Initial Population Size
            1000        Number of Generations
            1300        Maximum Population Size
            0.01        Minimum Coverage
            0.01        Minimum Accuracy
            10          Crossover to Mutation ratio
            1           Base Fitness Weighting
            0           Fitness Ext1 Weighting
            0           Fitness Ext2 Weighting
            10          Max Features to allow in Antecedent
            10          Max Features to allow in Consequent
            null        List of Features to Ignore
        */
        printConfigDetails(cp);
        System.out.println("Complete.");
        System.out.println("------------------------------------\n");
        
        // get file paths for codex and database
        System.out.println("\nParsing Data Dictionary...");

        ArrayList<Integer> featuresToOmit = new ArrayList<>();
        featuresToOmit.add(12); // V_ID
        featuresToOmit.add(15); // P_ID
        featuresToOmit.add(22); // C_CASE
        LookupTable lookupTable = LookupTable.ParseFile(lookupFilePath, featuresToOmit); // parse lookup table file

        System.out.println("Complete.");
        System.out.println("------------------------------------\n");
        System.out.println("Parsing data set...");
        Database database = Database.ParseFile(dataFilePath, lookupTable, -1); // parse database file
        System.out.println("Complete. Data set contains " + database.getNumDataItems() + " items.");
        System.out.println("------------------------------------\n");
        System.out.println("Parsing known rules...");
        
        ArrayList<Rule> knownRules = parser.parseKnownRules(ruleFilePath, featuresToOmit);
        ArrayList<RuleRegex> knownRegexs = parser.parseKnownRuleRegexs(ruleFilePath);
        System.out.println("Complete.");
        System.out.println("------------------------------------\n");
        System.out.println("Parsing WEKA rules...");
        ArrayList<Rule> wekaRules = parser.parseWekaRules(wekaFilePath, lookupTable, featuresToOmit);
        System.out.println("Complete.");
        System.out.println("------------------------------------\n");
        
        EvolutionManager evolutionManager = new EvolutionManager(database, lookupTable, knownRules, knownRegexs, wekaRules, cp);
        evolutionManager.evolve();
        evolutionManager.toFile("outputRules.txt", cp); //Keep in mind that as is, this will just keep appending rules to this file after each run
        System.out.println("Evolution complete. \nLearned rules output to outputRules.txt");
    }

    /* parse the arguments
     *     -d <datafile>
     *     -r <rulefile>
     *     -w <wekafile>
     *     -c <configfile>
     * ex: java main.java -d data.csv -w weka.txt -r rules.txt -c config.txt
     * use these switches so we don't rely on ordering
     * in case we want any combination of these files for our personal tests
     * e.g. I don't forsee myself always using a rulefile and wekafile
     */
    private static void parseArgs(String args[]) {
        try {
            for (int i = 0; i < args.length; ++i) {
                switch (args[i]) {
                    case "-d":
                        dataFilePath = args[++i];
                        break;
                    case "-r":
                        ruleFilePath = args[++i];
                        break;
                    case "-w":
                        wekaFilePath = args[++i];
                        break;
                    case "-l":
                        lookupFilePath = args[++i];
                        break;
                    case "-c":
                        configFilePath = args[++i];
                        break;
                    default:
                        break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ERROR: Malformed commandline: " + Arrays.toString(args));
            System.out.println("Available optional switches: \n\t-d <datafile> \n\t-r <rulefile> \n\t-w <wekafile> \n\t-l <lookupfile>");
        }
    }
    
    /** printConfigDetails: method to quickly print the configuration details 
     * for this run of the program
     * 
     * @param cp - the ConfigParameters object to display info about
     * 
     * this should be handy if and when we get into running a series of 
     * consecutive configurations on our system to evaluate a personal experiment
     */
    private static void printConfigDetails(ConfigParameters cp){
        System.out.println("------Configuration parameters------");
        System.out.println("Initial Population Size: \t\t"+cp.initialPopSize);
        System.out.println("Number Of Generations: \t\t\t"+cp.numGenerations);
        System.out.println("Maximum Population Size: \t\t"+cp.populationMax);
        System.out.println("Min Coverage: \t\t\t\t"+cp.minCoverage);
        System.out.println("Min Accuracy: \t\t\t\t"+cp.minAccuracy);
        System.out.println("Crossover to Mutation Ratio: \t\t"+cp.crossToMute);
        System.out.println("Base Fitness Weighting: \t\t"+cp.baseFitnessWeight);
        System.out.println("Ext1 Fitness Weighting: \t\t"+cp.ext1FitnessWeight);
        System.out.println("Ext2 Fitness Weighting: \t\t"+cp.ext2FitnessWeight);
        System.out.println("Num Antecedent Features: \t\t"+cp.numFeatAntecedent);
        System.out.println("Num Consequent Features: \t\t"+cp.numFeatConsequent);
        if (cp.featuresToIgnore != null) {
            System.out.print("Indices of Features to Ignore: \t\t");
            System.out.print("[");
            cp.featuresToIgnore.forEach((feature) -> {
                System.out.print(" " + feature + " ");
            });
            System.out.println("]");
        }
        System.out.println("------------------------------------\n");
    }
}

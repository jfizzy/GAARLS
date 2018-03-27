
import java.util.ArrayList;
import java.util.Arrays;
import Rule.Rule;

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
        // get file paths for codex and database
        System.out.println("\nParsing Data Dictionary...");

        ArrayList<Integer> featuresToOmit = new ArrayList<>();
        featuresToOmit.add(12); // V_ID
        featuresToOmit.add(15); // P_ID
        featuresToOmit.add(22); // C_CASE
        LookupTable lookupTable = LookupTable.ParseFile(lookupFilePath, featuresToOmit); // parse lookup table file

        System.out.println("Complete.\nParsing data set...");
        Database database = Database.ParseFile(dataFilePath, lookupTable, 100000); // parse database file
        System.out.println("Complete.");

        Parser parser = new Parser();
        ArrayList<Rule> knownRules = parser.parseKnownRules(ruleFilePath, featuresToOmit);
        ArrayList<Rule> wekaRules = parser.parseWekaRules(wekaFilePath, lookupTable, featuresToOmit);
        
        //new wrapper class containing the config parameter values read from file
        ConfigParameters cp = parser.parseConfigParameters(configFilePath);
        if (cp != null) {
            printConfigDetails(cp); // I assume this will help a lot
        }
        //TODO: need to decide who is going to link these options up to the proper locations

        EvolutionManager evolutionManager = new EvolutionManager(database, lookupTable, knownRules, 10);
        evolutionManager.evolve(1000, 1000, 1300);
        evolutionManager.toFile("outputRules.txt"); //Keep in mind that as is, this will just keep appending rules to this file after each run
        System.out.println("Evolution complete. \n Learned rules output to outputRules.txt");
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
        System.out.println("---Configuration parameters---");
        System.out.println("Initial Population Size: \t\t"+cp.initialPopSize);
        System.out.println("Number Of Generations: \t\t\t"+cp.numGenerations);
        System.out.println("Maximum Population Size: \t\t"+cp.populationMax);
        System.out.println("Min Coverage: \t\t\t\t"+cp.minCoverage);
        System.out.println("Min Accuracy: \t\t\t\t"+cp.minAccuracy);
        System.out.println("Probability of Crossover: \t\t"+cp.probOfCrossover);
        System.out.println("Probability of Mutation: \t\t"+cp.probOfMutation);
        System.out.println("Base Fitness Weighting: \t\t"+cp.baseFitnessWeight);
        System.out.println("Ext1 Fitness Weighting: \t\t"+cp.ext1FitnessWeight);
        System.out.println("Ext2 Fitness Weighting: \t\t"+cp.ext2FitnessWeight);
        System.out.println("Num Antecedent Features: \t\t"+cp.numFeatAntecedent);
        System.out.println("Num Consequent Features: \t\t"+cp.numFeatConsequent);
        System.out.print("Indices of Features to Ignore: \t\t");
        System.out.print("[");
        cp.featuresToIgnore.forEach((feature) -> {
            System.out.print(" "+feature+" ");
        });
        System.out.println("]");
        System.out.println("------------------------------");
    }
}

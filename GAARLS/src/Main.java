
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
        // get file paths for codex and database
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
            cp = new ConfigParameters(1000,1000,1300,0.01f,0.01f,10,1f,0f,0f,100,10,10,null, null);
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
            100         Individuals to Trim
            10          Max Features to allow in Antecedent
            10          Max Features to allow in Consequent
            null        List of Feature Indices to Ignore
            null        List of required features for rules
        */
        System.out.print(cp.formattedConfigDetails());
        
        // get file paths for codex and database
        System.out.println("Parsing Data Dictionary...");
        LookupTable lookupTable = LookupTable.ParseFile(lookupFilePath, cp.featuresToIgnore); // parse lookup table file
        System.out.println("Complete.");
        System.out.println("------------------------------------\n");
        System.out.println("Parsing data set...");
        Database database = Database.ParseFile(dataFilePath, lookupTable, -1); // parse database file
        System.out.println("Complete. Data set contains " + database.getNumDataItems() + " items.");
        System.out.println("------------------------------------\n");
        System.out.println("Parsing known rules...");
        ArrayList<Rule> knownRules = parser.parseKnownRules(ruleFilePath, cp.featuresToIgnore);
        ArrayList<RuleRegex> knownRegexs = parser.parseKnownRuleRegexs(ruleFilePath);
        System.out.println("Complete.");
        System.out.println("------------------------------------\n");
        System.out.println("Parsing WEKA rules...");
        ArrayList<Rule> wekaRules = parser.parseWekaRules(wekaFilePath, lookupTable, cp.featuresToIgnore);
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
}


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
        ArrayList<Rule> knownRules = parser.parseKnownRules(ruleFilePath);

        EvolutionManager evolutionManager = new EvolutionManager(database, lookupTable, knownRules, 10);
        evolutionManager.evolve(1000, 1000, 1300);
        evolutionManager.toFile("outputRules.txt"); //Keep in mind that as is, this will just keep appending rules to this file after each run
        System.out.println("Evolution complete. \n Learned rules output to outputRules.txt");
    }

    /* parse the arguments
     *     -d <datafile>
     *     -r <rulefile>
     *     -w <wekafile>
     * ex: java main.java -d data.csv -w weka.txt -r rules.txt
     * use these switches so we don't rely on ordering
     * in case we want any combination of these files for our personal tests
     * e.g. I don't forsee myself always using a rulefile and wekafile
     */
    private static void parseArgs(String args[]) {
        try {
            for (int i = 0; i < args.length; ++i) {
                if (args[i].equals("-d")) {
                    dataFilePath = args[++i];
                } else if (args[i].equals("-r")) {
                    ruleFilePath = args[++i];
                } else if (args[i].equals("-w")) {
                    wekaFilePath = args[++i];
                } else if (args[i].equals("-l")) {
                    lookupFilePath = args[++i];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ERROR: Malformed commandline: " + Arrays.toString(args));
            System.out.println("Available optional switches: \n\t-d <datafile> \n\t-r <rulefile> \n\t-w <wekafile> \n\t-l <lookupfile>");
        }
    }
}

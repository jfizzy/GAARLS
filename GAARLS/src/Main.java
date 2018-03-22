import java.util.ArrayList;

/*
    Main driver for the program
 */
public class Main
{
    public static void main(String args[])
    {
        // get file paths for codex and database
        System.out.println("\nParsing Data Dictionary...");

        ArrayList<Integer> featuresToOmit = new ArrayList<>();
        featuresToOmit.add(12); // V_ID
        featuresToOmit.add(15); // P_ID
        featuresToOmit.add(22); // C_CASE
        LookupTable lookupTable = LookupTable.ParseFile("NCDB_Data_Dictionary.doc", featuresToOmit); // parse lookup table file

        System.out.println("Complete.\nParsing data set...");
        Database database = Database.ParseFile("NCDB_1999_to_2015.csv", lookupTable, 100000); // parse database file
        System.out.println("Complete.");

        EvolutionManager evolutionManager = new EvolutionManager(database, lookupTable, 10);
        evolutionManager.evolve(100, 1000, 1300);
        //evolutionManager.ToFile("");
    }
}

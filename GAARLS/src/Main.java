
/*
    Main driver for the program
 */
public class Main
{
    public static void main(String args[])
    {
        // get file paths for codex and database
        System.out.println("\nParsing Data Dictionary...");
        LookupTable lookupTable = LookupTable.ParseFile("NCDB_Data_Dictionary.doc"); // parse lookup table file
        System.out.println("Complete.\nParsing data set...");
        Database database = Database.ParseFile("NCDB_1999_to_2015.csv", lookupTable, -1); // parse database file
        System.out.println("Complete.");
        EvolutionManager evolutionManager = new EvolutionManager(database, lookupTable, 10);
        evolutionManager.evolve(1000, 1000, 1300);
        //evolutionManager.ToFile("");
    }
}


/*
    Main driver for the program
 */
public class Main
{
    public static void main(String args[])
    {
        // get file paths for codex and database
        LookupTable lookupTable = LookupTable.ParseFile("NCDB_Data_Dictionary.doc"); // parse lookup table file
        Database database = Database.ParseFile("NCDB_1999_to_2015.csv", lookupTable, -1); // parse database file
        EvolutionManager evolutionManager = new EvolutionManager(database, lookupTable, 10);
        evolutionManager.evolve(1000, 1000, 800);
        //evolutionManager.ToFile("");
    }
}

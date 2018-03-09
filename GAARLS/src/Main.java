
/*
    Main driver for the program
 */
public class Main
{
    public static void main(String args[])
    {
        // get file paths for codex and database
        LookupTable lookupTable = LookupTable.ParseFile(""); // parse lookup table file
        Database database = Database.ParseFile("NCDB_1999_to_2015.csv", lookupTable, -1); // parse database file
        EvolutionManager evolutionManager = new EvolutionManager(database, lookupTable);
        evolutionManager.IntializePopulation(0);
        evolutionManager.Evolve(0);
        evolutionManager.ToFile("");
    }
}

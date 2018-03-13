import Rule.Rule;
import java.io.*;
import java.util.Scanner;

/**
 * Class: Database
 * Intended functionality: A container class that houses a value array with look up functions to access it
 * Feature Owner: Peter
 */

public class Database
{
    // public functions

    /**
     * Static util function to parse a file and return a new database object
     * @param filePath file path + name
     * @param databaseCodex lookup table to translate file symbols
     * @param limitItemSetToSize optional parameter to truncate number of items actually read from file.
     *                           Used for debugging purposes to increase iteration time
     *                           Use -1 if full data set is desired
     * @return
     */
    public static Database ParseFile(String filePath, LookupTable databaseCodex, int limitItemSetToSize)
    {
        float[] dataSet = null;
        int numFeatures = 0;
        int numItemsInFile = 0;
        try
        {
            File dataFile = new File(filePath);

            // count number of lines in data set
            LineNumberReader lineCounter = new LineNumberReader(new FileReader(dataFile));
            while (lineCounter.skip(Integer.MAX_VALUE) > 0) { /* do nothing*/ }
            final int numLinesInFile = lineCounter.getLineNumber();

            // DEBUG Functionality
            if (limitItemSetToSize != -1)
            {
                numItemsInFile = Math.min(numLinesInFile - 1, limitItemSetToSize);
            }
            else
            {
                numItemsInFile = numLinesInFile - 1; // ASSUMPTION: first line the csv file is the column headings for the database
            }

            lineCounter.close();
            assert (numItemsInFile > 0);

            // Parse data set and translate feature values for each item
            Scanner scanner = new Scanner(dataFile);

            numFeatures = databaseCodex.NumFeatures;
            final int dataSetSize = numFeatures * numItemsInFile;
            dataSet = new float[dataSetSize];

            int itemIndex = 0; // offset of the item in the 1-d array
            scanner.nextLine(); // skip the header line
            int numLinesRead = 0;
            while (scanner.hasNextLine() && /*for debug functionality*/ numLinesRead < numItemsInFile )
            {
                String item = scanner.nextLine();
                String[] itemFeatures = item.split(",");

                // iterate through each feature symbol in the item and translate to a float value for the database
                for (int featureId = 0; featureId < numFeatures; ++featureId)
                {
                    float symbolValue = databaseCodex.TranslateFeatureSymbol(featureId, itemFeatures[featureId]);
                    dataSet[itemIndex + featureId] = symbolValue;
                }

                itemIndex += numFeatures;
                numLinesRead ++;
            }
            if (itemIndex != dataSetSize) // number of items found != expected items in file
            {
                System.out.println("Database.ParseFile() Warning: Number of items parsed differs from number of lines in file.");
                System.out.println("Database.ParseFile() Found: " + itemIndex / numFeatures + ". Expected: " + numItemsInFile );
            }

            scanner.close();
        }
        catch (FileNotFoundException fnfe)
        {
            System.out.println("Database.ParseFile(): Could not open file " + filePath);
            System.out.println(fnfe.getMessage());
        }
        catch (IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }

        return new Database(dataSet, numItemsInFile, numFeatures);
    }

    /**
     * NOTE: This is not intended to be an actual function call. Only to illustrate typical api for database queries
     * @param rule information as to what is being querried
     * @return normalized value between [0-1] respective to query details
     */
    public float QueryDatabase(Rule rule)
    {
        return 0;
    }

    public float[] GetDatabase() {return mDatatable;}

    // private functions
    private Database(float[] flattenedTable, int rows, int columns)
    {
        mDatatable = flattenedTable;
        mNumTableEntries = rows;
        mNumDataValues = columns;
    }

    // private members
    private float[] mDatatable; // data table of data symbols translated to floats. Flattened for data locality
    private int mNumTableEntries; // number of rows in database
    private int mNumDataValues; // number of columns in database
}

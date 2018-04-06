import Rule.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class: Database
 * Intended functionality: A container class that houses a value array with look up functions to access it
 * Feature Owner: Peter
 */

public class Database
{
    // public functions
    public int getNumDataItems(){
        return mNumTableEntries;
    }


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
            ArrayList<Integer> desiredFeatureIndices = databaseCodex.GetFileParsingIndices();
            while (scanner.hasNextLine() && /*for debug functionality*/ numLinesRead < numItemsInFile )
            {
                String item = scanner.nextLine();
                String[] itemFeatures = item.split(",");

                // iterate through each feature symbol in the item and translate to a float value for the database
                int featureCount = 0;
                for (int desiredFeature : desiredFeatureIndices)
                {
                    float symbolValue = databaseCodex.TranslateFeatureSymbol(featureCount, itemFeatures[desiredFeature]);
                    dataSet[itemIndex + featureCount] = symbolValue;
                    featureCount++;
                }

                itemIndex += numFeatures;
                numLinesRead ++;
            }
            if (itemIndex != dataSetSize) // number of items found != expected items in file
            {
                System.out.println("Database.ParseFile() Warning: Number of items parsed differs from number of lines in file.");
                System.out.println("Database.ParseFile() Found: " + itemIndex / numFeatures + ". Expected: " + numItemsInFile );
            }

            ArrayList<Integer> addedIndices = databaseCodex.GetAddedIndices();

            for (int addedIndice : addedIndices)
            {
                scanner = new Scanner(dataFile); // reset scanner to beginning of file
                if (addedIndice == 24 - 1)  // C_OCCUR 0 offset
                {
                    InsertCollisionOccurrence(dataSet, filePath, databaseCodex, numItemsInFile);
                }
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
     * API for calculating values relevant to rule in the context of the data set by parsing the data table and checking
     * the rule against each line
     * ASSUMPTION: that the rule is well formed and has atleast one antecedent and consequent
     * NOTE: This function mutates member functions of rule. Each value checked will be stored in the rule itself.
     * Currently checks:
     * Coverage => How many times does the rule occur in the datat set, normalized against the number of data items
     * Accuracy => How many times does the rule occur in the data set, normalized against the number of times the anticedent occurs
     * @param rule rule to test
     */
    public void EvaluateRule(Rule rule)
    {
        int correctPredictions = 0;
        int occurancesOfAnticedent = 0;
        int occurancesOfConsequent = 0;
        float ruleRange = 1f;

        // extract antecedent and consequent indices to cut down on table queries
        ArrayList<Integer> antecedent = new ArrayList<>();
        ArrayList<Integer> consequent = new ArrayList<>();
        FeatureRequirement[] featureRequirements = rule.getFeatureReqs();
        for (int i = 0; i < featureRequirements.length; ++i)
        {
            int participation = featureRequirements[i].getParticipation();
            if (participation == FeatureRequirement.pFlag.ANTECEDENT.getValue())
            {
                antecedent.add(i);
                ruleRange *= 1 - featureRequirements[i].getRangeCoverage();
            }
            else if (participation == FeatureRequirement.pFlag.CONSEQUENT.getValue())
            {
                consequent.add(i);
                ruleRange *= 1 - featureRequirements[i].getRangeCoverage();
            }
        }

        for (int tableIndex = 0; tableIndex < mDatatable.length; tableIndex += mNumDataValues)
        {
            boolean antecedentIsPresent = true;
            boolean consequentIsPresent = true;

            // check if all parts of the antecedent occur
            for (int i : antecedent)
            {
                antecedentIsPresent &= featureRequirements[i].evaluate(mDatatable[tableIndex + i]);
            }
            // check if all parts of the consequent occur
            for (int i : consequent)
            {
                consequentIsPresent &= featureRequirements[i].evaluate(mDatatable[tableIndex + i]);
            }

            if (antecedentIsPresent)
            {
                occurancesOfAnticedent++;
                if (consequentIsPresent)
                {
                    correctPredictions++;
                }
            }
            if (consequentIsPresent)
            {
                occurancesOfConsequent++;
            }
        }
        float normalizedCoverage = occurancesOfAnticedent / (float)mNumTableEntries;
        float normalizeAccuracy = occurancesOfAnticedent > 0 ? (correctPredictions / (float)occurancesOfAnticedent) : 0;
        float normalizedCompleteness = occurancesOfConsequent > 0 ? correctPredictions / (float)occurancesOfConsequent : 0;

        rule.setAccuracy(normalizeAccuracy);
        rule.setCoverage(normalizedCoverage);
        rule.setRangeCoverage(ruleRange);
        rule.setCompleteness(normalizedCompleteness);
    }

    private static void InsertCollisionOccurrence(float[] dataset, String dataTableFileName, LookupTable lookupTable, int numLinesInDataset) throws IOException
    {
        ArrayList<Integer> monthlyCollisionCounts = new ArrayList<>();
        ; // skip the headers
        String currentMonth;
        String currentCaseId;
        int fatalityCounter = 0;

        final int C_MONTH = 1;
        final int C_SEV = 4;
        final String FATALITY = "1";
        final int C_CASE = 22;
        final int C_OCCUR = lookupTable.featureMap.get("C_OCCUR");

        // Calculate the number of fatal collisions each month
        String[] splitLine = null;
        currentMonth = "";
        currentCaseId = "";
        Scanner fileScanner = new Scanner(new File(dataTableFileName));
        fileScanner.nextLine(); // skip the header
        for (int lineNumber = 0; lineNumber < numLinesInDataset; ++lineNumber)
        {
            splitLine = fileScanner.nextLine().split(",");
            String month = splitLine[C_MONTH];
            String caseId = splitLine[C_CASE];

            if (!currentMonth.equals(month) && month.charAt(0) != '-' /* disregard unknown months */) // next month!
            {
                if (currentMonth.length() > 0) {
                    monthlyCollisionCounts.add(fatalityCounter);
                }
                fatalityCounter = 0;
                currentMonth = month;
            }

            if (!currentCaseId.equals(caseId))
            {
                currentCaseId = caseId;
                String severity = splitLine[C_SEV];
                if (splitLine[C_SEV].equals(FATALITY))
                {
                    fatalityCounter++;
                }
            }
        }
        monthlyCollisionCounts.add(fatalityCounter);

        // Assign HIGH/LOW to each collision depending on average of months around it
        final float LOW = 1; // hardcoded values from the translator
        final float HIGH = 2; // hardcoded values from the translator
        final float UNKNOWN = -11;
        float monthlyOccurrence = 0; // what will be assigned to each collision in a month
        int collisionCountIndex = -1; // index of the collision count in @monthlyCollisionCounts
        int offset = 0;
        currentMonth = "";
        fileScanner = new Scanner(new File(dataTableFileName));
        fileScanner.nextLine(); // skip the header
        for (int lineNumber = 0; lineNumber < numLinesInDataset; ++lineNumber)
        {
            splitLine = fileScanner.nextLine().split(",");
            String month = splitLine[C_MONTH];
            if (!currentMonth.equals(month)) // next month!
            {
                if (month.charAt(0) ==  '-') // negative
                {
                    monthlyOccurrence = UNKNOWN;
                }
                else
                {
                    collisionCountIndex++; // first time this happens collisionIndex will equal 0

                    int occurrencesInMonth = monthlyCollisionCounts.get(collisionCountIndex);
                    // Get average of current month with it's neighbours
                    float average = 0;
                    if (collisionCountIndex > 0 && collisionCountIndex < monthlyCollisionCounts.size() - 1)
                    {
                        average = monthlyCollisionCounts.get(collisionCountIndex - 1) + monthlyCollisionCounts.get(collisionCountIndex) + monthlyCollisionCounts.get(collisionCountIndex+1);
                        average /= 3;
                    }
                    else if (collisionCountIndex == 0 && collisionCountIndex < monthlyCollisionCounts.size() - 1)
                    {
                        average = monthlyCollisionCounts.get(collisionCountIndex) + monthlyCollisionCounts.get(collisionCountIndex+1);
                        average /= 2;
                    }
                    else if (collisionCountIndex == monthlyCollisionCounts.size() - 1 && collisionCountIndex > 0)
                    {
                        average = monthlyCollisionCounts.get(collisionCountIndex) + monthlyCollisionCounts.get(collisionCountIndex-1);
                        average /= 2;
                    }
                    else
                    {
                        average = monthlyCollisionCounts.get(collisionCountIndex);
                    }
                    // determine if this month is high or low compared to it's neighbours
                    if (occurrencesInMonth <= average)
                    {
                        monthlyOccurrence = LOW;
                    }
                    else
                    {
                        monthlyOccurrence = HIGH;
                    }
                }

                currentMonth = month;
            }

            dataset[offset + C_OCCUR] = monthlyOccurrence;
            offset += lookupTable.NumFeatures;
        }
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

    public static void main(String[] args)
    {
        LookupTable table = LookupTable.ParseFile("");

//        ArrayList<Integer> ommitAddedFeature = new ArrayList<>();
//        ommitAddedFeature.add(22);
//        ommitAddedFeature.add(23);
//        table = LookupTable.ParseFile("", ommitAddedFeature);
//
//        long systemTime = System.currentTimeMillis();
//        Database database = Database.ParseFile("NCDB_1999_to_2015.csv", table, 100000);
//        System.out.println("Run Time: " + (System.currentTimeMillis() - systemTime));
//
//        table = LookupTable.ParseFile("");
//
//        systemTime = System.currentTimeMillis();
//        database = Database.ParseFile("NCDB_1999_to_2015.csv", table, 1000);
//        System.out.println("Run Time: " + (System.currentTimeMillis() - systemTime));

        Database database = Database.ParseFile("NCDB_1999_to_2015.csv", table, 1000000);
        RuleManager ruleManager = new RuleManager(table);

        Rule rule = new Rule();
        FeatureRequirement[] featureRequirements = rule.getFeatureReqs();
        featureRequirements[2].setBoundRange(6, 1, 0);
        featureRequirements[2].setParticipation(1);
        featureRequirements[4].setBoundRange(1,1,0);
        featureRequirements[4].setParticipation(2);
        database.EvaluateRule(rule);
        System.out.println(table.TranslateRule(rule));
        System.out.println("Accuracy: " + rule.getAccuracy() + ". Coverage: " + rule.getCoverage());

        Rule rule2 = new Rule();
        FeatureRequirement[] featureRequirements2 = rule2.getFeatureReqs();
        featureRequirements2[16].setBoundRange(0, 0, 0);
        featureRequirements2[16].setParticipation(1);
        featureRequirements2[0].setBoundRange(2000, 2015, 2 / 12.f);
        featureRequirements2[0].setParticipation(2);
        database.EvaluateRule(rule2);
        System.out.println(table.TranslateRule(rule2));
        System.out.println("Accuracy: " + rule2.getAccuracy() + ". Coverage: " + rule2.getCoverage());


        Rule rule3 = new Rule();
        FeatureRequirement[] featureRequirements3 = rule3.getFeatureReqs();
        featureRequirements3[16].setBoundRange(0, 0, 0);
        featureRequirements3[16].setParticipation(1);
        featureRequirements3[0].setBoundRange(2000, 2001, 2 / 12.f);
        featureRequirements3[0].setParticipation(2);
        database.EvaluateRule(rule3);
        System.out.println(table.TranslateRule(rule3));
        System.out.println("Accuracy: " + rule.getAccuracy() + ". Coverage: " + rule.getCoverage());

        //TODO: move this to a JUnit test
        System.out.println("rule equals rule2? " + rule.equals(rule2)); //Should eval to true
        System.out.println("rule equals rule3? " + rule.equals(rule3)); //Should eval to false



/*        for (int i = 0; i < 20; ++i)
        {

            rule = ruleManager.generateRule();
            System.out.println(table.TranslateRule(rule));
            if (rule.getCoverage() > 0 || rule.getAccuracy() > 0)
            {
                System.out.println(table.TranslateRule(rule));
                System.out.println("Accuracy: " + rule.getAccuracy() + ". Coverage: " + rule.getCoverage());
            }
        }*/
    }
}

import java.util.HashMap;
import java.util.Random;
import Rule.FeatureRequirement;

/**
 * Class: SymbolTranslatorBase
 * Intended functionality: SymbolTranslator will be assigned a particular feature and is in charge of:
 *  1. one to one conversion of all symbols of a given feature to a float value
 *  2. one to one conversion of a float value to a given feature key
 *  3. Generation of random values for a featureRequirement instance
 *  Children class are responsible for featureRequirementGeneration
 *
 * Feature Owner: Peter
 */

public abstract class SymbolTranslatorBase
{
    public float TranslateFeatureSymbol(String symbol)
    {
        if (mValueLookupTable.containsKey(symbol))
        {
            return (float)mValueLookupTable.get(symbol);
        }
        else
        {
            System.out.println("Symbol not found for feature " + mFeatureName + ". Got: " + symbol);
            return 0;
        }
    }

    public String FeatureValueToKey(float value)
    {
        int key = (int)value;
        if (mSymbolLookupTable.containsKey(key))
        {
            return mSymbolLookupTable.get(key);
        }
        else
        {
            System.out.println("Key not found for feature " + mFeatureName + ". Got: " + key);
            return "UNDEFINED";
        }
    }

    public String FeatureValueToDescription(float value)
    {
        int key = (int)value;
        if (mTranslationLookupTable.containsKey(key))
        {
            return mFeatureName + ": " + mTranslationLookupTable.get(key);
        }
        else
        {
            System.out.println("Key not found for feature " + mFeatureName + ". Got: " + key);
            return "UNDEFINED";
        }
    }

    public int GetFileFeatureIndex() {return mFileFeatureId;}
    public String GetFeatureName() {return  mFeatureName;}
    public HashMap<String, Float> getmValueReverseLookupTable() {
        return mValueReverseLookupTable;
    }

    public abstract String FeatureRequirementToDescription(FeatureRequirement featureRequirement);

    public abstract void GenerateRandomFeatureRequirement(FeatureRequirement toRandomize, int wildcards);

    // protected methods
    protected SymbolTranslatorBase(int fileFeatureId, String featureName, int symbolSize, int[] values, String[] symbols, String[] translations)
    {
        mFileFeatureId = fileFeatureId;
        mFeatureName = featureName;
        mValueLookupTable = new HashMap<>();
        mSymbolLookupTable = new HashMap<>();
        mTranslationLookupTable = new HashMap<>();
        mValueReverseLookupTable = new HashMap<>();

        // assert keys.length == values.length == translations.length
        for (int featureValueIdx = 0; featureValueIdx < values.length; ++featureValueIdx)
        {
            mSymbolLookupTable.put(values[featureValueIdx], symbols[featureValueIdx]);
            mValueLookupTable.put(symbols[featureValueIdx], values[featureValueIdx]);
            mTranslationLookupTable.put(values[featureValueIdx], translations[featureValueIdx]);
            mValueReverseLookupTable.put(translations[featureValueIdx], (float)values[featureValueIdx]);
        }

        String unknownSymbol = "";
        int unknownValue = -11;
        String notProvidedSymbol = "";
        int notProvidedValue = -22;
        String notApplicableSymbol = "";
        int notApplicableValue = -33;
        String otherThanPreviousSymbol = "";
        int otherThanPreviousValue = -44;

        for (int i = 0; i < symbolSize; ++i)
        {
            unknownSymbol += "U";
            notProvidedSymbol += "X";
            notApplicableSymbol += "N";
            otherThanPreviousSymbol +="Q";
        }

        mSymbolLookupTable.put(unknownValue, unknownSymbol);
        mSymbolLookupTable.put(notProvidedValue, notProvidedSymbol);
        mSymbolLookupTable.put(notApplicableValue, notApplicableSymbol);
        mSymbolLookupTable.put(otherThanPreviousValue, otherThanPreviousSymbol);

        mTranslationLookupTable.put(unknownValue, "Unknown");
        mTranslationLookupTable.put(notProvidedValue, "Jurisdiction does not provide this data element");
        mTranslationLookupTable.put(notApplicableValue, "Data element is not applicable");
        mTranslationLookupTable.put(otherThanPreviousValue, "Choice is other than the preceding values");

        mValueLookupTable.put(unknownSymbol, unknownValue);
        mValueLookupTable.put(notProvidedSymbol, notProvidedValue);
        mValueLookupTable.put(notApplicableSymbol, notApplicableValue);
        mValueLookupTable.put(otherThanPreviousSymbol, otherThanPreviousValue);

        if (mRandomValueGenerator == null)
        {
            mRandomValueGenerator = new Random();
        }
    }

    // protected members
    protected static Random mRandomValueGenerator;
    protected int[] mSymbolValues;
    protected HashMap<Integer, String> mTranslationLookupTable;
    protected String mFeatureName;

    // private members
    private HashMap<Integer, String> mSymbolLookupTable;
    private HashMap<String, Integer> mValueLookupTable;
    private HashMap<String, Float> mValueReverseLookupTable;
    private int mFileFeatureId;


}


import RuleManager.FeatureRequirement;
import RuleManager.Rule;

/**
 * Class: LookupTable
 * Intended functionality: A container class that translates feature values into float values, and provides utility
 * functions to generate feature values in a correct range
 * Feature Owner: Peter
 */

public class LookupTable
{
    //public functions
    public static LookupTable ParseFile(String filePath)
    {
        return new LookupTable();
    }

    /**
     * NOTE: This is not intended to be an actual function call. Only to illustrate typical api for feature value generation
     * @param featureId feature to create values for
     * @param toModify feature requirement that will house the changes
     *                 NOTE: @toModify is altered through this function call
     */
    public void GenerateRandomValue(int featureId, FeatureRequirement toModify)
    {
        // The intention behind adding @toModify as a parameter to modify rather than returning a value is that depending
        // on which feature is being generated for, the structure of the return type might change
    }

    /**
     * ToString() method for a rule that inserts feature names and translates feature values into symbols appropriate for
     * the data set
     * @param rule
     * @return
     */
    public String TranslateRule(Rule rule)
    {
        // ie TranslateRule(Rule[0,1]) = " Rule: Weather = Rainy; Day = Monday; \n"
        return "";
    }

    /**
     * Util function used when parsing the database to translate a value in the database to it's float representation
     * @param featureId
     * @param symbol symbol in the database assigned to @featureId in the item
     * @return the float representation of the symbol.
     */
    public float TranslateFeatureSymbol(int featureId, String symbol)
    {
        // TODO
        return -1f;
    }

    // private functions
    private LookupTable()
    {

    }

    // private members

}

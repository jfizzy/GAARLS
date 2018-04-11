import Rule.FeatureRequirement;

/**
 * Class: DiscreteSymbolTranslator
 * Intended functionality: impliments a discrete value generation for GenerateRandomFeatureRequirements given a list of
 * possible values
 *
 *
 * Feature Owner: Peter
 */

public class DiscreteSymbolTranslator extends SymbolTranslatorBase
{
    // public methods
    public DiscreteSymbolTranslator(int fileFeatureId, String featureName, int symbolSize, int[] values, String[] symbols,  String[] descriptions)
    {
        super(fileFeatureId, featureName, symbolSize, values, symbols, descriptions);
        mSymbolValues = values;
    }

    @Override
    public void GenerateRandomFeatureRequirement(FeatureRequirement toRandomize, int wildcards)
    {
        if (wildcards != 2) {
            int randomValueIdx = mRandomValueGenerator.nextInt(mSymbolValues.length);
            int newValue = mSymbolValues[randomValueIdx];

            toRandomize.setBoundRange(newValue, newValue, 0);
        }
    }

    @Override
    public String FeatureRequirementToDescription(FeatureRequirement featureRequirement)
    {
        int featureValue = (int)featureRequirement.getLowerBound();
        if (mTranslationLookupTable.containsKey(featureValue))
        {
            return mFeatureName + ": " + mTranslationLookupTable.get(featureValue);
        }
        else
        {
            System.out.println("Key not found for feature " + mFeatureName + ". Got: " + featureValue);
            return "UNDEFINED";
        }
    }
}

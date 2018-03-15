import Rule.FeatureRequirement;

/**
 * Class: DiscreteSymbolTranslator
 * Intended functionality: impliments a discrete value generation for GenerateRandomFeatureRequirements given a list of
 * possible values
 *
 *
 * Feature Owner: Peter
 */

public class RangeSymbolTranslator extends SymbolTranslatorBase
{
    // public methods
    public RangeSymbolTranslator(String featureName, int symbolSize, int[] values, String[] symbols,  String[] descriptions)
    {
        super(featureName, symbolSize, values, symbols, descriptions);
        mSymbolValues = values;
    }

    @Override
    public void GenerateRandomFeatureRequirement(FeatureRequirement toRandomize)
    {
        int randomValueIdx1 = mRandomValueGenerator.nextInt(mSymbolValues.length);
        int randomValueIdx2 = mRandomValueGenerator.nextInt(mSymbolValues.length);

        int value1 = mSymbolValues[randomValueIdx1];
        int value2 = mSymbolValues[randomValueIdx2];

        float rangeCoverage = 0;
        if (value1 < value2)
        {
            rangeCoverage = (value2 - value1 + 1) / (float)mSymbolValues.length;
        }
        else if (value1 > value2)
        {
            rangeCoverage = (mSymbolValues.length - (value1 - value2 + 1)) / (float)mSymbolValues.length;
        }
        else
        {
            rangeCoverage = 1 / (float)mSymbolValues.length;
        }

        toRandomize.setBoundRange(value1, value2, rangeCoverage);
    }

    @Override
    public String FeatureRequirementToDescription(FeatureRequirement featureRequirement)
    {
        int lowerBound = (int)featureRequirement.getLowerBound();
        int upperBound = (int)featureRequirement.getUpperBound();
        if (mTranslationLookupTable.containsKey(lowerBound) && mTranslationLookupTable.containsKey(upperBound))
        {
            return mFeatureName + ": [" + mTranslationLookupTable.get(lowerBound) + " - " + mTranslationLookupTable.get(upperBound) + "]";
        }
        else
        {
            System.out.println("Keys not found for feature " + mFeatureName + ". Got: [" + lowerBound + "," + upperBound + "]");
            return mFeatureName + ": [UNDEFINED-UNDEFINED]";
        }
    }
}

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
    public RangeSymbolTranslator(int fileFeatureid, String featureName, int symbolSize, int[] values, String[] symbols,  String[] descriptions, boolean canFlipRange)
    {
        super(fileFeatureid, featureName, symbolSize, values, symbols, descriptions);
        mSymbolValues = values;
        mCanFlipRange = canFlipRange;
    }

    @Override
    public void GenerateRandomFeatureRequirement(FeatureRequirement toRandomize, int wildcards)
    {
        int value1;
        int value2;

        // randomize if wildcard
        if (wildcards == 1) {
            value2 = Math.round(toRandomize.getUpperBound());
            do {
                int randomValueIdx = mRandomValueGenerator.nextInt(mSymbolValues.length);
                value1 = mSymbolValues[randomValueIdx];
            } while (value2 < value1);
        } else if (wildcards == 2) {
            value1 = Math.round(toRandomize.getLowerBound());
            do {
                int randomValueIdx = mRandomValueGenerator.nextInt(mSymbolValues.length);
                value2 = mSymbolValues[randomValueIdx];
            } while (value2 < value1);
        } else {
            int randomValueIdx1 = mRandomValueGenerator.nextInt(mSymbolValues.length);
            int randomValueIdx2 = mRandomValueGenerator.nextInt(mSymbolValues.length);
            value1 = mSymbolValues[randomValueIdx1];
            value2 = mSymbolValues[randomValueIdx2];
        }


        if (!mCanFlipRange && value1 > value2)
        {
           int temp = value1;
           value1 = value2;
           value2 = temp;
        }

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

    boolean mCanFlipRange;
}

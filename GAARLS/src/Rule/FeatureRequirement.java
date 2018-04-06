package Rule;

/**
 * Class: FeatureRequirement Intended functionality: Associated with a feature
 * for a given data set. Encodes a value range that a feature is expected to
 * exist in and an evaluation function that determines if a feature value is
 * within that range.
 *
 * Key Methods: evaluate -> is a given float acceptable for the requirements of
 * this feature? copy -> make a exact copy of this FeatureRequirement object.
 *
 * Feature Owner: James
 */
public class FeatureRequirement {

    // private members
    private final int featureID;
    private pFlag participation;
    private float upperBound;
    private float lowerBound;
    private float rangeCoverage;

    // Getters and Setters
    public int getFeatureID() {
        return this.featureID;
    }

    public int getParticipation() { // return the numeric value
        return participation.getValue();
    }

    public void setParticipation(int value) {
        switch (value) {
            case 0:
                this.participation = pFlag.IGNORE;
                break;
            case 1:
                this.participation = pFlag.ANTECEDENT;
                break;
            case 2:
                this.participation = pFlag.CONSEQUENT;
                break;
            default:
                this.participation = pFlag.IGNORE; // ignore by default
        }
    }

    public float getUpperBound() {
        return upperBound;
    }

    public float getLowerBound() {
        return lowerBound;
    }

    /**
     * Note: Assumes lowerBound and upperBound are valid values. "Chose not to
     * throw from this function because I don't want Rule package imported into
     * lookupTable -Peter
     *
     * @param lowerBound
     * @param upperBound
     * @param rangeCoverage
     */
    public void setBoundRange(float lowerBound, float upperBound, float rangeCoverage) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.rangeCoverage = rangeCoverage;
    }

    public float getRangeCoverage() {
        return rangeCoverage;
    }

    // public constructor
    /**
     * Creates a FeatureRequirement (probably for usage in a Rule)
     *
     * @param featureID
     * @param pInt
     * @param upper
     * @param lower
     * @param rangeCoverage
     */
    public FeatureRequirement(int featureID, int pInt, float upper, float lower, float rangeCoverage){
        this.featureID = featureID;
        switch (pInt) {
            case 0:
                this.participation = pFlag.IGNORE;
                break;
            case 1:
                this.participation = pFlag.ANTECEDENT;
                break;
            case 2:
                this.participation = pFlag.CONSEQUENT;
                break;
            default:
                this.participation = pFlag.IGNORE; // ignore by default TODO: could decide to throw except
        }
        this.upperBound = upper;
        this.lowerBound = lower;
        this.rangeCoverage = rangeCoverage;
    }

    //private constructor (used to make copies only)
    private FeatureRequirement(int featureID, pFlag p, float upper, float lower, float rangeCoverage) {
        this.featureID = featureID;
        this.participation = p;
        this.upperBound = upper;
        this.lowerBound = lower;
        this.rangeCoverage = rangeCoverage;
    }

    // public functions
    /**
     * Checks if a value is within the range required
     *
     * @param value value for feature. TODO ASSUMPTION: Does not check if the
     * value is valid for a feature set or comes from a feature that this
     * requirement is associated with
     * @return true if the value is in the range required false otherwise
     */
    public boolean evaluate(float value) {
        // value on the edge. Always true
        if (value == this.lowerBound || value == this.upperBound) {
            return true;
        }
        // discrete bounds check
        if (this.lowerBound == this.upperBound) {
            return false; // value didn't equal either the upper or lower in the previous case
        }

        return value > this.lowerBound && value < this.upperBound;
    }

    /**
     * Helper function to duplicate state when mutating/doing crossovers
     *
     * @return a shallow copy clone of current state.
     */
    public FeatureRequirement copy() {
        return new FeatureRequirement(this.featureID, this.participation, this.upperBound, this.lowerBound, this.rangeCoverage);
    }

    // public fields
    // TODO may want to make changes to this for negation?
    public enum pFlag { // flag for rule participation
        IGNORE(0), ANTECEDENT(1), CONSEQUENT(2); // corresponding numerical values

        private final int value;

        private pFlag(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    @Override
    public String toString() {
        // I am choosing not to include the rangeCOverage in this as it should not be a factor in determining the
        // inherent similarity between Rules
        return "$"+this.featureID + "$" + this.participation + "$" + this.lowerBound + "$" + this.upperBound + "$";
    }
}

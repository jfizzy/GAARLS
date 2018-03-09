package RuleManager;

import java.lang.Float;

/**
 * Class: FeatureRequirement Intended functionality: Associated with a feature
 * for a given data set. Encodes a value range that a feature is expected to
 * exist in and an evaluation function that determines if a feature value is
 * within that range Feature Owner: James
 */
public class FeatureRequirement {

    // private members
    //private final int featureID; // not sure that we need this 
    // as the index in the rule specifies which 
    // feature it pertains to
    private pFlag participation;
    private float upperBound;
    private float lowerBound;

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

    public void setUpperBound(float upperBound) throws InvalidFeatReqException {
        int comparison = Float.compare(upperBound, this.lowerBound);
        if (comparison > 0) {
            this.upperBound = upperBound;
        } else if (comparison == 0) { // becoming discrete
            //TODO
        } else {
            throw new InvalidFeatReqException("Attempted to set invalid Upper Bound");
        }
    }

    public float getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(float lowerBound) throws InvalidFeatReqException{
        int comparison = Float.compare(this.upperBound, lowerBound);
        if (comparison > 0) {
            this.lowerBound = lowerBound;
        }else if(comparison == 0){ // becoming discrete
            //TODO
        }else{
            throw new InvalidFeatReqException("Attempted to set invalid Lower Bound");
        }
    }

    // public constructor
    public FeatureRequirement(int pInt, float upper, float lower) throws InvalidFeatReqException {
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
        
        int comparison = Float.compare(upper, lower);
        if (comparison > 0) { // this must be enforced
            this.upperBound = upper;
            this.lowerBound = lower;
        } else if (comparison == 0) { // this means it is discrete
            //TODO
        } else {
            throw new InvalidFeatReqException("Upper Bound < Lower Bound which is invalid");
        }
    }

    //private constructor (used to make copies)
    private FeatureRequirement(pFlag p, float upper, float lower) {
        this.participation = p;

        this.upperBound = upper;
        this.lowerBound = lower;
    }

    // public functions
    /**
     * Checks if a value is within the range required
     *
     * @param value value for feature. ASSUMPTION: Does not check if the value
     * is valid for a feature set or comes from a feature that this requirement
     * is associated with
     * @return if the value is in the range required
     */
    public boolean evaluate(float value) {
        int compareUpper = Float.compare(this.upperBound, value);
        int compareLower = Float.compare(value, this.lowerBound);
        return (compareUpper >= 0 && compareLower <= 0);
    }

    /**
     * Helper function to duplicate state when mutating/doing crossovers
     *
     * @return a shallow copy clone of current state.
     */
    public FeatureRequirement copy() {
        return new FeatureRequirement(this.participation, this.upperBound, this.lowerBound); // private constructor
    }

    // public fields
    private enum pFlag { // flag for rule participation
        IGNORE(0), ANTECEDENT(1), CONSEQUENT(2); // corresponding numerical values

        private final int value;

        private pFlag(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    // exceptions
    public class InvalidFeatReqException extends Exception {

        public InvalidFeatReqException(String message) {
            super(message);
        }
    }
}

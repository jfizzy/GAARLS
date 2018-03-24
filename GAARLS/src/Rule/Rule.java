package Rule;

import java.util.ArrayList;
import java.security.MessageDigest;

/**
 * Class: Rule Intended functionality: Container class of FeatureRequirements.
 * Container size should be equal to LookupTable.NUM_FEATURES
 *
 * Feature Owner: James
 */
public class Rule {

    // private members
    private FeatureRequirement featureReqs[];
    private static int features = 22;
    private float coverage;
    private float accuracy;
    private float rangeCoverage;
    private float completeness;

    public float getRangeCoverage() {
        return rangeCoverage;
    }

    public void setRangeCoverage(float rangeCoverage) {
        this.rangeCoverage = rangeCoverage;
    }

    public float getCompleteness() {
        return completeness;
    }

    public void setCompleteness(float completeness) {
        this.completeness = completeness;
    }

    public void setCoverage(float coverage) {
        this.coverage = coverage;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getCoverage() {
        return coverage;
    }

    public float getAccuracy() {
        return accuracy;
    }

    // getters and setters
    public FeatureRequirement getFeatureReq(int index) {
        return this.featureReqs[index];
    }

    public FeatureRequirement[] getFeatureReqs() {
        return featureReqs;
    }

    public static void setNumFeatures(int numFeatures) {
        features = numFeatures;
    }

    //public constructor
    public Rule() {
        this.featureReqs = new FeatureRequirement[this.features];

        for (int i = 0; i < this.featureReqs.length; i++) {
            featureReqs[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value

        }
    }
    //public constructor that accepts featureReqs

    public Rule(FeatureRequirement[] featureReqs) {
        this.featureReqs = new FeatureRequirement[this.features];
        for (int i = 0; i < this.featureReqs.length; i++) {
            this.featureReqs[i] = featureReqs[i].copy(); // shallow copy
        }
    }

    // public methods
    /**
     * New comparison operator for Rule objects. We define two rules as equal if
     * they have identical clauses.
     *
     * @param obj: the rule being compared to this instance of Rule for equality
     * @return True if other and this have same FeatureRequirement for each
     * element of the Rule; else false.
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null) // check if obj is null
        {
            return false;
        }
        if (!Rule.class.isAssignableFrom(obj.getClass())) {     // check if obj is a Rule object
            return false;
        }
        final Rule other = (Rule) obj;
        final FeatureRequirement[] othersFR = other.getFeatureReqs();

        // Look for any difference in feature requirement values
        for (int i = 0; i < othersFR.length; i++) {
            if (othersFR[i].getParticipation() != this.featureReqs[i].getParticipation()) {
                return false;
            }
            if (othersFR[i].getUpperBound() != this.featureReqs[i].getUpperBound()) {
                return false;
            }
            if (othersFR[i].getLowerBound() != this.featureReqs[i].getLowerBound()) {
                return false;
            }
        }

        // If here, all values were identical, meeting our definition for equality
        return true;
    }
    
    /**
     * Another comparison operator for Rule objects. Checks a given hash against
     * that of this Rule. 
     *
     * @param hashCode: the signature of the previously encountered Rule
     * @return True if other and this have same FeatureRequirement for each
     * element of the Rule; else false.
     */
    public boolean equals(int hashCode) {
        return this.generateID() == hashCode;
    }

    /**
     * Basic function to allow us to update the feature requirements as needed
     *
     * @param index
     * @param p
     * @param upperBound
     * @param lowerBound
     * @return true if the update was successful false otherwise
     */
    public boolean updateFeatureRequirement(int index, int p, float upperBound, float lowerBound) { //TODO could do this a few ways
        try {
            FeatureRequirement fr = featureReqs[index]; // index could be out of bounds
            fr.setParticipation(p);
            fr.setBoundRange(lowerBound, upperBound, 0);
            return true;
        } catch (Exception e) {
            System.out.println("Issue updating feature req");
            return false;
        }
    }

    /**
     * Basic function to allow us to replace a feature requirement entirely
     *
     * @param index
     * @param fr
     * @return
     */
    public boolean replaceFeatureRequirement(int index, FeatureRequirement fr) { //TODO this is another option
        try {
            featureReqs[index] = fr;
            return true;
        } catch (IndexOutOfBoundsException iobe) {
            System.out.println(iobe.getMessage());
            return false;
        }
    }

    /**
     * Helper function to quickly return the featureReqs in the antecedent of
     * the rule
     *
     *
     * @return ArrayList containing the antecedents of this rule null if there
     * are none
     */
    public ArrayList<FeatureRequirement> antecedent() {
        ArrayList<FeatureRequirement> ante = new ArrayList<>();
        for (FeatureRequirement featureReq : this.featureReqs) {
            if (featureReq.getParticipation() == 1) {
                ante.add(featureReq);
            }
        }
        if (ante.isEmpty()) {
            return null;
        }
        return ante;
    }

    /**
     * Helper function to quickly return the featureReqs on the consequent of
     * the rule
     *
     *
     * @return ArrayList containing the antecedents of this rule null if there
     * are none
     */
    public ArrayList<FeatureRequirement> consequent() {
        ArrayList<FeatureRequirement> cons = new ArrayList<>();
        for (FeatureRequirement featureReq : this.featureReqs) {
            if (featureReq.getParticipation() == 2) {
                cons.add(featureReq);
            }
        }
        if (cons.isEmpty()) {
            return null;
        }
        return cons;
    }

    /**
     * Helper function to assist with mutation/cross overs
     *
     * @return shallow copy of Rule
     */
    public Rule copy() {
        // does a shallow copy of @mFeatureRequirements using FeatureRequirement.Clone()
        return new Rule(this.featureReqs);
    }

    /**
     * Potential util function for cross over. Not set in stone so feel free to
     * change
     *
     * @param toMerge parent2. NOTE: Not modified in function call
     * @param featuresToMerge bool flag list. false = parent1's feature. true =
     * parent2's feature
     * @return
     */
    public Rule merge(Rule toMerge, ArrayList<Boolean> featuresToMerge) {
        assert (featuresToMerge.size() == Rule.features); // Safety check for fuckery!
        FeatureRequirement newFeatureReqs[] = new FeatureRequirement[Rule.features];

        for (int i = 0; i < Rule.features; i++) {
            if (featuresToMerge.get(i)) { // True meaning !this
                newFeatureReqs[i] = toMerge.getFeatureReq(i).copy();
            } else { // False meaning this
                newFeatureReqs[i] = this.getFeatureReq(i).copy();
            }
        }
        return new Rule(newFeatureReqs); // make a new Rule using the merged frs
    }

    public int generateID() {
        String returnVal = "?";
        for (FeatureRequirement fr : this.featureReqs) {
            returnVal += fr.toString() + "?";
        }
        return returnVal.hashCode();
    }

    // private methods
}

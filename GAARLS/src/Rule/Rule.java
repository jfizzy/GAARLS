package Rule;

import java.util.ArrayList;

/**
 * Class: Rule Intended functionality: Container class of FeatureRequirements.
 * Container size should be equal to LookupTable.NUM_FEATURES
 *
 * Feature Owner: James
 */
public class Rule {

    // private members
    private FeatureRequirement featureReqs[]; 
    private static int features;
    private float coverage;
    private float accuracy;

    public void setCoverage(float coverage) { this.coverage = coverage; }
    public void setAccuracy(float accuracy) { this.accuracy = accuracy; }
    public float getCoverage() { return coverage; }
    public float getAccuracy() { return accuracy; }

    // getters and setters
    public FeatureRequirement getFeatureReq(int index){
        return this.featureReqs[index];
    }
    
    public FeatureRequirement[] getFeatureReqs() {
        return featureReqs;
    }

    public static void setNumFeatures(int numFeatures)
    {
        features = numFeatures;
    }

    //public constructor
    public Rule() {
        this.featureReqs = new FeatureRequirement[this.features];
        try {
            for (int i = 0; i < this.featureReqs.length; i++) {
                featureReqs[i] = new FeatureRequirement(i, 0, 0f, 0f, 0f); // default initial value
            }
        } catch (FeatureRequirement.InvalidFeatReqException ifre) {
            System.out.println(ifre.getMessage());
        }
    }

    //private constructor
    private Rule(FeatureRequirement[] featureReqs) {
        this.featureReqs = new FeatureRequirement[this.features];
        for (int i = 0; i < this.featureReqs.length; i++) {
            this.featureReqs[i] = featureReqs[i].copy(); // shallow copy
        }
    }
    
    // public methods
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
            if (Float.compare(upperBound, fr.getUpperBound()) != 0) { // see if change necessary
                fr.setUpperBound(upperBound);
            }
            if (Float.compare(lowerBound, fr.getLowerBound()) != 0) // see if change necessary
            {
                fr.setLowerBound(lowerBound);
            }
            return true;
        } catch (FeatureRequirement.InvalidFeatReqException ifre) {
            System.out.println(ifre.getMessage());
            return false;
        } catch (IndexOutOfBoundsException iobe) {
            System.out.println("Index was out of bounds");
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
        
        for(int i=0;i<Rule.features;i++){
            if(featuresToMerge.get(i)){ // True meaning !this
                newFeatureReqs[i] = toMerge.getFeatureReq(i).copy();
            }else{ // False meaning this
                newFeatureReqs[i] = this.getFeatureReq(i).copy();
            }
        }
        return new Rule(newFeatureReqs); // make a new Rule using the merged frs
    }

    // private methods
}

import Rule.FeatureRequirement;

import java.util.ArrayList;

/*
 * Authors: 	Peter Ebear, David Engel, Evan Loughlin, James MacIsaac, Shane Sims
 * Project: 	Genetic Algorithm based Association Rule Learning System
 * Course: 	CPSC 599.44 - Machine Learning - Winter 2018
 * Professor:	Dr. Jörg Denzinger
 * University:	University of Calgary
 */

/**
 * ConfigParameters: wrapper class to hold all of the configuration file's
 * defined parameters so that classes who need this information can use them.
 * 
 * @author James MacIsaac
 */
public class ConfigParameters {
    public final int initialPopSize;
    public final int numGenerations;
    public final int populationMax;
    
    public final float minCoverage;
    public final float minAccuracy;
    public final int crossToMute;
    
    public final float baseFitnessWeight;
    public final float ext1FitnessWeight;
    public final float ext2FitnessWeight;
    
    public final int individualsToTrim;
    
    public final int numFeatAntecedent;
    public final int numFeatConsequent;
    public final ArrayList<Integer> featuresToIgnore;
    public final ArrayList<FeatureRequirement> requiredFeatures;
    
    public ConfigParameters(int initPopSize, int numGens, int popMax, 
            float minCov, float minAcc, int crossToMute, 
            float baseFitW, float ext1FitW, float ext2FitW, int iToTrim,int numFeatA, 
            int numFeatC, ArrayList<Integer> featToIg, ArrayList<FeatureRequirement> requiredFeatures){
        this.initialPopSize = initPopSize;
        this.numGenerations = numGens;
        this.populationMax = popMax;
        this.minCoverage = minCov;
        this.minAccuracy = minAcc;
        this.crossToMute = crossToMute;
        this.baseFitnessWeight = baseFitW;
        this.ext1FitnessWeight = ext1FitW;
        this.ext2FitnessWeight = ext2FitW;
        this.individualsToTrim = iToTrim;
        this.numFeatAntecedent = numFeatA;
        this.numFeatConsequent = numFeatC;
        this.featuresToIgnore = featToIg;
        this.requiredFeatures = requiredFeatures;
    }
    
    /** printConfigDetails: method to quickly print the configuration details 
     * for this run of the program
     * 
     * this should be handy if and when we get into running a series of 
     * consecutive configurations on our system to evaluate a personal experiment
     * @return formatted string with all config details listed
     */
    public String formattedConfigDetails(){
        String returnVal = ""+
        "------Configuration parameters------\n"+
        "Initial Population Size: \t\t"+this.initialPopSize+"\n"+
        "Number Of Generations: \t\t\t"+this.numGenerations+"\n"+
        "Maximum Population Size: \t\t"+this.populationMax+"\n"+
        "Min Coverage: \t\t\t\t"+this.minCoverage+"\n"+
        "Min Accuracy: \t\t\t\t"+this.minAccuracy+"\n"+
        "Crossover to Mutation Ratio: \t\t"+this.crossToMute+"\n"+
        "Base Fitness Weighting: \t\t"+this.baseFitnessWeight+"\n"+
        "Ext1 Fitness Weighting: \t\t"+this.ext1FitnessWeight+"\n"+
        "Ext2 Fitness Weighting: \t\t"+this.ext2FitnessWeight+"\n"+
        "Individuals to Trim: \t\t\t"+this.individualsToTrim+"\n"+
        "Num Antecedent Features: \t\t"+this.numFeatAntecedent+"\n"+
        "Num Consequent Features: \t\t"+this.numFeatConsequent+"\n";
        if (this.featuresToIgnore != null) {
            returnVal += "Features to Ignore: \t\t\t"+
            "[";
            returnVal = this.featuresToIgnore.stream().map((feature) -> " " + ConfigParameters.indexToFeat(feature) + " ").reduce(returnVal, String::concat);
            returnVal += "]\n";
        }
        if (this.requiredFeatures != null) {
            returnVal += "Required Features: \t\t\t"+
            "[";
            returnVal = this.requiredFeatures.stream().map((featureReq) -> featureReq.getFeatureID()).map((id) -> {
                int realID = id;
                realID = this.featuresToIgnore.stream().filter((f) -> (id >= f)).map((_item) -> 1).reduce(realID, Integer::sum);
                return realID;
            }).map((realID) -> " " + ConfigParameters.indexToFeat(realID) + " ").reduce(returnVal, String::concat);
            returnVal += "]\n";
        }
        returnVal += "------------------------------------\n\n";
        return returnVal;
    }
    
    /**
     * indexToFeat: used to print the common name of features to the user
     * 
     * @param index
     * @return Name of feature
     *         null otherwise
     */
    private static String indexToFeat(int index){
        switch (index){
            case 0:
                return "C_YEAR";
            case 1:
                return "C_MONTH";
            case 2:
                return "C_WDAY";
            case 3:
                return "C_HOUR";
            case 4:
                return "C_SEV";
            case 5:
                return "C_VEHS";
            case 6:
                return "C_CONF";
            case 7:
                return "C_RCFG";
            case 8:
                return "C_WTHR";
            case 9:
                return "C_RSUR";
            case 10:
                return "C_RALN";
            case 11:
                return "C_TRAF";
            case 12:
                return "V_ID";
            case 13:
                return "V_TYPE";
            case 14:
                return "V_YEAR";
            case 15:
                return "P_ID";
            case 16:
                return "P_SEX";
            case 17:
                return "P_AGE";
            case 18:
                return "P_PSN";
            case 19:
                return "P_ISEV";
            case 20:
                return "P_SAFE";
            case 21:
                return "P_USER";
            case 22:
                return "C_CASE";
            case 23:
                return "C_OCCUR";
            default:
                return null;
        }
    }
}

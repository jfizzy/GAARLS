
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
    
    public final int numFeatAntecedent;
    public final int numFeatConsequent;
    public final ArrayList<Integer> featuresToIgnore;
    public final ArrayList<FeatureRequirement> requiredFeatures;
    
    public ConfigParameters(int initPopSize, int numGens, int popMax, 
            float minCov, float minAcc, int crossToMute, 
            float baseFitW, float ext1FitW, float ext2FitW, int numFeatA, 
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
        "Num Antecedent Features: \t\t"+this.numFeatAntecedent+"\n"+
        "Num Consequent Features: \t\t"+this.numFeatConsequent+"\n";
        if (this.featuresToIgnore != null) {
            returnVal += "Indices of Features to Ignore: \t\t"+
            "[";
            returnVal = this.featuresToIgnore.stream().map((feature) -> " " + feature + " ").reduce(returnVal, String::concat);
            returnVal += "]\n";
        }
        if (this.requiredFeatures != null) {
            returnVal += "Indices of Required Features: \t\t"+
            "[";
            returnVal = this.requiredFeatures.stream().map((feature) -> " " + feature + " ").reduce(returnVal, String::concat);
            returnVal += "]\n";
        }
        returnVal += "------------------------------------\n\n";
        return returnVal;
    }
}


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
    public final float probOfCrossover;
    public final float probOfMutation;
    
    public final float baseFitnessWeight;
    public final float ext1FitnessWeight;
    public final float ext2FitnessWeight;
    
    public final int numFeatAntecedent;
    public final int numFeatConsequent;
    public final ArrayList<Integer> featuresToIgnore;
    
    public ConfigParameters(int initPopSize, int numGens, int popMax, 
            float minCov, float minAcc, float probCross, float probMut, 
            float baseFitW, float ext1FitW, float ext2FitW, int numFeatA, 
            int numFeatC, ArrayList<Integer> featToIg){
        this.initialPopSize = initPopSize;
        this.numGenerations = numGens;
        this.populationMax = popMax;
        this.minCoverage = minCov;
        this.minAccuracy = minAcc;
        this.probOfCrossover = probCross;
        this.probOfMutation = probMut;
        this.baseFitnessWeight = baseFitW;
        this.ext1FitnessWeight = ext1FitW;
        this.ext2FitnessWeight = ext2FitW;
        this.numFeatAntecedent = numFeatA;
        this.numFeatConsequent = numFeatC;
        this.featuresToIgnore = featToIg;
    }
}

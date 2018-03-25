
import java.util.ArrayList;

/*
 * Authors: 	Peter Ebear, David Engel, Evan Loughlin, James MacIsaac, Shane Sims
 * Project: 	Genetic Algorithm based Association Rule Learning System
 * Course: 	CPSC 599.44 - Machine Learning - Winter 2018
 * Professor:	Dr. Jörg Denzinger
 * University:	University of Calgary
 */

/**
 * ConfigParameters: Wrapper class for the configuration parameters to keep them
 * central and accessible to everyone/thing
 * 
 * @author James MacIsaac
 */
public class ConfigParameters {

    // TODO: this class is definitely ready to be adapted for additional 
    //  variables that we may need or want
    // Basic Setup parameters
    public final int initialPopSize;
    public final int numGenerations;
    public final int populationMax;

    //Thresholds
    public final float minCoverage;
    public final float minAccuracy;

    //Evolutionary Method proc chances
    public final float probOfCrossover;
    public final float probOfMutation;

    //Weights for all the things
    public final float baseFitnessWeight;
    public final float ext1FitnessWeight;
    public final float ext2FitnessWeight;
    // definitely will need others...

    //Rule styling values
    public final int numFeatAntecedent;
    public final int numFeatConsequent;
    public final ArrayList<Integer> featuresToIgnore;

    public ConfigParameters(int popSz, int numGens, int popMax, float minCov,
            float minAcc, float cOfXover, float cOfMut, float baseFW,
            float ext1FW, float ext2FW, int nfAnte, int nfCons, ArrayList<Integer> featToIg) {
        this.initialPopSize = popSz;
        this.numGenerations = numGens;
        this.populationMax = popMax;

        this.minCoverage = minCov;
        this.minAccuracy = minAcc;

        this.probOfCrossover = cOfXover;
        this.probOfMutation = cOfMut;

        this.baseFitnessWeight = baseFW;
        this.ext1FitnessWeight = ext1FW;
        this.ext2FitnessWeight = ext2FW;

        this.numFeatAntecedent = nfAnte;
        this.numFeatConsequent = nfCons;
        this.featuresToIgnore = featToIg;
    }
}

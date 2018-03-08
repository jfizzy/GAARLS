/**
 * Class: FeatureRequirement
 * Intended functionality: Associated with a feature for a given data set. Encodes a value range that a feature is expected to
 * exist in and an evaluation function that determines if a feature value is within that range
 * Feature Owner: TODO
 */

public class FeatureRequirement
{
    // public functions
    public FeatureRequirement()
    {

    }

    /**
     * Checks if a value is within the range required
     * @param value value for feature.
     *              ASSUMPTION: Does not check if the value is valid for a feature set or comes from
     *              a feature that this requirement is associated with
     * @return if the value is in the range required
     */
    public boolean IsSatisfied(float value)
    {
        return false;
    }

    /**
     * Helper function to duplicate state when mutating/doing crossovers
     * @return a shallow copy clone of current state.
     */
    public FeatureRequirement Clone()
    {
        return new FeatureRequirement();
    }

    // private functions

    // private members

}

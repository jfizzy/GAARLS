package Rule;

import java.util.ArrayList;

public class RuleRegex {
    private ArrayList<FeatureRequirement> featureRequirements;

    public RuleRegex() {
       featureRequirements = new ArrayList<>();
    }

    public RuleRegex(ArrayList<FeatureRequirement> featureRequirements) {
        this.featureRequirements = featureRequirements;
    }

    public boolean addFeatureRequirement(FeatureRequirement req) {
        return featureRequirements.add(req);
    }

    public ArrayList<FeatureRequirement> getFeatureRequirements() {
        return featureRequirements;
    }

    // Checks whether the specified clauses in the regex match a given rule
    // If these clauses/featureReqs match then the rest of the rule doesn't matter
    // if at least one of these clauses doesn't match then we don't match the rule
    // Note that an empty set of featureRequirements matches every rule
    public boolean matches(Rule rule) {
        for (FeatureRequirement req: featureRequirements) {
            // TODO: Override equals for FeatureRequirement to check internals
            if (!rule.getFeatureReq(req.getFeatureID()).equals(req))
                return false;
        }
        return true;
    }
}

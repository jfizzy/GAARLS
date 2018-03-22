import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Rule.Rule;

public class Parser {

    public ArrayList<Rule> parseKnownRules(String ruleFilePath) {
        File ruleFile = new File(ruleFilePath);
        ArrayList<Rule> knownRules = new ArrayList<>();
        if (ruleFile.exists()) {
            System.out.println("Found rule file '" + ruleFilePath + "'. Parsing rules...");
            try {
                BufferedReader br = new BufferedReader(new FileReader(ruleFile));
                String rule;
                // pull out the relevant portions of the rule
                Pattern pattern = Pattern.compile("([CVP]_\\w*):\\s\\[?([\\w\\-\\s:,]*)\\]?");
                Matcher matcher;
                while ((rule = br.readLine()) != null) {
                    // split the antecedents and consequents so we set participation correctly
                    String parts[] = rule.split("=>");
                    Rule temp = new Rule();
                    for (int i = 0; i < parts.length; ++i) {
                        matcher = pattern.matcher(parts[i]);
                        while (matcher.find()) {
                            // group 1 is the feature name e.g. C_YEAR
                            int featureId = LookupTable.featureMap.get(matcher.group(1));
                            // group 2 is our min max, or discrete value
                            String bounds[] = matcher.group(2).split("\\s-\\s");
                            float min = 0.0f;
                            try {
                                min = LookupTable.featureValueMaps[featureId].get(bounds[0].trim());
                            } catch (NullPointerException e) {
                                // TODO: Remove this after certain that regex matches all possible feature values
                                System.out.println("ID for " + matcher.group(1) + " " + featureId);
                                System.out.println(LookupTable.featureValueMaps[featureId].get(bounds[0].trim()));
                                System.out.println("Size: " + LookupTable.featureValueMaps[featureId].size());
                                System.out.println(bounds[0].trim());
                                System.exit(1);
                            }
                            float max;
                            // range
                            if (bounds.length == 2) {
                                max = LookupTable.featureValueMaps[featureId].get(bounds[1].trim());
                            // discrete
                            } else {
                                max = min;
                            }
                            if (!temp.updateFeatureRequirement(featureId, i+1, max, min)) {
                               System.out.println("WARNING: Error updating feature requirement: " + matcher.group(1) + "=" + matcher.group(2));
                            }
                        }
                    }
                    knownRules.add(temp);
                }
            } catch (IOException e) {
                System.out.println("ERROR: I/O error when parsing rule file '" + ruleFilePath + "'");
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Complete. Parsed " + knownRules.size() + " rules.");
        return knownRules;
    }
}

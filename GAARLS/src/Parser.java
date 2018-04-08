import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Rule.FeatureRequirement;
import Rule.Rule;
import Rule.RuleRegex;

public class Parser {

    public ArrayList<Rule> parseKnownRules(String ruleFilePath, ArrayList<Integer> featuresToOmit) {
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
                    if (rule.toLowerCase().contains("regex"))
                        continue;
                    // split the antecedents and consequents so we set participation correctly
                    String parts[] = rule.split("=>");
                    if (parts.length < 2) {
                        continue;
                    }
                    Rule temp = new Rule();
                    boolean blockedRule = false;
                    for (int i = 0; i < parts.length; ++i) {
                        matcher = pattern.matcher(parts[i]);
                        while (matcher.find() && !blockedRule) {
                            // group 1 is the feature name e.g. C_YEAR
                            if (LookupTable.featureMap.get(matcher.group(1)) != null) {
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
                                if (!temp.updateFeatureRequirement(featureId, i + 1, max, min)) {
                                    System.out.println("WARNING: Error updating feature requirement: " + matcher.group(1) + "=" + matcher.group(2));
                                }
                            } else {
                                System.out.println("WARNING: Rule \n" + rule + "\ncontains omitted feature " + matcher.group(1) + ". Skipping...");
                                blockedRule = true;
                                break;
                            }
                        }
                        if (blockedRule)
                            break;
                    }
                    if (temp.consequent() == null || temp.antecedent() == null || blockedRule)
                        continue;
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

    public ArrayList<RuleRegex> parseKnownRuleRegexs(String ruleFilePath) {
        File ruleFile = new File(ruleFilePath);
        ArrayList<RuleRegex> knownRegexs = new ArrayList<>();
        if (ruleFile.exists()) {
            System.out.println("Found rule file '" + ruleFilePath + "'. Parsing rule regexs...");
            try {
                BufferedReader br = new BufferedReader(new FileReader(ruleFile));
                String rule;
                // pull out the relevant portions of the rule
                Pattern pattern = Pattern.compile("([CVP]_\\w*):\\s\\[?([\\w\\-\\s:,]*)\\]?");
                Matcher matcher;
                while ((rule = br.readLine()) != null) {
                    if (rule.toLowerCase().contains("regex") == false)
                        continue;
                    // split the antecedents and consequents so we set participation correctly
                    String parts[] = rule.split("=>");
                    RuleRegex temp = new RuleRegex();
                    boolean blockedRule = false;
                    for (int i = 0; i < parts.length; ++i) {
                        matcher = pattern.matcher(parts[i]);
                        while (matcher.find() && !blockedRule) {
                            // group 1 is the feature name e.g. C_YEAR
                            if (LookupTable.featureMap.get(matcher.group(1)) != null) {
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
                                if (!temp.addFeatureRequirement(new FeatureRequirement(featureId, i+1, min, max, 0))) {
                                    System.out.println("WARNING: Error updating feature requirement: " + matcher.group(1) + "=" + matcher.group(2));
                                }
                            } else {
                                System.out.println("WARNING: Rule \n" + rule + "\ncontains omitted feature " + matcher.group(1) + ". Skipping...");
                                blockedRule = true;
                                break;
                            }
                        }
                        if (blockedRule)
                            break;
                    }
                    if (temp.getFeatureRequirements().size() == 0 || blockedRule)
                        continue;
                    knownRegexs.add(temp);
                }
            } catch (IOException e) {
                System.out.println("ERROR: I/O error when parsing rule file '" + ruleFilePath + "'");
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Complete. Parsed " + knownRegexs.size() + " rule regexs.");
        return knownRegexs;
    }

    public ArrayList<Rule> parseWekaRules (String wekaFilePath, LookupTable lookupTable, ArrayList<Integer> featuresToOmit) {
        File wekaFile = new File(wekaFilePath);
        ArrayList<Rule> wekaRules = new ArrayList<>();

        if (wekaFile.exists()) {
            System.out.println("Found WEKA file '" + wekaFilePath + "'. Parsing rules...");
            try {
                BufferedReader br = new BufferedReader(new FileReader(wekaFile));
                String rule;
                // pull out the relevant portions of the rule
                Pattern pattern = Pattern.compile("(\\w*)=(\\d*)");
                Matcher matcher;
                while ((rule = br.readLine()) != null) {
                    String parts[] = rule.split("==>");
                    if (parts.length < 2) {
                        continue;
                    }
                    Rule temp = new Rule();
                    boolean blockedRule = false;
                    for (int i = 0; i < parts.length; ++i) {
                        matcher = pattern.matcher(parts[i]);
                        while (matcher.find() && !blockedRule) {
                            if (LookupTable.featureMap.get(matcher.group(1)) != null) {
                                int featureID = LookupTable.featureMap.get(matcher.group(1));
                                float value = lookupTable.TranslateFeatureSymbol(featureID, matcher.group(2));
                                if (!temp.updateFeatureRequirement(featureID, i+1, value, value)) {
                                    System.out.println("WARNING: Error updating feature requirement: " + matcher.group(1) + "=" + matcher.group(2));
                                }
                            } else {
                                System.out.println("WARNING: Rule \n" + rule + "\ncontains omitted feature " + matcher.group(1) + ". Skipping...");
                                blockedRule = true;
                                break;
                            }
                        }
                        if (blockedRule)
                            break;
                    }
                    if (temp.consequent() == null || temp.antecedent() == null || blockedRule)
                        continue;
                    wekaRules.add(temp);
                }
            } catch (IOException e) {
                System.out.println("ERROR: I/O error when parsing rule file '" + wekaFilePath + "'");
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Complete. Parsed " + wekaRules.size() + " rules.");
        /*for (Rule rule : wekaRules) {
            System.out.println(lookupTable.TranslateRule(rule));
        }*/
        return wekaRules;
    }
    
    public ConfigParameters parseConfigParameters(String configFilePath) {
        File configFile = new File(configFilePath);
        if (configFile.exists()) {
            System.out.println("Found config file '" + configFilePath + "'. Parsing configuration parameters...");
            try {
                BufferedReader br = new BufferedReader(new FileReader(configFile));
                String paramLine;
                // pull out the relevant portions of the rule

                Pattern emptyLinePatt = Pattern.compile("^\\s*$");
                Pattern initPopPatt = Pattern.compile("^INITIAL_POP_SIZE\\s*=\\s*\\d*$");
                Pattern numGenPatt = Pattern.compile("^NUM_GENERATIONS\\s*=\\s*\\d*$");
                Pattern popMaxPatt = Pattern.compile("^MAX_POPULATION\\s*=\\s*\\d*$");

                Pattern minCovPatt = Pattern.compile("^MIN_COVERAGE\\s*=\\s*\\d*(\\.\\d*)?$");
                Pattern minAccPatt = Pattern.compile("^MIN_ACCURACY\\s*=\\s*\\d*(\\.\\d*)?$");

                Pattern crossToMutePatt = Pattern.compile("^CROSS_TO_MUTE\\s*=\\s*\\d*$");

                Pattern bFWPatt = Pattern.compile("^BASE_FITNESS_WEIGHT\\s*=\\s*\\d*(\\.\\d*)?$");
                Pattern e1FWPatt = Pattern.compile("^EXT1_FITNESS_WEIGHT\\s*=\\s*\\d*(\\.\\d*)?$");
                Pattern e2FWPatt = Pattern.compile("^EXT2_FITNESS_WEIGHT\\s*=\\s*\\d*(\\.\\d*)?$");

                Pattern numFAPatt = Pattern.compile("^NUM_FEATURES_ANTE\\s*=\\s*\\d*$");
                Pattern numFCPatt = Pattern.compile("^NUM_FEATURES_CONS\\s*=\\s*\\d*$");
                Pattern featTIPatt = Pattern.compile("^FEATURES_TO_IGNORE\\s*=\\s*(\\d*|[CVP]_[A-Z]{1,5})?((\\s*,\\s*\\d*)|(\\s*,\\s*[CVP]_[A-Z]{1,5}))*$");

                Pattern featReqPatt = Pattern.compile("^REQUIRED_FEATURE\\s*=\\s*([^;]+;[^;]+;[^;]+;[^;]+)$");

                Integer initialPopSize = null, numGenerations = null, populationMax = null, crossToMute = null;
                Float minCoverage = null, minAccuracy = null;
                Float baseFitnessWeight = null, ext1FitnessWeight = null, ext2FitnessWeight = null;
                Integer numFeatAntecedent = null, numFeatConsequent = null;
                ArrayList<Integer> featuresToIgnore = null;
                ArrayList<FeatureRequirement> requiredFeatures = new ArrayList<>();

                while ((paramLine = br.readLine()) != null) {
                    if (paramLine.contains("//")) {
                        paramLine = paramLine.split("//")[0]; // remove comments from line
                    }
                    paramLine = paramLine.trim();
//                    paramLine = paramLine.toUpperCase();
                    try {
                        if (emptyLinePatt.matcher(paramLine).find()) {
                            continue; // nothing to due for empties
                        } else if (initPopPatt.matcher(paramLine).find()) {
                            initialPopSize = Integer.parseInt(paramLine.split("=")[1].trim());
                        } else if (numGenPatt.matcher(paramLine).find()) {
                            numGenerations = Integer.parseInt(paramLine.split("=")[1].trim());
                        } else if (popMaxPatt.matcher(paramLine).find()) {
                            populationMax = Integer.parseInt(paramLine.split("=")[1].trim());
                        } else if (minCovPatt.matcher(paramLine).find()) {
                            minCoverage = Float.parseFloat(paramLine.split("=")[1].trim());
                        } else if (minAccPatt.matcher(paramLine).find()) {
                            minAccuracy = Float.parseFloat(paramLine.split("=")[1].trim());
                        } else if (bFWPatt.matcher(paramLine).find()) {
                            baseFitnessWeight = Float.parseFloat(paramLine.split("=")[1].trim());
                        } else if (e1FWPatt.matcher(paramLine).find()) {
                            ext1FitnessWeight = Float.parseFloat(paramLine.split("=")[1].trim());
                        } else if (e2FWPatt.matcher(paramLine).find()) {
                            ext2FitnessWeight = Float.parseFloat(paramLine.split("=")[1].trim());
                        } else if (numFAPatt.matcher(paramLine).find()) {
                            numFeatAntecedent = Integer.parseInt(paramLine.split("=")[1].trim());
                        } else if (numFCPatt.matcher(paramLine).find()) {
                            numFeatConsequent = Integer.parseInt(paramLine.split("=")[1].trim());
                        } else if (featTIPatt.matcher(paramLine).find()) {
                            String[] feats = paramLine.split("=")[1].trim().split(",");
                            ArrayList<Integer> featList = new ArrayList<>();
                            for (String feat : feats) {
                                try{
                                    int i = Integer.parseInt(feat.trim());
                                    if(!featList.contains(i)) // dont add it twice
                                        featList.add(i);
                                } catch (NumberFormatException e){
                                    Integer featIndex = Parser.featToIndex(feat); // get index
                                    if(featIndex != null && !featList.contains(featIndex)){ // don't add it twice
                                        featList.add(featIndex);
                                    }else{
                                        System.out.println("Feature name did not resolve");
                                    }
                                }
                            }
                            if (!featList.isEmpty()) {
                                featuresToIgnore = featList;
                            }
                        } else if (crossToMutePatt.matcher(paramLine).find()){
                            crossToMute = Integer.parseInt(paramLine.split("=")[1].trim());
                        } else if (featReqPatt.matcher(paramLine).find()) {
                            System.out.println("FOUND REQUIRED FEATURE");
                            Matcher matcher = featReqPatt.matcher(paramLine);
                            if (matcher.find()) {
                                // indices refer to the following
                                // 0: feature name
                                // 1: min value
                                // 2: max value
                                // 3: participation (must be non 0 or *)
                                String parts[] = matcher.group(1).split(";");
                                System.out.println("Splitting the feautre required");
                                System.out.println("Got: " + parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3]);
                                if (parts.length == 4 && LookupTable.featureMap.get(parts[0]) != null) {
                                    int featureID = LookupTable.featureMap.get(parts[0].trim());
                                    try {
                                        float min;
                                        if (parts[1].trim().compareTo("*") == 0) {
                                            min = -1f;
                                        } else {
                                            System.out.println("Finding value for: " + parts[1]);
                                            min = LookupTable.featureValueMaps[featureID].get(parts[1].trim());
                                        }
                                        float max;
                                        if (parts[2].trim().compareTo("*") == 0) {
                                            max = -1f;
                                        } else {
                                            System.out.println("Finding value for: " + parts[2]);
                                            max = LookupTable.featureValueMaps[featureID].get(parts[2].trim());
                                        }
                                        int participation;
                                        if (parts[3].trim().compareTo("*") == 0) {
                                            participation = -1;
                                        } else if (parts[3].trim().compareTo("0") == 0){
                                            System.out.println("WARNING: Required Feature participation should be non-zero.");
                                            System.out.println("Trouble feature was " + matcher.group(1) + ". Ignoring this parameter...");
                                            continue;
                                        } else {
                                            try {
                                                participation = Integer.parseInt(parts[3].trim());
                                            } catch (NumberFormatException e) {
                                                System.out.println("WARNING: Couldn't parse participation value to integer.");
                                                System.out.println("Trouble feature was " + matcher.group(1) + ". Ignoring this parameter...");
                                                continue;
                                            }
                                        }
                                        requiredFeatures.add(new FeatureRequirement(featureID, participation, max, min, 0));
                                    } catch (NullPointerException e) {
                                        System.out.println("WARNING: Couldn't translate feature values for feature " + parts[0]);
                                        System.out.println("Trouble feature was: " + matcher.group(1) + ". Ignoring this parameter..." );
                                        continue;
                                    }
                                } else {
                                    System.out.println("WARNING: Parameter line was probably malformed.");
                                    System.out.println("Trouble feature was: " + matcher.group(1) + ". Ignoring this parameter...");
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR: There was an issue parsing [" + paramLine + "]");
                    }

                }
                ConfigParameters cp = new ConfigParameters(
                        initialPopSize != null ? initialPopSize : 1000,
                        numGenerations != null ? numGenerations : 1000,
                        populationMax != null ? populationMax : 1300,
                        minCoverage != null ? minCoverage : 0.01f, 
                        minAccuracy != null ? minAccuracy : 0.01f,
                        crossToMute != null ? crossToMute : 10,
                        baseFitnessWeight != null ? baseFitnessWeight : 1.0f,
                        ext1FitnessWeight != null ? ext1FitnessWeight : 0f, 
                        ext2FitnessWeight != null ? ext2FitnessWeight : 0f,
                        numFeatAntecedent != null ? numFeatAntecedent : 10, 
                        numFeatConsequent != null ? numFeatConsequent : 10,
                        featuresToIgnore != null ? featuresToIgnore : null,
                        // these don't need logic as the array is either null or contains stuff, we don't need to check
                        requiredFeatures

                );
                return cp; // return the new obj
            } catch (IOException io) {
                System.out.println("ERROR: I/O error when parsing rule file '" + configFilePath + "'");
                System.out.println(io.getMessage());
                return null; // had an issue
            }
        }
        return null; // couldnt find file
    }
    
    /**
     * featToIndex: translates the string version of the feature
     * to its index in the list of features.
     * (Used to omit features by name)
     * 
     * @param featStr
     * @return Int of index for specified feature
     *         null otherwise
     */
    private static Integer featToIndex(String featStr){
        switch (featStr){
            case "C_YEAR":
                return 0;
            case "C_MONTH":
                return 1;
            case "C_WDAY":
                return 2;
            case "C_HOUR":
                return 3;
            case "C_SEV":
                return 4;
            case "C_VEHS":
                return 5;
            case "C_CONF":
                return 6;
            case "C_RCFG":
                return 7;
            case "C_WTHR":
                return 8;
            case "C_RSUR":
                return 9;
            case "C_RALN":
                return 10;
            case "C_TRAF":
                return 11;
            case "V_ID":
                return 12;
            case "V_TYPE":
                return 13;
            case "V_YEAR":
                return 14;
            case "P_ID":
                return 15;
            case "P_SEX":
                return 16;
            case "P_AGE":
                return 17;
            case "P_PSN":
                return 18;
            case "P_ISEV":
                return 19;
            case "P_SAFE":
                return 20;
            case "P_USER":
                return 21;
            case "C_CASE":
                return 22;
            case "C_OCCUR":
                return 23;
            default:
                return null;
        }
    }

}

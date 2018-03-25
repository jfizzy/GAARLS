
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
                            if (!temp.updateFeatureRequirement(featureId, i + 1, max, min)) {
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

                Pattern minCovPatt = Pattern.compile("^MIN_COVERAGE\\s*=\\s*\\d*(.\\d*)?$");
                Pattern minAccPatt = Pattern.compile("^MIN_ACCURACY\\s*=\\s*\\d*(.\\d*)?$");

                Pattern pXPatt = Pattern.compile("^P_OF_CROSSOVER\\s*=\\s*\\d*(.\\d*)?$");
                Pattern pMPatt = Pattern.compile("^P_OF_MUTATION\\s*=\\s*\\d*(.\\d*)?$");

                Pattern bFWPatt = Pattern.compile("^BASE_FITNESS_WEIGHT\\s*=\\s*\\d*(.\\d*)?$");
                Pattern e1FWPatt = Pattern.compile("^EXT1_FITNESS_WEIGHT\\s*=\\s*\\d*(.\\d*)?$");
                Pattern e2FWPatt = Pattern.compile("^EXT2_FITNESS_WEIGHT\\s*=\\s*\\d*(.\\d*)?$");

                Pattern numFAPatt = Pattern.compile("^NUM_FEATURES_ANTE\\s*=\\s*\\d*$");
                Pattern numFCPatt = Pattern.compile("^NUM_FEATURES_CONS\\s*=\\s*\\d*$");
                Pattern featTIPatt = Pattern.compile("^FEATURES_TO_IGNORE\\s*=\\s*\\d(\\s*,\\s*\\d)*$");

                Integer initialPopSize = null, numGenerations = null, populationMax = null;
                Float minCoverage = null, minAccuracy = null;
                Float probOfCrossover = null, probOfMutation = null;
                Float baseFitnessWeight = null, ext1FitnessWeight = null, ext2FitnessWeight = null;
                Integer numFeatAntecedent = null, numFeatConsequent = null;
                ArrayList<Integer> featuresToIgnore = null;

                while ((paramLine = br.readLine()) != null) {
                    if (paramLine.contains("//")) {
                        paramLine = paramLine.split("//")[0]; // remove comments from line
                    }
                    paramLine = paramLine.trim();
                    paramLine = paramLine.toUpperCase();
                    try {
                        if (emptyLinePatt.matcher(paramLine).find()) {
                            continue; // nothing to due for empties
                        } else if (initPopPatt.matcher(paramLine).find()) {
                            initialPopSize = Integer.parseInt(paramLine.split("=")[1].trim());
                            continue;
                        } else if (numGenPatt.matcher(paramLine).find()) {
                            numGenerations = Integer.parseInt(paramLine.split("=")[1].trim());
                            continue;
                        } else if (popMaxPatt.matcher(paramLine).find()) {
                            populationMax = Integer.parseInt(paramLine.split("=")[1].trim());
                            continue;
                        } else if (minCovPatt.matcher(paramLine).find()) {
                            minCoverage = Float.parseFloat(paramLine.split("=")[1].trim());
                            continue;
                        } else if (minAccPatt.matcher(paramLine).find()) {
                            minAccuracy = Float.parseFloat(paramLine.split("=")[1].trim());
                            continue;
                        } else if (pXPatt.matcher(paramLine).find()) {
                            probOfCrossover = Float.parseFloat(paramLine.split("=")[1].trim());
                            continue;
                        } else if (pMPatt.matcher(paramLine).find()) {
                            probOfMutation = Float.parseFloat(paramLine.split("=")[1].trim());
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
                                featList.add(Integer.parseInt(feat.trim()));
                            }
                            if (!featList.isEmpty()) {
                                featuresToIgnore = featList;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR: There was an issue parsing [" + paramLine + "]");
                    }

                }
                //TODO: lets talk about these default parameters and set them appropriately
                ConfigParameters cp = new ConfigParameters(
                        initialPopSize != null ? initialPopSize : 100, 
                        numGenerations != null ? numGenerations : 1000,
                        populationMax != null ? populationMax : 1000, 
                        minCoverage != null ? minCoverage : 0.01f, 
                        minAccuracy != null ? minAccuracy : 0.01f, 
                        probOfCrossover != null ? probOfCrossover : 0.85f, 
                        probOfMutation != null ? probOfMutation : 0.15f, 
                        baseFitnessWeight != null ? baseFitnessWeight : 1.0f,
                        ext1FitnessWeight != null ? ext1FitnessWeight : 1.0f, 
                        ext2FitnessWeight != null ? ext2FitnessWeight : 1.0f,
                        numFeatAntecedent != null ? numFeatAntecedent : 2, 
                        numFeatConsequent != null ? numFeatConsequent : 2,
                        featuresToIgnore != null ? featuresToIgnore : null
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

    
}

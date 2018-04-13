# Genetic Algorithm-based Association Rule Learning System
Machine Learning System

Running the Learning System

From the directory GAARLS, containing:

ConfigParameters.java
Database.java
DiscreteSymbolTranslator.java
EvolutionManager.java
FitnessManager.java
LookupTable.java
Main.java
Parser.java
RangeSymbolTranslator.java
RuleManager.java
config.txt
weka.txt
rules.txt
NCDB_1999_to_2015.csv
./Rule:
FeatureRequirement.java
Rule.java
RuleRegex.java


1. Add any configuration parameters to file config.txt (an example file is provided for convenience)

2. Add any known rules to avoid to rules.txt (an example file is provided for convenience)

3. Add any WEKA rules for penalization in the fitness function to  weka.txt (an example file is provided for convenience)

3. Issue the following commands:

javac *.java
java Main

4. View learned rules in outputRules.txt


Command Line Arguments

The GAARLS system also accepts a few command line arguments. These are used to change the default input files for the data set, known rules, weka rules, and configuration settings. They need not be specified unless the user wishes to use files other than the examples provided with the system.

-d <data_file_path>

-r <rule_file_path>

-w <weka_rule_file_path>

-c <config_file_path>


Guide to Configuration File Parameters



INITIAL_POP_SIZE=<integer value>

The number of rules to generate for our initial population. Defaults to 1000


NUM_GENERATIONS=<integer value>

The number of generations to perform during evolution. Defaults to 1000


MAX_POPULATION=<integer value>

The maximum population of rules we should have before culling. Defaults to 1300


MIN_COVERAGE=<floating point value>

The min coverage value for rules allowed to enter our population. Defaults to 0.01. Rules without a coverage of at least this value are assigned a fitness of 0.

Value restrictions: 0.0 - 1.0


MIN_ACCURACY=<floating point value>

The min accuracy value for rules allowed to enter our population. Defaults to 0.01. Rules without an accuracy of at least this value are assigned a fitness of 0.

Value restrictions: 0.0 - 1.0


CROSS_TO_MUTE=<integer value>

The number of crossovers we perform before doing a mutation. Defaults to 10.


BASE_FITNESS_WEIGHT=<floating point value>

The weight assigned to our base fitness when evaluating overall fitness of a rule. Defaults to 1.0.

Value restrictions: 0.0 - 1.0


EXT1_FITNESS_WEIGHT=<floating point value>

The weight assigned to the first extension of the fitness function (Hamming Distance from Weka Rules). Defaults to 0.0.

Value restrictions: 0.0 - 1.0


EXT2_FITNESS_WEIGHT=<floating point value>

The weight assigned to the second extension of the fitness function (Completeness Factor). Defaults to 0.0.

Value restrictions: 0.0 - 1.0


INDIVIDUALS_TO_TRIM=<integer value>

The number of individuals to trim from the population once the maximum population is reached. Defaults to 100. If 100 will be bigger than the MAX_POPULATION then it defaults to MAX_POPULATION / 10 = 10%.

Value restrictions: 0 - MAX_POPULATION.

Special note: It is highly discouraged to have a value as high as MAX_POPULATION.


NUM_FEATURES_ANTE=<integer value>

The number of features to have in the antecedent when using an initial population of fixed size rules. Defaults to random number of features.
Value restrictions: 1 - (23 - NUM_FEATURES_CONS)

Warning: The higher this number is specified to be, the more difficult (longer) it will be to generate a valid initial state. 

Special note: There are currently 24 features in our dataset. We need at least 1 antecedent and consequent. If FEATURES_TO_IGNORE is used this max value should be reduced by the number of ignored features.

NUM_FEATURES_CONS=<integer value>

The number of features to have in the consequent when using an initial population of fixed size rules. Defaults to random number of features.

Value restrictions: 1 - (23 - NUM_FEATURES_ANTE)

Warning: The higher this number is specified to be, the more difficult (longer) it will be to generate a valid initial state. 

Special note: There are currently 24 features in our dataset. We need at least 1 antecedent and consequent. If FEATURES_TO_IGNORE is used this max value should be reduced by the number of ignored features.


REQUIRED_FEATURE=<FEAT_NAME;MIN_VALUE;MAX_VALUE;PARTICIPATION_VALUE>

A feature that we require in any generated rules throughout the initialization and evolution of the population. We require one REQUIRED_FEATURE line per feature that should be required in generated rules.

FEAT_NAME is the english name of the feature as defined in the Data Dictionary provided by the National Collision Database.

[MIN|MAX]_VALUE is the minimum and maximum value for the required feature, it should be provided in the english description in the format outlined by the Data Dictionary as well. To allow any value for a minimum or maximum the wildcard ‘*’ can be used in place of an actual value. 

PARTICIPATION_VALUE is where in a rule the feature should occur. This can have a value of 1, to require the feature be in the antecedent, 2 to require the feature be in the consequent, or a wildcard ‘*’ in place of an actual value to allow for either.

Examples:

REQUIRED_FEATURE=P_AGE;*;*;* (requires P_AGE with any range exists somewhere in all rules)

REQUIRED_FEATURE=P_AGE;18 Years Old;*;* (requires P_AGE with a range of 18 to any value exists somewhere in all rules)

REQUIRED_FEATURE=P_AGE;*;65 Years Old;1 (requires P_AGE with a range of any value to 65 exists in the antecedent of all rules)

REQUIRED_FEATURE=P_AGE;18 Years Old;22 Years Old;2 (requires P_AGE with a range of 18 to 22 exists in the consequent of all rules)

Special Note: For discrete features such as  C_CONF, MIN_VALUE is used. A MIN_VALUE that is set, with a wildcard value for MAX_VALUE will require MIN_VALUE be present in all rules. This is to avoid having to duplicate long strings in the config file. If you are in doubt as to whether or not a feature is discrete, simply specify the same value in both MIN_VALUE and MAX_VALUE to avoid surprising behaviour.

Examples:

REQUIRED_FEATURE=C_SEV;Collision producing at least one fatality;*;2


REQUIRED_FEATURE=C_SEV;Collision producing at least one fatality;Collision producing at least one fatality;2

Both of the above will produce the same behaviour as C_SEV is a discrete-valued feature.


FEATURES_TO_IGNORE=<FEATURE1[,FEATURE2,FEATURE3,...]>

A singular value or optionally a list of features to ignore when generating rules. These are specified as the english feature names given in the Data Dictionary provided by the National Collision Database.

Special Note: Ignoring features will cause the size of our feature list to change, be careful to ensure that parameters like NUM_FEAT_ANTE and NUM_FEAT_CONS are updated accordingly.


Usage of Known Rules

As indicated in the basic running instructions for the system a file named rules.txt may be used to read known rules into the system. These rules may be specified as follows with one rule per line in the file.

Rule: [A] C_SEV: Collision producing non-fatal injury |  => [C] C_WDAY: [Wednesday - Friday] | P_ID: 1 |

Each rule specified must contain at least one consequent and antecedent. Each rule must also contain the english versions of feature names and feature values as outlined by the NCDB Data Dictionary. Note that the input format for rules is identical to the output of the system found in outputRules.txt. The user may directly copy rules from the output into this file to avoid them in future runs.

Additionally, the user may specify a Rule Regex. This will perform partial matches and exclude any rules that contain at least the specified clauses. These are specified as follows.

Rule Regex: [A] P_ID: 1 | => [C] C_WDAY: Friday |

The above example would avoid producing any rules with P_ID=1 in the antecedent, and C_WDAY=Friday in the consequent.

Note that the parsing of these rules is somewhat flexible. For instance the ‘[A]’ and ‘[C]’ strings may be omitted for ease of use when manually specifying rules, however, for best results the above formats should be adhered to. 

Usage of WEKA Rules

The user may also specify a list of WEKA rules for use in the fitness function via the file weka.txt. Rules that are similar to these will have their fitness penalized in order to direct the system to create rules different from those of the WEKA system. The format of these rules is as follows, and is identical to the output produced by WEKA.

1. C_SEV=2 P_PSN=11 4073813 ==> P_AGE=21 4046791    <conf:(0.99)> lift:(1.39) lev:(0.19) [1144548] conv:(43.35)

It is worth noting that the WEKA output file in its entirety may be copied into weka.txt. The additional header information provided by the WEKA system will be ignored during parsing.

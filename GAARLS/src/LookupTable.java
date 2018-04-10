import Rule.FeatureRequirement;
import Rule.Rule;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class: LookupTable
 * Intended functionality: A container class that translates feature values into float values, and provides utility
 * functions to generate feature values in a correct range
 * NOTE: ParseFile is hard coded for each column in the data set, and does not include the last column.
 * Any reordering of the data set or additions/removals will need to be reflected in
 * Feature Owner: Peter
 */

public class LookupTable
{
    public static final int NUM_FEATURES_IN_FILE = 23;
    public static final int NUM_ADDED_FEATURES = 1;
    public static LookupTable ParseFile(String filePath)
    {
        return ParseFile(filePath, new ArrayList<>());
    }
    //public functions
    public static LookupTable ParseFile(String filePath, ArrayList<Integer> featuresToOmit)
    {
        ArrayList<Boolean> desiredFeaturesList = new ArrayList<>();
        for (int i = 0; i < NUM_FEATURES_IN_FILE + NUM_ADDED_FEATURES; ++i) desiredFeaturesList.add(true);
        if (featuresToOmit != null) {
            for (int i = 0; i < featuresToOmit.size(); ++i) desiredFeaturesList.set(featuresToOmit.get(i), false);
        }

        ArrayList<SymbolTranslatorBase> featureTranslators = new ArrayList<>();
        int translatorIdx = 0;

        // Feature 1: Collision Year
        {
            int numValues = 17; // 1999-2015
            int symbolSize = 4;
            String featureName = "C_YEAR";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                int year = 1999 + i;
                values[i] = year;
                symbols[i] = Integer.toString(year);
                translations[i] = Integer.toString(year);
            }
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new RangeSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations, false));
            }
            translatorIdx++;
        }

        // Feature 2: Collision Month
        {
            int numValues = 12; // 12 months
            int symbolSize = 2;
            String featureName = "C_MONTH";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                int month = 1 + i;
                values[i] = month;
                symbols[i] = (month < 10 ? "0" : "") + Integer.toString(month);
            }
            translations = new String[]{
                    "January"
                    , "February"
                    , "March"
                    , "April"
                    , "May"
                    , "June"
                    , "July"
                    , "August"
                    , "September"
                    , "October"
                    , "November"
                    , "December"
            };
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new RangeSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations, true));
            }
            translatorIdx++;
        }

        // Feature 3: Collision Day of the Week
        {
            int numValues = 7; // 7 days
            int symbolSize = 1;
            String featureName = "C_WDAY";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                int day = 1 + i;
                values[i] = day;
                symbols[i] = Integer.toString(day);
            }
            translations = new String[]{
                    "Monday"
                    , "Tuesday"
                    , "Wednesday"
                    , "Thursday"
                    , "Friday"
                    , "Saturday"
                    , "Sunday"
            };
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new RangeSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations, true));
            }
            translatorIdx++;
        }

        // Feature 4: Collision Hour
        {
            int numValues = 24; // 24 hours in a day
            int symbolSize = 2;
            String featureName = "C_HOUR";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                values[i] = i;
                symbols[i] = (i < 10 ? "0" : "") + Integer.toString(i);
                String hour = Integer.toString(i);
                translations[i] = hour + ":00 to " + hour + ":59";
            }
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new RangeSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations, true));
            }
            translatorIdx++;
        }

        // Feature 5: Collision Severity
        {
            int symbolSize = 1;
            String featureName = "C_SEV";

            int[] values = new int[]{1, 2};
            String[] symbols = new String[]{"1", "2"};
            String[] translations = new String[]{"Collision producing at least one fatality", "Collision producing non-fatal injury"};
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 6: Num Vehicles in Collision
        {
            int numValues = 99;
            int symbolSize = 2;
            String featureName = "C_VEHS";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues - 1; i++) {
                int numVehicles = 1 + i;
                values[i] = numVehicles;
                symbols[i] = (numVehicles < 10 ? "0" : "") + Integer.toString(numVehicles);
                translations[i] = symbols[i] + " vehicles involved";
            }
            values[numValues - 1] = numValues - 1;
            symbols[numValues - 1] = Integer.toString(numValues - 1);
            translations[numValues - 1] = symbols[numValues - 1] + " or more vehicles involved";
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new RangeSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations, false));
            }
            translatorIdx++;
        }

        // Feature 7: accident configuration
        {
            int numValues = 18;
            int symbolSize = 2;
            String featureName = "C_CONF";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            int counter = 0;
            values[counter] = 1; translations[counter] = "One Vehicle: Hit a moving object"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 2; translations[counter] = "One Vehicle: Hit a stationary object"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 3; translations[counter] = "One Vehicle: Ran off left shoulder"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 4; translations[counter] = "One Vehicle: Ran off right shoulder"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 5; translations[counter] = "One Vehicle: Rollover on roadway"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 6; translations[counter] = "One Vehicle: Other one vehicle configuration"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;

            values[counter] = 21; translations[counter] = "Two Vehicle (SameDir): Rear end collision"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 22; translations[counter] = "Two Vehicle (SameDir): Approaching side-swipe"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 23; translations[counter] = "Two Vehicle (SameDir): Left turn across opposing traffic"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 24; translations[counter] = "Two Vehicle (SameDir): Right turn including turn conflicts"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 25; translations[counter] = "Two Vehicle (SameDir): Other two vehicle (SD) configuration"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;

            values[counter] = 31; translations[counter] = "Two Vehicle (DiffDir): Head on collision"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 32; translations[counter] = "Two Vehicle (DiffDir): Approaching side-swipe"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 33; translations[counter] = "Two Vehicle (DiffDir): Left turn across opposing traffic"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 34; translations[counter] = "Two Vehicle (DiffDir): Right turn including turn conflicts"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 35; translations[counter] = "Two Vehicle (DiffDir): Right angle collision"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;
            values[counter] = 36; translations[counter] = "Two Vehicle (DiffDir): Other two vehicle (DD) configuration"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;

            values[counter] = 41; translations[counter] = "Two Vehicle (Parked): Hit a parked motor vehicle"; symbols[counter] = (values[counter] < 10 ? "0" : "") + Integer.toString(values[counter]); counter++;

            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 8: Road configuration
        {
            int numValues = 12;
            int symbolSize = 2;
            String featureName = "C_RCFG";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                values[i] = i + 1;
                symbols[i] = (values[i] < 10 ? "0" : "") + Integer.toString(values[i]);
            }

            translations = new String[]{
                    "Non-intersection"
                    , "At an intersection of atleast two public roadways"
                    , "Intersection with parking lot entrance/exit, private driveway or laneway"
                    , "Railroad level crossing"
                    , "Bridge,overpass,viaduct"
                    , "Tunnel or underpass"
                    , "Passing or climbing lane"
                    , "Ramp"
                    , "Traffic circle"
                    , "Express lane of a freeway system"
                    , "Collector lane of a freeway system"
                    , "Transfer lane of a freeway system"
            };
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 9: Weather
        {
            int numValues = 7;
            int symbolSize = 1;
            String featureName = "C_WTHR";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                values[i] = i + 1;
                symbols[i] = Integer.toString(values[i]);
            }

            translations = new String[]{
                    "Clear and Sunny"
                    , "Overcast, cloudy but no precipitation"
                    , "Raining"
                    , "Snowing, not including drifting snow"
                    , "Freezing rain, sleet, hail"
                    , "Visibility limitation"
                    , "Strong Wind"
            };
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 10: Road surface
        {
            int numValues = 9;
            int symbolSize = 1;
            String featureName = "C_RSUR";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                values[i] = i + 1;
                symbols[i] = Integer.toString(values[i]);
            }

            translations = new String[]{
                    "Dry, normal"
                    , "Wet"
                    , "Snow (fresh, loose snow)"
                    , "Slush, wet snow"
                    , "Icy"
                    , "Sand/gravel/dirt"
                    , "Muddy"
                    , "Oil"
                    , "Flooded"
            };
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 11: Road allignment
        {
            int numValues = 6;
            int symbolSize = 1;
            String featureName = "C_RALN";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                values[i] = i + 1;
                symbols[i] = Integer.toString(values[i]);
            }

            translations = new String[]{
                    "Straight and level"
                    , "Straight with gradient"
                    , "Curved and level"
                    , "Curved with gradient"
                    , "Top of hill or gradient"
                    , "Bottom of hill or gradient"
            };
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 12: Traffic conditions
        {
            int numValues = 18;
            int symbolSize = 2;
            String featureName = "C_TRAF";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                values[i] = i + 1;
                symbols[i] = (values[i] < 10 ? "0" : "") + Integer.toString(values[i]);
            }

            translations = new String[]{
                    "Traffic signals fully operational"
                    , "Traffic signals in flashing mode"
                    , "Stop sign"
                    , "Yield sign"
                    , "Warning sign"
                    , "Pedestrian crosswalk"
                    , "Police officer"
                    , "School guard, flagman"
                    , "School crossing"
                    , "Reduced speed zone"
                    , "No passing zone sign"
                    , "Markings on the road"
                    , "School bus stopped with school bus signal lights flashing"
                    , "School bus stopped with school bus signal lights not flashing"
                    , "Railway crossing with signals, or signals and gates"
                    , "Railway crossing with signs only"
                    , "Control device not specified"
                    , "No control present"
            };
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 13: Vehicle Id
        {
            int numValues = 99;
            int symbolSize = 2;
            String featureName = "V_ID";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues - 1; i++) {
                values[i] = i + 1;
                translations[i] = symbols[i] = (values[i] < 10 ? "0" : "") + Integer.toString(values[i]);
            }
            values[numValues - 1] = numValues;
            symbols[numValues - 1] = symbols[numValues - 1] = (values[numValues - 1] < 10 ? "0" : "") + Integer.toString(values[numValues - 1]);
            translations[numValues - 1] = "Vehicle sequence number assigned to pedestrians";

            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 14: Vehicle type
        {
            int numValues = 17;
            int symbolSize = 2;
            String featureName = "V_TYPE";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            int vehicleType = 1;
            for (int i = 0; i < numValues; i++) {
                // Fuck me
                if (vehicleType == 2) vehicleType = 5;
                else if (vehicleType == 12) vehicleType = 14;
                else if (vehicleType == 15) vehicleType = 16;
                values[i] = vehicleType;
                symbols[i] = (vehicleType < 10 ? "0" : "") + Integer.toString(values[i]);
                vehicleType++;
            }

            translations = new String[]{
                    "Light Duty Vehicle"
                    , "Panel/cargo van (< 4536 KG)"
                    , "Other trucks and vans (< 4536 KG)"
                    , "Unit Trucks (> 4536 KG)"
                    , "Road tractor"
                    , "School bus"
                    , "Smaller school bus"
                    , "Urban and intercity bus"
                    , "Motorcycle and moped"
                    , "Off road vehicles"
                    , "Bicycle"
                    , "Purpose-build motorhome"
                    , "Farm equipment"
                    , "Construction Equipment"
                    , "Fire engine"
                    , "Snowmobile"
                    , "Street car"
            };
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 15: Vehicle Year
        {
            int numValues = 117; // 1960-2015
            int symbolSize = 4;
            String featureName = "V_YEAR";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                int year = 1900 + i;
                values[i] = year;
                symbols[i] = Integer.toString(year);
                translations[i] = Integer.toString(year);
            }
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new RangeSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations, false));
            }
            translatorIdx++;
        }

        // Feature 16: Passenger id
        {
            int numValues = 99;
            int symbolSize = 2;
            String featureName = "P_ID";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                int id = i+1;
                values[i] = id;
                symbols[i] = (values[i] < 10 ? "0" : "") + Integer.toString(values[i]);
                translations[i] = Integer.toString(id);
            }
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 17: Passenger Sex yeah buddy
        {
            int numValues = 2;
            int symbolSize = 1;
            String featureName = "P_SEX";

            int[] values = {0,1};
            String[] symbols = {"F", "M"};
            String[] translations = {"Female", "Male"};
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 18: Passenger age
        {
            int numValues = 100;
            int symbolSize = 2;
            String featureName = "P_AGE";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];

            values[0] = 0; symbols[0] = "0"; translations[0] = "Less than 1 Year Old";
            for (int i = 1; i < numValues - 1; i++) {
                int age = i;
                values[i] = age;
                symbols[i] = (values[i] < 10 ? "0" : "") + Integer.toString(values[i]);
                translations[i] = Integer.toString(age) + " Years Old";
            }
            values[numValues-1] = numValues - 1;
            symbols[numValues - 1] = (values[numValues - 1] < 10 ? "0" : "") + Integer.toString(values[numValues - 1]);
            translations[numValues - 1] = Integer.toString(values[numValues - 1]) + " Years or older";

            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new RangeSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations, false));
            }
            translatorIdx++;
        }

        // Feature 19: Passenger Position
        {
            int numValues = 31;
            int symbolSize = 2;
            String featureName = "P_PSN";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];

            int counter = 0;
            values[counter] = 11; translations[counter] = "Driver"; symbols[counter] = Integer.toString(values[counter]);counter++;
            values[counter] = 12; translations[counter] = "Front row, center"; symbols[counter] = Integer.toString(values[counter]);counter++;
            values[counter] = 13; translations[counter] = "Front row, right outboard, including motocycle passenger in sidecar"; symbols[counter] = Integer.toString(values[counter]);counter++;
            
            values[counter] = 21; translations[counter] = "Second row, left outboard, including motorcycle passenger"; symbols[counter] = Integer.toString(values[counter]);counter++;
            values[counter] = 22; translations[counter] = "Second row, center"; symbols[counter] = Integer.toString(values[counter]);counter++;
            values[counter] = 23; translations[counter] = "Second row, right outboard"; symbols[counter] = Integer.toString(values[counter]);counter++;
            
            String[] rowNames = {"Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth"};
            String[] positionNames = {"left outboard", "center", "right outboard"};
            for (int rowNumber = 3; rowNumber < 10; rowNumber++)
            {
                String rowName = rowNames[rowNumber - 3];
                for (int position = 0; position < 3; position++)
                {
                    values[counter] = rowNumber * 10 + position + 1;
                    symbols[counter] = Integer.toString(values[counter]);
                    translations[counter] = rowName + " row, " + positionNames[position];
                    counter++;
                }
            }

            values[counter] = 96; translations[counter] = "Position unknown, but the person was an occupant"; symbols[counter] = Integer.toString(values[counter]);counter++;
            values[counter] = 97; translations[counter] = "Sitting on someones lap"; symbols[counter] = Integer.toString(values[counter]);counter++;
            values[counter] = 98; translations[counter] = "Outside passenger compartment"; symbols[counter] = Integer.toString(values[counter]);counter++;
            values[counter] = 99; translations[counter] = "Pedestrian"; symbols[counter] = Integer.toString(values[counter]);counter++;

            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 20: Injury Severity
        {
            int numValues = 3;
            int symbolSize = 1;
            String featureName = "P_ISEV";

            int[] values = {1,2,3};
            String[] symbols = {"1", "2", "3"};
            String[] translations = {"No Injury", "Injury", "Fatality"};
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 21: Pasenger Safety
        {
            int numValues = 7;
            int symbolSize = 2;
            String featureName = "P_SAFE";

            int[] values = {1,2,9, 10,11,12,13};
            String[] symbols = {"01", "02", "09", "10", "11","12","13"};
            String[] translations = {
                    "No safety device used or No child restraint used"
                    , "Safety device used or child restraint used"
                    , "Helmet worn"
                    , "Reflective clothing worn"
                    , "both helmet and reflective clothing used"
                    , "Other safety device used"
                    , "No safety device equipped"
            };
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 22: Passenger type
        {
            int numValues = 5;
            int symbolSize = 1;
            String featureName = "P_USER";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                values[i] = i + 1;
                symbols[i] = Integer.toString(i + 1);
            }
            translations = new String[]{
                    "Motor Vehicle Driver"
                    , "Motor Vehicle Passenger"
                    , "Pedestrian"
                    , "Bicyclist"
                    , "Motorcyclist"
                    };
            if (desiredFeaturesList.get(translatorIdx) )
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        // Feature 23: Collision id
        {
            int numValues = 2400000;
            int symbolSize = 15;
            String featureName = "C_CASE";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            for (int i = 0; i < numValues; i++) {
                int id = i+1;
                values[i] = id;
                String symbol = Integer.toString(values[i]);
                for (int j = symbol.length(); j < symbolSize; ++j)
                {
                    symbol = "0" + symbol;
                }
                symbols[i] = symbol;
                translations[i] = Integer.toString(id);
            }
            if (desiredFeaturesList.get(translatorIdx))
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        //-------------------------------------------ADDED FEATURES--------------------------------------------------------------//
        int numFileFeatureTranslators = featureTranslators.size();
        // Feature 24: Collision Occurance
        {
            int numValues = 2;
            int symbolSize = 1;
            String featureName = "C_OCCUR";

            int[] values = new int[numValues];
            String[] symbols = new String[numValues];
            String[] translations = new String[numValues];
            values[0] = 1; symbols[0] = "L"; translations[0] = "Low Fatal Collisions";
            values[1] = 2; symbols[1] = "H"; translations[1] = "Low Fatal Collisions";

            if (desiredFeaturesList.get(translatorIdx))
            {
                featureTranslators.add( new DiscreteSymbolTranslator(translatorIdx,featureName, symbolSize, values, symbols, translations));
            }
            translatorIdx++;
        }

        featureValueMaps = new HashMap[featureTranslators.size()];
        for (int i = 0; i < featureTranslators.size(); ++i) {
            featureMap.put(featureTranslators.get(i).mFeatureName, i);
            featureValueMaps[i] = featureTranslators.get(i).getmValueReverseLookupTable();
        }

        return new LookupTable(featureTranslators.toArray(new SymbolTranslatorBase[featureTranslators.size()]),
                numFileFeatureTranslators,
                featureTranslators.size() - numFileFeatureTranslators );
    }

    /**
     * Generate a random value for a featureRequirement, respective to the allowable values of a given feature
     * @param featureId feature to create values for
     * @param toModify feature requirement that will house the changes
     *                 NOTE: @toModify is altered through this function call
     */
    public void GenerateRandomValue(int featureId, FeatureRequirement toModify, int wildcards)
    {
        if (featureId >= 0 && featureId < mFeatureLookupTable.length)
        {
            mFeatureLookupTable[featureId].GenerateRandomFeatureRequirement(toModify, wildcards);
        }
        else
        {
            System.out.println("Error: LookupTable.GenerateRandomValue(): featureId out of range. Got: " + featureId);
        }
    }

    /**
     * ToString() method for a rule that inserts feature names and translates feature values into symbols appropriate for
     * the data set
     * @param rule
     * @return
     */
    public String TranslateRule(Rule rule)
    {
        String translation = "Rule: [A] ";
        FeatureRequirement[] featureRequirements = rule.getFeatureReqs();
        ArrayList<Integer> antecedent = new ArrayList<>();
        ArrayList<Integer> consequent= new ArrayList<>();
        for (int i = 0; i < featureRequirements.length; ++i)
        {
            int participation = featureRequirements[i].getParticipation();
            if (participation == FeatureRequirement.pFlag.ANTECEDENT.getValue()) antecedent.add(i);
            else if (participation == FeatureRequirement.pFlag.CONSEQUENT.getValue()) consequent.add(i);
        }
        for (int id : antecedent)
        {
            translation += mFeatureLookupTable[id].FeatureRequirementToDescription(featureRequirements[id]) + " | ";
        }
        translation += " => [C] ";
        for (int id : consequent)
        {
            translation += mFeatureLookupTable[id].FeatureRequirementToDescription(featureRequirements[id]) + " | ";
        }
        return translation;
    }

    /**
     * ToString() method for a featureRequirement that inserts the feature name and translates the feature value into symbols appropriate for
     * that feature
     * @param featureId
     * @param featureRequirement
     * @return
     */
    private String TranslateFeatureRequirement(int featureId, FeatureRequirement featureRequirement)
    {
        if (featureId >= 0 && featureId < mFeatureLookupTable.length)
        {
            return mFeatureLookupTable[featureId].FeatureRequirementToDescription(featureRequirement);
        }
        else
        {
            System.out.println("Error: LookupTable.TranslateFeatureRequirement(): featureId out of range. Got: " + featureId);
            return "INVALID";
        }
    }

    /**
     * Util function used when parsing the database to translate a value in the database to it's float representation
     * @param featureId
     * @param symbol symbol in the database assigned to @featureId in the item
     * @return the float representation of the symbol.
     */
    public float TranslateFeatureSymbol(int featureId, String symbol)
    {
        if (featureId >= 0 && featureId < mFeatureLookupTable.length)
        {
            return mFeatureLookupTable[featureId].TranslateFeatureSymbol(symbol);
        }
        else
        {
            System.out.println("Error: LookupTable.TranslateFeatureSymbol(): featureId out of range. Got: " + featureId);
            return -1;
        }
    }

    /**
     * Util function used when parsing the database to translate a value in the database to it's float representation
     * @param featureId
     * @param value feature value for @featureId
     * @return the float representation of the symbol.
     */
    public String TranslateFeatureValue(int featureId, float value)
    {
        if (featureId >= 0 && featureId < mFeatureLookupTable.length)
        {
            return mFeatureLookupTable[featureId].FeatureValueToDescription(value);
        }
        else
        {
            System.out.println("Error: LookupTable.TranslateFeatureSymbol(): featureId out of range. Got: " + featureId);
            return "INVALID";
        }
    }

    public ArrayList<Integer> GetAddedIndices()
    {
        ArrayList<Integer> addedIndices = new ArrayList<>();
        for (int i = NumFileFeatures; i < NumFeatures; ++i)
        {
            addedIndices.add(mFeatureLookupTable[i].GetFileFeatureIndex());
        }
        return addedIndices;
    }

    public ArrayList<Integer> GetFileParsingIndices()
    {
        ArrayList<Integer> fileIndices = new ArrayList<>();
        for (int i = 0; i < NumFileFeatures; ++i)
        {
            fileIndices.add(mFeatureLookupTable[i].GetFileFeatureIndex());
        }
        return fileIndices;
    }

    public String getFeatureName(int featureIndex) {
        return mFeatureLookupTable[featureIndex].GetFeatureName();
    }

    // private functions
    private LookupTable(SymbolTranslatorBase[] featureTranslators, int numFileFeatures, int numAddedFeatures)
    {
        mFeatureLookupTable = featureTranslators;
        NumFileFeatures = numFileFeatures;
        NumAddedFeatures = numAddedFeatures;
        NumFeatures = mFeatureLookupTable.length;
        Rule.setNumFeatures(NumFeatures);
    }

    // public members
    public static HashMap<String, Integer> featureMap = new HashMap<>();
    public static HashMap<String, Float>[] featureValueMaps;

    // private members
    private SymbolTranslatorBase[] mFeatureLookupTable;
    public int NumFileFeatures;
    public int NumAddedFeatures;
    public int NumFeatures;

    public static void main(String args[])
    {
        ArrayList<Integer> test = new ArrayList<>();
        for (int i = 0; i < NUM_FEATURES_IN_FILE;++i)
        {
            test.add(i);
        }

        LookupTable lookupTable = LookupTable.ParseFile("", test); // parse lookup table file
        System.out.println("Empty lookup table: " + (lookupTable.mFeatureLookupTable.length == 0));
        test.clear();
        lookupTable = LookupTable.ParseFile("", test); // parse lookup table file
        System.out.println("Full lookup table: " + (lookupTable.mFeatureLookupTable.length == NUM_FEATURES_IN_FILE));

        Parser parser = new Parser();

        ConfigParameters cp = parser.parseConfigParameters("config.txt", null);

        Database database = Database.ParseFile("NCDB_1999_to_2015.csv", lookupTable, 10); // parse database file
        float[] datatable = database.GetDatabase();
        for (int i = 0; i < lookupTable.NumFeatures; ++i)
        {
            System.out.print(lookupTable.TranslateFeatureValue(i, datatable[i]) + " ");
        }
        System.out.println();

        try
        {
            FeatureRequirement feature = new FeatureRequirement(11, 0, 0, 0, 0);
            lookupTable.GenerateRandomValue(11, feature, 3);
            System.out.println( lookupTable.TranslateFeatureRequirement(11, feature));
        }
        catch (Exception e)
        {

        }
        RuleManager ruleManager = new RuleManager(lookupTable, new ArrayList<>());
        Rule testRule = ruleManager.generateRuleRandomSize();

        System.out.println(lookupTable.TranslateRule(testRule));
    }
}

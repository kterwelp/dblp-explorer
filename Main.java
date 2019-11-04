package src;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        //The Runtime class is used to calculate memory consumption of the program
        Runtime runTime = Runtime.getRuntime();

        long memoryStart = runTime.getRuntime().totalMemory() - runTime.getRuntime().freeMemory();

        System.out.println("This citation analysis program works for file \"outputacm.txt\" (version 1 on the website).");

        String keyword = "";
        int n = 0;

        //This try-catch block allows the user to enter a keyword and integer for number of tiers
        try {
            System.out.print("Enter a keyword: ");
            Scanner keywordInput = new Scanner(System.in);
            keyword = keywordInput.nextLine();

            //Keyword:  steady-state - 733 5th tier

            System.out.print("Enter an integer (number of tiers): ");
            Scanner nInput = new Scanner(System.in);
            n = nInput.nextInt();

        } catch (InputMismatchException ime) {
            ime.printStackTrace();
            System.out.println("Incorrect input entered. Exiting program...");
            System.exit(0);
        }

        //This starts the clock for the program execution run time
        long startTime = System.currentTimeMillis();

        JSONArray jArray = new JSONArray();

        //This try-catch block reads the "outputacm.txt" file and converts the groups of lines into
        //JSON objects by parsing the information.  The JSON objects are stored inside a JSON array.
        //This allows for the information to be easily searchable later in the program.
        try {
            FileInputStream inputFile = new FileInputStream("outputacm.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFile));

            String line = "";
            String previousLine = "";
            boolean nextObject = false;
            JSONObject jObject = null;
            int citationLength = 0;
            String citationsArray[] = null;
            String citationsStr = "";
            while ((line = br.readLine()) != null)
            {

                if (line.isEmpty() || line.charAt(0) != '#')
                {
                    if (jObject != null)
                    {
                        jArray.put(jObject);
                    }
                    previousLine = line;
                    jObject = null;
                    citationsArray = null;
                    citationLength = 0;
                    citationsStr = "";
                    continue;
                }
                else if ((previousLine.isEmpty() || previousLine.charAt(0) != '#') &&
                        line.charAt(0) == '#')
                {
                    jObject = new JSONObject();
                }

                if (line.charAt(1) == '*')
                {
                    String title = line.substring(2);
                    jObject.put("title", title);
                }
                else if(line.charAt(1) == '@')
                {
                    String author = line.substring(2);
                    jObject.put("author", author);
                }
                else if(line.charAt(1) == 't')
                {
                    String year = line.substring(2);
                    jObject.put("year", year);
                }
                else if(line.charAt(1) == 'c')
                {
                    String context = line.substring(2);
                    jObject.put("publication venue", context);
                }
                else if(line.charAt(1) == '!')
                {
                    String abstracts = line.substring(2);
                    jObject.put("abstract", abstracts);
                }
                else if(line.charAt(1) == '%')
                {
                    citationsStr += line.substring(2) + ", ";
                    jObject.put("citations", citationsStr);
                }
                else if(line.contains("index"))
                {
                    String index = line.substring(6);
                    jObject.put("index", index);

                    int indexInt = Integer.parseInt(index) + 1;

                    if (indexInt % 100000 == 0)
                    {
                        System.out.println("JSON Record # " + indexInt + " is completed.");
                    }
                }

                previousLine = line;

            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //The keywordArray holds the index of all articles that contain the keyword
        //The tierArray hold the index of all articles belonging to each tier
        ArrayList<String> keywordArray = new ArrayList<String>();
        ArrayList<String> tierArray = new ArrayList<String>();

        keywordTask[] kwTask = new keywordTask[10];

        //For loop is used to divide JSON array data into 10 groups of objects
        //This allows for multiple threads to search the objects simultaneously
        int range = 62981;
        for (int k = 0; k < 10; k++)
        {
            if (k == 9)
            {
                kwTask[k] = new keywordTask(keyword, keywordArray, jArray, (k)*range, jArray.length());
                break;
            }

            kwTask[k] = new keywordTask(keyword, keywordArray, jArray, (k)*range, (k+1)*range);
        }

        //This for loop starts each thread inside the keywordTask class
        for (int m = 0; m < 10; m++)
        {
            kwTask[m].start();
        }

        //This for loop adds the keywordArrays from each thread to the keywordArray
        //in the parent thread of the main program
        for (int p = 0; p < 10; p++)
        {
            keywordArray.addAll(kwTask[p].returnKeywordArray());
        }

        //This for loop collects all the threads created prior to executing the remaining
        //code of this main function for the parent thread
        for (int r = 0; r < 10; r++)
        {
            kwTask[r].join();
        }

        //This short delay for the parent thread helps prevent issues with executing the
        //remaining code prior to completing all child threads
        Thread.sleep(100);

        //If a keyword is not found in any article, the keywordArray is empty and this
        //group of statements will execute
        if (keywordArray.isEmpty())
        {
            System.out.println("The keyword " + keyword + " was not found in any article title.");
            System.out.println("Exiting program...");
            System.exit(0);
        }

        //This for loop creates a tier with each iteration for a total of n tiers (n specified by user).
        for (int x = 0; x < n; x++)
        {
            ParseCitationsTask[] pcTask = new ParseCitationsTask[10];

            //For loop is used to divide JSON array data into 10 groups of objects
            //This allows for multiple threads to search the objects simultaneously
            int range2 = 62981;
            for (int k = 0; k < 10; k++)
            {
                if (k == 9)
                {
                    pcTask[k] = new ParseCitationsTask(keywordArray, tierArray, jArray, (k)*range2, jArray.length());
                    break;
                }

                pcTask[k] = new ParseCitationsTask(keywordArray, tierArray, jArray, (k)*range2+1, (k+1)*range2);

            }

            //This for loop starts each thread inside the ParseCitationsTask class
            for (int m = 0; m < 10; m++)
            {
                pcTask[m].start();
            }

            //This for loop adds the tierArrays from each thread to the tierArray
            //in the parent thread of the main program
            for (int p = 0; p < 10; p++)
            {
                tierArray.addAll(pcTask[p].returnTierArray());
            }

            //This for loop collects all the threads created prior to executing the remaining
            //code of this main function for the parent thread
            for (int r = 0; r < 10; r++)
            {
                pcTask[r].join();
            }

            //This short delay for the parent thread helps prevent issues with executing the
            //remaining code prior to completing all child threads
            Thread.sleep(100);

            //This code executes if the tierArray is empty.  This means there are less tiers
            //than the number specified by the user and the program will end.
            if (tierArray.isEmpty())
            {
                System.out.println("There are " + x + " tiers for the keyword " + keyword + ".");
                System.out.println("Exiting program...");
                System.exit(0);
            }

            int tierNumber = x + 1;
            System.out.println("\nTier " + tierNumber + " Papers:");

            //The tempTierArray copies the tierArray so that it can print each JSON object
            //in order of the year and then remove it.  This makes certain a JSON object
            //is not printed twice.
            ArrayList<String> tempTierArray = (ArrayList<String>)tierArray.clone();
            int max = 0;
            int tempIndex = 0;
            int number = 1;
            JSONObject maxJObj = null;
            JSONObject prevObj = null;

            //This while loop finds the JSON objects with the most recent year and prints
            //that JSON object prior to removing it from the tempTierArray.
            while(!tempTierArray.isEmpty())
            {
                for (int s = 0; s < tempTierArray.size(); s++)
                {
                    int curIndex = Integer.parseInt(tempTierArray.get(s));
                    JSONObject jObj = jArray.getJSONObject(curIndex);

                    if (s == 0)
                    {
                        max = Integer.parseInt(jObj.getString("year"));
                        maxJObj = jObj;
                        tempIndex = 0;
                    }
                    else if (Integer.parseInt(jObj.getString("year")) >= max)
                    {
                        max = Integer.parseInt(jObj.getString("year"));
                        maxJObj = jObj;
                        tempIndex = s;
                    }
                }

                tempTierArray.remove(tempIndex);

                if (prevObj != null && maxJObj.getString("title").compareTo(prevObj.getString("title")) == 0)
                {
                    continue;
                }

                if (maxJObj.has("abstract"))
                {
                    System.out.println(number + ". " + "Index: " + maxJObj.getString("index") + ", Title: " + maxJObj.getString("title") + ", Author(s): " +
                            maxJObj.getString("author") + ", Year: " + maxJObj.getString("year") + ", Citations: " + maxJObj.getString("citations") +
                            ", Publication Venue: " + maxJObj.getString("publication venue") + ", Abstract: " + maxJObj.getString("abstract"));
                }
                else
                {
                    System.out.println(number + ". " + "Index: " + maxJObj.getString("index") + ", Title: " + maxJObj.getString("title") + ", Author(s): " +
                            maxJObj.getString("author") + ", Year: " + maxJObj.getString("year") + ", Citations: " + maxJObj.getString("citations") +
                            ", Publication Venue: " + maxJObj.getString("publication venue"));
                }

                prevObj = maxJObj;
                number++;
            }

            //The keywordArray and tierArray are cleared so they can be reused for the next tier.
            keywordArray.clear();
            keywordArray = (ArrayList<String>) tierArray.clone();
            tierArray.clear();
        }

        long endTime = System.currentTimeMillis();

        //This is the program execution time.
        long totalTime = (endTime - startTime) / 1000;
        long memoryEnd = runTime.getRuntime().totalMemory() - runTime.getRuntime().freeMemory();

        //This is the total memory used by the program.
        long totalMemoryUsed = memoryEnd - memoryStart;

        System.out.println("\nProgram execution time: " + totalTime + " seconds");
        System.out.println("Total memory used by program: " + totalMemoryUsed + " bytes");

    }
}

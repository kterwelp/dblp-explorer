package src;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class ParseCitationsTask extends Thread {

    private int rangeStart, rangeEnd;
    private ArrayList<String> keywordArray;
    private ArrayList<String> tierArray;
    private JSONArray jArray;

    public ParseCitationsTask(ArrayList<String> keywordArray, ArrayList<String> tierArray, JSONArray jArray, int rangeStart, int rangeEnd)
    {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.keywordArray = keywordArray;
        this.tierArray = tierArray;
        this.jArray = jArray;
    }

    @Override
    public void run() {

        //This for loop searches a particular range of the JSON array.
        //The citations of the JSON objects in the JSON array are parsed
        //if they exist.
        //If the citation (index) exists in the keywordArray, the index of
        //the article with that citation is added to the tierArray.
        //This code determines which articles belong to the current tier.
        for (int p = rangeStart; p < rangeEnd; p++)
        {
            JSONObject jObj = jArray.getJSONObject(p);
            String parseCitationsArr[];

            if (jObj.has("citations"))
            {
                String tempStr = jObj.getString("citations");
                parseCitationsArr = tempStr.split(", ");
            }
            else
            {
                continue;
            }

            for (int r = 0; r < parseCitationsArr.length; r++)
            {
                if (keywordArray.contains(parseCitationsArr[r]))
                {
                    tierArray.add(jObj.getString("index"));
                    break;
                }
            }
        }
    }

    //This function returns the tierArray
    public ArrayList<String> returnTierArray()
    {
        return tierArray;
    }
}

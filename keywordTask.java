package src;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class keywordTask extends Thread {

    private int k;
    private int rangeStart, rangeEnd;
    private JSONObject jObj;
    private String keyword;
    private ArrayList<String> keywordArray;
    private JSONArray jArray;


    public keywordTask(String keyword, ArrayList<String> keywordArray, JSONArray jArray, int rangeStart, int rangeEnd)
    {
        this.keyword = keyword;
        this.keywordArray = keywordArray;
        this.jArray = jArray;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    @Override
    public void run() {

            //This for loop searches a particular range of the JSON array.
            //The index of the JSON object is added to the keywordArray if the keyword matches
            //a string in the title for the article.
            for (int k = rangeStart+1; k < rangeEnd; k++)
            {
                JSONObject jObj = jArray.getJSONObject(k);

                if (jObj.getString("title").toLowerCase().contains(keyword.toLowerCase()))
                {
                    keywordArray.add(jObj.getString("index"));
                }
            }
        }

    //This function returns the keywordArray
    public ArrayList<String> returnKeywordArray()
    {
        return keywordArray;
    }
}

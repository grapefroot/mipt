import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class VenueSearcher {
    private String CLIENT_ID;
    private String CLIENT_SECRET;

    public VenueSearcher(String client_id, String client_secret) {
        CLIENT_ID = client_id;
        CLIENT_SECRET = client_secret;
    }

    public ArrayList<FoursquareVenue> call(String coordinateX, String coordinateY) throws IOException {
        ArrayList result = parseResponse(makeCall("https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&v=20130815&ll=" + coordinateX + "," + coordinateY));
        return result;
    }

    private static String makeCall(String url) throws IOException {
        URL myurl = new URL(url);

        InputStream ins;
        HttpsURLConnection con;

        con = (HttpsURLConnection) myurl.openConnection();

        try {
            ins = con.getInputStream();
        } catch (FileNotFoundException e) {
            ins = con.getErrorStream();
        }

        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader in = new BufferedReader(isr);

        StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            builder.append(inputLine);
        }

        in.close();
        return builder.toString();
    }


    private ArrayList parseResponse(final String response) {

        ArrayList<FoursquareVenue> temp = new ArrayList<>();
        try {

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);

            // make an jsonObject in order to parse the response
            if (jsonObject.has("response")) {
                if (jsonObject.getJSONObject("response").has("venues")) {
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("venues");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        FoursquareVenue poi = new FoursquareVenue();
                        if (jsonArray.getJSONObject(i).has("name")) {
                            poi.setName(jsonArray.getJSONObject(i).getString("name"));

                            if (jsonArray.getJSONObject(i).has("location")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("location").has("address")) {
                                    if (jsonArray.getJSONObject(i).getJSONObject("location").has("city")) {
                                        poi.setCity(jsonArray.getJSONObject(i).getJSONObject("location").getString("city"));
                                    }

                                    if (jsonArray.getJSONObject(i).has("contact")) {
                                        JSONObject contact = jsonArray.getJSONObject(i).getJSONObject("contact");
                                        if (contact.has("formattedPhone")) {
                                            poi.setPhone(contact.getString("formattedPhone"));
                                        }
                                        if (contact.has("phone")) {
                                            poi.setPhone(contact.getString("phone"));
                                        }
                                        if(contact.has("twitter")) {
                                            poi.setTwitter(contact.getString("twitter"));
                                        }
                                        if(contact.has("twitterName")) {
                                            poi.setTwitterName(contact.getString("twitterName"));
                                        }
                                        if(contact.has("facebook")) {
                                            poi.setFacebook(contact.getString("facebook"));
                                        }
                                        if(contact.has("facebookName")) {
                                            poi.setFacebookName(contact.getString("facebookName"));
                                        }
                                    }

                                    if (jsonArray.getJSONObject(i).has("categories")) {
                                        if (jsonArray.getJSONObject(i).getJSONArray("categories").length() > 0) {
                                            if (jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).has("icon")) {
                                                poi.setCategory(jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getString("name"));
                                            }
                                        }
                                    }

                                    temp.add(poi);
                                }
                            }
                        }
                    }
                }
            }

        } catch (
                Exception e
                )

        {
            e.printStackTrace();
            return new ArrayList<FoursquareVenue>();
        }

        return temp;
    }

}

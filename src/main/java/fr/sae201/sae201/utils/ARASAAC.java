package fr.sae201.sae201.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sae201.sae201.models.Pictograms.PictogramActions;
import fr.sae201.sae201.models.Pictograms.PictogramHair;
import fr.sae201.sae201.models.Pictograms.PictogramSkin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class ARASAAC {

    private final static String API_URL = "https://api.arasaac.org/api";
    private final static String LANG = "fr";

    public static JsonNode searchPicto(String... query){
        StringBuilder url = new StringBuilder(API_URL + "/pictograms/" + LANG + "/search/");
        for (String s : query) {
            url.append(s).append("%20");
        }
        if (query.length == 0) return null;
        try {
            return sendGetRequest(String.valueOf(url));
        } catch (IOException e) {
            return null;
        }
    }

    public static List<String> getKeywords(){
        try {
            String keyWords=sendGetRequest(API_URL + "/keywords/" + LANG).toString();
            String[] keywordsArray = keyWords.replace("\"", "").split(",");
            List<String> keywordList = Arrays.asList(keywordsArray);
            return keywordList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode sendGetRequest(String query) throws IOException {
            System.out.println("[GET] " + query);
            // Création de l'objet URL
            URL url = new URL(query);

            // Ouverture de la connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Paramètres de la requête
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            // Lecture de la réponse
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Fermeture de la connexion
            connection.disconnect();
            if (!response.isEmpty()){
                // Traitement de la réponse
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readTree(response.toString());
            }
           return null;
    }

    public static JsonNode getPictogrammeURL(int pictoId, boolean plural, boolean color,
                                      String backgroundColor, PictogramActions action, PictogramSkin skin,
                                      PictogramHair hair){
        String url = API_URL + "/pictograms/" + pictoId;

        url += "?plural=" + plural;
        url += "&color=" + color;
        //TODO: Fix le background color (chercher pourquoi le backgroundColor n'est pas pris en compte par ARASAAC)
        /*if (!backgroundColor.equals("none")){
            url += "&backgroundColor=" + backgroundColor;
        }*/

        if (action != PictogramActions.NONE){
            url += "&action=" + action.getAction();
        }
        url += "&skin=" + skin.getSkin();
        url += "&hair=" + hair.getHair();
        url += "&url=true";

        try {
            return sendGetRequest(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static JsonNode getPictogrammeURL(int pictoId){
        String url = API_URL + "/pictograms/" + pictoId;
        url += "?url=true";
        try {
            return sendGetRequest(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

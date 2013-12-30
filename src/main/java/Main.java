import queries.impl.ArithmeticExpression;
import queries.impl.DatasetExpression;
import queries.impl.FLWORExpression;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 30.12.13
 * Time: 8:58
 */
public class Main {

    private static final String USER_AGENT = "Mozilla/5.0";

    private static List<String> postResponse(String query) throws Exception {
        //query = query.replace('\n', ' ').replaceAll(" ", "%20");
        //String url = "http://192.168.0.39:19002/query?query=" + query;
        URL obj = new URL("http://192.168.0.39:19001/");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        //con.setRequestMethod("GET");

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes("query=" + query);
        wr.flush();
        wr.close();


        //add request header
        //con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        // System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        List<String> response = new ArrayList<String>();

        while ((inputLine = in.readLine()) != null) {
            response.add(inputLine);
        }
        in.close();

        return response;
    }

    private static List<String> getResponse(String query) throws Exception {
        query = query.replace('\n', ' ').replaceAll(" ", "%20");
        String url = "http://192.168.0.39:19002/query?query=" + query;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        List<String> response = new ArrayList<String>();

        while ((inputLine = in.readLine()) != null) {
            response.add(inputLine);
        }
        in.close();

        return response;
    }

    public static void main(String[] args) throws Exception {
        FLWORExpression f = new FLWORExpression()
                .from(new FLWORExpression()
                        .from(new FLWORExpression()
                                .from(new DatasetExpression("Employees"))
                                .where(new ArithmeticExpression("$this.id < 10")))
                        .where(new ArithmeticExpression("$this.id > 1")))
                .where(new ArithmeticExpression("$this.age = 31"));
        String query = "use dataverse Company;\n" + f.translate();
        System.out.println(query);
        List<String> response = getResponse(query);
        for(String s:response){
            System.out.println(s);
        }
    }
}

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import parser.AQLSyntaxTree;
import parser.AsterixConnector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 30.12.13
 * Time: 8:58
 */
public class Main {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static String postResponse(String query) throws Exception {
        //query = query.replace('\n', ' ').replaceAll(" ", "%20");
        //String url = "http://192.168.0.39:19002/query?query=" + query;
        URL obj = new URL("http://192.168.1.172:19001/");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        //con.setRequestMethod("GET");

        //add request header
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
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static List<String> getResponse(String query) throws Exception {
        query = query.replace('\n', ' ').replaceAll(" ", "%20");
        String url = "http://192.168.1.172:19002/query?query=" + query;
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

    public static InputStream getResponseStream(String query) throws Exception {
        query = query.replace('\n', ' ').replaceAll(" ", "%20");
        String url = "http://192.168.1.172:19001/query?query=" + query;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        return con.getInputStream();
    }

    public static void generateFiles(int number) throws FileNotFoundException {
        PrintWriter out = new PrintWriter("test" + number + ".adm");
        String format = "{\"a\":%d,\"b\":%d,\"c\":%d,\"d\":%d,\"e\":%d,\"f\":%d,\"g\":%d,\"h\":%d}";
        Random r = new Random(System.currentTimeMillis());
        for(int i = 0; i < number; i++) {
            out.write(String.format(format, i, r.nextInt(), r.nextInt(), r.nextInt(),
                    r.nextInt(), r.nextInt(), r.nextInt(), r.nextInt()));
        }
        out.close();
    }

    public static float testFilter(int name, int param) throws Exception {
        String query = "use dataverse Test;\n" +
                "\n" +
                "let $a := for $f in dataset Test%d\n" +
                "where $f.e = %d return 1\n" +
                "return count($a);";
        String res[] = postResponse(String.format(query, name, param)).split(" ");
        return Float.parseFloat(res[4]);
    }

    public static float testJoin(int name, int name2) throws Exception {
        String query = "use dataverse Test;let $a := for $f in dataset Test%d \n" +
                "for $e in dataset Test%d \n" +
                "where $f.b = $e.h return 1\n" +
                "return count($a);\n";
        String res[] = postResponse(String.format(query, name2, name)).split(" ");
        return Float.parseFloat(res[4]);
    }

    public static float testJoin2(int name, int name2) throws Exception {
        String query = "use dataverse Test;let $a := for $f in dataset Test%d \n" +
                "for $e in dataset Test%d \n" +
                "where $f.e < $e.c return 1\n" +
                "return count($a);\n";
        String res[] = postResponse(String.format(query, name2, name)).split(" ");
        return Float.parseFloat(res[4]);
    }

    public static float testJoin3(int name, int name2) throws Exception {
        String query = "use dataverse Test;let $a := for $f in dataset Test%d \n" +
                "for $e in dataset Test%d \n" +
                "where $f.e - $e.c < $f.b return 1\n" +
                "return count($a);\n";
        String res[] = postResponse(String.format(query, name2, name)).split(" ");
        return Float.parseFloat(res[4]);
    }

    /*public static String testFilter(int name) throws Exception {
        String query = "use dataverse Test;\n" +
                "\n" +
                "let $a := for $f in dataset Test%d\n" +
                "where $f.e = 3 return 1\n" +
                "return count($a);";
        return postResponse(String.format(query, name));
    }*/

    public static void testJoins(int name) {
        Random r = new Random(System.currentTimeMillis());
        float sums[] = new float[40];
        int c[] = new int[40];
        int g = 2;
        int shift = 13;
        for (int i = 0; i < 30; i++) {
            int ind = r.nextInt(g) + shift;
            c[ind]++;
            System.out.println(i + ") " + (1<<ind));
            float s = 0;
            try {
                s = testJoin3(1<<ind, name);
            }catch(Exception e){
                System.out.println("miss");
            }
            sums[ind] += s;
            System.out.println(i + ") " + name + " x " + (1<<ind) + " t: " + s);
        }
        for (int i = 0; i < g; i++) {
            System.out.println(name + " " + (1<<(i + shift)) + " " + (sums[i + shift] / c[i + shift]));
        }

    }

    public static void main(String[] args) throws Exception {
        File fXmlFile = new File("src/main/resources/query.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();

        AsterixConnector connector = new AsterixConnector("http://192.168.1.172:19002/");

        AQLSyntaxTree tree = new AQLSyntaxTree(connector, new AQLSyntaxTree.MyXMLElement((Element)doc.getDocumentElement()));

        String query = "use dataverse Test;\ncount(" + tree.translate() + ");";
        /*String query = String.format("use dataverse Test;for $a in(for $r in dataset Metadata.Datatype\n" +
                " for $s in dataset Metadata.Dataset\n" +
                " where $s.DatasetName=\"%s\" and $r.DatatypeName =  $s.DataTypeName\n" +
                "return $r.Derived.Record.Fields)\n" +
                "for $b in $a\n" +
                "return $b;", "Test1024");*/
        System.out.println(query);
        System.out.println(tree.getExecutionTime());
        System.out.println(connector.getResponse(query));

        //generateFiles(2 << 17);
        //generateFiles(2 << 18);
        //generateFiles(2 << 16);
        //testJoins(8192);

    }
}

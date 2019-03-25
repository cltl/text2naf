import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kaf.LP;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class createNafFromText {
    static final String layer = "raw";
    static final String name = "vua-naf2text";
    static final String version = "1.0";
    static final String nafversion = "3.0";

    static String testparameters = "--text \"This is a text.\" --language en --url cltl.nl\file1";

    static public void main (String[] args) {

        String pathToTextFile = "";
        String date = "";
        String url = "";
        String language = "";
        if (args.length==0) {
            args = testparameters.split(" ");
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--textfile") && args.length>(i+1)) {
                pathToTextFile = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--language") && args.length>(i+1)) {
                language = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--date") && args.length>(i+1)) {
                date = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--url") && args.length>(i+1)) {
                url = args[i+1];
            }
        }




        if (url.isEmpty() ) {
            System.err.println("No url provided. Aborting");
        }

        if (language.isEmpty() ) {
            System.err.println("No language provided. Aborting");
        }

        if (pathToTextFile.isEmpty()) {
            String text = getStringFromInputStream(System.in);
            if (date.isEmpty()) {
                createNafStreamFromText(text, language, url);
            }
            else {
                createNafStreamFromText(text, language, url, date);
            }
        }

        if (!pathToTextFile.isEmpty()) {
            File textFile = new File (pathToTextFile);
            if (date.isEmpty()) {
                createNafFileFromTextFile(textFile, language, url);
            }
            else {
                createNafFileFromTextFile(textFile, language, url, date);
            }
        }



      }

      static void createNafStreamFromText (String contents, String language, String url) {
          String date = createTimestamp();
          createNafStreamFromText(contents, language, url, date);
      }

      static void createNafStreamFromText (String contents, String language, String url, String date) {
          String strBeginDate = eu.kyotoproject.util.DateUtil.createTimestamp();
          String strEndDate = null;
          KafSaxParser kafSaxParser = new KafSaxParser();

          kafSaxParser.getKafMetaData().setCreationtime(date);
          kafSaxParser.getKafMetaData().setUrl(url);
          kafSaxParser.getKafMetaData().setLanguage(language);
          kafSaxParser.rawText = contents;

          strEndDate = eu.kyotoproject.util.DateUtil.createTimestamp();
          String host = "";
          try {
              host = InetAddress.getLocalHost().getHostName();
          } catch (UnknownHostException e) {
              e.printStackTrace();
          }
          LP lp = new LP(name,version, strBeginDate, strBeginDate, strEndDate, host);
          kafSaxParser.getKafMetaData().addLayer(layer, lp);
          kafSaxParser.writeNafToStream(System.out);
      }


      static void createNafFileFromTextFile (File txtFile, String language, String url) {
          String date = createTimestamp();
          createNafFileFromTextFile(txtFile, language, url, date);
      }

      static void createNafFileFromTextFile (File txtFile, String language, String url, String date) {
          String strBeginDate = eu.kyotoproject.util.DateUtil.createTimestamp();
          String strEndDate = null;
          KafSaxParser kafSaxParser = new KafSaxParser();

          String contents = null;
            try {
                contents = new String(Files.readAllBytes(Paths.get(txtFile.getAbsolutePath())));
                if (contents != null) {
                    kafSaxParser.getKafMetaData().setCreationtime(date);
                    kafSaxParser.getKafMetaData().setUrl(url);
                    kafSaxParser.getKafMetaData().setLanguage(language);
                    kafSaxParser.rawText = contents;

                    strEndDate = eu.kyotoproject.util.DateUtil.createTimestamp();
                    String host = "";
                    try {
                       host = InetAddress.getLocalHost().getHostName();
                    } catch (UnknownHostException e) {
                      e.printStackTrace();
                    }
                    LP lp = new LP(name,version, strBeginDate, strBeginDate, strEndDate, host);
                    kafSaxParser.getKafMetaData().addLayer(layer, lp);

                    String nafFile =  txtFile.getAbsolutePath() + ".naf";
                    OutputStream fos = new FileOutputStream(nafFile);
                    kafSaxParser.writeNafToStream(fos);
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

      }



    /**
     * Get current system time as formatted string
     * @return
     */
    static public String getDateString () {
        SimpleDateFormat formatter
            = new SimpleDateFormat ("yyyy.MM.dd G 'at' HH:mm:ss a zzz");
        Date currentTime_1 = new Date(System.currentTimeMillis());
        String dateString = formatter.format(currentTime_1);
        return dateString;
    }

    static public Calendar getCaledarObject (String dateString) {
        Calendar calendarObj = Calendar.getInstance();
        SimpleDateFormat formatter
            = new SimpleDateFormat ("yyyy.MM.dd G 'at' HH:mm:ss a zzz");
        try {
            formatter.parse(dateString);
            calendarObj = formatter.getCalendar();

        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return calendarObj;
    }

    /** Returns current timestamp as xs:Date format. */
    static public String createTimestamp() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    // convert InputStream to String
    	private static String getStringFromInputStream(InputStream is) {

    		BufferedReader br = null;
    		StringBuilder sb = new StringBuilder();

    		String line;
    		try {

    			br = new BufferedReader(new InputStreamReader(is));
    			while ((line = br.readLine()) != null) {
    				sb.append(line);
    			}

    		} catch (IOException e) {
    			e.printStackTrace();
    		} finally {
    			if (br != null) {
    				try {
    					br.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		}

    		return sb.toString();

    	}

}

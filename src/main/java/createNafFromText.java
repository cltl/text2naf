import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kaf.LP;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class createNafFromText {
    static final String layer = "raw";
    static final String name = "vua-naf2text";
    static final String version = "1.0";
    static final String nafversion = "3.0";
    static boolean CDATA = false;

    //static String testparameters = "--text \"This is a text.\" --language en --url cltl.nl\file1";
    static String testparameters = "--folder /Users/piek/Desktop/188_texts_to_rerun --extension .txt --language nl";

    static public void main (String[] args) {
        String folder = "";
        String extension = "";
        String pathToTextFile = "";
        String date = "";
        String uri = "";
        String language = "";
        if (args.length==0) {
            args = testparameters.split(" ");
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--textfile") && args.length>(i+1)) {
                pathToTextFile = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--folder") && args.length>(i+1)) {
                folder = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--extension") && args.length>(i+1)) {
                extension = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--language") && args.length>(i+1)) {
                language = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--date") && args.length>(i+1)) {
                date = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--cdata")) {
                CDATA = true;
            }
            else if (arg.equalsIgnoreCase("--uri") && args.length>(i+1)) {
                uri = args[i+1];
            }
        }




        if (uri.isEmpty() && folder.isEmpty()) {
            System.err.println("No URI provided. Aborting");
        }

        if (language.isEmpty() ) {
            System.err.println("No language provided. Aborting");
        }

        if (pathToTextFile.isEmpty() && folder.isEmpty()) {
            String text = getStringFromInputStream(System.in);
            if (date.isEmpty()) {
                createNafStreamFromText(text, language, uri);
            }
            else {
                createNafStreamFromText(text, language, uri, date);
            }
        }

        if (!pathToTextFile.isEmpty()) {
            File textFile = new File (pathToTextFile);
            if (date.isEmpty()) {
                createNafFileFromTextFile(textFile, language, uri);
            }
            else {
                createNafFileFromTextFile(textFile, language, uri, date);
            }
        }
        if (!folder.isEmpty()) {
            File folderFile = new File (folder);
            if (date.isEmpty()) {
                createNafFileForFolder(folderFile, extension, language);
            }
            else {
                createNafFileForFolder(folderFile, extension, language, date);
            }
        }



      }

      static void createNafStreamFromText (String contents, String language, String uri) {
          String date = createTimestamp();
          createNafStreamFromText(contents, language, uri, date);
      }

      static void createNafStreamFromText (String contents, String language, String uri, String date) {
          String strBeginDate = eu.kyotoproject.util.DateUtil.createTimestamp();
          String strEndDate = null;
          KafSaxParser kafSaxParser = new KafSaxParser();

          kafSaxParser.getKafMetaData().setCreationtime(date);
          kafSaxParser.getKafMetaData().setUrl(uri);
          kafSaxParser.getKafMetaData().setLanguage(language);
          kafSaxParser.rawText = contents;
          if (CDATA) kafSaxParser.addCDATA();
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


      static void createNafFileFromTextFile (File txtFile, String language, String uri) {
          String date = createTimestamp();
          createNafFileFromTextFile(txtFile, language, uri, date);
      }

      static void createNafFileFromTextFile (File txtFile, String language, String uri, String date) {
          String strBeginDate = eu.kyotoproject.util.DateUtil.createTimestamp();
          String strEndDate = null;
          KafSaxParser kafSaxParser = new KafSaxParser();

          String contents = null;
            try {
                contents = new String(Files.readAllBytes(Paths.get(txtFile.getAbsolutePath())));
                if (contents != null) {
                    kafSaxParser.getKafMetaData().setCreationtime(date);
                    kafSaxParser.getKafMetaData().setUrl(uri);
                    kafSaxParser.getKafMetaData().setLanguage(language);
                    kafSaxParser.rawText = contents;
                    if (CDATA) kafSaxParser.addCDATA();

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

    static public Calendar getCalendarObject (String dateString) {
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

    static void createNafFileForFolder (File folder, String extension, String language, String date) {
        ArrayList<File> files = makeRecursiveFileList(folder, extension);
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            createNafFileFromTextFile(file, language, file.getName(), date);
        }
    }
    static void createNafFileForFolder (File folder, String extension, String language) {
        ArrayList<File> files = makeRecursiveFileList(folder, extension);
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            createNafFileFromTextFile(file, language, file.getName());
        }
    }

    static public ArrayList<File> makeRecursiveFileList(File inputFile, String theFilter) {
             ArrayList<File> acceptedFileList = new ArrayList<File>();
             File[] theFileList = null;
             if ((inputFile.canRead())) {
                 theFileList = inputFile.listFiles();
                 for (int i = 0; i < theFileList.length; i++) {
                     File newFile = theFileList[i];
                     if (newFile.isDirectory()) {
                         ArrayList<File> nextFileList = makeRecursiveFileList(newFile, theFilter);
                         acceptedFileList.addAll(nextFileList);
                     } else {
                         if (newFile.getName().endsWith(theFilter)) {
                             acceptedFileList.add(newFile);
                         }
                     }
                 }
             } else {
                 System.out.println("Cannot access file:" + inputFile + "#");
                 if (!inputFile.exists()) {
                     System.out.println("File/folder does not exist!");
                 }
             }
             return acceptedFileList;
     }


}

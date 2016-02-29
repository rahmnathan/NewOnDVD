package back_end;

import gui.*;
import com.jaunt.*;
import java.io.*;
import java.util.ArrayList;

/**
 * @author Nathan
 * Created December 2015
 */

public class movieRatings {
    
    /* Assigning the information from our websites to variables to reduce scraping.
     * To increase number of ratings given, increase ratingCount.
     */
    
    public static int ratingCount = 40;
    public static Document siteVisitor = siteVisit();
    public static ArrayList<String> metaLinksFinal = metaLinksFinal(siteVisitor);
    public static ArrayList<String> titlesFinal = titlesFinal(metaLinksFinal);
    
    /* This is the location of my locally stored text files. Change this path to
     * wherever you want these files stored and they will be automatically
     * generated
     */
    
    static String savedData = "C:\\Users\\Nathan\\Documents\\NetBeansProjects\\newOnDVD\\src\\storage\\";        
    
    public static void main(String[] args) {
        
        // Verifying that we have current data using checkFile() method
        
        if(!checkFile()){
            Icons.checkIcons();
            fileWrite(titlesFinal, "titles");
            fileWrite(metaLinksFinal, "links");
            fileWrite(avgRatings(RTratings(titlesFinal), imdbRatings(titlesFinal), metaRatings(siteVisitor)), "avgRatings");
        }
        
        // Calling our JFrame window
        
        gui.window();
    }
    
    public static Document siteVisit(){
        
        // Scraping MetaCritic - catching the exception if the page doesn't load
        
        UserAgent userAgent = new UserAgent();
        try{
        Document main = userAgent.visit("http://www.metacritic.com/browse/dvds/release-date/new-releases/date");
        return main;
        }catch(ResponseException e){return null;}
    }
    
    public static ArrayList<String> metaLinksFinal(Document main){

        // Getting Metacritic Links
        
        ArrayList<String> links = new ArrayList<>();
        ArrayList<Element> initialScrape = new ArrayList<>();

        // Links Are Contained In <a> Tags
        
        Elements scrape = main.findEvery("<a>");
        for(Element scrape1:scrape){
            initialScrape.add(scrape1);
        }
        
        // Links start in the 36th tag
        
        for(int links1 = 36;links1<198;links1++){
            String sortLinks = initialScrape.get(links1).getText();
            
            // Strings containing less than 80 char do not have links in them
            
            if(sortLinks.length()>80){ 
                String makeString = (initialScrape.get(links1)).toXMLString();
                
                // Links begin at char 9 and trail with ">  Cleaning them up here
                
                links.add(makeString.substring(9, makeString.length()-2));
            }
        }
        return links;
    }

    public static ArrayList<String> titlesFinal(ArrayList<String> links){

        // Getting Titles from MetaCritic
        
        ArrayList<String> capList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        int caps3 = 0;
        for(String title : links){
            
            // Titles start at index 32
            
            String title1 = ((title.substring(32)).replace("-", " ")).replace("   ", " ");
            
            // Capitalizing Titles
            
            for(String words : title1.split(" ")){
                capList.add(Character.toUpperCase(words.charAt(0)) + words.substring(1));
            }
            
            // Assembling Titles
            
            String finalTitle = "";
            for(String h : title1.split(" ")){
                
                // Had to add a space after each word so there is a trailing space
                
                finalTitle = finalTitle + capList.get(caps3)+ " ";
                caps3++;
            }
            
            // Assembling final list and Removing trailing space
            
            titleList.add(finalTitle.substring(0,finalTitle.length()-1));
        }
        return titleList; 
    }
        
    public static ArrayList<String> metaRatings(Document main){
        
        // Getting ratings from MetaCritic
        
        ArrayList<String> metaRatingsFinal = new ArrayList<>();
        ArrayList<Element> scrapeList = new ArrayList<>();
        
        // Metacritic ratings are contained in the <span> tags
        
        Elements ratings = main.findEvery("<span>");
        for(Element scrape5 : ratings){
            scrapeList.add(scrape5);
        }
        
        // Metacritic ratings start at line 47 and occur every 4 elements
        
        for(int counting = 47;counting < 250;counting+=4){
            metaRatingsFinal.add((scrapeList.get(counting)).getText());
        }
        return metaRatingsFinal;
    }
    
    public static ArrayList<String> imdbRatings(ArrayList<String> titleList){    
    
        // Getting IMBD ratings from omdb
        
        ArrayList<String> imdbRateFinal = new ArrayList<>();
        ArrayList<String> imdbRate1 = new ArrayList<>();
        
        // Removing dates from titles. omdb has a seperate section for release year
        
        for (String imdbSearch : titleList.subList(0, ratingCount)){
            if (imdbSearch.contains("2014")){
                imdbSearch = imdbSearch.replace(" 2014", "");
            } else if (imdbSearch.contains("2015")){
                imdbSearch = imdbSearch.replace(" 2015", "");
            }
            
            // Visiting Site
            
            UserAgent userAgent = new UserAgent();
            String omdbLink = "http://www.omdbapi.com/?t=" + imdbSearch.replace(" ","+")+"&y=2015&plot=short&r=xml";
            
            // Catching exception if the website doesn't load
            
            Document ratings5;
            try{
            ratings5 = userAgent.visit(omdbLink);
            }catch(ResponseException e){ratings5 = null;}
            
            // Everything is contained in the outerHTML of the <movie> tag
            
            Element test = ratings5.findEvery("<movie>");
            imdbRate1.add(test.outerHTML());
        }
        
        /* Cleaning out unecessary data.
         * We're catching the OutOfBoundsException in case
         * the rating isn't found
         */
        
        for (String sort : imdbRate1){
            try{
            imdbRateFinal.add(sort.split("imdbRating")[1].substring(2,5));
            }catch(ArrayIndexOutOfBoundsException e){imdbRateFinal.add("N/A");}
        }
        return imdbRateFinal;
    }
    
    public static ArrayList<String> RTratings(ArrayList<String> titleList){
        
        // Getting RottenTomatoe's ratings
        
        ArrayList<String> rtRatings = new ArrayList<>();
        for (String rtSearch : titleList.subList(0, ratingCount)){
            boolean check = true;
            UserAgent userAgent = new UserAgent();
            ArrayList<String> rtRatings1 = new ArrayList<>();
            
            /* Visiting movie link on RT. Not all searchs return the correct
             * movie. Sequals/remakes/similar movies get mixed in here. Needs to be
             * optimized
             */
            
            String rtLink = "http://www.rottentomatoes.com/m/" + rtSearch.replace(" ", "_");
            
            // Catching the exception if the link is not found
            
            try{
            userAgent.visit(rtLink);}
            catch(ResponseException e){
                check = false;
                rtRatings.add("tbd");
            }
            
            if(check){
                Elements rtSearch1 = userAgent.doc.findEvery("<div>");
                for (Element rtSearch2 : rtSearch1){
                    rtRatings1.add(rtSearch2.getText().trim());
                }

                /* Sometimes RT throws integers that are not related to the ratings.
                 * We check for that here by checking the length of the string
                 */

                if (rtRatings1.get(235).length() > 1){

                    /* Ratings are in line 235 and are formatted as "9.9/10"
                     * We're getting rid of "/10" here 
                     */

                    String[] rtRatings2 = rtRatings1.get(235).split("/");

                    // RottenTomatoes rates out of 5 so I had to double their score

                    float rtRatings3 = Float.valueOf(rtRatings2[0])*2;
                    rtRatings.add(String.valueOf(rtRatings3));
                } else{
                    rtRatings.add("tbd");
                }
            }
        }
        return rtRatings;
    }

    public static void fileWrite(ArrayList<String> save, String name){
        
        // Writing current information to files
        
        File ratingsSave = new File(savedData + name +".txt");
        BufferedWriter writer;
        
        /* Splitting everything up with the unique string "splithere159"
         * so we can split it easily later
         */
        
        try{
        writer = new BufferedWriter(new FileWriter(ratingsSave));
        for (String x : save){
            writer.append(x + "splithere159");
        }
        writer.close();
        }catch(IOException e){}
    }
    
    public static ArrayList<String> savedFile(String file){
        
        // Getting information stored in our file
        
        ArrayList<String> savedFileData = new ArrayList<String>();
        File ratingsRead = new File(savedData + file + ".txt");
        BufferedReader reader;
        String[] sort = null;
        try{
            reader = new BufferedReader(new FileReader(ratingsRead));
            sort = reader.readLine().split("splithere159");
        } catch(IOException e){}
        for (String x : sort){
            savedFileData.add(x);
        }
        return savedFileData;
    }
    
    public static boolean checkFile(){
        
        // Checking to see if the titles in the file match current titles
        
        File ratingsRead = new File(savedData + "titles.txt");
        BufferedReader reader;
        String[] sort = null;
        
        // Catching the exception if file doesn't exist or is empty
        
        try{
            reader = new BufferedReader(new FileReader(ratingsRead));
            sort = reader.readLine().split("splithere159");
            if (!sort[0].contains(titlesFinal.get(0)) || sort == null){
                return false;
            } else{
                return true;
            }
        } catch(IOException | NullPointerException e){return false;}
    }
    
    public static ArrayList<String> avgRatings(ArrayList<String> rtRatings,ArrayList<String> imbdRate1, ArrayList<String> metaRatingsFinal){
        
        /* Getting Average ratings
         * Not all sites provided ratings for all movies
         */
        
        ArrayList<String> finalRate = new ArrayList<>();
        for(int countRating = 0 ; countRating < ratingCount ; countRating++){
            boolean mt;
            boolean rt;
            boolean imbd;
            float rtAVG = 0;
            float metaAVG = 0;
            float imbdAVG = 0;
            float con;
            String finalRates;
            
            // Checking which ratings we have succesfully pulled
            
            if (metaRatingsFinal.get(countRating).contains("tbd")){
                mt = false;
            } else{
                mt = true;
                metaAVG = Float.valueOf(metaRatingsFinal.get(countRating));
            } if(rtRatings.get(countRating).contains("tbd")){
                rt = false;
            } else{
                rt = true;
                rtAVG = Float.valueOf(rtRatings.get(countRating));
            } if (imbdRate1.get(countRating).contains("N/A")){
                imbd = false;
            } else {
                imbd = true;
                imbdAVG = Float.valueOf(imbdRate1.get(countRating));
            }
            
            // Calculating averages based on which ratings we've scraped
            
            if (mt && rt && imbd){
                con = ((metaAVG+rtAVG+imbdAVG)/3);
                finalRate.add(String.valueOf(con).substring(0,3));
            } else if (mt && rt){
                con = ((metaAVG+rtAVG)/2);
                finalRate.add(String.valueOf(con).substring(0,3));
            } else if (mt && imbd){
                con = ((metaAVG+imbdAVG)/2);
                finalRate.add(String.valueOf(con).substring(0,3));
            } else if (rt && imbd){
                con = ((rtAVG+imbdAVG)/2);
                finalRate.add(String.valueOf(con).substring(0,3));
            } else if (mt){
                finalRate.add(String.valueOf(metaAVG));
            } else if (rt){
                finalRate.add(String.valueOf(rtAVG));
            } else if (imbd){
                finalRate.add(String.valueOf(imbdAVG));
            } else{
                finalRate.add(metaRatingsFinal.get(countRating));
            }
        }
        return finalRate;
    }
}
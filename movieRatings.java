import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Nathan
 * Created December 2015
 */

public class movieRatings {
    
    //To increase number of ratings given, increase ratingCount
    
    static int ratingCount = 9;
    
    // userAgent.visit() Throws ResponseException
    
    public static void main(String[] args) throws ResponseException, IOException, NotFound{
                
        /*
        This code (Icons) Scrapes cover art for movies from MetaCritic in a local folder
        with corresponding names.
        
        Document siteVisitor = siteVisit();
        Icons icon = new Icons();
        Icons.saveImage(iconLinks(titlesFinal(linksFinal(siteVisitor)), linksFinal(siteVisitor)), titlesFinal(linksFinal(siteVisitor)));
        iconLinks(titlesFinal(linksFinal(siteVisitor)), linksFinal(siteVisitor));
        */
        
        /* 
        These statements print out the lists of information given by each
        method. For ease of debugging I will leave them here. Simply uncomment
        them to see the corresponding list printed out.

        System.out.println(titlesFinal(linksFinal(siteVisitor)));
        System.out.println(linksFinal(siteVisitor));
        System.out.println(metaRatings(siteVisitor));
        System.out.println(RTratings(titlesFinal(linksFinal(siteVisitor))));
        System.out.println(imdbRatings(titlesFinal(linksFinal(siteVisitor))));
        */
        
        // Calling our JFrame window
        
        NewJFrame.window();
    }
    
    public static Document siteVisit() throws ResponseException{
        
        // Scraping MetaCritic
        
        UserAgent userAgent = new UserAgent();
        Document main = userAgent.visit("http://www.metacritic.com/browse/dvds/release-date/new-releases/date");
        return main;
    }
    
    public static ArrayList<String> linksFinal(Document main){

        // Getting Metacritic Links
        
        ArrayList<String> links = new ArrayList<>();
        ArrayList<Element> initialScrape = new ArrayList<>();

        // Links Are Contained In <a> Tags
        
        Elements scrape = main.findEvery("<a>");
        for(Element scrape1:scrape){
            initialScrape.add(scrape1);
        }
        
        //Links start in the 36th tag
        
        for(int links1 = 36;links1<60;links1++){
            String sortLinks = initialScrape.get(links1).getText();
            
            //Strings containing less than 80 char do not have links in them
            
            if(sortLinks.length()>80){ 
                String makeString = (initialScrape.get(links1)).toXMLString();
                
                //Links begin at char 9 and trail with ">  Cleaning them up here
                
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
            String title1 = ((title.substring(32)).replace("-", " ")).replace("   ", " ");
            
            /* Capitalizing Titles
            The titles are currently in a string like "harry potter" so we're
            splitting them up to capitalize each word
            */
            
            for(String words : title1.split(" ")){
                capList.add(Character.toUpperCase(words.charAt(0)) + words.substring(1));
            }
            
            // Assembling Titles
            
            String finalTitle = "";
            for(String h : title1.split(" ")){
                
                //Had to add a space after each word so there is a trailing space
                
                finalTitle = finalTitle + capList.get(caps3)+ " ";
                caps3++;
            }
            
            //Assembling final list and Removing trailing space
            
            titleList.add(finalTitle.substring(0,finalTitle.length()-1));
        }
        return titleList; 
    }
    
    public static ArrayList<String> iconLinks(ArrayList<String> titleList, ArrayList<String> linkList) {
        
        // Pulling links out of our list and visiting the respective site
        
        ArrayList<String> iconsFinal = new ArrayList<>();
        for (String link : linkList){
            ArrayList<Element> icons = new ArrayList<>();
            UserAgent userAgent = new UserAgent();
            try{
            userAgent.visit(link);
            } catch (ResponseException e){}
            
            // Grabbing image links
            
            Elements iconList = userAgent.doc.findEvery("<img>");
            for (Element iconSplit : iconList){
                icons.add(iconSplit);
            }
            
            /* Links are the 3rd item in the list. We're turning them into
            strings and cutting out the first 43 char as they are part of the
            tag, not the link. We then split on a '"' to remove the trailing
            data
            */
            
            String icons1  = icons.get(2).toXMLString().substring(44);
            String[] icons2 = icons1.split("\"");
            
            // Adding the linkn to our final ArrayList
            
            iconsFinal.add(icons2[0]);
        }
        return iconsFinal;
    }
    
    public static ArrayList<String> iconPaths()throws ResponseException {
        ArrayList<String> iconPaths = new ArrayList<>();
        for (String title : titlesFinal(linksFinal(siteVisit()))){
            String path = "C:\\Users\\Nathan\\Documents\\NetBeansProjects\\movieRatings\\src\\img\\" + title + ".png";
            iconPaths.add(path);
        }
        return iconPaths;
    }

        
    public static ArrayList<String> metaRatings(Document main){
        
        // Getting ratings from MetaCritic
        
        ArrayList<String> metaRatingsFinal = new ArrayList<>();
        ArrayList<Element> scrapeList = new ArrayList<>();
        
        //metacritic ratings are contained in the <span> tags
        
        Elements ratings = main.findEvery("<span>");
        for(Element scrape5 : ratings){
            scrapeList.add(scrape5);
        }
        
        /* Metacritic ratings start at line 47 and occur every 4 elements
        Currently collects the first 50 ratings. This can be increased by
        increasing the limit of counting.
        */
        
        for(int counting = 47;counting < 250;counting+=4){
            metaRatingsFinal.add((scrapeList.get(counting)).getText());
        }
        return metaRatingsFinal;
    }
    
    public static ArrayList<String> imdbRatings(ArrayList<String> titleList) throws ResponseException, NotFound{    
    
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
            Document ratings5 = userAgent.visit(omdbLink);
            
            // Scraping page
            
            Element test = ratings5.findEvery("<movie>");
            imdbRate1.add(test.outerHTML());
        }
        
        // Clearing out unecessary data
        
        for (String sort : imdbRate1){
            imdbRateFinal.add(sort.split("imdbRating")[1].substring(2,5));
        }
        return imdbRateFinal;
    }
    
    public static ArrayList<String> RTratings(ArrayList<String> titleList){
        
        //Getting RottenTomatoe's ratings
        
        ArrayList<String> rtRatings = new ArrayList<>();
        for (String rtSearch : titleList.subList(0, ratingCount)){
            boolean check = true;
            UserAgent userAgent = new UserAgent();
            ArrayList<String> rtRatings1 = new ArrayList<>();
            
            /*Visiting movie link on RT. Not all searchs return the correct
            movie. Sequals/remakes/similar movies get mixed in here. Needs to be
            optimized*/
            
            String rtLink = "http://www.rottentomatoes.com/m/" + rtSearch.replace(" ", "_");
            
            //Catching the exception if the link is not found
            
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
                We check for that here by checking the length of the string
                */

                if (rtRatings1.get(235).length() > 1){

                    /*Ratings are in line 235 and are formatted as "9.9/10"
                    We're getting rid of "/10" here */

                    String[] rtRatings2 = rtRatings1.get(235).split("/");

                    //RottenTomatoes rates out of 5 so I had to double their score

                    float rtRatings3 = Float.valueOf(rtRatings2[0])*2;
                    rtRatings.add(String.valueOf(rtRatings3));
                } else{
                    rtRatings.add("tbd");
                }
            }
        }
        return rtRatings;
    }

    
    public static ArrayList<String> avgRatings(ArrayList<String> rtRatings,ArrayList<String> imbdRate1, ArrayList<String> metaRatingsFinal){
        
        /* Getting Average ratings
        Not all sites provided ratings for all movies
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
            
            //Checking which ratings we have succesfully pulled
            
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
            
            //Calculating averages based on which ratings we've scraped
            
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
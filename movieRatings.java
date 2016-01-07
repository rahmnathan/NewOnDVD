import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import java.util.ArrayList;

/**
 * @author Nathan
 * Created December 2015
 */

public class movieRatings{
    
    /* userAgent.visit() Throws ResponseException */
    
    public static void main(String[] args) throws ResponseException{
        
        //Printing Links, Titles, And Average Ratings
        
        System.out.println(linksFinal(siteVisit()));
        System.out.println(titlesFinal(linksFinal(siteVisit())));
        System.out.println(avgRatings(imbdRatings(titlesFinal(linksFinal(siteVisit()))), RTratings(titlesFinal(linksFinal(siteVisit()))), metaRatings(siteVisit())));
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
        Elements scrape;

        // Links Are Contained In <a> Tags
        
        scrape = main.findEvery("<a>");
        for(Element scrape1:scrape){
            initialScrape.add(scrape1);
        }
        
        //Links start in the 36th tag
        
        for(int links1 = 36;links1<100;links1++){
            String sortLinks = initialScrape.get(links1).getText();
            if(sortLinks.length()>80){ //There is unecessary data in strings shorter than 80 char
                String makeString = (initialScrape.get(links1)).toXMLString();
                
                //Links begin at char 9 and trail with ">  Cleaning them up here
                
                String makeString2 = makeString.substring(9);
                links.add(makeString2.replace("\">",""));
            }
        }
        return links;
    }

    public static ArrayList<String> titlesFinal(ArrayList<String> links){

        // Getting Titles
        
        ArrayList<String> capList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        int caps3 = 0;
        for(String title : links){
            String title1 = ((title.substring(32)).replace("-", " ")).replace("   ", " ");
            
            // Capitalizing Titles
            
            for(String caps : title1.split(" ")){
                String caps1 = Character.toUpperCase(caps.charAt(0)) + caps.substring(1);
                capList.add(caps1);
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
        
    public static ArrayList<String> metaRatings(Document main){
        
        /* Getting ratings */
        
        ArrayList<String> metaRatingsFinal = new ArrayList<>();
        ArrayList<Element> scrapeList = new ArrayList<>();
        
        //metacritic ratings are contained in the <span> tags
        
        Elements ratings = main.findEvery("<span>");
        for(Element scrape5 : ratings){
            scrapeList.add(scrape5);
        }
        
        //ratings start at line 47 and occur every 4 elements
        
        for(int counting = 47;counting < 100;counting+=4){
            metaRatingsFinal.add((scrapeList.get(counting)).getText());
        }
        return metaRatingsFinal;
    }
    
    public static ArrayList<String> imbdRatings(ArrayList<String> titleList) throws ResponseException{    
    
        // Getting IMBD ratings
        
        String imbdLink;
        Elements ratingSearch;
        ArrayList<String> imbdRate1 = new ArrayList<>();
        for (String imbdSearch : titleList.subList(0, 14)){
            UserAgent userAgent = new UserAgent();
            ArrayList<String> finalLinks = new ArrayList<>();
            
            /*IMBD links cannot be directly created as the title is not 
            contained in the url. Thus we must search IMBD for the movie
            and select the first (most relevant) link. This is not perfectly
            accurate and cannot account for sequals/remakes. Hopefully this can be
            optimized in the future*/
            
            imbdLink = "http://www.imdb.com/find?ref_=nv_sr_fn&q=" + imbdSearch.replace(" ", "+") + "&s=all";
            userAgent.visit(imbdLink);
            
            // IMBD links are contained in the <a> tags
            
            ratingSearch = userAgent.doc.findEvery("<a>");
            for (Element scrape2 : ratingSearch){
                String convert = scrape2.toXMLString();
                
                // <a> tags with links contain "title/tt" so we are looking for that
                
                if (convert.contains("title/tt")){
                    String movieLink1 = convert.substring(9).replace("\">", "");
                    finalLinks.add(movieLink1);
                    break;
                }

            }
            
            //Visiting movie links and scraping ratings
            
            for (String imbdRatings : finalLinks){
                ArrayList<String> imbdRate= new ArrayList<>();
                userAgent.visit(imbdRatings);
                
                //IMBD ratings are contained in the <span> tag
                
                Elements imbdRatingSearch = userAgent.doc.findEvery("<span>");

                for (Element ratings3 : imbdRatingSearch){
                    String ratings4 = ratings3.getText().trim();
                    imbdRate.add(ratings4);
                }
                
                /* Ratings are held in the 34th position of this list.
                For an unknown reason, IMBD occasionally returns &nbsp
                instead of the ratings as it should. We take care of that here
                by replacing it with tbd, but hopefully this can be solved in
                the future*/
                
                String ratings5 = imbdRate.get(34);
                if (ratings5.contains("nbsp")){
                    imbdRate1.add("tbd");
                } else{
                    imbdRate1.add(ratings5);
                }
            }
        }
        return imbdRate1;
    }
    
    public static ArrayList<String> RTratings(ArrayList<String> titleList){
        
        //Getting RT ratings
        
        ArrayList<String> rtRatings = new ArrayList<>();
        for (String rtSearch : titleList.subList(0,14)){
            UserAgent userAgent = new UserAgent();
            ArrayList<String> rtRatings1 = new ArrayList<>();
            
            /*Visiting movie link on RT. Not all searchs return the correct
            movie. Sequals/remakes/similar movies get mixed in here. Needs to be
            optimized*/
            
            String rtLink = "http://www.rottentomatoes.com/m/" + rtSearch.replace(" ", "_");
            
            //Catch the exception if the link is not found
            
            try{
            userAgent.visit(rtLink);}
            catch(ResponseException e){
                break;
            }
            Elements rtSearch1 = userAgent.doc.findEvery("<div>");
            for (Element rtSearch2 : rtSearch1){
                rtRatings1.add(rtSearch2.getText().trim());
            }
            
            /* Sometimes RT throws integers that are not related to the ratings.
            We check for that here by checking the length of the string
            */
            
            if (rtRatings1.get(235).length() > 2){
                
                /*Ratings are in line 235 and are formatted as "9.9/10"
                We're getting rid of "/10" here */
                
                String[] rtRatings2 = rtRatings1.get(235).split("/");
                String rtRatings5 = rtRatings2[0];
                
                //RottenTomatoes rates out of 5 so I had to double their score
                
                float rtRatings3 = Float.valueOf(rtRatings5)*2;
                rtRatings.add(String.valueOf(rtRatings3));
            } else{
                rtRatings.add("tbd");
            }
        }
        return rtRatings;
    }

    
    public static ArrayList<String> avgRatings(ArrayList<String> rtRatings,ArrayList<String> imbdRate1, ArrayList<String> metaRatingsFinal){
        
        /* Getting Average ratings
        Not all sites provided ratings for all movies
        */
        
        ArrayList<String> finalRate = new ArrayList<>();
        for(int count14 = 0 ; count14 < 14 ; count14++){
            boolean mt;
            boolean rt;
            boolean imbd;
            float rtAVG = 0;
            float metaAVG = 0;
            float imbdAVG = 0;
            float con;
            String finalRates;
            
            //Checking which ratings we have succesfully pulled
            
            if (metaRatingsFinal.get(count14).contains("tbd")){
                mt = false;
            } else{
                mt = true;
                metaAVG = Float.valueOf(metaRatingsFinal.get(count14));
            } if(rtRatings.get(count14).contains("tbd")){
                rt = false;
            } else{
                rt = true;
                rtAVG = Float.valueOf(rtRatings.get(count14));
            } if (imbdRate1.get(count14).contains("tbd") || imbdRate1.get(count14).length()<2){
                imbd = false;
            } else {
                imbd = true;
                imbdAVG = Float.valueOf(imbdRate1.get(count14));
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
                finalRate.add(metaRatingsFinal.get(count14));
            }
        }
        return finalRate;
    }
}
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import java.util.ArrayList;

/**
 * @author Nathan
 */
class movieRatings {
    
    /* userAgent.visit() Throws ResponseException */
    
    public static void main(String[] args) throws ResponseException {
        
        /* Creating lists and visiting site */
        
        UserAgent userAgent = new UserAgent();
        ArrayList<String> links = new ArrayList<>();
        ArrayList<Element> ar = new ArrayList<>();
        userAgent.visit("http://www.metacritic.com/browse/dvds/release-date/new-releases/date");
        
        /* Getting Links */
        
        Elements scrape;
        scrape = userAgent.doc.findEvery("<a>");
        for(Element scrape1:scrape){
            ar.add(scrape1);
        }
        for(int links1 = 36;links1<100;links1++){
            String sortlinks = ar.get(links1).getText();
            if(sortlinks.length()>80){
                String makestring = (ar.get(links1)).toXMLString();
                String makestring2 = makestring.substring(9);
                links.add(makestring2.replace("\">",""));
            }
        }
        System.out.println(links);
        
        /* Getting Titles */
        
        ArrayList<String> capList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        int caps3 = 0;
        for(String title : links){
            String title1 = ((title.substring(32)).replace("-", " ")).replace("   ", " ");
            
            /* Capitalizing and Assembling Titles */
            
            for(String caps : title1.split(" ")){
                String caps1 = Character.toUpperCase(caps.charAt(0)) + caps.substring(1);
                capList.add(caps1);
            }
            String finalTitle = "";
            for(String h : title1.split(" ")){
                finalTitle = finalTitle + capList.get(caps3)+ " ";
                caps3++;
            }
            titleList.add(finalTitle);
            
        }
        
        System.out.println(titleList);
        
        /* Getting ratings */
        
        ArrayList<Element> scrapeList = new ArrayList<>();
        ArrayList<String> metaRatings = new ArrayList<>();
        Elements ratings = userAgent.doc.findEvery("<span>");
        for(Element scrape5 : ratings){
            scrapeList.add(scrape5);
        }
        for(int counting = 47;counting < 100;counting+=4){
            metaRatings.add((scrapeList.get(counting)).getText());
        }
        
        // Getting IMBD ratings
        
        String imbdLink;
        Elements ratingSearch;
        ArrayList<String> imbdRate1 = new ArrayList<>();
        for (String imbdSearch : titleList.subList(0, 14)){
            ArrayList<String> finalLinks = new ArrayList<>();
            
            //Searching for movie links
            
            imbdLink = "http://www.imdb.com/find?ref_=nv_sr_fn&q=" + imbdSearch.replace(" ", "+") + "&s=all";
            userAgent.visit(imbdLink);
            ratingSearch = userAgent.doc.findEvery("<a>");
            for (Element scrape2 : ratingSearch){
                String convert = scrape2.toXMLString();
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
                Elements imbdRatingSearch = userAgent.doc.findEvery("<span>");

                for (Element ratings3 : imbdRatingSearch){
                    String ratings4 = ratings3.getText().trim();
                    imbdRate.add(ratings4);
                }
                String ratings5 = imbdRate.get(34);
                if (ratings5.contains("nbsp")){
                    imbdRate1.add("tbd");
                } else{
                    imbdRate1.add(ratings5);
                }
            }
        }
        
        //Getting RT ratings
        
        ArrayList<String> rtRatings = new ArrayList<>();
        for (String rtSearch : titleList.subList(0,14)){
            ArrayList<String> rtRatings1 = new ArrayList<>();
            String rtLink = "http://www.rottentomatoes.com/m/" + rtSearch.substring(0,rtSearch.length()-1).replace(" ", "_");
            try{
            userAgent.visit(rtLink);}
            catch(ResponseException e){
                break;
            }
            Elements rtSearch1 = userAgent.doc.findEvery("<div>");
            for (Element rtSearch2 : rtSearch1){
                rtRatings1.add(rtSearch2.getText().trim());
            }
            if (rtRatings1.get(235).length() > 2){
                String[] rtRatings2 = rtRatings1.get(235).split("/");
                String rtRatings5 = rtRatings2[0];
                
                //RottenTomatoes rates out of 5 so I had to double their score
                
                float rtRatings3 = Float.valueOf(rtRatings5)*2;
                rtRatings.add(String.valueOf(rtRatings3));
            } else{
                rtRatings.add("tbd");
            }
        }

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
            if (metaRatings.get(count14).contains("tbd")){
                mt = false;
            } else{
                mt = true;
                metaAVG = Float.valueOf(metaRatings.get(count14));
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
                finalRate.add(metaRatings.get(count14));
            }
        }
        System.out.println(finalRate);
    }
    
}
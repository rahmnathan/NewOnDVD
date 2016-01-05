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
        
        ArrayList<String> ar3 = new ArrayList<>();
        ArrayList<String> ar4 = new ArrayList<>();
        int caps3 = 0;
        for(String title : links){
            String title1 = ((title.substring(32)).replace("-", " ")).replace("   ", " ");
            
            /* Capitalizing and Assembling Titles */
            
            for(String caps : title1.split(" ")){
                String caps1 = Character.toUpperCase(caps.charAt(0)) + caps.substring(1);
                ar3.add(caps1);
            }
            String finaltitle = "";
            for(String h : title1.split(" ")){
                finaltitle = finaltitle + ar3.get(caps3)+ " ";
                caps3++;
            }
            ar4.add(finaltitle);
            
        }
        
        System.out.println(ar4);
        
        /* Getting ratings */
        
        ArrayList<Element> ar1 = new ArrayList<>();
        ArrayList<String> mtratings = new ArrayList<>();
        Elements ratings = userAgent.doc.findEvery("<span>");
        for(Element scrape5 : ratings){
            ar1.add(scrape5);
        }
        for(int counting = 47;counting < 100;counting+=4){
            mtratings.add((ar1.get(counting)).getText());
        }
        
        // Getting IMBD ratings
        
        String imbdlink;
        Elements ratingsearch;
        ArrayList<String> imbdrate1 = new ArrayList<>();
        for (String imbdsearch : ar4.subList(0, 14)){
            ArrayList<String> ar5 = new ArrayList<>();
            
            //Searching for movie links
            
            imbdlink = "http://www.imdb.com/find?ref_=nv_sr_fn&q=" + imbdsearch.replace(" ", "+") + "&s=all";
            userAgent.visit(imbdlink);
            ratingsearch = userAgent.doc.findEvery("<a>");
            for (Element scrape2 : ratingsearch){
                String convert = scrape2.toXMLString();
                if (convert.contains("title/tt")){
                    String movielink1 = convert.substring(9).replace("\">", "");
                    ar5.add(movielink1);
                    break;
                }

            }
            
            //Visiting movie links and scraping ratings
            
            for (String imbdratings : ar5){
                ArrayList<String> imbdrate= new ArrayList<>();
                userAgent.visit(imbdratings);
                Elements imbdratingsearch = userAgent.doc.findEvery("<span>");

                for (Element ratings3 : imbdratingsearch){
                    String ratings4 = ratings3.getText().trim();
                    imbdrate.add(ratings4);
                }
                String ratings5 = imbdrate.get(34);
                if (ratings5.contains("nbsp")){
                    imbdrate1.add("tbd");
                } else{
                    imbdrate1.add(ratings5);
                }
            }
        }
        
        //Getting RT ratings
        
        ArrayList<String> rtratings = new ArrayList<>();
        for (String rtsearch : ar4.subList(0,14)){
            ArrayList<String> rtratings1 = new ArrayList<>();
            String rtlink = "http://www.rottentomatoes.com/m/" + rtsearch.substring(0,rtsearch.length()-1).replace(" ", "_");
            try{
            userAgent.visit(rtlink);}
            catch(ResponseException e){
                break;
            }
            Elements rtsearch1 = userAgent.doc.findEvery("<div>");
            for (Element rtsearch2 : rtsearch1){
                String rtsearch3 = rtsearch2.getText().trim();
                rtratings1.add(rtsearch3);
            }
            if (rtratings1.get(235).length() > 2){
                String[] rtratings2 = rtratings1.get(235).split("/");
                String rtratings5 = rtratings2[0];
                float rtratings3 = Float.valueOf(rtratings5)*2;
                String rtratings4 = String.valueOf(rtratings3);
                rtratings.add(rtratings4);
            } else{
                rtratings.add("tbd");
            }
        }

        /* Getting Average ratings
        Not all sites provided ratings for all movies
        */
        
        ArrayList<String> finalrate = new ArrayList<>();
        for(int count6 = 0 ; count6 < 14 ; count6++){
            boolean mt;
            boolean rt;
            boolean imbd;
            float rtavg = 0;
            float mtavg = 0;
            float imbdavg = 0;
            float con;
            String finalrates;
            if (mtratings.get(count6).contains("tbd")){
                mt = false;
            } else{
                mt = true;
                mtavg = Float.valueOf(mtratings.get(count6));
            } if(rtratings.get(count6).contains("tbd")){
                rt = false;
            } else{
                rt = true;
                rtavg = Float.valueOf(rtratings.get(count6));
            } if (imbdrate1.get(count6).contains("tbd") || imbdrate1.get(count6).length()<2){
                imbd = false;
            } else {
                imbd = true;
                imbdavg = Float.valueOf(imbdrate1.get(count6));
            }
            if (mt && rt && imbd){
                con = ((mtavg+rtavg+imbdavg)/3);
                finalrate.add(String.valueOf(con).substring(0,3));
            } else if (mt && rt){
                con = ((mtavg+rtavg)/2);
                finalrate.add(String.valueOf(con).substring(0,3));
            } else if (mt && imbd){
                con = ((mtavg+imbdavg)/2);
                finalrate.add(String.valueOf(con).substring(0,3));
            } else if (rt && imbd){
                con = ((rtavg+imbdavg)/2);
                finalrate.add(String.valueOf(con).substring(0,3));
            } else if (mt){
                finalrate.add(String.valueOf(mtavg));
            } else if (rt){
                finalrate.add(String.valueOf(rtavg));
            } else if (imbd){
                finalrate.add(String.valueOf(imbdavg));
            } else{
                finalrate.add(mtratings.get(count6));
            }
        }
        System.out.println(finalrate);
    }
    
}
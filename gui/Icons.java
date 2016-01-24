package gui;

import back_end.movieRatings;
import com.jaunt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class Icons {
    
    /* This is where i'm locally storing the photos. You can put this folder
     * anywhere as long as you use the correct path to it, and the Icons will
     * automatically populate the folder.
     */

    static String iconFolder = "C:\\Users\\Nathan\\Documents\\NetBeansProjects\\newOnDVD\\src\\images\\";
    
    public static void saveImage(ArrayList<String> imageUrl, ArrayList<String> titleList, int iconCount) throws IOException {
        
        // Here we are visiting links to our Icons and downloading the pictures
        
        int count = 0;
        for(String link : imageUrl.subList(0, iconCount)){    
            URL url = new URL(link);
            OutputStream os;
            
            // Assembling paths
            
            String titleList1 = iconFolder + titleList.get(count) + ".png";
            count++;
            
            // Opening the link and outputting the file to my hard drive
            
            try (InputStream is = url.openStream()) {
            os = new FileOutputStream(titleList1);
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
        }
            os.close();
    }}
    
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
             * strings and cutting out the first 43 char as they are part of the
             * tag, not the link. We then split on a '"' to remove the trailing
             * data.
            */
            
            String icons1  = icons.get(2).toXMLString().substring(44);
            String[] icons2 = icons1.split("\"");
            
            // Adding the linkn to our final ArrayList
            
            iconsFinal.add(icons2[0]);
        }
        return iconsFinal;
    }
    
    public static ArrayList<String> iconPaths() {
        
        // Assembling the paths with our title list
        
        ArrayList<String> iconPaths = new ArrayList<>();
        for (String title : movieRatings.titlesFinal){
            String path = iconFolder + title + ".png";
            iconPaths.add(path);
        }
        return iconPaths;
    }
    
    public static void checkIcons(){
        
        // Getting list of files
        
        File iconCheck = new File(iconFolder);
        File[] pathList = iconCheck.listFiles();
        
        /*
         * Checking if we have the most current Icons.
         * We're doing this by comparing the first Icon name
         * to each of the titles in the title list.
         */
        
        int numNeeded = 0;
        try{
        for (String title : movieRatings.titlesFinal.subList(0, movieRatings.ratingCount)){
            if(pathList.length == 0){
                numNeeded = movieRatings.ratingCount;
                break;
            }
            else if (pathList[0].toString().contains(title)){
                break;
            } else{
                numNeeded++;
            }
        }
    }catch(NullPointerException e){ numNeeded = movieRatings.ratingCount;}

        
        // If we're missing Icons, we call the saveImage method
        
        if (numNeeded != 0){
            try{
            Icons.saveImage(iconLinks(movieRatings.titlesFinal, movieRatings.metaLinksFinal), movieRatings.titlesFinal, numNeeded);
            }catch(IOException e){}
        }
    }

}
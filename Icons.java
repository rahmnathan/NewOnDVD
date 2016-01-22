import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

public class Icons {
    public static void saveImage(ArrayList<String> imageUrl, ArrayList<String> titleList, int iconCount) throws IOException {
        
        // This is where i'm locally storing the photos

        String iconFolder = "C:\\Users\\Nathan\\Documents\\NetBeansProjects\\movieRatings\\src\\img\\";
        
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
    }

}}
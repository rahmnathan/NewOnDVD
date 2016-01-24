import com.jaunt.Document;
import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

public class gui extends JFrame {
    
    // Setting all of our variables
    
    static Document site = movieRatings.siteVisitor;
    static ArrayList<String> metaLinksFinal = movieRatings.savedFile("links");
    static ArrayList<String> titleList = movieRatings.savedFile("titles");
    static ArrayList<String> iconPaths = Icons.iconPaths();
    static ArrayList<String> ratings = movieRatings.savedFile("avgRatings");
    
    // Creating the components we'll be using
    
    static ScrollPane scrollPane = new ScrollPane();
    static Panel panel1 = new Panel();
    static JFrame frame = new JFrame("Movies");
    
    // Setting up some dimensions we'll use later

    static Dimension window = new Dimension(595, 450);
    static Dimension icon = new Dimension(100, 125);
    static Dimension title = new Dimension(100, 20); 
    
    
    
    public static void window(){
        
        // Setting up our JFrame
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        
        // Setting up our scroll pane
        
        panel1.setLayout(null);
        scrollPane.setLocation(0, 0);
        frame.add(scrollPane);
        scrollPane.setSize(580, 450);
        scrollPane.setVisible(true);
        frame.setSize(window);
        panel1.setSize(500, 2000);
        scrollPane.add(panel1);
        
        int n = 0;
        int xLocation = 10;
        int yLocationIcon = 10;
        int yLocationTitle = 135;
        
        // We're displaying 5 movies per line. This for-loop does that for us
        
        for (String link : metaLinksFinal){
            if (n > 0 && n%5 == 0){
                yLocationIcon+=185;
                yLocationTitle+=185;
                xLocation = 10;
            }
            
            // Setting up our Icon. Listening for mouse click to play movie
            
            JLabel iconLabel = new javax.swing.JLabel();
            iconLabel.setIcon(new javax.swing.ImageIcon(iconPaths.get(n)));
            iconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    openPage(link);
                }
            });
            
            // Configuring size and location for the title, icon, and ratings and adding them
            
            JLabel ratingLabel = new javax.swing.JLabel();
            JLabel titleLabel = new javax.swing.JLabel();
            titleLabel.setText(titleList.get(n));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setSize(title);
            titleLabel.setLocation(xLocation, yLocationTitle);
            try{
            ratingLabel.setText(ratings.get(n));
            }catch(IndexOutOfBoundsException e){}
            ratingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            ratingLabel.setSize(title);
            ratingLabel.setLocation(xLocation, yLocationTitle + 15);
            iconLabel.setSize(icon);
            iconLabel.setLocation(xLocation, yLocationIcon);
            panel1.add(ratingLabel);
            panel1.add(titleLabel);
            panel1.add(iconLabel);
            xLocation+=110;
            n++;
        }
        scrollPane.add(panel1);
        frame.setVisible(true);
    }
    public static void openPage(String link){
        
        // This method opens the metacritic link when you click an Icon
        
        try{
        Desktop.getDesktop().browse(new URI(link));
        }catch(IOException | URISyntaxException e){}
    }
}
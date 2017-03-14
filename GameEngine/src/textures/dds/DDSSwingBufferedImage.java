package textures.dds;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DDSSwingBufferedImage {
	private static final String [] DDS_PATHS = {
	        "res/alltur.dds"
	    };
	    
	    public static void main(String [] args) {
	        try {
	            JPanel panel = new JPanel();
	            panel.setBackground(Color.ORANGE);
	            for(int i=0; i<DDS_PATHS.length; i++) {
	                panel.add(createDDSLabel(DDS_PATHS[i]));
	            }
	            JFrame frame = new JFrame();
	            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            frame.setTitle("DDS Swing BufferedImage");
	            frame.setSize(420, 310);
	            frame.setVisible(true);
	            frame.getContentPane().add(panel);
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    private static JLabel createDDSLabel(String path) throws IOException {

	        // DDS画像をバッファに読み込みます
	        FileInputStream fis = new FileInputStream(path);
	        byte [] buffer = new byte[fis.available()];
	        fis.read(buffer);
	        fis.close();

	        // DDSReaderでBufferedImageを作成します
	        int [] pixels = DDSReader.read(buffer, DDSReader.ARGB, 0);
	        int width = DDSReader.getWidth(buffer);
	        int height = DDSReader.getHeight(buffer);
	        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        image.setRGB(0, 0, width, height, pixels, 0, width);

	        // JLabelにImageIconを追加します
	        ImageIcon icon = new ImageIcon(image.getScaledInstance(128, 128, BufferedImage.SCALE_SMOOTH));
	        return new JLabel(icon);
	    }
}

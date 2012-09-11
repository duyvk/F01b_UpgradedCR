package datageek.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import datageek.entity.Item;

public class CouponPanel extends JPanel {
    
    private ArrayList<JCheckBox> arrayCheckBox = new ArrayList<>();
    private JButton submitButton;
    private JFrame frame;
    
    private int userID;
    
    // object to deal with data
    private InteracterGUI interacterGUI;
    
    private ArrayList<String[]> allNewCoupons;
    
    // action 
    private void submitButtonMouseClicked(MouseEvent e) {
    	ArrayList<String> idCouponSelected = new ArrayList<>();
		for (int i=0; i< arrayCheckBox.size(); i++){
			if (arrayCheckBox.get(i).isSelected()){
				idCouponSelected.add(allNewCoupons.get(i)[0]);
			}
		}
		
		// step 13
		
		ArrayList<Item> itemSelected = interacterGUI.getUserInput(idCouponSelected);
		
		// step 14
		
		interacterGUI.updateHistoricalData(itemSelected);
		
		// step 15
		askUpdateFriends();
	}
    
    private void askUpdateFriends(){
		int n = JOptionPane.showConfirmDialog(
			    this,
			    "Do you want to select some friends?",
			    "An Inane Question",
			    JOptionPane.YES_NO_OPTION);
		if (n==0){
			//
			
			NewFriendPanel newFriendPanel = new NewFriendPanel(interacterGUI);
			newFriendPanel.setOpaque(true); //content panes must be opaque
			frame.setContentPane(newFriendPanel);
			
			//Display the window.
	        frame.pack();
	        frame.setVisible(true);
		} else if (n==1){
			System.out.println("no");
		}
    }
    
    private boolean checkData(){
    	return true;
    }
    
    // init
    public CouponPanel(JFrame frame) throws IOException, JSONException {
        super(new BorderLayout());
        
        this.frame = frame;
        
        // VUDAO MUST SET
        this.userID = 1;	// HOANG SET 1 FOR TESTING
        this.interacterGUI = new InteracterGUI(userID);
        
        
        // step 2
        this.interacterGUI.readNewCoupon();
        
        // step 5
        
        if (interacterGUI.checkUserHistory() == false){
        	//go to step 6 and step 7
        	
        	allNewCoupons = interacterGUI.returnCoupons();
        }else {
        	//go to step 8 and 9
        	System.out.println("Go herexXXXXXXXXXXXXXXXXX");
        	allNewCoupons = interacterGUI.couponRecommend();
        	
        }
        
        // step 6 + 10
       
        //allNewCoupons = interacterGUI.returnCoupons();

        // step: 7
        //Create the check boxes.
        for (int i=0; i< allNewCoupons.size(); i++){
        	String[] coupons = allNewCoupons.get(i);
        	
        	JCheckBox jCheckBox = new JCheckBox(coupons[1]);
        	arrayCheckBox.add(jCheckBox);
        }
        
        submitButton = new JButton();
        submitButton.setText("submit");
        
        submitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				submitButtonMouseClicked(e);
			}
		});

        /*Put the check boxes in a column in a panel*/
        
        JPanel checkPanel = new JPanel(new GridLayout(0, 1));
        for (int i=0; i< arrayCheckBox.size(); i++){
        	checkPanel.add(arrayCheckBox.get(i));
        } 
        checkPanel.add(submitButton);

        add(checkPanel, BorderLayout.LINE_START);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }
        
    public void readNewCoupon(){
    	
    }

    public static void main(String[] args) throws IOException, JSONException {
        JFrame.setDefaultLookAndFeelDecorated(true);
        

        //Create and set up the window.
        JFrame frame = new JFrame("Coupon List");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newCouponPane = new CouponPanel(frame);
        newCouponPane.setOpaque(true); //content panes must be opaque
        //frame.setContentPane(newContentPane);
        frame.add(newCouponPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
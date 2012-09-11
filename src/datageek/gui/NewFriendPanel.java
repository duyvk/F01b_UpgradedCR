package datageek.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import datageek.entity.User;

public class NewFriendPanel extends JPanel {
    
	private InteracterGUI interacterGUI;
	
    private ArrayList<JCheckBox> friendsCheckBox = new ArrayList<>();
    private ArrayList<String[]> friendList;
    private JButton submitButton;

    
    // action 
    private void submitButtonMouseClicked(MouseEvent e) {
    	ArrayList<String> friendSelectedList = new ArrayList<>();
		for (int i=0; i< friendsCheckBox.size(); i++){
			if (friendsCheckBox.get(i).isSelected()){
				friendSelectedList.add(friendList.get(i)[0]);
			}
		}
		
		// step 18
		ArrayList<User> friendListObj = interacterGUI.getUserInputFriend(friendSelectedList);
		
		// step 19
		
		interacterGUI.updateFriends(friendListObj);
		
		
		
	}
    
   
    // init
    public NewFriendPanel(InteracterGUI interacterGUI) {
        super(new BorderLayout());
            
        this.interacterGUI = interacterGUI;
      //get friend list
        friendList = interacterGUI.friendToAdd();
        
        for (int i=0; i< friendList.size(); i++){
        	JCheckBox checkBox = new JCheckBox(friendList.get(i)[0]);
        	friendsCheckBox.add(checkBox);
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
        for (int i=0; i<friendsCheckBox.size(); i++){
        	checkPanel.add(friendsCheckBox.get(i));
        } 
        checkPanel.add(submitButton);

        add(checkPanel, BorderLayout.LINE_START);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }
  
}
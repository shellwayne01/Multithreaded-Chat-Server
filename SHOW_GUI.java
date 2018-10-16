package project3_v4;

public class SHOW_GUI {

	public static void main(String[] args) {
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				GUI_Frame gui = new GUI_Frame();
			}
		});
		
		
		
	}

}

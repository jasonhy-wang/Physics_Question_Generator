import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.function.DoubleToIntFunction;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ViewStats extends JFrame {
	private static JFrame window;

	private String studentName;
	private Student student;
	private int studentWrongQuestions;
	private int studentTotalQuestions;
	private double accuracy = 0;
	private Color orange = new Color(255, 168, 104);

	private JButton back;

	private Color indigo = new Color(56, 53, 74);
	private Color lightBlue = new Color(162, 236, 250);
	private Font font = new Font("Arial", Font.BOLD, 30);

	// Constructor
	ViewStats(Student student) {
		super("Summary Page");
		// Set the frame to full screen
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setResizable(false);

		// Set up the main panel
		JPanel panel = new JPanel();
		panel.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
//		panel.setMinimumSize(panel.getPreferredSize());
		panel.setSize(panel.getPreferredSize());
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		this.setUndecorated(true);
		
		// Focus the frame
		this.requestFocusInWindow();

		// Make the frame visible
		this.setVisible(true);

		this.setContentPane(panel);


		this.student = student;
		studentName = student.getName();
		studentWrongQuestions = student.getIncorrectQuestions();
		studentTotalQuestions = student.getTotalQuestions();
		accuracy = ((studentTotalQuestions - studentWrongQuestions) / (double) studentTotalQuestions) * 100.00;


		back = new JButton(new ImageIcon("BackButton.png"));
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		back.setFont(font);
		back.setContentAreaFilled(false); 
		back.setBorder(BorderFactory.createEmptyBorder());

		JLabel name = new JLabel(studentName);
		name.setFont(new Font("Serif", Font.BOLD, 100));
		name.setForeground(orange);
		name.setSize(name.getPreferredSize());
		name.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		panel.setBackground(indigo);
		panel.add(name);

		JLabel stats = new JLabel(String.format("Accuracy " + "%.2f", accuracy) + "%");
		stats.setFont(new Font("Serif", Font.BOLD, 150));
		stats.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
		stats.setHorizontalAlignment(JLabel.CENTER);
		stats.setForeground(lightBlue);

		JLabel wrong = new JLabel("Total Incorrect: " + Integer.toString(studentWrongQuestions));
		wrong.setFont(new Font("Serif", Font.BOLD, 50));
		wrong.setHorizontalAlignment(JLabel.CENTER);
		wrong.setForeground(lightBlue);

		JLabel total = new JLabel("Total Questions: " + Integer.toString(studentTotalQuestions));
		total.setFont(new Font("Serif", Font.BOLD, 50));
		total.setHorizontalAlignment(JLabel.CENTER);
		total.setForeground(lightBlue);

		panel.add(Box.createRigidArea(new Dimension(0, 100)));
		
		panel.add(stats);
		
		panel.add(Box.createRigidArea(new Dimension(0, 150)));
		
		JPanel panel1 = new JPanel();
		panel1.add(wrong);
		panel1.add(Box.createRigidArea(new Dimension(100, 0)));
		panel1.add(total);
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
		panel1.setBackground(indigo);
		panel.add(panel1);
		
//		panel.add(Box.createRigidArea(new Dimension(0, 150)));
		back.setAlignmentX(JButton.LEFT_ALIGNMENT);
		JPanel a = new JPanel();
		a.add(back);
		a.setLayout(new FlowLayout(FlowLayout.LEFT));
		a.setBackground(indigo);
		panel.add(a);
		
		
	

	} // End of constructor

}

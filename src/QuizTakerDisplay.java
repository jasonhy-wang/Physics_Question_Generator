
//Graphics & GUI imports
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.sun.media.jfxmedia.Media;

import data_structures.SimpleLinkedList;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//Keyboard imports
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

class QuizTakerDisplay extends JFrame {

	// Class variables
	private static JFrame window;

	private boolean questionAnswered;
	private boolean correct = false;
	private boolean correct1 = false;

	//Need to change
	private JButton answer1, answer2, answer3, answer4;
	private JButton nextButton, exitButton;

	private JLabel label;
	private JLabel questionLabel;
	private JPanel panel,panel1;
	private JLabel clapping = new JLabel(new ImageIcon("clapping.gif"));

	private long time;

	private JPanel panel2;
	private Font font1 = new Font("Serif", Font.BOLD, 100);
	private Font font2 = new Font("Arial", Font.ITALIC, 50);
	private Font font3 = new Font("Serif", Font.BOLD, 25);
	private Color indigo = new Color(56, 53, 74);
	private Color lightBlue = new Color(162, 236, 250);
	private Color orange = new Color(255, 168, 104);
	private Color defaultColor = new JButton().getBackground();
	private int questionNum = 0;
	private String[] ids;
	private double[] values;
	private static int questionWrong;
	private static SimpleLinkedList<String> questionStatement;
	private static SimpleLinkedList<Question> wrongQuestions;
	private SimpleLinkedList<String> answers;
	private SimpleLinkedList<String[]> choices;
	private SimpleLinkedList<String[]> variableIDs;
	private SimpleLinkedList<double[]> variableValues;
	private SimpleLinkedList<Question> rootQuestions;

	private static Student student;
	private boolean right = true;
	private boolean clicked = false;
	private boolean finished = false;
	private boolean wordAnswer = false;

	private SimpleLinkedList<JButton> buttonList = new SimpleLinkedList<JButton>();

	// Constructor
	QuizTakerDisplay(SimpleLinkedList<String> question, SimpleLinkedList<String[]> choices,
			SimpleLinkedList<String> answers, SimpleLinkedList<String[]> variableIDs,
			SimpleLinkedList<double[]> variableValues, SimpleLinkedList<Question> rootQuestions, Student student) {

		super("Practice Like A Physicist");
		// Set the frame to full screen
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setResizable(false);
		window = this;
		// Set up the main panel
		panel = new JPanel() {
			//Label clap = new JLabel(clapping);
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				//if user gets right answer go to the next question
				if (correct) {
					correct = false;
					correct1 = true;

				} else if (correct1) {
					if (time > 0) {
						if (System.currentTimeMillis() - time >= 500) {
							time = 0;
							this.remove(clapping);
							correct1 = false;
							ActionEvent e = new ActionEvent(nextButton, 0, "");
							nextButton.getActionListeners()[0].actionPerformed(e);
						}
					} else {
						time = System.currentTimeMillis();
					}
				}

				repaint();
			}
		};

		//setting size of main panel
		panel.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

		// Focus the frame
		this.requestFocusInWindow();
		this.setUndecorated(true);

		// Make the frame visible
		this.setVisible(true);

		this.questionStatement = question;
		this.choices = choices;
		this.answers = answers;
		this.variableIDs = variableIDs;
		this.variableValues = variableValues;
		this.rootQuestions = rootQuestions;

		// creating buttons for each choice based on number of options
		panel1 = new JPanel();
		SimpleLinkedList<JButton> buttonlist = new SimpleLinkedList<JButton>();

		panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
		panel2.setBackground(indigo);
		
		if (variableIDs.get(questionNum) == null) {
			displayWordAnswerQuestions();
			//panel2.add(new JLabel (new ImageIcon(rootQuestions.get(questionNum).getImage())));
		} else {
			displayNumberAnswerQuestions();
			//creating panel for the variables 			
			ids = variableIDs.get(0);
			values = variableValues.get(0);
			for (int j = 0; j < ids.length; j++) {
				try {
					try {
						Double.parseDouble(ids[j]);
					} catch (NumberFormatException e) {
						panel2.add(new JLabel(new ImageIcon(QuizEditor.stringToImage(ids[j]))));
						JLabel value = new JLabel(" = " + String.format("%.2f", values[j]) + "  ");
						value.setFont(font2);
						value.setForeground(lightBlue);
						panel2.add(value);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		nextButton = new JButton();
		try {
			nextButton = new JButton(new ImageIcon(ImageIO.read(new File("nextButton.png"))));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		nextButton.setContentAreaFilled(false);
		nextButton.setBorder(BorderFactory.createEmptyBorder());
		exitButton = new JButton("Exit");

		questionLabel = new JLabel(
				"<html><div style='text-align: center;'>" + getQuestionStatment().get(questionNum) + "</div></html");
		questionLabel.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
		questionLabel.setHorizontalAlignment(JLabel.CENTER);


		double lengthOnRow = Math.ceil(getQuestionStatment().get(questionNum).length() / 3.00);

		int size;

		if (lengthOnRow < 47) {
			size = 100;
		} else {
			size = (int) (100 - (lengthOnRow - 46));
		}

		questionLabel.setFont(new Font("Serif", Font.BOLD, size));
		questionLabel.setForeground(lightBlue);

		label = new JLabel("Question #" + Integer.toString(questionNum + 1));
		label.setFont(new Font("Serif", Font.BOLD, 100));
		label.setForeground(orange);
		nextButton.addActionListener(new NextButtonListener());
		int x = 50;
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		panel.add(Box.createRigidArea(new Dimension(0, x)));
		panel.add(label);
		panel.add(Box.createRigidArea(new Dimension(0, x)));
		questionLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		questionLabel.setPreferredSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
				100 + (int) (Math.ceil(
						questionLabel.getPreferredSize().getHeight() * (questionLabel.getPreferredSize().getWidth()
								/ Toolkit.getDefaultToolkit().getScreenSize().getWidth())))));
		questionLabel.setMinimumSize(questionLabel.getPreferredSize());
		
		panel.add(questionLabel);
		panel.add(Box.createRigidArea(new Dimension(0, x)));
		panel2.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		panel.add(panel2);
		panel.add(Box.createRigidArea(new Dimension(0, x)));
		panel1.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		panel.add(panel1);
		panel.add(Box.createRigidArea(new Dimension(0, x)));
		panel.setBackground(indigo);
		JPanel panel3 = new JPanel();

		//nextButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
		exitButton.setFont(font3);
		exitButton.addActionListener(new ExitButtonListener());

		panel3.setBackground(indigo);
		panel3.setOpaque(true);
		panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));
		panel3.add(Box.createHorizontalStrut(50));
		panel3.add(exitButton);
		Component c = Box.createHorizontalStrut((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth());
		panel3.add(c);
		panel3.add(nextButton);
		panel3.add(Box.createHorizontalStrut(50));
		panel.add(panel3);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JScrollPane scroll = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		this.setContentPane(scroll);
		java.awt.Rectangle r = panel3.getBounds();
		panel.setPreferredSize(
				new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), r.y + r.height + 50));
		c.setPreferredSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()
				- exitButton.getWidth() - nextButton.getWidth() - 100), 0));

		questionWrong = 0;
		wrongQuestions = new SimpleLinkedList<Question>();

		this.student = student;


	} // End of constructor

	public static SimpleLinkedList<String> getQuestionStatment() {
		return questionStatement;
	}

	public static SimpleLinkedList<Question> getWrongQuestions() {
		return wrongQuestions;
	}

	public static int getQuestionWrong() {
		return questionWrong;
	}

	public void playMusic() {
		try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("CorrectSound.wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }

	}

	public void displayNumberAnswerQuestions() {
		SimpleLinkedList<JButton> buttonlist = new SimpleLinkedList<JButton>();
		for (int i = 0; i < choices.get(questionNum).length; i++) {
			JButton button = new JButton(String.format("%.2f", Double.parseDouble(choices.get(questionNum)[i])));
			button.setFont(font3);
			button.setOpaque(true);
			button.setBorderPainted(true);
			buttonlist.add(button);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					if (button.getText().equals(answers.get(questionNum))) {
						correct = true;
						finished = true;
						for (int i = 0; i < buttonlist.size(); i++) {
							if ((buttonlist.get(i).getBackground()) != defaultColor) {
								right = false;
							}
						}
						button.setBackground(Color.GREEN);
						playMusic();
					} else {
						button.setBackground(Color.RED);
					}
					clicked = true;
				}
			});

			panel1.add(button);
			panel1.add(Box.createRigidArea(new Dimension(100, 0)));
			panel1.setBackground(indigo);
		}
	}

	public void displayWordAnswerQuestions() {
		SimpleLinkedList<JButton> buttonlist = new SimpleLinkedList<JButton>();
		for (int i = 0; i < choices.get(questionNum).length; i++) {
			JButton button = new JButton(choices.get(questionNum)[i]);
			button.setFont(font3);
			button.setOpaque(true);
			button.setBorderPainted(true);
			buttonlist.add(button);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (button.getText().equals(answers.get(questionNum))) {
						correct = true;
						finished = true;
						for (int i = 0; i < buttonlist.size(); i++) {
							if ((buttonlist.get(i).getBackground()) != defaultColor) {
								right = false;
							}
						}
						button.setBackground(Color.GREEN);
						playMusic();
					} else {
						button.setBackground(Color.RED);
					}
					clicked = true;
				}
			});

			panel1.add(button);
			panel1.add(Box.createRigidArea(new Dimension(100, 0)));
			panel1.setBackground(indigo);
		}
	}

	private class NextButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			panel2.removeAll();
			panel1.removeAll();
		

			if (!right || !clicked || !finished) {
				if (!wrongQuestions.contain(rootQuestions.get(questionNum))) {
					wrongQuestions.add(rootQuestions.get(questionNum));
				}
				questionWrong++;
			}
			questionNum++;
			if (questionNum == getQuestionStatment().size()) {
				new SummaryPage(wrongQuestions, student);
				dispose();
				return;
			}

			if (variableIDs.get(questionNum) == null) {
				displayWordAnswerQuestions();

				//panel2.add(new JLabel (new ImageIcon(rootQuestions.get(questionNum).getImage())));
			} else {
				displayNumberAnswerQuestions();
				ids = variableIDs.get(questionNum);
				values = variableValues.get(questionNum);
				if (ids != null) {
					for (int j = 0; j < ids.length; j++) {
						try {
							panel2.add(new JLabel(new ImageIcon(QuizEditor.stringToImage(ids[j]))));
							JLabel value = new JLabel(" = " + String.format("%.2f", values[j]) + "  ");
							value.setFont(font2);
							value.setForeground(lightBlue);
							panel2.add(value);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			right = true;
			clicked = false;
			finished = false;
		
			
			//questionLabel.setText("<html><div style='text-align: center;'>" + getQuestionStatment().get(questionNum) + "</div></html");
			questionLabel = new JLabel("<html><div style='text-align: center;'>" + getQuestionStatment().get(questionNum) + "</div></html");
			System.out.println(questionLabel.size());
			questionLabel.setPreferredSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
					(int) (Math.ceil(
							questionLabel.getPreferredSize().getHeight() * (questionLabel.getPreferredSize().getWidth()
									/ Toolkit.getDefaultToolkit().getScreenSize().getWidth())))));
			System.out.println(questionLabel.size());
			questionLabel.setMinimumSize(questionLabel.getPreferredSize());
			System.out.println(questionLabel.size());
			

			label.setText("Question #" + Integer.toString(questionNum + 1));
		
			
			revalidate();
		}
	}

	private class ExitButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			window.dispose();
		}
	}

}

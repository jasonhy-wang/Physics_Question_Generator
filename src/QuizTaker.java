import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.imgscalr.Scalr;

import data_structures.SimpleLinkedList;

import javax.swing.JComboBox;

/*
 * To Do
 * Main UI better
 * Preset questions
 * Enter key listener
 * Show current user
 */

public class QuizTaker {

	private int numQuestions;
	private int numNumQuestions;
	private int numWordQuestions;

	private Database database;
	private SimpleLinkedList<Subject> subjects;
	private SimpleLinkedList<Unit> units;
	private SimpleLinkedList<Question> rootQuestions;
	private SimpleLinkedList<Question> questions;
	private SimpleLinkedList<Double> answers;
	private SimpleLinkedList<double[]> choices;
	private SimpleLinkedList<String> problemStatements;
	private SimpleLinkedList<String[]> variableIDs;
	private SimpleLinkedList<double[]> variableValues;
	private Subject chosenSubject;
	private Unit chosenUnit;
	private static Student student;

	private Color indigo, lightBlue;
	private static Random rand;
	private BufferedImage logo;

	private static JFrame window;
	private JPanel title;
	private JPanel mainPanel;
	private JPanel optionsPanel;
	private JButton start;
	private JButton exit;
	private JComboBox<String> subject, unit;
	private JTextField numQuestionsField;

	QuizTaker(Student student) {
		this.student = student;
		
		window = new JFrame();

		database = new Database();

		subjects = database.getSubjects();

		indigo = new Color(56, 53, 74);
		lightBlue = new Color(162, 236, 250);

		////////////////////////////////////////////////// GUI
		////////////////////////////////////////////////// STUFF/////////////////////////////////////

		window.setSize(Toolkit.getDefaultToolkit().getScreenSize());

		try {
			logo = ImageIO.read(new File("logo.png"));
			logo = Scalr.resize(logo, (int) (window.getHeight() / 2));
		} catch (IOException e) {
			logo = null;
		}

		title = new LogoPanel();
		title.setBackground(indigo);
		title.setBorder(BorderFactory.createEmptyBorder(0, 0,
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 5), 0));

		start = new JButton("START");
		start.addActionListener(new StartButtonActionListener());
		start.setAlignmentX(Component.CENTER_ALIGNMENT);

		exit = new JButton("EXIT");
		exit.addActionListener(new ExitButtonActionListener());
		exit.setAlignmentX(Component.CENTER_ALIGNMENT);

		unit = new JComboBox<String>();
		unit.addActionListener(new UnitActionListener());

		subject = new JComboBox<String>();
		subject.addActionListener(new SubjectActionListener());
		addSubjects();

		numQuestionsField = new JTextField("# of Questions");
		numQuestionsField.addFocusListener(new NumQuestionsFocusListener());

		optionsPanel = new JPanel();
		optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 5));
		optionsPanel.setBackground(indigo);
		optionsPanel.add(subject);
		optionsPanel.add(unit);
		optionsPanel.add(numQuestionsField);
		optionsPanel.setVisible(true);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(indigo);
		mainPanel.add(title);
		mainPanel.add(optionsPanel);
		mainPanel.add(start);
		mainPanel.add(exit);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0,
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 5), 0));
		mainPanel.setVisible(true);

		window.add(mainPanel, BorderLayout.CENTER);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setUndecorated(true);
		window.requestFocusInWindow();
		window.setVisible(true);
	}

	private void addSubjects() {
		for (int i = 0; i < subjects.size(); i++) {
			subject.addItem(
					subjects.get(i).getName() + " " + subjects.get(i).getGrade() + " " + subjects.get(i).getLevel());
		}
	}

	private void addUnits() {

		units = chosenSubject.getUnits();

		if (unit != null) {
			unit.removeAllItems();
		}

		for (int i = 0; i < units.size(); i++) {
			unit.addItem(units.get(i).getName());
		}

	}

	private void startQuiz() {
		answers = new SimpleLinkedList<Double>();
		choices = new SimpleLinkedList<double[]>();
		problemStatements = new SimpleLinkedList<String>();
		variableIDs = new SimpleLinkedList<String[]>();
		variableValues = new SimpleLinkedList<double[]>();
		questions = new SimpleLinkedList<Question>();

		SimpleLinkedList<Symbol> formula;
		SimpleLinkedList<Variable> tempVariables;
		Question tempQ;
		int tempRand;

		double[] wrongAns;
		double[] tempWrongAns;
		double ans;
		String[] IDs;
		double[] values;
		String problemStatement;

		rand = new Random();
		rootQuestions = chosenUnit.getQuestions();

		for (int i = 0; i < numQuestions; i++) {
			tempQ = rootQuestions.get(rand.nextInt(rootQuestions.size()));
			tempVariables = new SimpleLinkedList<Variable>();

			ans = tempQ.getAnswer();
			answers.add(ans);

			tempWrongAns = tempQ.getFalseAnswers();
			wrongAns = new double[3];
			wrongAns[0] = tempWrongAns[0];
			wrongAns[1] = tempWrongAns[1];
			wrongAns[2] = tempWrongAns[2];
			tempRand = rand.nextInt(4);
			if (tempRand == 3) {
				choices.add(new double[] { ans, wrongAns[0], wrongAns[1], wrongAns[2] });
			} else if (tempRand == 2) {
				choices.add(new double[] { wrongAns[0], ans, wrongAns[1], wrongAns[2] });
			} else if (tempRand == 1) {
				choices.add(new double[] { wrongAns[0], wrongAns[1], ans, wrongAns[2] });
			} else {
				choices.add(new double[] { wrongAns[0], wrongAns[1], wrongAns[2], ans });
			}

			problemStatement = tempQ.getProblemStatement();
			problemStatements.add(problemStatement);

			formula = tempQ.getFormula();
			for (int j = 0; j < formula.size(); j++) {
				if (formula.get(j) instanceof Variable) {
					if(!((Variable) formula.get(j)).isConstant()) {
						boolean variableUsed = false;
						for (int k = 0; k < tempVariables.size(); k++) {
							if (tempVariables.get(k).getId().equals(formula.get(j).getId())) {
								variableUsed = true;
								break;
							}
						}
						if (variableUsed) continue;
						tempVariables.add((Variable) formula.get(j));
					}
				}
			}
			IDs = new String[tempVariables.size()];
			values = new double[tempVariables.size()];
			for (int k = 0; k < tempVariables.size(); k++) {
				IDs[k] = tempVariables.get(k).getId();
				values[k] = tempVariables.get(k).getValue();
			}
			variableIDs.add(IDs);
			variableValues.add(values);

			questions.add(new Question(problemStatement, formula));

		}
		new QuizTakerDisplay(problemStatements, choices, answers, variableIDs, variableValues, questions);
		window.dispose();

	}

	static void startQuiz(SimpleLinkedList<Question> rootQuestions) {
		SimpleLinkedList<Double> answers = new SimpleLinkedList<Double>();
		SimpleLinkedList<double[]> choices = new SimpleLinkedList<double[]>();
		SimpleLinkedList<String> problemStatements = new SimpleLinkedList<String>();
		SimpleLinkedList<String[]> variableIDs = new SimpleLinkedList<String[]>();
		SimpleLinkedList<double[]> variableValues = new SimpleLinkedList<double[]>();
		SimpleLinkedList<Question> questions = new SimpleLinkedList<Question>();

		SimpleLinkedList<Symbol> formula;
		SimpleLinkedList<Variable> tempVariables;
		Question tempQ;
		int tempRand;

		double[] wrongAns;
		double[] tempWrongAns;
		double ans;
		String[] IDs;
		double[] values;
		String problemStatement;

		rand = new Random();

		for (int i = 0; i < rootQuestions.size(); i++) {
			tempQ = rootQuestions.get(i);
			tempVariables = new SimpleLinkedList<Variable>();

			ans = tempQ.getAnswer();
			answers.add(ans);

			tempWrongAns = tempQ.getFalseAnswers();
			wrongAns = new double[3];
			wrongAns[0] = tempWrongAns[0];
			wrongAns[1] = tempWrongAns[1];
			wrongAns[2] = tempWrongAns[2];
			tempRand = rand.nextInt(4);
			if (tempRand == 3) {
				choices.add(new double[] { ans, wrongAns[0], wrongAns[1], wrongAns[2] });
			} else if (tempRand == 2) {
				choices.add(new double[] { wrongAns[0], ans, wrongAns[1], wrongAns[2] });
			} else if (tempRand == 1) {
				choices.add(new double[] { wrongAns[0], wrongAns[1], ans, wrongAns[2] });
			} else {
				choices.add(new double[] { wrongAns[0], wrongAns[1], wrongAns[2], ans });
			}

			problemStatement = tempQ.getProblemStatement();
			problemStatements.add(problemStatement);

			formula = tempQ.getFormula();
			for (int j = 0; j < formula.size(); j++) {
				if (formula.get(j) instanceof Variable) {
					if(!((Variable) formula.get(j)).isConstant()) {
						boolean variableUsed = false;
						for (int k = 0; k < tempVariables.size(); k++) {
							if (tempVariables.get(k).getId().equals(formula.get(j).getId())) {
								variableUsed = true;
								break;
							}
						}
						if (variableUsed) continue;
						tempVariables.add((Variable) formula.get(j));
					}
				}
			}
			IDs = new String[tempVariables.size()];
			values = new double[tempVariables.size()];
			for (int k = 0; k < tempVariables.size(); k++) {
				IDs[k] = tempVariables.get(k).getId();
				values[k] = tempVariables.get(k).getValue();
			}
			variableIDs.add(IDs);
			variableValues.add(values);

			questions.add(new Question(problemStatement, formula));

		}
		new QuizTakerDisplay(problemStatements, choices, answers, variableIDs, variableValues, questions, student);
		window.dispose();

	}
	
	private void findNumEachQuestions() {
		
	}

	////////////////////////////////////////////////////// PRIVATE
	////////////////////////////////////////////////////// CLASSES////////////////////////////////

	private class SubjectActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String subjectName = (String) subject.getSelectedItem();

			for (int i = 0; i < subjects.size(); i++) {
				if (subjectName.equals(subjects.get(i).getName() + " " + subjects.get(i).getGrade() + " "
						+ subjects.get(i).getLevel())) {
					chosenSubject = subjects.get(i);
				}
			}

			addUnits();
		}

	}

	private class UnitActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if (unit.getSelectedItem() != null) {
				String unitName = (String) unit.getSelectedItem();

				for (int i = 0; i < units.size(); i++) {
					if (unitName.equals(units.get(i).getName())) {
						chosenUnit = units.get(i);
					}
				}
			}
		}

	}

	private class NumQuestionsFocusListener implements FocusListener {

		public void focusGained(FocusEvent e) {
			if (numQuestionsField.getText().trim().equals("# of Questions")) {
				numQuestionsField.setText("");
			}
		}

		public void focusLost(FocusEvent e) {
			if (numQuestionsField.getText().trim().equals("")) {
				numQuestionsField.setText("# of Questions");
			}
		}

	}

	private class StartButtonActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			boolean validNum = true;

			try {
				numQuestions = Integer.parseInt(numQuestionsField.getText());
			} catch (Exception s) {
				validNum = false;
			}

			if (validNum && (numQuestions >= 1)) {
				startQuiz();
			} else {
				JOptionPane.showMessageDialog(null, "Invalid # of Questions");
			}
		}

	}

	private class ExitButtonActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			window.dispose();
		}

	}

	private class LogoPanel extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setDoubleBuffered(true);

			g.drawImage(logo, (int) ((window.getWidth() / 2) - (logo.getWidth() / 2)), 0, null);

			repaint();
		}
	}
}

/**
 * DO NOT MODIFY THIS FILE
 * This file is currently being worked on in WindowBuilder, and
 * may not parse correctly if modified
 */
package checkers.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import checkers.Board;
import checkers.Move;
import javafx.scene.layout.Border;

/**
 * Application window
 * 
 * @author Daniel Nash
 *
 */
public class GameWindow extends JFrame {

	private static final long serialVersionUID = 7384928133320272861L;
	JPanel contentPane;
	private JPanel movePanel;
	private JPanel boardPanel;
	private JLabel lblStatus;
	private JScrollPane scrollPanel;
	public volatile Board board;
	volatile boolean cont = true;
	public volatile static GameWindow frame;
	volatile boolean changed = true;
	volatile CheckerSquare[][] cs = new CheckerSquare[8][8];
	public volatile int choice = 0;
	public volatile JButton moves[] = new JButton[20];
	public volatile ArrayList<Move> movesList;
	public CheckerButtonUI cbtn_ui[] = new CheckerButtonUI[20];
	public boolean gameStarted = false;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 * @param b - board for the window
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static void main(@SuppressWarnings("unused") String[] args, Board b) throws InvocationTargetException, InterruptedException {
		EventQueue.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					frame = new GameWindow(b);
					colorBoard(frame);
					setSizes(frame);
					frame.setVisible(true);
					frame.board = new Board();
					return;
					// frame.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Sets the sizes of the window components
	 * 
	 * @param frame
	 */
	private static void setSizes(GameWindow frame) {
		frame.setSize(1000, 500);
		for (Component cnv : frame.movePanel.getComponents()) {
			int height = cnv.getHeight();
			if (cnv instanceof Canvas)
				((Canvas) cnv).setSize(500, height);
			cnv.setPreferredSize(new Dimension(500, height));
		}
		frame.pack();
	}

	/**
	 * Colors the squares on the board
	 * 
	 * @param frame
	 */
	private static void colorBoard(GameWindow frame) {
		Component[] components = frame.boardPanel.getComponents();
		for (Component cmp : components) {
			if (!(cmp instanceof Canvas))
				continue;
			Canvas cnv = (Canvas) cmp;
			int s = Integer.parseInt(cnv.getName());
			int r = s % 10;
			int c = (s - r) / 10;
			if ((r + c) % 2 == 0) {
				cnv.setBackground(Color.BLACK);
			} else {
				cnv.setBackground(Color.WHITE);
			}
		}
	}

	/**
	 * Create the frame.
	 * @param b - board
	 * 
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public GameWindow(Board b) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
	UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		this.board = b;
		setTitle("Checkers");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 872, 379);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel lblPane = new JPanel();

		lblStatus = new JLabel("Checkers Game");
		lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD).deriveFont(15f));
		lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblPane.add(lblStatus, BorderLayout.CENTER);

		contentPane.add(lblPane, BorderLayout.NORTH);
		lblPane.setBackground(Color.WHITE);

		boardPanel = new JPanel();
		boardPanel.setBackground(Color.WHITE);
		boardPanel.setPreferredSize(new Dimension(500, 500));
		boardPanel.setSize(new Dimension(500, 500));
		boardPanel.setMaximumSize(new Dimension(500, 500));
		contentPane.add(boardPanel, BorderLayout.CENTER);
		boardPanel.setLayout(new GridLayout(8, 8, 0, 0));
		boardPanel.setBorder(new LineBorder(Color.BLACK));

		for (int r = 0; r < 8; r++)
			for (int c = 0; c < 8; c++) {
				cs[r][c] = new CheckerSquare(frame, ((r + c) % 2 == 0));
				cs[r][c].isBlack = ((r + c) % 2 == 0);
				cs[r][c].setName(String.valueOf(r) + String.valueOf(c));
				boardPanel.add(cs[r][c]);
				cs[r][c].setChecker(board.board[r + 1][c + 1]);
				cs[r][c].c = c + 1;
				cs[r][c].r = r + 1;
			}

		movePanel = new JPanel();
		movePanel.setBackground(Color.WHITE);
		movePanel.setMinimumSize(new Dimension(250, 10));
		scrollPanel = new JScrollPane(movePanel);
		scrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.add(scrollPanel, BorderLayout.EAST);
		movePanel.setLayout(new GridLayout(20, 1, 0, 0));
		movePanel.setName("movePnl");

		JCheckBox viewOutput = new JCheckBox();
		viewOutput.setText("Show Output");
		viewOutput.setSelected(true);
		viewOutput.addItemListener(new CheckHandler());
		contentPane.add(viewOutput, BorderLayout.SOUTH);
		
		for (int i = 0; i < moves.length; i++) {
			moves[i] = new JButton(String.valueOf("-----"));
			moves[i].setName(String.valueOf(i));
			cbtn_ui[i] = new CheckerButtonUI();
			cbtn_ui[i].aux_listener = new MoveHandler(this);

			moves[i].setUI(cbtn_ui[i]);
			//moves[i].addMouseListener(new MoveHandler(this));
			movePanel.add(moves[i]);
		}
	}

	JPanel getMovePanel() {
		return movePanel;
	}

	JPanel getBoardPanel() {
		return boardPanel;
	}

	public JLabel getLblStatus() {
		return lblStatus;
	}

	public void updateCheckers() {
		for (int r = 0; r < 8; r++)
			for (int c = 0; c < 8; c++)
				cs[r][c].setChecker(board.board[r + 1][c + 1]);

	}
}

class CheckHandler implements ItemListener {

	@Override
	public void itemStateChanged(ItemEvent e) {
		OutputFrame.frame.setVisible(e.getStateChange() != ItemEvent.DESELECTED);
	}
}

class MoveHandler implements MouseListener {

	private GameWindow win;

	public MoveHandler(GameWindow gameWindow) {
		win = gameWindow;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (((AbstractButton)e.getComponent()).getText().indexOf("-") != -1)
			return;
		win.choice = Integer.parseInt(e.getComponent().getName());
		if (!win.gameStarted)
			return;
		Move m = lookup(e);
		if (m == null) return;
		win.cs[m.getFx() - 1][m.getFy() - 1].isMouseIn = false;
		win.cs[m.getTx() - 1][m.getTy() - 1].isMouseIn = false;
		win.cs[m.getFx() - 1][m.getFy() - 1].repaint();
		win.cs[m.getTx() - 1][m.getTy() - 1].repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (!win.gameStarted)
			return;
		Move m = lookup(e);
		if (m == null) return;
		win.cs[m.getFx() - 1][m.getFy() - 1].isMouseIn = true;
		win.cs[m.getTx() - 1][m.getTy() - 1].isMouseIn = true;
		win.cs[m.getFx() - 1][m.getFy() - 1].repaint();
		win.cs[m.getTx() - 1][m.getTy() - 1].repaint();
		win.updateCheckers();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!win.gameStarted)
			return;
		Move m = lookup(e);
		if (m == null) return;
		win.cs[m.getFx() - 1][m.getFy() - 1].isMouseIn = false;
		win.cs[m.getTx() - 1][m.getTy() - 1].isMouseIn = false;
		win.cs[m.getFx() - 1][m.getFy() - 1].repaint();
		win.cs[m.getTx() - 1][m.getTy() - 1].repaint();
		win.updateCheckers();
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	private Move lookup(MouseEvent e) {
		if (Integer.valueOf(e.getComponent().getName()) < win.movesList.size() && e.getComponent().getName().indexOf("-") == -1) {
			return win.movesList.get(Integer.valueOf(e.getComponent().getName()));
		}
		else
			return null;
	}
}

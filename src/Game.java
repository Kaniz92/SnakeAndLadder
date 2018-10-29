/*
 * A.K.G. Silva
 * Java Assignment 2
 * MSc in AI
 */
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Game extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	int player1 = 0;
	int player2 = 0;
	boolean isPlayer1 = true;
	boolean isLadder = false;
	boolean isSnake = false;

	Container pane;
	int jPanelNumber = 0;

	Map<Integer, Integer> myMap = new HashMap<Integer, Integer>();
	Map<Integer, Integer> snakesMap = new HashMap<Integer, Integer>();
	Map<Integer, Integer> laddersMap = new HashMap<Integer, Integer>();

	String winner = "";

	public Game() {
		pane = this.getContentPane();
		pane.setLayout(new GridLayout(11, 10));

		// draw board
		drawBoard();

		// initialize snakes
		snakesMap.put(99, 80);
		snakesMap.put(95, 75);
		snakesMap.put(92, 88);
		snakesMap.put(89, 68);
		snakesMap.put(74, 53);
		snakesMap.put(49, 11);
		snakesMap.put(46, 25);
		snakesMap.put(16, 6);
		snakesMap.put(64, 60);
		snakesMap.put(62, 19);

		// initialize ladders
		laddersMap.put(78, 98);
		laddersMap.put(87, 94);
		laddersMap.put(71, 91);
		laddersMap.put(51, 67);
		laddersMap.put(28, 84);
		laddersMap.put(21, 42);
		laddersMap.put(2, 38);
		laddersMap.put(36, 44);
		laddersMap.put(15, 26);
		laddersMap.put(7, 14);
		laddersMap.put(8, 31);
	}

	private static void createAndShowGUI() {
		// set frame defined size
		JFrame frame = new Game();
		frame.setTitle("Snakes And Ladders");
		frame.setPreferredSize(new Dimension(700, 700));
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private void drawBoard() {
		for (int horz = 0; horz < 11; horz++) {
			for (int vert = 0; vert < 10; vert++) {
				if (horz == 10) {
					if (vert == 0 || vert == 1 || vert == 2) {
						if (vert == 2) {
							JPanel panel = new JPanel() {
								@Override
								protected void paintComponent(Graphics g) {
									super.paintComponent(g);
									g.setColor(Color.DARK_GRAY);
								}
							};
							pane.add(panel);
						} else {
							Image image = createImage("/images/icon_" + vert + ".png");
							JPanel panel = new JPanel() {
								@Override
								protected void paintComponent(Graphics g) {
									super.paintComponent(g);
									g.drawRoundRect(4, 4, 55, 55, 2, 2);
									g.drawImage(image, 5, 5, 50, 50, null);
								}
							};
							JButton b1 = new JButton();
							b1.setOpaque(false);
							b1.setContentAreaFilled(false);
							b1.setBorderPainted(false);
							b1.addActionListener(this);
							b1.setActionCommand(String.valueOf(vert));
							b1.setPreferredSize(new Dimension(70, 70));
							panel.add(b1);
							pane.add(panel);
						}

					} else if (vert == 4) {
						// add player text
						JPanel panel = new JPanel();
						JLabel jl = new JLabel("Player 1: ");
						panel.add(jl);
						pane.add(panel);
					} else if (vert == 5 || vert == 7 || vert == 9) {
						// empty
						JPanel panel = new JPanel() {
							@Override
							protected void paintComponent(Graphics g) {
								super.paintComponent(g);
								g.setColor(Color.DARK_GRAY);
							}
						};
						pane.add(panel);
					} else if (vert == 6) {
						// player text
						JPanel panel = new JPanel();
						JLabel jl = new JLabel("Player 2: ");
						panel.add(jl);
						pane.add(panel);
					} else if (vert == 8) {
						// add winner text
						JPanel panel = new JPanel();
						JLabel jl = new JLabel("Winner : ");
						jl.setHorizontalTextPosition(JLabel.CENTER);
						jl.setVerticalTextPosition(JLabel.CENTER);
						panel.add(jl);
						pane.add(panel);
					}

				} else {
					// set background of cell
					Image image = createImage("/images/" + jPanelNumber + ".PNG");
					JPanel panel = new JPanel() {
						@Override
						protected void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(image, 0, 0, 70, 70, null);
						}
					};

					// calculate cell index
					int evenRowIndex = 100 - (horz * 10 + vert);
					int oddRowIndex = vert + 1 + (100 - horz * 10) - 10;
					int i = horz % 2 == 0 ? evenRowIndex : oddRowIndex;

					// set hashmap of panel index and cell index to later show the player position
					myMap.put(i, jPanelNumber);
					jPanelNumber++;

					pane.add(panel);
				}
			}
		}
		pane.setVisible(true);
	}

	// read cell images
	private Image createImage(String path) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL).getImage();
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String action = ae.getActionCommand();
		if (winner != "") {
			reset();
		} else {
			// if click throw die button
			if (action.equals("1")) {
				int prevPos = 0;
				if (isPlayer1) {
					prevPos = player1;
					if (player1 > 0) {
						clearPreviousPlayerPosition(player1);
					}

					player1 += getRandomDice();

					showNextPlayerPosition(prevPos, player1);

					// if ladder position
					int ladderPos = goUpLadder(player1) != -1 ? goUpLadder(player1) : player1;

					// if snake position
					int snakePos = goDownSnake(player1) != -1 ? goDownSnake(player1) : player1;

					if (isLadder || isSnake) {
						clearPreviousPlayerPosition(player1);
						player1 = isLadder ? ladderPos : snakePos;
						showNextPlayerPosition(prevPos, player1);
					}

					// show number
					JLabel jl = new JLabel(String.valueOf(player1));
					showPlayerCount(jl);
				} else {
					prevPos = player2;
					if (player2 > 0) {
						clearPreviousPlayerPosition(player2);
					}

					player2 += getRandomDice();

					showNextPlayerPosition(prevPos, player2);

					// if ladder position
					int ladderPos = goUpLadder(player2) != -1 ? goUpLadder(player2) : player2;

					// if snake position
					int snakePos = goDownSnake(player2) != -1 ? goDownSnake(player2) : player2;

					if (isLadder || isSnake) {
						clearPreviousPlayerPosition(player2);
						player2 = isLadder ? ladderPos : snakePos;
						showNextPlayerPosition(prevPos, player2);
					}
					// show number
					JLabel jl = new JLabel(String.valueOf(player2));
					showPlayerCount(jl);
				}
				// toggle players
				isPlayer1 = !isPlayer1;
				isLadder = false;
				isSnake = false;

			} // if click reset button
			else if (action.equals("0")) {
				reset();
			}
		}
	}

	private void showPlayerCount(JLabel jl) {
		int newCellIndex = isPlayer1 ? 104 : 106;

		// set new player position
		JPanel newPosition = new JPanel();
		newPosition.add(jl);
		JPanel newPositionOriginal = (JPanel) pane.getComponent(newCellIndex);
		pane.remove(newPositionOriginal);
		pane.add(newPosition, newCellIndex);
		pane.validate();
		pane.repaint();
	}

	private void showDiceCount(JLabel jl) {
		int newCellIndex = 102;

		// set new player position
		JPanel newPosition = new JPanel();
		newPosition.add(jl);
		JPanel newPositionOriginal = (JPanel) pane.getComponent(newCellIndex);
		pane.remove(newPositionOriginal);
		pane.add(newPosition, newCellIndex);
		pane.validate();
		pane.repaint();
	}

	private int goUpLadder(int position) {
		if (laddersMap.containsKey(position)) {
			isLadder = true;
			return laddersMap.get(position);
		}
		return -1;
	}

	private int goDownSnake(int position) {
		if (snakesMap.containsKey(position)) {
			isSnake = true;
			return snakesMap.get(position);
		}
		return -1;
	}

	private int getRandomDice() {
		int num = (int) (Math.random() * 6) + 1;
		// show dice count
		JLabel jl1 = new JLabel(String.valueOf(num));
		showDiceCount(jl1);
		return num;
	}

	private void clearPreviousPlayerPosition(int previousPlayerPos) {
		int pereviousCellIndex = myMap.get(previousPlayerPos);
		// remove previous player position
		Image image = createImage("/images/" + pereviousCellIndex + ".PNG");
		JPanel oldPosition = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(image, 0, 0, 70, 70, null);
			}
		};
		JPanel oldPositionOriginal = (JPanel) pane.getComponent(pereviousCellIndex);
		pane.remove(oldPositionOriginal);
		pane.add(oldPosition, pereviousCellIndex);
		pane.validate();
		pane.repaint();
	}

	private void showNextPlayerPosition(int prevPos, int nextPlayerPos) {
		if (nextPlayerPos < 100) {
			updatePlayerPosition(nextPlayerPos);
		} else if (nextPlayerPos == 100) {
			updatePlayerPosition(nextPlayerPos);
			String label = "";
			if (isPlayer1) {
				label = "icon_3.png";
			} else {
				label = "blue.jpg";
			}

			int cellIndex = 108;
			Image image = createImage("/images/" + label);
			JPanel oldPosition = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawRoundRect(4, 4, 55, 55, 2, 2);
					g.drawImage(image, 5, 5, 50, 50, null);
				}
			};
			JPanel oldPositionOriginal = (JPanel) pane.getComponent(cellIndex);
			pane.remove(oldPositionOriginal);
			pane.add(oldPosition, cellIndex);
			pane.validate();
			pane.repaint();

		} else {
			updatePlayerPosition(prevPos);
			if (isPlayer1) {
				player1 = prevPos;
			} else {
				player2 = prevPos;
			}
		}
	}

	private void updatePlayerPosition(int nextPlayerPos) {
		// get corresponding panel index
		int newCellIndex = myMap.get(nextPlayerPos);

		// set new player position
		JPanel newPosition = new JPanel();
		newPosition.setBackground(isPlayer1 ? Color.RED : Color.BLUE);
		JPanel newPositionOriginal = (JPanel) pane.getComponent(newCellIndex);
		pane.remove(newPositionOriginal);
		pane.add(newPosition, newCellIndex);
		pane.validate();
		pane.repaint();
	}

	private void cleartText(int position) {
		JLabel jl = new JLabel("");
		JPanel newPosition = new JPanel();
		newPosition.add(jl);
		JPanel newPositionOriginal = (JPanel) pane.getComponent(position);
		pane.remove(newPositionOriginal);
		pane.add(newPosition, position);
		pane.validate();
		pane.repaint();
	}

	private void reset() {
		// re-initializa player 1 and player 2 positions
		clearPreviousPlayerPosition(player1);
		clearPreviousPlayerPosition(player2);

		// reinitialize UI variables
		cleartText(102);

		// clear player 1 field
		cleartText(104);

		// clear player 2 field
		cleartText(106);

		// clear winner field
		JPanel oldPosition = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};
		JPanel oldPositionOriginal = (JPanel) pane.getComponent(108);
		pane.remove(oldPositionOriginal);
		pane.add(oldPosition, 108);
		pane.validate();
		pane.repaint();

		player1 = 0;
		player2 = 0;
		isPlayer1 = true;
		isLadder = false;
		isSnake = false;
		winner = "";

	}
}

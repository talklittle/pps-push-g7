/* 
 * 	$Id: BoardPanel.java,v 1.1 2007/09/06 14:51:49 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package push.sim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import push.sim.Player.Direction;
import sl.shapes.RegularPolygon;

public final class BoardPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final Color[] playerColors = { new Color(53, 204, 53),
			new Color(229, 229, 0), new Color(255, 147, 25),
			new Color(229, 0, 0), new Color(198, 0, 229),
			new Color(66, 66, 255) };
	public static Point2D MouseCoords;

	private Board board;

	private GameEngine engine;

	Cursor curCursor;
	private Color[][] cellColors;

	public BoardPanel() {
		this.setPreferredSize(new Dimension(600, 600));
		this.setBackground(Color.white);
	}

	static final int PIECE_SIZE = 60;
	static final int BOARD_X = 50;
	static final int BOARD_Y = 100;

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2D = (Graphics2D) g;
		if (engine != null && engine.players != null
				&& engine.players.size() == 6) {
			Font f = new Font("Sans-Serif", Font.BOLD, 24);
			g2D.setFont(f);

			AffineTransform af = g2D.getTransform();

			AffineTransform rot = (AffineTransform) af.clone();
			FontMetrics fm = this.getFontMetrics(g2D.getFont());
			Rectangle2D bounds = fm.getStringBounds("0: "
					+ engine.playerAtDirection(Direction.NW).getName(), g2D);
			rot.rotate(Math.toRadians(-30), BOARD_X + PIECE_SIZE * 2, BOARD_Y);
			g2D.setTransform(rot);
			g2D.setColor(playerColors[0]);
			g2D.fillRect(
					(int) (bounds.getMinX() + BOARD_X + PIECE_SIZE * 2 - bounds
							.getWidth() / 2),
					(int) (BOARD_Y + bounds.getMinY()),
					(int) bounds.getWidth(), (int) bounds.getHeight());
			g2D.setColor(Color.black);
			g2D.drawString("0: "
					+ engine.playerAtDirection(Direction.NW).getName(),
					(int) (BOARD_X + PIECE_SIZE * 2 - bounds.getWidth() / 2),
					(int) (BOARD_Y));
			g2D.setTransform(af);

			rot = (AffineTransform) af.clone();
			bounds = fm.getStringBounds(
					"1: " + engine.playerAtDirection(Direction.NE).getName(),
					g2D);
			rot.rotate(Math.toRadians(30), BOARD_X + PIECE_SIZE * 7, BOARD_Y);
			g2D.setTransform(rot);
			g2D.setColor(playerColors[1]);
			g2D.fillRect(
					(int) (bounds.getMinX() + BOARD_X + PIECE_SIZE * 7 - bounds
							.getWidth() / 2),
					(int) (BOARD_Y + bounds.getMinY()),
					(int) bounds.getWidth(), (int) bounds.getHeight());
			g2D.setColor(Color.black);
			g2D.drawString("1: "
					+ engine.playerAtDirection(Direction.NE).getName(),
					(int) (BOARD_X + PIECE_SIZE * 7 - bounds.getWidth() / 2),
					(int) (BOARD_Y));
			g2D.setTransform(af);

			rot = (AffineTransform) af.clone();
			bounds = fm.getStringBounds(
					"2: " + engine.playerAtDirection(Direction.E).getName(),
					g2D);
			rot.rotate(Math.toRadians(90), BOARD_X + PIECE_SIZE * 9, BOARD_Y
					+ PIECE_SIZE * 9 / 2);
			g2D.setTransform(rot);
			g2D.setColor(playerColors[2]);
			g2D.fillRect(
					(int) (bounds.getMinX() + BOARD_X + PIECE_SIZE * 9 - bounds
							.getWidth() / 2), (int) (BOARD_Y + PIECE_SIZE * 9
							/ 2 + bounds.getMinY() -10), (int) bounds.getWidth() ,
					(int) bounds.getHeight());
			g2D.setColor(Color.black);
			g2D.drawString("2: "
					+ engine.playerAtDirection(Direction.E).getName(),
					(int) (BOARD_X + PIECE_SIZE * 9 - bounds.getWidth() / 2),
					(int) (BOARD_Y + PIECE_SIZE * 9 / 2) - 10);
			g2D.setTransform(af);

			rot = (AffineTransform) af.clone();
			bounds = fm.getStringBounds(
					"3: " + engine.playerAtDirection(Direction.SE).getName(),
					g2D);
			rot.rotate(Math.toRadians(140), BOARD_X + PIECE_SIZE * 7, BOARD_Y
					+ PIECE_SIZE * 9);
			g2D.setTransform(rot);
			g2D.setColor(playerColors[3]);
			g2D.fillRect(
					(int) (bounds.getMinX() + BOARD_X + PIECE_SIZE * 7 - bounds
							.getWidth() / 2),
					(int) (BOARD_Y + PIECE_SIZE * 9 + bounds.getMinY()),
					(int) bounds.getWidth(), (int) bounds.getHeight());
			g2D.setColor(Color.black);
			g2D.drawString("3: "
					+ engine.playerAtDirection(Direction.SE).getName(),
					(int) (BOARD_X + PIECE_SIZE * 7 - bounds.getWidth() / 2),
					(int) (BOARD_Y + PIECE_SIZE * 9));
			g2D.setTransform(af);

			rot = (AffineTransform) af.clone();
			bounds = fm.getStringBounds(
					"4: " + engine.playerAtDirection(Direction.SW).getName(),
					g2D);
			rot.rotate(Math.toRadians(210), BOARD_X + PIECE_SIZE * 2, BOARD_Y
					+ PIECE_SIZE * 9);
			g2D.setTransform(rot);
			g2D.setColor(playerColors[4]);
			g2D.fillRect(
					(int) (bounds.getMinX() + BOARD_X + PIECE_SIZE * 2 - bounds
							.getWidth() / 2),
					(int) (BOARD_Y + PIECE_SIZE * 9 + bounds.getMinY()),
					(int) bounds.getWidth(), (int) bounds.getHeight());
			g2D.setColor(Color.black);
			g2D.drawString("4: "
					+ engine.playerAtDirection(Direction.SW).getName(),
					(int) (BOARD_X + PIECE_SIZE * 2 - bounds.getWidth() / 2),
					(int) (BOARD_Y + PIECE_SIZE * 9));
			g2D.setTransform(af);

			rot = (AffineTransform) af.clone();
			bounds = fm.getStringBounds(
					"5: " + engine.playerAtDirection(Direction.W).getName(),
					g2D);
			rot.rotate(Math.toRadians(-90), BOARD_X, BOARD_Y + PIECE_SIZE * 9
					/ 2);
			g2D.setTransform(rot);
			g2D.setColor(playerColors[5]);
			g2D.fillRect(
					(int) (bounds.getMinX() + BOARD_X - bounds.getWidth() / 2),
					(int) (BOARD_Y + PIECE_SIZE * 9 / 2 + bounds.getMinY() -10),
					(int) bounds.getWidth(), (int) bounds.getHeight());
			g2D.setColor(Color.black);
			g2D.drawString("5: "
					+ engine.playerAtDirection(Direction.W).getName(),
					(int) (BOARD_X - bounds.getWidth() / 2),
					(int) (BOARD_Y + PIECE_SIZE * 9 / 2) - 10);
			g2D.setTransform(af);
		}
		if (board != null) {
			Font f = new Font("Sans-Serif", Font.PLAIN, 14);
			g2D.setFont(f);
			for (int i = 0; i < board.board.length; i++) {
				int[] row = board.getRow(i);
				int length = i;
				if (length > 4)
					length = 8 - length;
				int offset = 4 - length;
				length += 5;
				g2D.setColor(Color.black);

				Color alpha = new Color(255, 255, 255, 255);
				for (int j = 0; j < length; j++) {
					Ellipse2D s = new Ellipse2D.Double(offset * PIECE_SIZE / 2
							+ j * PIECE_SIZE + BOARD_X, i * PIECE_SIZE
							+ BOARD_Y, PIECE_SIZE - 2, PIECE_SIZE - 2);
					// RegularPolygon s = new
					// RegularPolygon(offset*PIECE_SIZE*3/4 + j* PIECE_SIZE +
					// j*PIECE_SIZE/2 + BOARD_X+PIECE_SIZE/2,
					// i * PIECE_SIZE/2 + BOARD_Y + PIECE_SIZE/2,
					// PIECE_SIZE/2+2, 8,Math.toRadians(22.5));
					if (cellColors[i][j * 2 + offset] != null) {
						g2D.setColor(cellColors[i][j * 2 + offset]);
						g2D.fill(s);
					} else {
						g2D.setColor(Color.black);
						g2D.draw(s);
					}
				}
			}
			for (MoveResult m : engine.lastRound) {
				if (m.isSuccess()) {
					g2D.setStroke(new BasicStroke(4));
				} else {
					float[] dash = { 1, 1, 1, 1, 0, 0 };
					g2D.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT,
							BasicStroke.JOIN_MITER, 2.0f, dash, 0));
				}

				Color alpha = playerColors[m.getPlayerId()];
				alpha = new Color(alpha.getRed(), alpha.getGreen(),
						alpha.getBlue(), 255);
				g2D.setColor(alpha);
				g2D.drawLine(m.getMove().getX() * PIECE_SIZE / 2 + PIECE_SIZE
						/ 2 + BOARD_X, m.getMove().getY() * PIECE_SIZE
						+ BOARD_Y + PIECE_SIZE / 2, PIECE_SIZE
						* m.getMove().getNewX() / 2 + PIECE_SIZE / 2 + BOARD_X,
						BOARD_Y + PIECE_SIZE * m.getMove().getNewY()
								+ PIECE_SIZE / 2);
				Ellipse2D s = new Ellipse2D.Double(PIECE_SIZE
						* m.getMove().getNewX() / 2 + PIECE_SIZE / 2 + BOARD_X
						- 8, BOARD_Y + PIECE_SIZE * m.getMove().getNewY()
						+ PIECE_SIZE / 2 - 8, 16, 16);
				g2D.fill(s);
			}
			g2D.setStroke(new BasicStroke(1));
			if (board != null) {
				f = new Font("Sans-Serif", Font.PLAIN, 14);
				g2D.setFont(f);
				FontMetrics fm = this.getFontMetrics(g2D.getFont());
				for (int i = 0; i < board.board.length; i++) {
					int[] row = board.getRow(i);
					int length = i;
					if (length > 4)
						length = 8 - length;
					int offset = 4 - length;
					length += 5;
					g2D.setColor(Color.black);

					Color alpha = new Color(255, 255, 255, 255);
					for (int j = 0; j < length; j++) {

						String more = "";
						if (board.lastBoard != null)
							more = " (" + board.lastBoard[i][j * 2 + offset]
									+ ")";
						g2D.setColor(Color.black);
						Rectangle2D bounds = fm.getStringBounds("" + row[offset + j * 2] + more
//								+"("+(offset+j*2)+","+i+")"
								, g2D);
						g2D.drawString("" 
								+ row[offset + j * 2] + more
//						 +"("+(offset+j*2)+","+i+")"
//						+ (Math.abs(4 - offset+j*2) + Math.abs(i))
								, (int) (offset * PIECE_SIZE / 2 + j * PIECE_SIZE
										+ PIECE_SIZE / 2 + BOARD_X - bounds.getWidth()/2), i
										* PIECE_SIZE + PIECE_SIZE / 2 + BOARD_Y
										+ 5);
					}
				}
			}
		}
	}

	public BoardPanel(GameEngine eng, boolean editable) {
		setEngine(eng);
		setBoard(engine.getBoard(), editable);

		this.editable = editable;
	}

	public void initColors() {
		cellColors = new Color[18][18];
		for (int j = 0; j < 9; j++) {
			int length = j;
			if (length > 4)
				length = 8 - length;
			int offset = 4 - length;
			length += 5;
			for (int i = 0; i < length; i++) {
				// Cell is (i,j) but indexed (j,i)
				Direction closest = null;
				Direction closest2 = null;
				int closestn = 8;
				int closestn2 = 8;
				Point conv = new Point(i * 2 + offset, j);
				for (Direction d : engine.positions) {
					int dx = d.getHome().x - conv.x;
					int dy = d.getHome().y - conv.y;
					int s = -1;
					if (Math.abs(dx) == Math.abs(dy))
						s = Math.abs(dx);
					else if (Math.abs(dy) == 0)
						s = Math.abs(dx) / 2;
					else if (Math.abs(dx) == 0)
						s = Math.abs(dy);
					else if (Math.abs(dx) == 1)
						s = Math.abs(dy);
					else if (Math.abs(dx) == 2)
						s = Math.abs(dy);
					else {
						s = (int) Math.ceil((double) (Math.abs(dx) + Math
								.abs(dy)) / 2.0d);
					}
					if (s <= closestn) {
						closest2 = closest;
						closestn2 = closestn;
						closest = d;
						closestn = s;
					}
				}
				if (closestn != closestn2) {
					Color c = playerColors[engine.positions.indexOf(closest)];
					cellColors[j][conv.x] = new Color(c.getRed(), c.getGreen(),
							c.getBlue(), 200 - 50 * closestn);
				}
			}
		}
	}

	public void setEngine(GameEngine eng) {
		engine = eng;
	}

	private boolean editable;

	public void setBoard(Board b, boolean editable) {
		board = b;
		initColors();
		repaint();
		revalidate();
	}

	Line2D selectedLine = null;

}

/* 
 * 	$Id: Board.java,v 1.6 2007/11/28 16:30:18 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package push.sim;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Jon Bell 5+7+9+11+13+11+9+7+5 The board is: еееее ееееее еееееее
 *         ееееееее еееееееее ееееееее еееееее ееееее еееее
 */
public final class Board {
	public GameEngine engine;
	int[][] lastBoard;
	int[][] board;
	Point toExpanded(Point p)
	{
		int x = 0;
		int y = 0;
		
		int length = p.y;
		if (length > 4)
			length = 8 - length;
		int offset = 4 - length;

		x = offset+(p.x)+p.x;
		return new Point(x,p.y);
	}
	void saveBoard()
	{
		lastBoard=new int[9][17];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				lastBoard[i][j] = board[i][j];
			}
		}
	}
	/**
	 * Gets a row of the hexagon, with the top one being 0
	 * 
	 * @param i
	 */
	int[] getRow(int i) {
		return board[i];
//		return r;
	}

	/**
	 * Indexed y,x
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	int getCell(int i, int j) {
		return board[i][j];
	}

	void setCell(int i, int j, int v) {
		board[i][j] = v;
	}

	int[][] queue;

	/**
	 * Moves coins from (u,v) to (i,j)
	 * 
	 * @param i
	 * @param j
	 * @param u
	 * @param v
	 */
	void queueMove(int i, int j, int u, int v) {
		if(queue[j][i] == -100)
			queue[j][i]=board[j][i];
		if(queue[v][u] == -100)
			queue[v][u] =board[v][u];
		queue[j][i] += board[v][u];
		queue[v][u] -=board[v][u];
		board[v][u] = 0;

	}

	public void executeQueue() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 17; j++) {
				if(queue[i][j] >= 0)
				board[i][j] = queue[i][j];
			}
		}
		initQueue();
	}

	private double round(double n) {
		return Math.round(100 * n) / 100;
	}

	public Board() {
		init();
		
	}

	public void initQueue() {
		queue = new int[9][17];
		for(int i=0;i<9;i++)
			for(int j=0;j<17;j++)
				queue[i][j]=-100;
	}

	public void init() {
		board = new int[9][17];
		for(int i=0;i<9;i++)
			for(int j=0;j<17;j++)
				board[i][j]=0;
		for(int j=0;j<9;j++)
		{
			int length = j;
			if (length > 4)
				length = 8 - length;
			int offset = 4 - length;
			length += 5;
			for(int i=0;i<length;i++)
			{
				board[j][offset+i*2]=1;
			}
		}
		
		initQueue();
	}

}

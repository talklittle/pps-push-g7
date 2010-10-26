/*
 * 	$Id: ConfigurationPanel.java,v 1.3 2007/11/14 22:00:22 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package push.sim;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public final class ConfigurationPanel extends JPanel implements ChangeListener, ItemListener, ListSelectionListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	private GameConfig config;

	static Font config_font = new Font("Arial", Font.PLAIN, 14);

	private JLabel roundLabel;
	private JSpinner roundSpinner;
	
	private JList scoresList;
	

	private JLabel playerLabel;
	private JComboBox playerBox;
	private JButton remove;
	private JButton add;
	private JList selectedPlayers;

	private Class<Player> selectedPlayer;

	private JSlider speedSlider;
	protected JLabel interactiveHelp;
	
	public ConfigurationPanel(GameConfig config)
	{
		this.config = config;

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Configuration"));
		this.setPreferredSize(new Dimension(350, 1200));
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(layout);

		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		JPanel panel = new JPanel(new FlowLayout());
		

		roundLabel = new JLabel("Number of Rounds: ");
		roundLabel.setFont(config_font);
		roundSpinner = new JSpinner(new SpinnerNumberModel(this.config.getMaxRounds(), 1, null, 1));
		roundSpinner.setPreferredSize(new Dimension(120, 25));
		roundSpinner.addChangeListener(this);	
		panel.add(roundLabel);
		panel.add(roundSpinner);
		layout.setConstraints(panel, c);
		this.add(panel);
		
		panel = new JPanel();
		BoxLayout layout2 = new BoxLayout(panel,BoxLayout.Y_AXIS);
		panel.setLayout(layout2);
		
		playerLabel = new JLabel("Select Player:");
		panel.add(playerLabel);
		// make player combo box
		playerBox = new JComboBox(config.getPlayerListModel());
		playerBox.addItemListener(this);
		playerBox.setRenderer(new ClassRenderer());
		panel.add(playerBox);
		add = new JButton("Add to playerslist");
		add.addActionListener(this);
		panel.add(add);
		selectedPlayers = new JList();
		selectedPlayers.setMinimumSize(new Dimension(120, 100));
		selectedPlayers.setCellRenderer(new ClassRenderer());
		selectedPlayers.setModel(new DefaultListModel());
		panel.add(selectedPlayers);
		remove = new JButton("Remove selected player");
		remove.addActionListener(this);
		panel.add(remove);
		layout.setConstraints(panel, c);
		this.add(panel);
		
		panel = new JPanel(new FlowLayout());
		JLabel scoresLabel = new JLabel("Scores:");
		panel.add(scoresLabel);
		scoresList = new JList();
		scoresList.setCellRenderer(new ListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				JLabel l = new JLabel();
				Score s = (Score) value;
				l.setText(s.playerIndex + ": " + s.playerName + " - " + s.score);
				Font font = new Font(l.getFont().getName(),Font.PLAIN,14);
				l.setFont(font);
				l.setOpaque(true);
				l.setBackground(BoardPanel.playerColors[s.playerIndex]);
				return l;
			}
		});
		panel.add(scoresList);
		layout.setConstraints(panel, c);
		this.add(panel);
//		config.setPlayerClass((Class<Player>) playerBox.getSelectedItem());

		panel = new JPanel(new FlowLayout());
		speedSlider = new JSlider(0, 1000);
		speedSlider.setValue(0);
		panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("Delay (0 - 1000ms):"));
		panel.add(speedSlider);
		layout.setConstraints(panel, c);
		this.add(panel);
			
		
	}

	
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		roundSpinner.setEnabled(enabled);
		playerBox.setEnabled(enabled);
	}

	public void stateChanged(ChangeEvent arg0)
	{
		if (arg0.getSource().equals(roundSpinner))
			config.setMaxRounds(((Integer) ((JSpinner) arg0.getSource()).getValue()).intValue());
		else
			throw new RuntimeException("Unknown State Changed Event!!");
	}

	public void itemStateChanged(ItemEvent arg0)
	{

	}

	
	public JSlider getSpeedSlider()
	{
		return speedSlider;
	}

	public void valueChanged(ListSelectionEvent e)
	{
	}
	public void setScores(ArrayList<Integer> scores,ArrayList<Player> players)
	{
		DefaultListModel mod = new DefaultListModel();
		for(int i=0;i<6;i++)
		{
			Score s = new Score();
			s.playerIndex = i;
			s.playerName = players.get(i).getName();
			s.score = scores.get(i);
			mod.addElement(s);
		}
		scoresList.setModel(mod);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultListModel m = (DefaultListModel) selectedPlayers.getModel();
		if(e.getSource() == add)
		{
			if(m.size() == 6)
			{
				JOptionPane.showMessageDialog(this, "Error: You have already added 6 players. Please remove one before adding another.");
			}
			else
				m.addElement(playerBox.getSelectedItem());
		}
		else if(e.getSource() == remove)
		{	
			m.removeElement(selectedPlayers.getSelectedValue());
		}
		ArrayList<Class<Player>> ret = new ArrayList<Class<Player>>();
		for(int i = 0; i<m.size();i++)
		{
			ret.add((Class<Player>) m.get(i));
		}
		config.setSelectedPlayers(ret);
	}
}
class Score
{
	int playerIndex;
	String playerName;
	int score;
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package push.sim;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;


import org.apache.log4j.Logger;

public class GameConfig {

	int gameDelay = 500;
	int number_of_rounds;
	int current_round;
	public int num_mosquitos = 1000;
	int max_rounds = 100;
	private ArrayList<Class<Player>> availablePlayers;
	private ArrayList<Class<Player>> selectedPlayers;
	public static Random random;
	private Properties props;
	private String confFileName;
	private Logger log = Logger.getLogger(this.getClass());
	int num_lights = 5;

	public ArrayList<Class<Player>> getSelectedPlayers() {
		return selectedPlayers;
	}
	public void setSelectedPlayers(ArrayList<Class<Player>> selectedPlayers) {
		this.selectedPlayers = selectedPlayers;
	}
	public void setMaxRounds(int v) {
		this.max_rounds = v;
	}

	public int getMaxRounds() {
		return max_rounds;
	}

	public GameConfig(String filename) {
		confFileName = filename;
		props = new Properties();
		availablePlayers = new ArrayList<Class<Player>>();
		load();
	}

	/**
	 * Read in configuration file.
	 * 
	 * @param file
	 */
	public void load() {
		try {
			FileInputStream in = new FileInputStream(confFileName);
			props.loadFromXML(in);
		} catch (IOException e) {
			System.err.println("Error reading configuration file:"
					+ e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		extractProperties();
	}

	/**
	 * Get the game configuration parameters out of the Property object.
	 * 
	 */
	private void extractProperties() {
		String s;

		// READ IN CLASSES
		s = props.getProperty("push.classes");
		if (s != null) {
			String[] names = s.split(" ");
			for (int i = 0; i < names.length; i++) {
				try {
					if(names[i] != "")
					availablePlayers.add((Class<Player>) Class
							.forName(names[i]));
				} catch (ClassNotFoundException e) {
					log.error("[Configuration] Class not found: " + names[i]);
				}
			}
		}
		File sourceFolder = new File("bin"+System.getProperty("file.separator")+"push"+System.getProperty("file.separator"));
		for(File f : sourceFolder.listFiles())
		{

			if(f.getName().length() == 2 && f.getName().substring(0,1).equals("g"))
			{
				for(File c : f.listFiles())
				{
					if(c.getName().endsWith(".class") ){
						String className = c.toString().replace(System.getProperty("file.separator"),".").replace("bin.","");						className = className.substring(0, className.length() - 6);
						 Class theClass = null;
				          try{
				            theClass = Class.forName(className, false,this.getClass().getClassLoader());
				            if(theClass.getSuperclass() != null && theClass.getSuperclass().toString().equals("class push.sim.Player"))
				            {
				            	if(!availablePlayers.contains((Class<Player>) theClass))
				            		availablePlayers.add((Class<Player>) theClass);
				            }
				          }catch(NoClassDefFoundError e){
				            continue;
				          } catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							continue;
						}

					}
					else if(c.isDirectory())
					{
						for(File ca : c.listFiles())
						{
							if(ca.getName().endsWith(".class") ){
								String className = ca.toString().replace(c.toString(),"").replaceAll("/", ".");
								className = className.substring(0, className.length() - 6);
								 Class theClass = null;
						          try{
						            theClass = Class.forName(className, false,this.getClass().getClassLoader());
						            if(theClass.getSuperclass() != null && theClass.getSuperclass().toString().equals("class mosquito.sim.Player"))
						            {
						            	if(!availablePlayers.contains((Class<Player>) theClass))
						            		availablePlayers.add((Class<Player>) theClass);
						            }
						          }catch(NoClassDefFoundError e){
						            continue;
						          } catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									continue;
								}

							}
							else if(c.isDirectory())
							{
								
							}
						}
					}
				}
			}
		}
		if (availablePlayers.size() == 0)
			log.fatal("No player classes loaded!!!");
		if (availablePlayers.size() == 0)
			log.fatal("No player classes loaded!!!");
		if(props.getProperty("push.seed") != null)
		{
			long seed = Long.valueOf(props.getProperty("push.seed"));
			random = new Random(seed);
		}
		else
			random = new Random();
	}

	

	public ComboBoxModel getPlayerListModel() {
		DefaultComboBoxModel m = new DefaultComboBoxModel();
		for (Class c : availablePlayers) {
			m.addElement(c);
		}
		return m;
	}

	public int getNumMosquitos() {
		// TODO Auto-generated method stub
		return num_mosquitos;
	}

	public void setNumMosquitos(int intValue) {
		num_mosquitos = intValue;
	}

}

package org.lerot.RaceResults;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.lerot.mywidgets.jswHorizontalPanel;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswPanel;
import org.lerot.mywidgets.jswPushButtonset;
import org.lerot.mywidgets.jswStyle;
import org.lerot.mywidgets.jswStyles;
import org.lerot.mywidgets.jswVerticalPanel;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertBluer;


public class raceresults extends JFrame implements ActionListener
{
	
	private static raceresults mframe;
	private static String version;
	private static boolean started;
	private static jswStyles tablestyles;
	private static jswStyles panelstyles;

	public static void main(String[] args)
	{
		try
		{
			PlasticLookAndFeel.setPlasticTheme(new DesertBluer());
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (Exception e)
		{
		}
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);

		mframe = new raceresults(900, 700);
		mframe.setVisible(true);

		mframe.addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		mframe.getContentPane().setLayout(
				new BoxLayout(mframe.getContentPane(), BoxLayout.X_AXIS));
		Dimension actual = new Dimension();
		actual.width = 900;
		actual.height = 700;
		mframe.setSize(actual);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - mframe.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - mframe.getHeight()) / 2);
		mframe.setLocation(x, y);

	}

	
	public String edattributename;
	public Font footnotefont;
	public Font headingfont;
	public Font promptfont;

	public jswPanel mainpanel;
	public String mode = "main";
	private String os;
	private String osversion;
	public String user;
	String userdir;
	private String userhome;
	public String username;
	public String view = "main";
	public String dotcontacts;
	private ImageIcon jstatIcon;
	public Properties props;
	public String propsfile;
	private String dbtitle;
	public String dbsource;
	public String docs;
	private jswLabel title;
	private jswLabel source;
	public String budir;
	private String dotresults;
	private String docsfolder;
	private raceresults topgui;
	private jswVerticalPanel bigpanel;
	private datasource currentcon;

	public raceresults(int w, int h)
	{
		super("RaceResults " + version);

		userdir = System.getProperty("user.dir");
		userhome = System.getProperty("user.home");
		user = System.getProperty("user.name");
		osversion = System.getProperty("os.version");
		os = System.getProperty("os.name");
		if (os.startsWith("Windows"))
		{
			dotresults = "C:/Users/" + user + "/.raceresults/";

		} else
		{
			dotresults = "/home/" + user + "/.raceresults/";

			docsfolder = "/home/" + user + "/Raceresults/";
		}
		java.net.URL jstatIconURL = ClassLoader.getSystemClassLoader()
				.getResource("raceresults.png");

		if (jstatIconURL != null)
		{
			jstatIcon = new ImageIcon(jstatIconURL);
			Image jstatIconImage = jstatIcon.getImage();
			this.setIconImage(jstatIconImage);
			new Dimension(jstatIcon.getIconWidth() + 2,
					jstatIcon.getIconHeight() + 2);
		} else
			System.out.println("no icon");
		System.out.println("user :" + user);
		System.out.println("user directory :" + userdir);
		System.out.println("operating system :" + os + "(" + osversion + ")");
		propsfile = dotresults + "properties.xml";
		props = readProperties(propsfile);
		dbsource = props.getProperty("database", "mcdb.sqlite");
		budir = props.getProperty("backxupdirectory", dotcontacts + "backups");
		docs = props.getProperty("docs", "Documents/correspondance/");
		currentcon = new datasource(dotcontacts + dbsource);

		topgui = this;
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		initiateStyles();

		bigpanel = new jswVerticalPanel("bigpanel", true);
		bigpanel.setBorder(BorderFactory.createLineBorder(Color.blue));
		bigpanel.setName("bigpanel");
		bigpanel.setPreferredSize(new Dimension(800, 500));
		bigpanel.setSize(new Dimension(800, 500));
		bigpanel.setMinimumSize(new Dimension(800, 500));
		getContentPane().add(bigpanel);
		jswHorizontalPanel optionBar = new jswHorizontalPanel();
		jswPushButtonset buttonset = new jswPushButtonset(this, "mode", false, false);
		buttonset.setBorder(jswStyle.makeLineBorder(Color.red, 1));
		buttonset.addNewOption("Browse");
		buttonset.addNewOption("Search");
		buttonset.addNewOption("Edit");
		buttonset.addNewOption("Tools");
		buttonset.setSelected("Browse");
		optionBar.add(" ", buttonset);
		optionBar.setBorder(jswStyle.makeLineBorder(Color.pink, 1));
		bigpanel.add(" ", optionBar);
		jswHorizontalPanel sourceBar = new jswHorizontalPanel();
		title = new jswLabel(dbtitle);
		sourceBar.add(title);
		source = new jswLabel(dbsource);
		sourceBar.add(source);
		sourceBar.setBorder(jswStyle.makeLineBorder(Color.GREEN, 1));
		bigpanel.add(" height=40 ", sourceBar);
		//selbox = new selectorBox(this, this);
		//bigpanel.add("FILLW", selbox);
		mainpanel = new jswVerticalPanel("mainpanel", false);
		bigpanel.add(" FILLH ", mainpanel);
		bigpanel.setBorder(jswStyle.makeLineBorder(Color.GRAY, 3));
		/*abrowsepanel = new browsePanel();
		mainpanel.add(" FILLH ", abrowsepanel);
		asearchpanel = new searchPanel();
		asearchpanel.makesearchPanel(selbox, this);
		aneditpanel = new editPanel();
		selbox.setEnabled(true);*/
		startup();
		/*toolspanel = new ToolsPanel(this);
		selbox.setTaglist();
		selbox.refreshAllContacts("1");
		refreshView();
		initDragAndDrop();*/
		raceresults.started = true;
	}


	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		System.out.println(" mcdb action :" + action);
		if (action.startsWith("MODE:"))
		{
			String vstr = action.substring(5);
			mode = vstr;
			if (mode.equalsIgnoreCase("tools"))
			{

			}

		} else if (action.startsWith("VIEW:"))
		{
			if (!mode.equalsIgnoreCase("EDIT")) mode = "BROWSE";
		} else
			System.out.println("action  " + action + " unrecognised in main ");
		
	}

	public void startup()
	{
		System.out.println(os + " " + userhome);
		System.out.println("opening :" + dbsource);
		Map<String, String> mychecks = currentcon.checkmcdb();
		if (mychecks.get("Valid").equalsIgnoreCase("yes"))
			System.out.println("Checks ok");
		else
			System.out.println("Checks NOT ok");
		String ncon = mychecks.get("No of Contacts");
		dbtitle = mychecks.get("Title");
		System.out.println("title " + dbtitle);
		title.setText(dbtitle);
		source.setText(" (" + dbsource + ")");
	
	}

	

	public static void initiateStyles()
	{
		tablestyles = jswStyles.getDefaultStyles();
		tablestyles.name = "table";
		jswStyle tablestyle = tablestyles.makeStyle("table");
		tablestyle.putAttribute("background", "red");
		jswStyle rowstyle = tablestyles.makeStyle("row");
		rowstyle.putAttribute("height", "10");
		jswStyle col0style = tablestyles.makeStyle("col_0");
		col0style.putAttribute("fontStyle", Font.BOLD);
		col0style.setHorizontalAlign("RIGHT");
		col0style.putAttribute("minwidth", "true");
		jswStyle col1style = tablestyles.makeStyle("col_1");
		col1style.putAttribute("fontStyle", Font.BOLD);
		col1style.setHorizontalAlign("RIGHT");
		jswStyle col2style = tablestyles.makeStyle("col_2");
		col2style.putAttribute("horizontalAlignment", "RIGHT");
		col2style.putAttribute("minwidth", "true");

		panelstyles = jswStyles.getDefaultStyles();
		panelstyles.name = "panel";
		jswStyle jswWidgetStyles = panelstyles.makeStyle("jswWidget");
		jswWidgetStyles.putAttribute("backgroundColor", "#e0dcdf");
		jswWidgetStyles.putAttribute("boxbackgroundColor", "GREEN");
		jswWidgetStyles.putAttribute("foregroundColor", "Black");
		jswWidgetStyles.putAttribute("borderWidth", "0");
		jswWidgetStyles.putAttribute("fontsize", "14");
		jswWidgetStyles.putAttribute("borderColor", "blue");

		jswStyle jswLabelStyles = panelstyles.makeStyle("jswLabel");
		jswStyle largelabelStyle = panelstyles.makeStyle("largeLabel");
		largelabelStyle.putAttribute("fontsize", "30");
		largelabelStyle.putAttribute("foregroundColor", "Red");

		jswStyle jswButtonStyles = panelstyles.makeStyle("jswButton");
		jswButtonStyles.putAttribute("fontsize", "10");
		

		jswStyle jswToggleButtonStyles = panelstyles
				.makeStyle("jswToggleButton");
		jswToggleButtonStyles.putAttribute("foregroundColor", "Red");

		jswStyle jswTextBoxStyles = panelstyles.makeStyle("jswTextBox");

		jswStyle jswTextFieldStyles = panelstyles.makeStyle("jswTextField");
		// jswTextFieldStyles.putAttribute("backgroundColor", "#e0dcdf");

		jswStyle jswDropDownBoxStyles = panelstyles.makeStyle("jswDropDownBox");
		// jswDropDownBoxStyles.putAttribute("backgroundColor","#C0C0C0");

		jswStyle jswhpStyles = panelstyles.makeStyle("jswContainer");
		jswhpStyles.putAttribute("backgroundColor", "#C0C0C0");

		jswStyle jswDropDownContactBoxStyles = panelstyles
				.makeStyle("jswDropDownContactBox");
		jswDropDownContactBoxStyles.putAttribute("backgroundColor", "#C0C0C0");
		jswDropDownContactBoxStyles.putAttribute("fontsize", "10");

		jswStyle jswScrollPaneStyles = panelstyles
				.makeStyle("jswScrollPaneStyles");
		jswScrollPaneStyles.putAttribute("backgroundColor", "#C0C0C0");
		jswScrollPaneStyles.putAttribute("fontsize", "10");

		jswStyle jswBorderStyle = panelstyles.makeStyle("borderstyle");
		jswBorderStyle.putAttribute("borderWidth", "1");
		// jswBorderStyle.putAttribute("borderColor", "#C0C0C0");
		jswBorderStyle.putAttribute("borderColor", "black");

		jswStyle hpanelStyle = panelstyles.makeStyle("hpanelstyle");
		hpanelStyle.putAttribute("borderWidth", "2");
		hpanelStyle.putAttribute("borderColor", "blue");
		hpanelStyle.putAttribute("height", "100");

		jswStyle pbStyle = panelstyles.makeStyle("jswPushButton");
		pbStyle.putAttribute("backgroundColor", "#C0C0C0");
		pbStyle.putAttribute("fontsize", "10");

		pbStyle.putAttribute("foregroundColor", "black");
		jswStyle greenfont = panelstyles.makeStyle("greenfont");
		greenfont.putAttribute("foregroundColor", "green");

	}

	public java.util.Properties readProperties(String propsfile)
	{
		Properties prop = new Properties();
		try
		{
			System.out.println(propsfile);
			prop.loadFromXML(new FileInputStream(propsfile));
			return prop;
		} catch (InvalidPropertiesFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	

	
	
}

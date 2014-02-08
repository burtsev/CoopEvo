import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class SimulationControl extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton showMap = new JButton();
  JButton hideMap = new JButton();

  public static boolean showM;

  public SimulationControl(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public SimulationControl() {
    this(null, "", false);
    showM = false;
  }
  void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    showMap.setText("Start map");
    showMap.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        showMap_mouseClicked(e);
      }
    });
    hideMap.setText("Stop map");
    hideMap.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        hideMap_mouseClicked(e);
      }
    });
    this.setTitle("Control panel");

    getContentPane().add(panel1, BorderLayout.CENTER);
    panel1.add(hideMap,  BorderLayout.EAST);
    panel1.add(showMap, BorderLayout.WEST);
  }

  void showMap_mouseClicked(MouseEvent e) {
showM = true;
  }

  void hideMap_mouseClicked(MouseEvent e) {
showM = false;
  }
  public void stopCtrl(){
    showMap.removeAll();
    hideMap.removeAll();
  }
  }

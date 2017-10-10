/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sampleui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import static sun.misc.ClassFileTransformer.add;

/**
 *
 * @author Prateek_Sharma
 */
public class DemoJFileChooser extends JPanel
   implements ActionListener {
   JButton go;
   
   JFileChooser chooser;
   String choosertitle;
   
  public DemoJFileChooser() {
    go = new JButton("Do it");
    go.addActionListener(this);
    add(go);
   }

  public void actionPerformed(ActionEvent e) {
    int result;
        
    chooser = new JFileChooser(); 
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle(choosertitle);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    //
    // disable the "All files" option.
    //
    chooser.setAcceptAllFileFilterUsed(false);
    //    
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
      System.out.println("getCurrentDirectory(): " 
         +  chooser.getCurrentDirectory());
      System.out.println("getSelectedFile() : " 
         +  chooser.getSelectedFile());
      }
    else {
      System.out.println("No Selection ");
      }
     }
   
  public Dimension getPreferredSize(){
    return new Dimension(200, 200);
    }
}

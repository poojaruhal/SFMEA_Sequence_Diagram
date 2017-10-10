/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sampleui;

import java.awt.Label;
import java.io.File;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import jxl.Workbook;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 *
 * @author Prateek_Sharma
 */
public class ExcelExporter {
    
    void fillData(JTable table, File file){
        
        
        try{
            WritableWorkbook workbook1 = Workbook.createWorkbook(file);
            WritableSheet sheet1 = workbook1.createSheet("First Sheet", 0);
            TableModel model = table.getModel();
            
            WritableFont arial10font = new WritableFont(WritableFont.ARIAL,10);
            WritableCellFormat arial10format = new WritableCellFormat(arial10font);
            
            for(int i=0;i<model.getColumnCount();i++){
                jxl.write.Label column = new jxl.write.Label(i,0,model.getColumnName(i));
                sheet1.addCell(column);                
            }
            int j=0;
            for(int i=0;i<model.getRowCount();i++){
                for(j=0;j<model.getColumnCount();j++){
                    jxl.write.Label row = new jxl.write.Label(j,i+1,model.getValueAt(i, j).toString());
                    sheet1.addCell(row);              
                }               
            }
            workbook1.write();
            workbook1.close();           
        }
        catch(Exception ex){
            ex.printStackTrace();
        }       
    }    
}

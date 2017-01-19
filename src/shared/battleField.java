/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import java.util.Hashtable;
import javax.swing.JTable;

/**
 *
 * @author al1as
 */
public class battleField {
    final int maxShipSize = 4;
    final int countShipsBySize[] = {0, 4, 3, 2, 1};
    Hashtable<Cell, Ship> field = new Hashtable<Cell, Ship>();
    
    public boolean initByTable(JTable table) {
        boolean res = true;
        for(int i = 1; i < 11; i++)
            for(int j = 1; j < 11; j++) {
                Object value = (Object)table.getModel().getValueAt(i, j);
                String content = "";
                if(value != null)
                    content = value.toString();
                if(content.equals("*")) {
                    Cell c = new Cell(i, j);
                    Ship sh;
                    if(field.get(c) == null) {
                        sh = new Ship();
                        field.put(c, sh);
                        scanNear(c, sh, table);
                    } 
                }
            }
        return true; // signals if field correct or not
    }
   
    private boolean scanByColumn(Cell c, Ship sh, JTable table) {
        if (c.row + 1 > 10) {
            return false;
        }
        Cell newCell = new Cell(c.row + 1, c.column);
        String content = "";
        Object value = (Object)table.getModel().getValueAt(newCell.row, newCell.column);
        content = "";
        if (value != null) {
            content = value.toString();
        }
        boolean res = false;
        while(content.equals("*") && sh.getSize() < maxShipSize && newCell.row < 11) {
            sh.incSize();
            field.put(newCell, sh);
            res = true;
            
            newCell = new Cell(newCell.row + 1, newCell.column);
            value = (Object) table.getModel().getValueAt(newCell.row, newCell.column);
            content = "";
            if (value != null) {
                content = value.toString();
            }
        }
        return res;
    }
    
    private boolean scanByRow(Cell c, Ship sh, JTable table) {
        if(c.column + 1 > 10)
            return false;
        Cell newCell = new Cell(c.row, c.column + 1);
        String content = "";
        Object value = (Object)table.getModel().getValueAt(newCell.row, newCell.column);
        content = "";
        if (value != null) {
            content = value.toString();
        }
        boolean res = false;
        while(content.equals("*") && sh.getSize() < maxShipSize && newCell.column < 11) {
            sh.incSize();
            field.put(newCell, sh);
            res = true;
            
            newCell = new Cell(newCell.row, newCell.column + 1);
            value = (Object) table.getModel().getValueAt(newCell.row, newCell.column);
            content = "";
            if (value != null) {
                content = value.toString();
            }
        }
        return res;
    }
    private void scanNear(Cell c, Ship sh, JTable table) {
        if(!scanByRow(c, sh, table))
            scanByColumn(c, sh, table);
    }
    
}
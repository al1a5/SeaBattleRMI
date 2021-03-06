/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_additional;

import java.io.Serializable;
import java.util.Hashtable;
import javax.swing.JTable;

/**
 *
 * @author al1as
 */
public class battleField implements Serializable{
    final int maxShipSize = 4;
    final int maxCountShipsBySize[] = {0, 4, 3, 2, 1}; 
    int countShipsBySize[] = {0, 0, 0, 0, 0};
    Hashtable<Cell, Ship> field = new Hashtable<Cell, Ship>();
    
    public boolean initByTableData(Object[][] data) {
        for(int i = 1; i < 11; i++)
            for(int j = 1; j < 11; j++) {
                Object value = data[i][j];
                String content = "";
                if(value != null)
                    content = value.toString();
                if(content.equals("◼")) {
                    Cell c = new Cell(i, j);
                    Ship sh;
                    if(field.get(c) == null) {
                        sh = new Ship();
                      //  field.put(c, sh);
                        if(!scanNear(c, sh, data))
                            return false;
                    } 
                }
            }
        for(int i = 1; i < 5; i++)
            if(countShipsBySize[i] != maxCountShipsBySize[i])
                return false;
        return true;
    }
    
    public Ship getAndRemoveShip(int row, int column) {
        Cell tmpCell = new Cell(row, column);
        Ship tmpShip = field.get(tmpCell);
        if(tmpShip != null) {
            // may be error here (not decreasing in hash, but decreasing in object here)
            tmpShip.decSize();
            field.remove(tmpCell);
        }
        return tmpShip;
    }
    
    public boolean isEmpty() {
        return (field.size() == 0);
    }
   
    private int scanByColumn(Cell c, Ship sh, Object[][] data) {
        int res = 0; // ship by this direction not found
        String content = "◼";
        Object value;
        if(c.row > 1 && c.column > 1) {
            Cell tmpCell = new Cell(c.row-1, c.column-1);
            if(field.get(tmpCell) != null)
                return -1;
        }
        if(c.row > 1) {
            Cell tmpCell = new Cell(c.row-1, c.column);
            if(field.get(tmpCell) != null)
                return -1;
        }
        if(c.row > 1 && c.column < 10) {
            Cell tmpCell = new Cell(c.row-1, c.column+1);
            if(field.get(tmpCell) != null)
                return -1;
        }
        while(content.equals("◼") && sh.getSize() < maxShipSize && c.row < 11) {
            if (c.column > 1) {
                Cell tmpCell = new Cell(c.row, c.column-1);
                if (field.get(tmpCell) != null) {
                    return -1;
                }
            }
            if (c.column > 10) {
                Cell tmpCell = new Cell(c.row, c.column+1);
                if (field.get(tmpCell) != null) {
                    return -1;
                }
            }
            sh.incSize();
            field.put(c, sh);
            res = sh.getSize(); //found ship
            // res = -1 if error and return
            
            c = new Cell(c.row + 1, c.column);
            if(c.row >= 11)
                break;
            else {
                value = data[c.row][c.column];
                content = "";
                if (value != null) {
                    content = value.toString();
                }
            }
        }
        return res;
    }
    
    private int scanByRow(Cell c, Ship sh, Object[][] data) {
        int res = 0; // ship by this direction not found
        String content = "◼";
        Object value;
        if(c.row > 1 && c.column > 1) {
            Cell tmpCell = new Cell(c.row-1, c.column-1);
            if(field.get(tmpCell) != null)
                return -1;
        }
        while(content.equals("◼") && sh.getSize() < maxShipSize && c.column < 11) {
            if(c.row > 1 && c.column < 10) {
                Cell tmpCell = new Cell(c.row-1, c.column);
                if(field.get(tmpCell) != null)
                    return -1;
                tmpCell.column += 1;
                if(field.get(tmpCell) != null)
                    return -1;
            }
            sh.incSize();
            field.put(c, sh);
            if(sh.getSize() > 1)
                res = sh.getSize(); //found ship
            // res = -1 if error and return
            
            c = new Cell(c.row, c.column + 1);
            if(c.column >= 11)
                break;
            else {
                value = data[c.row][c.column];
                content = "";
                if (value != null) {
                    content = value.toString();
                }
            }
        }
        return res;
    }
    private boolean scanNear(Cell c, Ship sh, Object[][] data) {
        int flag = scanByRow(c, sh, data);
        if(flag == -1)
            return false;
        else
            if(flag == 0) {
                sh.decSize(); // scanByRow increased this value
                flag = scanByColumn(c, sh, data);
                if(flag == -1)
                    return false;
            }
        // flag - размер текущего корабля
        countShipsBySize[flag]++;
        if(countShipsBySize[flag] > maxCountShipsBySize[flag])
            return false;
        return true;
    }
    
}
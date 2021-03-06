/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.rmi.*;
import java.rmi.server.*;
import client.IClientService;
import java.net.MalformedURLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import server_additional.Ship;
import server_additional.battleField;
/**
 *
 * @author al1as
 */
public class ServerConsole extends UnicastRemoteObject implements IServerConsole {

    final int playersMaxCount = 2;
    int playersConnected = 0;
    int victimId;
    battleField[] playersBF;
    int countBF = 0;
    IClientService[] clientObjects;
    
    
    public ServerConsole() throws Exception {
        // Server initialization here
        System.out.println("Initializing SeaBattleRMI server...");
        String serviceName = "rmi://localhost/SeaBattleRMIServerService";
        Naming.rebind(serviceName, this);
        System.out.println("Server object binded");
        
        playersBF = new battleField[2];
    }   
     
    
    @Override
    public int registerClient(String clientId) throws RemoteException {
        if(playersConnected > playersMaxCount)
            return -1;
        String objectName = "rmi://localhost/SeaBattleRMIClientService" + clientId;
        if(playersConnected == 0)
            clientObjects = new IClientService[2];
        try {
            clientObjects[playersConnected] = (IClientService) Naming.lookup(objectName);
            System.out.println("Connected client with id:" + clientId);
            System.out.println("His internal id is:" + String.valueOf(playersConnected));
            playersConnected++;
        } catch (NotBoundException ex) {
            Logger.getLogger(ServerConsole.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServerConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
        return playersConnected-1;
    }
    
    @Override
    public boolean sendTableData(Object[][] data, int playerId) throws RemoteException {
        playersBF[playerId] = new battleField();
        boolean approved = playersBF[playerId].initByTableData(data);
        if(!approved) // not appropriate field
            return false;
        // giving the first turn
        if(++countBF == playersMaxCount) {
            Random rn = new Random();
            int firstTurnPlayerId = rn.nextInt(playersConnected);
            clientObjects[firstTurnPlayerId].getReadyForTurn();
            victimId = playersConnected - firstTurnPlayerId - 1;
        }
        return true;
    }
    
    public int makeTurn(int row, int column) throws RemoteException {
        // returns 0 if ship is destroyed, 1 if ship is damaged and -1 if miss
        int res = -1;
        Ship currentShip = playersBF[victimId].getAndRemoveShip(row, column);
        if(currentShip != null) {
            if(currentShip.getSize() == 0)
                res = 0;
            else
                res = 1;
        }
        // send info to victim
        clientObjects[victimId].takeDamage(row, column, res);
        // is he winner?
        if(playersBF[victimId].isEmpty()) {
            clientObjects[victimId].makeLooser();
            clientObjects[playersMaxCount - victimId - 1].makeWinner();
        } else {
            // choose whose next turn
            int nextAttacker = playersMaxCount - victimId - 1;
            if(res < 0) {
                nextAttacker = victimId;
                victimId = playersMaxCount - nextAttacker - 1;  
            }
            clientObjects[nextAttacker].getReadyForTurn();    
        }
        return res;
    }
    
    public Object[][] getEnemyFiled(int enemyId) throws RemoteException {
        Object[][] data = clientObjects[enemyId].getTableData();
        return data;
    }
    
    public static void main (String[] args) throws Exception {
        ServerConsole server = new ServerConsole();  
    }    
}

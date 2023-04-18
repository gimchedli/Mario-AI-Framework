import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import engine.core.MarioGame;
import engine.core.MarioResult;

//import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;

//import javax.naming.spi.DirStateFactory.Result;
//import javax.swing.JFrame;

import com.opencsv.CSVWriter;

//import java.io.Writer;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.io.IOException;


public class RunExperiment {
    public static void printResults(MarioResult result) {
        System.out.println("****************************************************************");
        System.out.println("Game Status: " + result.getGameStatus().toString() +
                " Percentage Completion: " + result.getCompletionPercentage());
        System.out.println("Lives: " + result.getCurrentLives() + " Coins: " + result.getCurrentCoins() +
                " Remaining Time: " + (int) Math.ceil(result.getRemainingTime() / 1000f));
        System.out.println("Mario State: " + result.getMarioMode() +
                " (Mushrooms: " + result.getNumCollectedMushrooms() + " Fire Flowers: " + result.getNumCollectedFireflower() + ")");
        System.out.println("Total Kills: " + result.getKillsTotal() + " (Stomps: " + result.getKillsByStomp() +
                " Fireballs: " + result.getKillsByFire() + " Shells: " + result.getKillsByShell() +
                " Falls: " + result.getKillsByFall() + ")");
        System.out.println("Bricks: " + result.getNumDestroyedBricks() + " Jumps: " + result.getNumJumps() +
                " Max X Jump: " + result.getMaxXJump() + " Max Air Time: " + result.getMaxJumpAirTime());
        System.out.println("****************************************************************");
    }


    public static String[] writeDataLineByLine(File file, MarioResult result, String level, int marioType)
    {
        // first create file object for file placed at location
        // specified by filepath
        String[] data = {level, String.valueOf(marioType), String.valueOf(result.getCompletionPercentage()), String.valueOf((int) Math.ceil(result.getRemainingTime() / 1000f)), 
            String.valueOf(result.getKillsTotal()), String.valueOf(result.aliveOrDead()), String.valueOf(result.getDistanceTraversed())};
        if(result.aliveOrDead()==false){System.out.println("oopsie");}
        return data;
    }

    public static File createCSV(String filePath) {
        File file = new File(filePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = { "level", "progress", "time left", "kills" };
            
            writer.writeNext(header);          
            
            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            
            e.printStackTrace();
        }

        return file;
    }



    



    public static String getLevel(String filepath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
        }
        return content;
    }

    public static void main(String[] args) throws Exception{
        MarioGame game = new MarioGame();
        // printResults(game.playGame(getLevel("../levels/original/lvl-1.txt"), 200, 0));
        // printResults(game.runGame(new agents.robinBaumgarten.Agent(), getLevel("./levels/original/lvl-1.txt"), 20, 0, true));        // THIS IS A STAR
        // printResults(game.runGame(new agents.andySloane.Agent(), getLevel("./levels/ge/lvl-21.txt"), 20, 0, true));                  //  THIS IS TBA STAR
        // printResults(game.runGame(new agents.sergeyKarakovskiy.Agent(), getLevel("./levels/ge/lvl-21.txt"), 20, 0, true));           // THIS IS JUST JUMPING FORWARD

        // printResults(game.runGame(new agents.sergeyPolikarpov.Agent(), getLevel("./levels/ge/lvl-21.txt"), 20, 0, true));  ?????????

        // printResults(game.runGame(new agents.spencerSchumann.Agent(), getLevel("./levels/ge/lvl-21.txt"), 20, 0, true));

        String testingagent = "andySloane"; // this will be the name of the generated csv file that contains ressults
        int inputTime = 240;
        

        File file = new File("output/full_runs/" + Integer.toString(inputTime) + "/" + testingagent + ".csv");
            // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
        String[] header = { "Level", "Mario Type", "Progress", "Time Left", "Kills", "Mario Alive", "Distance"};
            
        writer.writeNext(header);


        for(int i = 1; i < 1001; i++) {
            String temp = "./levels/ge/lvl-" + Integer.toString(i) + ".txt" ;
            
            
            for(int j = 0; j < 3; j++){
                /*
                for (int k = 0; k < 6; k++){
                    writer.writeNext(writeDataLineByLine(file, game.runGame(new agents.robinBaumgarten.Agent(), getLevel(temp), 20, j, true), 
                    "notch/LVL-" + Integer.toString(i) + ".txt" , j));
                }**/
                
                writer.writeNext(writeDataLineByLine(file, game.runGame(new agents.andySloane.Agent(), getLevel(temp), inputTime, j, false), 
                "ge/LVL-" + Integer.toString(i) + ".txt" , j));
                //writer.close();              
            }
            System.out.println("Comleted level " + i);
            
            //printResults(game.runGame(new agents.robinBaumgarten.Agent(), getLevel(temp), 20, 2, true));
           
        } 
        //writer.writeNext(writeDataLineByLine(file, game.runGame(new agents.robinBaumgarten.Agent(), getLevel("./levels/ge/lvl-" + Integer.toString(60) + ".txt"), 20, 2, true), 
        //        "GE/LVL-" + Integer.toString(60) + ".txt" , 2)); 
        writer.close();


        // printResults(game.runGame(new agents.trondEllingsen.Agent(), getLevel("./levels/original/lvl-1.txt"), 20, 0, true));

    }
    
}

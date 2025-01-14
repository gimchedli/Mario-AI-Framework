import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import engine.core.MarioAgent;
import engine.core.MarioGame;
import engine.core.MarioResult;
import java.io.File;
import java.io.FileWriter;


import com.opencsv.CSVWriter;




public class RunExperiment {

    public static String[] writeDataLineByLine(File file, MarioResult result, String level, int marioType)
    {
        // first create file object for file placed at location
        // specified by filepath
        String[] data = {level, String.valueOf(marioType), String.valueOf(result.getCompletionPercentage()), String.valueOf((int) Math.ceil(result.getRemainingTime() / 1000f)), 
            String.valueOf(result.getKillsTotal()), String.valueOf(result.aliveOrDead()), String.valueOf(result.getDistanceTraversed())};
        //if(result.aliveOrDead()==false){System.out.println("oopsie");}
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


    public static MarioAgent getCurrentAgent(MarioAgent agent){
        return agent;
    }

    public static void main(String[] args) throws Exception{
        long startTime = System.nanoTime();
        MarioGame game = new MarioGame();

        //You can choose the agent you want to test as well as maximum time per lvl and generated lvl types from here
        MarioAgent currentAgent = new agents.robinBaumgarten.Agent();
        String generatedMapString = "patternOccur";
        int inputTime = 60;

        String str = String.valueOf(currentAgent);
        String[] arrOfStr = str.split("\\.", 3);       
        String testingagent = arrOfStr[1] + "_new_718+"; // this will be the name of the generated csv file that contains ressults
           
        // TODO: Figure out how to check if directory and file exsists
        
        File file = new File("output/full_runs/" + generatedMapString + "/" + Integer.toString(inputTime) + "/" + testingagent + ".csv");

        // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(file);

        // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile);

        // adding header to csv
        String[] header = { "Level", "Mario Type", "Progress", "Time Left", "Kills", "Mario Alive", "Distance"};
            
        writer.writeNext(header);

        
        // If you want to run experiment on original levels you'll have to change maximum i valu to 16 instead of 1001, 
        // since there are only 15 original levels

        for(int i = 722; i < 723; i++) {
            String temp = "./levels/" + generatedMapString + "/lvl-" + Integer.toString(i) + ".txt" ;
            for(int j = 0; j < 3; j++){
 
                writer.writeNext(writeDataLineByLine(file, game.runGame(currentAgent, getLevel(temp), inputTime, j, false, 1000), 
                generatedMapString + "LVL-" + Integer.toString(i) + ".txt" , j));            
            }
            System.out.println("Completed level " + i);
            
           
        }

        writer.close();

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;

        System.out.println(totalTime/1000000000);
    }


    // Experiment runtime:      20s / 60s / 240s
    // Sergei Polikarpov:       27s / 51s / 160s
    // Robin Baumgarten:        6579s / 
    // Andy Sloane:             12533s / 
    
}

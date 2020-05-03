/*
author: Aastha Jhunjhanuwala
University of Florida
Advanced Data Structures
UFID: 55271081
 */

 import java.io.*;
 import java.util.*;

import com.sun.javafx.scene.control.skin.IntegerFieldSkin;
/**
 * Class bplustree is used to run and print commands of the B+ tree execution in a separate output file.
 */
 public class bplustree{
     public static void main(String[] args){
        String inputfile = args[0];
        String inputline = null;
         try{
            //Read input file
            FileReader fileReader = new FileReader(inputfile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Create output file and set output to the file
            PrintStream outputfile = new PrintStream(new File("output_file.txt"));
            System.setOut(outputfile);

            inputline = bufferedReader.readLine();
            BPlusImpl bplusTree = new BPlusImpl();
            
            //Read first line and initialize the tree.
            String[] init = inputline.split("\\(");
            String[] order = init[init.length-1].split("\\)");
            int m = Integer.parseInt(order[0]);
            bplusTree.initialize(m);
            
            //Parse input file line by line and perform corresponding operations.
            while((inputline = bufferedReader.readLine()) != null){
                String[] input = inputline.split("\\(");
                String method  = input[0].toLowerCase();
                String[] params = input[1].split(",");
                String[] lastelem = params[params.length-1].split("\\)");
                params[params.length-1] = lastelem[0];

                if(method.equals("insert")){
                    bplusTree.insert(Integer.parseInt(params[0].trim()), Double.parseDouble(params[1]));
                }
                else if(method.equals("delete")){
                    bplusTree.delete(Integer.parseInt(params[0]));
                }
                else if(method.equals("search") && params.length == 1){
                    bplusTree.search(Integer.parseInt(params[0]));
                }
                else{
                    bplusTree.search(Integer.parseInt(params[0].trim()), Integer.parseInt(params[1]));
                }
            }
            bufferedReader.close();

         }
         //Catch file not found exception
         catch (FileNotFoundException ex){
			System.out.println("Unable to open " + inputfile );
        }
        //Catch all IO exceptions
		catch(IOException ex){
			System.out.println("Error in reading the file " + inputfile );
			ex.printStackTrace();
        }
        //Catch any other exceptions.
        catch(Exception ex){
            System.out.println("Folling error has occured ");
            ex.printStackTrace();
        }
	}
}
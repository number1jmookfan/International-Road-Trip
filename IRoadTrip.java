import java.util.*;
import java.io.*;

public class IRoadTrip {
	
	WorldGraph world;

    public IRoadTrip (String [] args) throws IOException{
    	Scanner borderScan = null; //initialize scanners and graph
    	Scanner capDistScan = null;
    	Scanner stateNameScan = null;
    	world = new WorldGraph();
    	
    	//initlialize files and scanners, if an error is detected it will exit
    	try {
	    	borderScan = new Scanner(new File(args[0]));
	    	capDistScan = new Scanner(new File(args[1]));
	        stateNameScan = new Scanner(new File(args[2]));
    	}
    	catch(Exception e){
    		System.err.println("implementation halted. Reason: " + e);
    		System.exit(0);
    	}	
    	
    	capDistScan.nextLine(); //get rid of useless first info line on both files
    	stateNameScan.nextLine(); 
    	
    	NameChanger nameChanger = new NameChanger(); // initialize namechanger
    	
    	//make hashmap linking country name and country code using values from state_name.tsv - 174 countries (that exist as of 12/31/2020) here - format: <name,countrycode>
    	HashMap<String, String> state_name = new HashMap<String, String>();
    	while(stateNameScan.hasNext()) {
    		String[] currentCountry = stateNameScan.nextLine().split("\t");
    		//hard-coding data cleanup, while kosovo and south sudan show up in borders.txt, they do not show up in capDist.csv, and are therefore not needed
    		if(currentCountry[4].strip().equals("2020-12-31") && !currentCountry[1].equals("KOS") && !currentCountry[1].equals("SSD")) {
    			state_name.put(nameChanger.alias(currentCountry[2]), currentCountry[1]);
    			//make namechanger put right names into the hashmap
    			System.out.println(nameChanger.alias(currentCountry[2]) + " , " + state_name.get(currentCountry[2]));
    		}
    	}
    	
    	//before we create graph for sure we need to make capdist accessible multiple times over - stored as a hashmap - <countrycode:countrycode,kmdist>
    	HashMap<String, Integer> capDist = new HashMap<String, Integer>();
    	
    	while(capDistScan.hasNext()) {
    		String[] currentDistance = capDistScan.nextLine().split(",");
    		//united kingdom is UKG in state_name, I change it to keep the data consistent 
    		if(currentDistance[1].equals("UK")) {
    			currentDistance[1] = "UKG";
    		}
    		if(currentDistance[3].equals("UK")) {
    			currentDistance[3] = "UKG";
    		}
    		//make sure we only add countries in state_name
    		if(state_name.containsValue(currentDistance[1]) && state_name.containsValue(currentDistance[3])) {
    			capDist.put(currentDistance[1]  + ":" + currentDistance[3], Integer.parseInt(currentDistance[4]));
    		}
    		System.out.println(currentDistance[1]  + ":" + currentDistance[3] + "  " + capDist.get(currentDistance[1]  + ":" + currentDistance[3]));
    	}
    	
    	//then go through each country in borders, match it to country code in state_name to get distance, then add that edge to world using distance in capDist
    	//instead of making a 3rd hashmap for borders.txt, i just put the info from borders + the info from the other two hashmaps directly into the graph
		
    	while(borderScan.hasNext()) {
    		String[] borderLine = borderScan.nextLine().split(" ");
    		//get country we go from - country on the left of the equals sign
    		String country = "";
    		int i = 0;
    		while(!borderLine[i].equals("=")) {
    			country += borderLine[i];
    			if(!borderLine[i+1].equals("=")) {
    				country += " ";
    			}
    			i++;
    		}
    		i++; //iterate past equals sign
    		country = nameChanger.alias(country); // make country what it should be
    		//edge case for countries with no bordering countries
    		System.out.println("AHAHAHAHA: " + country + " , " + i);
    		//find the countries that the first country borders, if the first country exists
    		if(state_name.containsKey(country)) {
    			//if the country has nothing adjacent to it, add in a node with no edges
        		if(i == borderLine.length) {
        			world.addEdge(country, "", -1);
        		}
        		else {
        			//get country code of first country
	    			String countryCode1 = state_name.get(country);
	    			while(i<borderLine.length) {
	        			String country2 = "";
	        			//we don't want the border distances or "km" to be added to the country name, so we add to country2 until we find one of those things
	        			while(i < borderLine.length && !borderLine[i].matches("[0-9,.]+") && !borderLine[i].equals("km") && !borderLine[i].equals("km;")) {
	        				country2 += borderLine[i] + " ";
	        				i++;
	        			}
	        			country2 = nameChanger.alias(country2.strip()); // make country2 what it should be
	        			//if country2 is in our list of state names, then we can add it to the graph
	        			if(state_name.containsKey(country2)) {
	        				//get country code of 2nd country
	        				String countryCode2 = state_name.get(country2);
	        				//then get distance between the two countries using the country codes
	        				int distance = capDist.get(countryCode1+":"+countryCode2);
	        				//still have to factor in an edge case here!!!! borders.txt straight up says denmark shares a border with canada, giving no notice that it's talking about greenland, unlike canada-> denmark where there are parentheses stating that greenland is there
	        				if(!(country.equals("denmark") && country2.equals("canada"))) {
	        					//after accounting for everything, we can finally add an edge to our graph
	        					world.addEdge(country, country2, distance);
	        				}
	        			}
	        			//then we iterate i once more 
	        			i++;
	        		}
	    		}
    		}
    		
    	}
    	
    	
    	
    }
    //we have the graph now!!!!!!!!

    public int getDistance (String country1, String country2) {
    	//uses worldgraph to get distances between 2 adjacent countries
    	NameChanger nameChanger = new NameChanger();
    	//make sure inputted strings are in correct format
    	country1 = nameChanger.alias(country1);
    	country2 = nameChanger.alias(country2);
    	//then check if they are adjacent, if they are, then we can return the edge weight of the countries
    	if(world.areAdjacent(country1, country2)) {
    		return world.edgeWeight(country1, country2);
    	}
    	//if the countries are not adjacent, return -1
        return -1;
    }


    public List<String> findPath (String sourceCountry, String finalCountry) {
        //initialize our namechanger and aliases for our inputs
    	NameChanger nameChanger = new NameChanger();
    	String source = nameChanger.alias(sourceCountry);
    	String last = nameChanger.alias(finalCountry);
    	//then get the hashmap of the finalized vertices from dijkstras algorithm
    	HashMap<String, String> finalized = world.dijkstra(source, last);
    	//then we check to see if we found a path to the destination country
    	if(finalized.containsKey(last)) {
    		//most of this is formatting stuff, we go through the hashmap starting from the last country, reversing our steps until we get to the first country
    		//we add each of these steps to the output, then reverse it
    		ArrayList<String> output = new ArrayList<String>();
    		String current = last;
    		//we go through the hashmap until we find the source vertex, and then stop
    		while(!current.equals(finalized.get(current))) {
    			//we set the previous country of the current one
    			String prevCountry = finalized.get(current);
    			//then we print out the paths, if we are at the first step or the last step, we use the user's input in the list and capitalize it, to not confuse the user
    			//this is because if we ran user input through nameChanger.alias() it might be a completely different country
    			if(current.equals(last)&& prevCountry.equals(source)) {
    				output.add(capitalize(sourceCountry) + " --> " + capitalize(finalCountry) + " (" + getDistance(current, finalized.get(current)) + " km.)");
    			}
    			else if(current.equals(last)) {
    				output.add(capitalize(prevCountry) + " --> " + capitalize(finalCountry) + " (" + getDistance(current, finalized.get(current)) + " km.)");
    			}
    			else if(prevCountry.equals(source)) {
    				output.add(capitalize(sourceCountry) + " --> " + capitalize(current) + " (" + getDistance(current, finalized.get(current)) + " km.)");
    			}
    			else {
	    			output.add(capitalize(prevCountry) + " --> " + capitalize(current) + " (" + getDistance(current, finalized.get(current)) + " km.)");
    			}
    			//then we update current as the last country to continue moving backwards
    			current = prevCountry;
    		}
    		//output is currently backwards so we need to reverse it
    		Collections.reverse(output);
    		return output;
    	}
    	//if we didn't find a path to the destination country, return an empty arrayList
    	else {
    		return new ArrayList<>();
    	}
    }
    
    //method used in findPath, capitalizes each word in a substring
    private static String capitalize(String input) {
    	String[] inputArray = input.split(" ");
    	input = "";
    	for(String s : inputArray) {
    		input += s.substring(0,1).toUpperCase() + s.substring(1);
    		input += " ";
    	}
    	return input.strip();
    }
    
    public void acceptUserInput() {
    	//initialize scanner getting user input and namechanger
    	Scanner input = new Scanner(System.in);
    	NameChanger nameChanger = new NameChanger();
    	//run until we exit
    	while(true) {
    		//prompts user for first country, exits if they say EXIT
    		System.out.println("Enter the name of the first country (type EXIT to quit): ");
    		String country1 = input.nextLine();
    		if(country1.equals("EXIT")) {
    			input.close();
    			System.exit(0);
    		}
    		//if the country is invalid, we output an error message and start over
    		if(!world.containsCountry(nameChanger.alias(country1))) {
    			System.out.println("Invalid country name. Please enter a valid country name.");
    			continue;
    		}
    		//do the same thing with country 2
    		System.out.println("Enter the name of the second country (type EXIT to quit): ");
    		String country2 = input.nextLine();
    		if(country2.equals("EXIT")) {
    			input.close();
    			System.exit(0);
    		}
    		if(!world.containsCountry(nameChanger.alias(country2))) {
    			System.out.println("Invalid country name. Please enter a valid country name.");
    			continue;
    		}
    		//we print a precursor message to the path
    		System.out.println("Path from " + capitalize(country1) +" to " + capitalize(country2) + ":");
    		
    		//measure how long the program takes to run
    		long timeBefore = System.currentTimeMillis();
    		
    		//then output the list correctly formatted
    		List<String> path = this.findPath(country1, country2);
    		//prints out every element in the path, if the path does not exist it will print nothing
    		for(String s : path) {
    			System.out.print(" - ");
    			System.out.println(s);
    		}
    		
    		long timeAfter = System.currentTimeMillis();
    		
    		System.out.println("Time Elapsed: " + (timeAfter-timeBefore) + " Milliseconds" );
    		
    	}
    }


    public static void main(String[] args) throws IOException{
        IRoadTrip a3 = new IRoadTrip(args);
        a3.acceptUserInput();
    }

}


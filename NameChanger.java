
public class NameChanger {
	
	//contains the edge cases
	String[][] aliases;
			
	public NameChanger(){
		//list of edge cases that don't follow the normal name changing rules, the correct name is in index 0 of the subarrays
		aliases = new String[][] {{"bosnia-herzegovina", "bosnia and herzegovina"}, {"myanmar", "burma"}, {"cape verde", "cabo verde"}, {"czech republic", "czechia"}, {"swaziland", "eswatini"}, {"german federal republic", "germany"}, {"people's republic of korea", "north korea"}, {"republic of korea", "south korea"}, {"kyrgyz republic", "kyrgyzstan"}, {"macedonia", "north macedonia"}, {"rumania", "romania"}, {"surinam", "suriname"}, {"east timor", "timor-leste"}, {"united states of america", "us", "united states", "usa"}, {"democratic republic of vietnam", "vietnam"}, {"congo", "republic of congo"}, {"cote d’ivoire", "cote d'ivoire"}, {"united kingdom", "uk"}};
	}
	
	//change the name of a country so a variety of names can be used for the same country
	public String alias(String countryName) {
		
		//first make everything lowercase
		countryName = countryName.toLowerCase().strip();
		
		//check for territories that are in completely different places than the real country and return an empty string so they will never be considered for the graph
		if(countryName.contains("(greenland)") ||countryName.contains("(kaliningrad)") ||countryName.contains("kaliningrad oblast") || countryName.contains("ceuta")) {
			return "";
		}
		
		String[] countryNameArray = countryName.split(" ");
		countryName  = "";
		//every string with a comma is just one word that needs to be put at the end without the comma, the one exception to this is "Saint Helena, Ascension, and Tristan da Cunha", but that doesnt exist in state_names so idgaf
		String putAtEnd = "";
		for(String s : countryNameArray) {
			//everything in parentheses is useless
			if(s.contains("(")) {
				break;
			}
			//every country with a slash doesn't need the name after the slash
			if(s.contains("/")) {
				String[] countryWithSlash = s.split("/");
				s = countryWithSlash[0];
			}
			if(s.contains(",")) {
				putAtEnd = s.replace(",","");
			}	
			else {
				countryName += s + " ";
			}
		}
		//if it finds the word with the comma, it saves it to put at the end, if not, then it just puts nothing at the end
		countryName += putAtEnd;
		//get rid of the last trailing whitespace
		countryName = countryName.strip();
		//get rid of "the", as it is not contained in state_name
		countryName= countryName.replace("the ", "");
		//despite all of this, there are still edge cases. I store them in an array of arrays aliases and check/possibly change them with this.
		//the correct name for the current country is in aliases[][0]
		for(String[] s1 : aliases) {
			for (String s2: s1) {
				if (s2.equals(countryName)) {
					countryName = s1[0];
					break;
				}
			}
		}
		
		//return changed string
		return countryName;
	}
}

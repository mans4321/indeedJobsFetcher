package indeed.model;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class JobScorer {

	private List<String> userSkills;
	
	public JobScorer(List<String> userSkills){
		this.userSkills = userSkills;
		this.userSkills = userSkills.stream().map(str -> str.toLowerCase()).collect(Collectors.toList());
	}
	
	public double getScore(String jobDes) {

		if(userSkills.isEmpty()){
			return 0;
		}

		HashMap<String, Integer> found = new HashMap<String, Integer>();
		double score = 0;
		
		String[] wordsInJobDes = jobDes.split("\\W+");
		for (String word : wordsInJobDes)
			if (userSkills.contains(word.trim().toLowerCase()) && !found.containsKey(word.trim().toLowerCase())) {
				score++;
				found.put(word.trim().toLowerCase(), 1);
			}
		return score / userSkills.size();
	}


}
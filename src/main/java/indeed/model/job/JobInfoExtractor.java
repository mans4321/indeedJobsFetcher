package indeed.model.job;

import indeed.utility.CleanText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JobInfoExtractor {

	public JobDescription extract(Document page, JobInfoSelectors jobInfoSelectors) {

		final String companyName = getCompanyName(page, jobInfoSelectors.getCompany());
		final String jobTitle = getTitle(page, jobInfoSelectors.getJobTitle());
		final String city = getCity(page, jobInfoSelectors.getCity());
		final String jobDescription = getJobDescriptionInHtmlFormat(page, jobInfoSelectors.getJobDes());
	
		return new JobDescription(jobTitle, companyName, city, jobDescription);

	}


	private String getCompanyName(Document doc, String selector) {
		String companyName = doc.selectFirst(selector).text();
		companyName = CleanText.simpleCleanText(companyName);
		companyName = CleanText.removeLegalEntityTypes(companyName);
		return companyName;
	}

	private String getTitle(Document doc, String selector) {
		String jobTitle = doc.selectFirst(selector).text();
		jobTitle = CleanText.simpleCleanText(jobTitle);
		return jobTitle;
	}

	private String getCity(Document doc, String selector) {
		String city = doc.selectFirst(selector).text();
		if (city != null && city.contains(","))
			city = city.split(",")[0];
		return city;
	}

	private String getJobDescriptionInHtmlFormat(Document doc, String selector) {
		Element jobDes = doc.selectFirst(selector);
		return  jobDes.children().toString();
	}
}

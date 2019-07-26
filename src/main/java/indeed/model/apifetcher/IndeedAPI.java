package indeed.model.apifetcher;

import indeed.model.job.JobDescription;
import indeed.model.job.JobInfoExtractor;
import indeed.model.job.JobInfoSelectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class IndeedAPI implements FetchJobsAPI {

	private JobInfoExtractor jobInfoExtractor;

	public IndeedAPI(){
		jobInfoExtractor = new JobInfoExtractor();
	}
	
	@Override
	public List<String> getResultPagesURL(String jobDes, String city, int numberOfPages) {
		List<String> urls = new ArrayList<>();
		int page = 0;
		while (page < numberOfPages) {
			urls.add(constructPageUrl(jobDes, city, page * 20));// page number
																// increases by
																// 20 for every
																// subsequent
																// page
			page++;
		}
		return urls;
	}

	@Override
	public List<String> getJobsUrlsOnPage(Document page) {
		List<String> urls = new ArrayList<>();

		Elements links = page.select("a");
		for (Element e : links) {
			int index = e.attr("onmousedown").indexOf("fromjk");
			if (index == -1)
				continue;

			String jobDeskey = e.attr("onmousedown");
			jobDeskey = jobDeskey.substring(index, jobDeskey.indexOf('&', index)).split("=")[1].trim();
			final String jobDesLink = "https://ca.indeed.com/viewjob?jk=" + jobDeskey;
			urls.add(jobDesLink);
		}
		return urls;
	}

	@Override
	public JobDescription extractJobDescriptionOnPage(Document page) {
		JobInfoSelectors selector = new JobInfoSelectors();
		selector.setJobDes("#jobDescriptionText");
		selector.setCompanyName("div.icl-u-lg-mr--sm.icl-u-xs-mr--xs");
		selector.setJobTitle(".jobsearch-JobInfoHeader-title");
		selector.setCity(".jobsearch-JobMetadataHeader-iconLabel");
		JobDescription jobDes = jobInfoExtractor.extract(page, selector);
		return jobDes;
	}

	private String constructPageUrl(String jobDes, String city, int page) {
		jobDes = jobDes.replace(" ", "+");
		return "https://www.indeed.ca/jobs?q=" + jobDes + "&l=" + city + "&start=" + page;
	}
}

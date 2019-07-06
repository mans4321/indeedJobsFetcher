package indeed.model.apifetcher;

import indeed.model.job.JobDescription;
import org.jsoup.nodes.Document;

import java.util.List;

public interface FetchJobsAPI {
	
	public List<String> getResultPagesURL(String jobDes, String city, int numberOfPages);
	
	public List<String> getJobsUrlsOnPage(Document page);
	
	public JobDescription extractJobDescriptionOnPage(Document page);
	
}

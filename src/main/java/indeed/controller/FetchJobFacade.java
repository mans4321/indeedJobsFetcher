package indeed.controller;

import indeed.model.JobScorer;
import indeed.model.apifetcher.FetchJobsAPI;
import indeed.model.apifetcher.IndeedAPI;
import indeed.model.job.JobDescription;
import indeed.model.job.JobSearchInfo;
import indeed.model.job.JobsList;
import indeed.model.threads.JobDescriptionFetcher;
import indeed.model.threads.JobDescriptionScorer;
import indeed.model.threads.JobUrlFetcher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;


@Service
public class FetchJobFacade {

	private final int NUMBER_OF_RESULT_PAGES = 3;

	private final int URL_FETCHR_THREADS = 10;

	private final int DESCRIPTION_FETCHER_THREADS = 10;

	private final int SCORER_THREADS = 3;

	private FetchJobsAPI indeedAPI;

	private BlockingQueue<String> jobsUrlQueue;

	private BlockingQueue<JobDescription> jobsDesQueue;

	private JobDescriptionFetcher jobDesFetcher;

	private JobDescriptionScorer jobDesScorer;

	private JobUrlFetcher jobUrlFetcher;


	public FetchJobFacade() {
		this.indeedAPI = new IndeedAPI();

		this.jobsUrlQueue = new LinkedBlockingQueue<>();
		this.jobsDesQueue = new LinkedBlockingQueue<>();
		
		jobUrlFetcher = new JobUrlFetcher(jobsUrlQueue, URL_FETCHR_THREADS);
		jobDesFetcher = new JobDescriptionFetcher(jobsUrlQueue, jobsDesQueue,
				DESCRIPTION_FETCHER_THREADS);
		
		jobDesScorer = new JobDescriptionScorer(jobsDesQueue,
				SCORER_THREADS);
		
	}

	@Async
	public Future<JobsList> fetch(JobSearchInfo searchInfo) {
		List<String> resultPages = getResultPagesFromJobApi(searchInfo);
		jobUrlFetcher.fetch(resultPages);
		jobDesFetcher.fetch();
		JobScorer jobScorer = new JobScorer(searchInfo.getSkills());
		return  jobDesScorer.score(jobScorer);
	}

	private List<String> getResultPagesFromJobApi(JobSearchInfo searchInfo) {
		List<String> resultPages = new LinkedList<>();

		for (String title : searchInfo.getTitles())
			for (String city : searchInfo.getCities()) {
				List<String> apiUrls = indeedAPI.getResultPagesURL(title, city, NUMBER_OF_RESULT_PAGES);
				resultPages.addAll(apiUrls);
			}
		return resultPages;
	}
}
package indeed.controller;

import indeed.model.job.JobSearchInfo;
import indeed.model.job.JobsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class IndeedRestController {


    @Autowired
    private FetchJobFacade fetchJobFacade;

    @GetMapping("/")
    @ResponseBody
    public String description() {
        return "fetch jobs";
    }

    @GetMapping("/fetch")
    @ResponseBody
    public JobsList getJobs(@RequestParam List<String> titles,
                            @RequestParam List<String> cities,
                            @RequestParam(required = false) List<String> skills,
                            @RequestParam(required = false ) Integer experience)
            throws ExecutionException, InterruptedException {

        if(titles == null)
            return new JobsList();

        JobSearchInfo searchInfo = new JobSearchInfo();
        searchInfo.setCities(cities);
        searchInfo.setTitles(titles);
        searchInfo.setUserExperience(experience);
        searchInfo.setUserSkills(skills);
        Future<JobsList> future = fetchJobFacade.fetch(searchInfo);
        return  future.get();
    }
}

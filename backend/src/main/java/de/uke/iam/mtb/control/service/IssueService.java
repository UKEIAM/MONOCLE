package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.Issue;
import de.uke.iam.mtb.control.repository.IssueRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class IssueService {

  IssueRepository issueRepository;
  EpisodeService episodeService;

  public IssueService(IssueRepository issueRepository, EpisodeService episodeService) {
    this.issueRepository = issueRepository;
    this.episodeService = episodeService;
  }

  public Issue addIssue(UUID episodeId, String details) {
    Episode episode = episodeService.getEpisodeReference(episodeId);

    Issue issue = new Issue();
    issue.setDetails(details);
    issue.setEpisode(episode);

    return issueRepository.save(issue);
  }

  public Issue updateIssue(UUID episodeId, String details) {
    Issue issue = issueRepository.findByEpisodeId(episodeId);
    issue.setDetails(details);

    return issueRepository.save(issue);
  }

  public void deleteIssueByEpisodeId(UUID episodeId) {
    Issue issue = issueRepository.findByEpisodeId(episodeId);
    issueRepository.deleteById(issue.getId());
  }

  public boolean existsByEpisodeId(UUID episodeId) {
    return issueRepository.existsByEpisodeId(episodeId);
  }
}

package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.EpisodeDto;
import de.uke.iam.mtb.api.model.JobStatusDto;
import de.uke.iam.mtb.api.model.TransferJobDto;
import de.uke.iam.mtb.control.models.TransferJob;
import de.uke.iam.mtb.control.models.enums.JobStatus;
import de.uke.iam.mtb.control.models.mapper.TransferJobMapper;
import de.uke.iam.mtb.control.repository.BwhcTransferRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BwhcTransferService {
    public final BwhcTransferRepository bwhcTransferRepository;
    public final EpisodeService episodeService;
    private final TransferJobMapper transferJobMapper;

    public BwhcTransferService(BwhcTransferRepository bwhcTransferRepository, EpisodeService episodeService,
        TransferJobMapper transferJobMapper) {
        this.bwhcTransferRepository = bwhcTransferRepository;
        this.episodeService = episodeService;
        this.transferJobMapper = transferJobMapper;
    }

    public TransferJobDto addTransferJob(UUID episode) {
        EpisodeDto episodeDto = episodeService.getEpisode(episode);
        TransferJobDto newJob = new TransferJobDto();
        newJob.setPatientId(episodeDto.getPatientId());
        newJob.setEpisodeId(episode);
        newJob.setStatus(JobStatusDto.OPEN);
        TransferJob transferJob = transferJobMapper.toEntity(newJob);
        bwhcTransferRepository.save(transferJob);
        return transferJobMapper.toDto(transferJob);
    }

    public List<TransferJob> getAllOpenJobs() {
        List<TransferJob> open = bwhcTransferRepository.getAllByStatus(JobStatus.OPEN).stream().toList();
//        List<TransferJob> error = transferJobRepository.getAllByStatus(JobStatus.ERROR).stream().toList();
//        List<TransferJob> inProgress = transferJobRepository.getAllByStatus(JobStatus.INPROGRESS).stream().toList();

        List<TransferJob> jobs = new ArrayList<>(open);
//        jobs.addAll(error);
//        jobs.addAll(inProgress);
        return jobs;
    }

    public void updateStatus(TransferJob job, JobStatus status) {
        job.setStatus(status);
        bwhcTransferRepository.save(job);
    }

    public void deleteAllByStatusDone() {
        List<TransferJob> done = bwhcTransferRepository.getAllByStatus(JobStatus.DONE).stream().toList();
        bwhcTransferRepository.deleteAll(done);
    }
}

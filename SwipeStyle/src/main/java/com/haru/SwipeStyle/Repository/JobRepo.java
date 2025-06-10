package com.haru.SwipeStyle.Repository;

import com.haru.SwipeStyle.Entities.JobEntity;
import com.haru.SwipeStyle.Entities.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepo extends JpaRepository<JobEntity, Long> {

    Optional<JobEntity> findByJobName(String jobName);

    List<JobEntity> findByCurrentStatus(JobStatus status);

    @Query("SELECT MAX(j.lastSuccessfulRunDate) FROM JobEntity j")
    LocalDateTime getLatestSuccessfulRunDate();

    @Modifying
    @Query("UPDATE JobEntity j SET j.currentStatus = :status, j.updatedAt = :updatedAt WHERE j.jobName = :jobName")
    void updateJobStatus(@Param("jobName") String jobName,
                         @Param("status") JobStatus status,
                         @Param("updatedAt") LocalDateTime updatedAt);
}
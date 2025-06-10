package com.haru.SwipeStyle.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "job_status")
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false, unique = true)
    private String jobName;

    @Column(name = "last_run_date")
    private LocalDateTime lastRunDate;

    @Column(name = "last_successful_run_date")
    private LocalDateTime lastSuccessfulRunDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private JobStatus currentStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public JobEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.currentStatus = JobStatus.IDLE;
    }

    public JobEntity(String jobName) {
        this();
        this.jobName = jobName;
    }

    public void setLastRunDate(LocalDateTime lastRunDate) {
        this.lastRunDate = lastRunDate;
        this.updatedAt = LocalDateTime.now();
    }


    public void setLastSuccessfulRunDate(LocalDateTime lastSuccessfulRunDate) {
        this.lastSuccessfulRunDate = lastSuccessfulRunDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCurrentStatus(JobStatus currentStatus) {
        this.currentStatus = currentStatus;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void startJob() {
        this.currentStatus = JobStatus.RUNNING;
        this.lastRunDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void completeJobSuccessfully() {
        this.currentStatus = JobStatus.COMPLETED;
        this.lastSuccessfulRunDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void failJob() {
        this.currentStatus = JobStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void setJobIdle() {
        this.currentStatus = JobStatus.IDLE;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", jobName='" + jobName + '\'' +
                ", lastRunDate=" + lastRunDate +
                ", lastSuccessfulRunDate=" + lastSuccessfulRunDate +
                ", currentStatus=" + currentStatus +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

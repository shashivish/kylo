package com.thinkbiganalytics.metadata.jpa.sla;

import com.google.common.collect.ComparisonChain;
import com.thinkbiganalytics.jpa.AbstractAuditedEntity;
import com.thinkbiganalytics.jpa.BaseJpaId;
import com.thinkbiganalytics.metadata.sla.api.AssessmentResult;
import com.thinkbiganalytics.metadata.sla.api.ObligationAssessment;
import com.thinkbiganalytics.metadata.sla.api.ServiceLevelAgreement;
import com.thinkbiganalytics.metadata.sla.api.ServiceLevelAssessment;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by sr186054 on 9/14/16.
 */
@Entity
@Table(name = "SLA_ASSESSMENT")
public class JpaServiceLevelAssessment extends AbstractAuditedEntity implements ServiceLevelAssessment {


    @EmbeddedId
    private SlaAssessmentId id;

    @Transient
    private ServiceLevelAgreement agreement;

    @Column(name = "SLA_ID")
    private String slaId;

    @Column(name = "MESSAGE")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "RESULT")
    private AssessmentResult result;

    @OneToMany(targetEntity = JpaObligationAssessment.class, mappedBy = "serviceLevelAssessment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ObligationAssessment> obligationAssessments = new HashSet<>();

    public JpaServiceLevelAssessment() {

    }

    public SlaAssessmentId getId() {
        return id;
    }

    public void setId(SlaAssessmentId id) {
        this.id = id;
    }


    @Override
    public String getServiceLevelAgreementId() {
        return slaId;
    }

    public ServiceLevelAgreement getAgreement() {
        return agreement;
    }

    public void setAgreement(ServiceLevelAgreement agreement) {
        this.agreement = agreement;
        this.setSlaId(agreement.getId().toString());
    }

    public String getSlaId() {
        return slaId;
    }

    public void setSlaId(String slaId) {
        this.slaId = slaId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AssessmentResult getResult() {
        return result;
    }

    public void setResult(AssessmentResult result) {
        this.result = result;
    }

    @Embeddable
    public static class SlaAssessmentId extends BaseJpaId implements ServiceLevelAssessment.ID, Serializable {

        private static final long serialVersionUID = 6965221468619613881L;

        @Column(name = "id", columnDefinition = "binary(16)")
        private UUID uuid;

        public static SlaAssessmentId create() {
            return new SlaAssessmentId(UUID.randomUUID());
        }

        public SlaAssessmentId() {
        }

        public SlaAssessmentId(Serializable ser) {
            super(ser);
        }

        @Override
        public UUID getUuid() {
            return this.uuid;
        }

        @Override
        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }
    }

    @Override
    public DateTime getTime() {
        return super.getCreatedTime();
    }

    @Override
    public Set<ObligationAssessment> getObligationAssessments() {
        return obligationAssessments;
    }

    public void setObligationAssessments(Set<ObligationAssessment> obligationAssessments) {
        this.obligationAssessments = obligationAssessments;
    }

    @Override
    public int compareTo(ServiceLevelAssessment sla) {

        ComparisonChain chain = ComparisonChain
            .start()
            .compare(this.getResult(), sla.getResult())
            .compare(this.getAgreement().getName(), sla.getAgreement().getName());

        if (chain.result() != 0) {
            return chain.result();
        }

        List<ObligationAssessment> list1 = new ArrayList<>(this.getObligationAssessments());
        List<ObligationAssessment> list2 = new ArrayList<>(sla.getObligationAssessments());

        chain = chain.compare(list1.size(), list2.size());

        Collections.sort(list1);
        Collections.sort(list2);

        for (int idx = 0; idx < list1.size(); idx++) {
            chain = chain.compare(list1.get(idx), list2.get(idx));
        }

        return chain.result();
    }

}
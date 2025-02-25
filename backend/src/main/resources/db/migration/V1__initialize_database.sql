create table if not exists addressbook
(
    id                     uuid not null
        constraint addressbook_pkey
            primary key,
    ukeinternal            boolean,
    addressbook_entry_type smallint,
    created_at             timestamp(6) with time zone,
    deleted_at             timestamp(6) with time zone,
    email                  varchar(255),
    fax                    varchar(255),
    firstname              varchar(255),
    lastname               varchar(255),
    location               varchar(255),
    plz                    varchar(255),
    street                 varchar(255),
    streetnumber           varchar(255),
    telephone              varchar(255),
    title                  varchar(255),
    updated_at             timestamp(6) with time zone,
    weburl                 varchar(255)
);

create table if not exists audittrail
(
    id            uuid not null
        constraint audittrail_pkey
            primary key,
    date_of_entry timestamp(6) with time zone,
    entry         varchar(255),
    user_id       varchar(255)
);

create table if not exists bwhctransfer
(
    id         uuid not null
        constraint bwhctransfer_pkey
            primary key,
    episode_id uuid,
    patient_id uuid,
    status     smallint
);

create table if not exists health_insurance
(
    ik                bigint not null
        primary key,
    anrede            varchar(255),
    antragsschluessel varchar(255),
    datum             varchar(255),
    fax_nummer        varchar(255),
    fax_vorwahl       varchar(255),
    h_lkz             varchar(255),
    h_ort             varchar(255),
    h_plz             varchar(255),
    h_strasse         varchar(255),
    namenszeile_1     varchar(255),
    namenszeile_2     varchar(255),
    namenszeile_3     varchar(255),
    namenszeile_4     varchar(255),
    p_lkz             varchar(255),
    p_ort             varchar(255),
    p_plz             varchar(255),
    postfach          varchar(255),
    tel_ruf_nummer    varchar(255),
    tel_vorwahl       varchar(255)
);

create table if not exists lab_number
(
    id                 varchar(255) not null
        constraint lab_number_pkey
            primary key,
    assigned           boolean,
    assigned_on        timestamp(6) with time zone,
    created_at         timestamp(6) with time zone,
    specimen_labelling varchar(255)
);

create table if not exists patient
(
    id                  uuid not null
        constraint patient_pkey
            primary key,
    consent             boolean,
    created_at          timestamp(6) with time zone,
    date_of_birth       date,
    date_of_death       date,
    deleted_at          timestamp(6) with time zone,
    first_name          varchar(255),
    gender              smallint,
    soarian_id          varchar(255),
    surname             varchar(255),
    updated_at          timestamp(6) with time zone,
    health_insurance_id bigint
        constraint fk_patient_health_insurance
            references health_insurance,
    municipality_key    varchar(255)
);

create table if not exists comment
(
    id          uuid not null
        constraint comment_pkey
            primary key,
    author      varchar(255),
    comment     text,
    created_at  timestamp(6) with time zone,
    deleted_at  timestamp(6) with time zone,
    highlighted boolean,
    updated_at  timestamp(6) with time zone,
    patient_id  uuid
        constraint fk_comment_patient
            references patient
);


create table if not exists step
(
    id             integer not null
        constraint pk_step
            primary key,
    name           varchar(255),
    skippable      boolean,
    parent_step_id integer
        constraint fk_step_parent_step
            references step
);

create table if not exists workflow
(
    id integer not null
        constraint workflow_pk
            primary key
);

create table if not exists episode
(
    id             uuid not null
        constraint episode_pkey
            primary key,
    created_at     timestamp(6) with time zone,
    decision       text,
    deleted_at     timestamp(6) with time zone,
    report         text,
    updated_at     timestamp(6) with time zone,
    patient_id     uuid
        constraint fk_episode_patient
            references patient,
    requirement_id uuid,
    workflow_id    integer
        constraint fk_episode_workflow
            references workflow
);

create table if not exists diagnose
(
    id                          uuid not null
        constraint diagnose_pkey
            primary key,
    created_at                  timestamp(6) with time zone,
    deleted_at                  timestamp(6) with time zone,
    guideline_treatment_status  smallint,
    recorded_on                 date,
    updated_at                  timestamp(6) with time zone,
    episode_id                  uuid
        constraint fk_diagnose_episode
            references episode,
    icd10                       jsonb,
    icdo3t                      jsonb,
    status_history              jsonb,
    is_germline_diagnosis_exist boolean,
    germline_diagnosis_icd10    jsonb,
    hpo_icd10                   jsonb,
    orphanet_code               jsonb,
    alpha_id_se_code            jsonb,
    who_grade                   jsonb,
    tnm_key                     jsonb,
    alt_tumor_key               jsonb
);

create table if not exists ecog_status
(
    id             uuid not null
        constraint ecog_status_pkey
            primary key,
    created_at     timestamp(6) with time zone,
    deleted_at     timestamp(6) with time zone,
    effective_date date,
    updated_at     timestamp(6) with time zone,
    value          bytea,
    episode_id     uuid
        constraint fk_ecog_status_episode
            references episode
);

create table if not exists family_member_diagnosis
(
    id           uuid not null
        constraint family_member_diagnosis_pkey
            primary key,
    created_at   timestamp(6) with time zone,
    deleted_at   timestamp(6) with time zone,
    details      text,
    relationship bytea,
    updated_at   timestamp(6) with time zone,
    episode_id   uuid
        constraint fk_family_member_diagnosis_episode
            references episode
);

create table if not exists genetic_counselling_request
(
    id         uuid not null
        constraint genetic_counselling_request_pkey
            primary key,
    created_at timestamp(6) with time zone,
    deleted_at timestamp(6) with time zone,
    issued_on  date,
    updated_at timestamp(6) with time zone,
    episode_id uuid
        constraint fk_genetic_counselling_request_episode
            references episode,
    reason     jsonb
);

create table if not exists care_plan
(
    id                             uuid not null
        constraint care_plan_pkey
            primary key,
    created_at                     timestamp(6) with time zone,
    deleted_at                     timestamp(6) with time zone,
    description                    text,
    issued_on                      date,
    updated_at                     timestamp(6) with time zone,
    diagnosis_id                   uuid
        constraint fk_care_plan_diagnose
            references diagnose,
    episode_id                     uuid
        constraint fk_care_plan_episode
            references episode,
    genetic_counselling_request_id uuid
        constraint fk_care_plan_genetic_counselling_request
            references genetic_counselling_request,
    status_reason                  jsonb,
    no_target_finding              jsonb
);

create table if not exists issue
(
    id         uuid not null
        constraint issue_pkey
            primary key,
    created_at timestamp(6) with time zone,
    deleted_at timestamp(6) with time zone,
    details    text,
    updated_at timestamp(6) with time zone,
    episode_id uuid
        constraint fk_issue_episode
            references episode
);

create table if not exists presentation
(
    id                   uuid not null
        constraint presentation_pkey
            primary key,
    created_at           timestamp(6) with time zone,
    date_of_presentation date,
    deleted_at           timestamp(6) with time zone,
    updated_at           timestamp(6) with time zone,
    episode_id           uuid
        constraint fk_presentation_episode
            references episode
);


create table if not exists requirement
(
    id                    uuid not null
        constraint requirement_pkey
            primary key,
    comment               varchar(255),
    created_at            timestamp(6) with time zone,
    deleted_at            timestamp(6) with time zone,
    intern_diagnostic     boolean,
    moleculare_diagnostic boolean,
    ngs                   boolean,
    ngs_type              smallint,
    others                boolean,
    others_type           smallint,
    recommended           boolean,
    standard              boolean,
    standard_type         smallint,
    updated_at            timestamp(6) with time zone,
    episode_id            uuid
        constraint fk_requirement_episode
            references episode
);

alter table episode
    add constraint fk_requirement_episode
        foreign key (requirement_id) references requirement;

create table if not exists specimen
(
    id         uuid not null
        constraint specimen_pkey
            primary key,
    created_at timestamp(6) with time zone,
    deleted_at timestamp(6) with time zone,
    labelling  varchar(255),
    type       smallint,
    updated_at timestamp(6) with time zone,
    episode_id uuid
        constraint fk_specimen_episode
            references episode,
    icd10      jsonb,
    collection jsonb
);

create table if not exists histology_reevaluation_request
(
    id          uuid not null
        constraint histology_reevaluation_request_pkey
            primary key,
    created_at  timestamp(6) with time zone,
    deleted_at  timestamp(6) with time zone,
    issued_on   date,
    updated_at  timestamp(6) with time zone,
    episode_id  uuid
        constraint fk_histology_reevaluation_request_episode
            references episode,
    specimen_id uuid
        constraint fk_histology_reevaluation_request_specimen
            references specimen
);

create table if not exists histology_report
(
    id                     uuid not null
        constraint histology_report_pkey
            primary key,
    created_at             timestamp(6) with time zone,
    deleted_at             timestamp(6) with time zone,
    issued_on              date,
    updated_at             timestamp(6) with time zone,
    episode_id             uuid
        constraint fk_histology_report_episode
            references episode,
    specimen_id            uuid
        constraint fk_histology_report_specimen
            references specimen,
    tumor_cell_content     jsonb,
    tumor_morphology       jsonb,
    differentiation_degree smallint
);

create table if not exists diagnose_histology_report
(
    diagnose_id         uuid not null
        constraint fk_diagnose_histology_report_diagnose
            references diagnose,
    histology_report_id uuid not null
        constraint fk_diagnose_histology_report_histology_report
            references histology_report
);

create table if not exists molecular_pathology_finding
(
    id                   uuid not null
        constraint molecular_pathology_finding_pkey
            primary key,
    created_at           timestamp(6) with time zone,
    deleted_at           timestamp(6) with time zone,
    issued_on            date,
    note                 text,
    performing_institute uuid,
    updated_at           timestamp(6) with time zone,
    episode_id           uuid
        constraint fk_molecular_pathology_finding_episode
            references episode,
    specimen_id          uuid
        constraint fk_molecular_pathology_finding_specimen
            references specimen,
    type_of_diagnostic   jsonb
);

create table if not exists ngs_report
(
    id                   varchar(255) not null
        constraint ngs_report_pkey
            primary key,
    copy_number_variants bytea,
    created_at           timestamp(6) with time zone,
    deleted_at           timestamp(6) with time zone,
    dna_fusions          bytea,
    issue_date           date,
    metadata             bytea,
    rna_fusions          bytea,
    rna_seqs             bytea,
    sequencing_type      varchar(255),
    simple_variants      bytea,
    tumor_cell_content   bytea,
    updated_at           timestamp(6) with time zone,
    episode_id           uuid
        constraint fk_ngs_report_episode
            references episode,
    specimen_id          uuid
        constraint fk_ngs_report_specimen
            references specimen,
    msi                  jsonb,
    tmb                  jsonb,
    brcaness             jsonb,
    hrd_score            jsonb
);

create table if not exists rebiopsy_request
(
    id          uuid not null
        primary key,
    created_at  timestamp(6) with time zone,
    deleted_at  timestamp(6) with time zone,
    issued_on   date,
    updated_at  timestamp(6) with time zone,
    episode_id  uuid
        constraint fk_rebiopsy_request_episode
            references episode,
    specimen_id uuid
        constraint fk_rebiopsy_request_specimen
            references specimen
);

create table if not exists care_plan_rebiopsy_request
(
    care_plan_id        uuid not null
        constraint fk_care_plan_rebiopsy_request_care_plan
            references care_plan,
    rebiopsy_request_id uuid not null
        constraint fk_care_plan_rebiopsy_request_rebiopsy_request
            references rebiopsy_request
);


create table if not exists step_info
(
    episode_id  uuid    not null
        constraint fk_step_info_episode
            references episode,
    step_id     integer not null
        constraint fk_step_info_step
            references step,
    step_status smallint,
    primary key (episode_id, step_id)
);

create table if not exists study_inclusion_request
(
    id                  uuid not null
        constraint study_inclusion_request_pkey
            primary key,
    created_at          timestamp(6) with time zone,
    deleted_at          timestamp(6) with time zone,
    issued_on           date,
    nct_number          varchar(255),
    updated_at          timestamp(6) with time zone,
    diagnose_id         uuid
        constraint fk_study_inclusion_request_diagnose
            references diagnose,
    episode_id          uuid
        constraint fk_study_inclusion_request_episode
            references episode,
    level_of_evidence   jsonb,
    supporting_variants jsonb,
    studies             jsonb
);

create table if not exists study_inclusion_request_ngs_report
(
    study_inclusion_request_id uuid         not null
        constraint study_inclusion_request_ngs_rep_study_inclusion_request_id_fkey
            references study_inclusion_request,
    ngs_report_id              varchar(255) not null
        references ngs_report,
    primary key (study_inclusion_request_id, ngs_report_id)
);

create table if not exists care_plan_study_inclusion_request
(
    care_plan_id            uuid not null
        constraint fk_care_plan_study_inclusion_request_care_plan
            references care_plan,
    study_inclusion_request uuid not null
        constraint fk_care_plan_study_inclusion_request_study_inclusion_request
            references study_inclusion_request
);

create table if not exists therapy_recommendation
(
    id                  uuid not null
        constraint therapy_recommendation_pkey
            primary key,
    created_at          timestamp(6) with time zone,
    deleted_at          timestamp(6) with time zone,
    issued_on           date,
    medication          bytea,
    priority            varchar(255),
    supporting_variants bytea,
    updated_at          timestamp(6) with time zone,
    diagnosis_id        uuid
        constraint fk_therapy_recommendation_diagnose
            references diagnose,
    episode_id          uuid
        constraint fk_therapy_recommendation_episode
            references episode,
    ngs_report_id       varchar(255)
        constraint fk_therapy_recommendation_ngs_report
            references ngs_report,
    level_of_evidence   jsonb
);

create table if not exists care_plan_therapy_recommendation
(
    care_plan_id              uuid not null
        constraint fk_care_plan_therapy_recommendation_care_plan
            references care_plan,
    therapy_recommendation_id uuid not null
        constraint fk_care_plan_therapy_recommendation_therapy_recommendation
            references therapy_recommendation
);

create table if not exists claim
(
    id                        uuid not null
        constraint claim_pkey
            primary key,
    created_at                timestamp(6) with time zone,
    deleted_at                timestamp(6) with time zone,
    issued_on                 date,
    updated_at                timestamp(6) with time zone,
    episode_id                uuid
        constraint fk_claim_episode
            references episode,
    therapy_recommendation_id uuid
        constraint fk_claim_therapy_recommendation
            references therapy_recommendation,
    is_claim_via_zpm_office   boolean,
    stage                     jsonb
);

create table if not exists claim_response
(
    id         uuid not null
        constraint claim_response_pkey
            primary key,
    created_at timestamp(6) with time zone,
    deleted_at timestamp(6) with time zone,
    issued_on  date,
    updated_at timestamp(6) with time zone,
    claim_id   uuid
        constraint fk_claim_response_claim
            references claim,
    episode_id uuid
        constraint fk_claim_response_episode
            references episode,
    status     varchar(255),
    reason     varchar(255)
);

create table if not exists molecular_therapy
(
    id                        uuid not null
        constraint molecular_therapy_pkey
            primary key,
    created_at                timestamp(6) with time zone,
    deleted_at                timestamp(6) with time zone,
    dosage                    varchar(255),
    note                      text,
    period_end                date,
    period_start              date,
    recorded_on               date,
    status                    smallint,
    updated_at                timestamp(6) with time zone,
    episode_id                uuid
        constraint fk_molecular_therapy_episode
            references episode,
    therapy_recommendation_id uuid
        constraint fk_molecular_therapy_therapy_recommendation
            references therapy_recommendation,
    realisation               varchar(255),
    not_done_reason           jsonb,
    medication                jsonb,
    reason_stopped            jsonb
);

create table if not exists molecular_therapy_response
(
    id             uuid not null
        constraint molecular_therapy_response_pkey
            primary key,
    created_at     timestamp(6) with time zone,
    deleted_at     timestamp(6) with time zone,
    effective_date date,
    updated_at     timestamp(6) with time zone,
    episode_id     uuid
        constraint fk_molecular_therapy_response_episode
            references episode,
    therapy_id     uuid
        constraint fk_molecular_therapy_response_therapy
            references molecular_therapy,
    value          jsonb,
    method         jsonb
);

create table if not exists guideline_therapy
(
    id                            uuid not null
        constraint guideline_therapy_pkey
            primary key,
    created_at                    timestamp(6) with time zone,
    deleted_at                    timestamp(6) with time zone,
    period_end                    date,
    period_start                  date,
    therapy_line                  integer,
    updated_at                    timestamp(6) with time zone,
    diagnosis_id                  uuid
        constraint fk_guideline_therapy_diagnose
            references diagnose,
    episode_id                    uuid
        constraint fk_guideline_therapy_episode
            references episode,
    medication                    jsonb,
    reason_stopped                jsonb,
    procedure                     jsonb,
    procedure_position            varchar(255),
    intention                     varchar(255),
    progress_date                 date,
    molecular_therapy_response_id uuid
        constraint fk_guideline_therapy_molecular_therapy_response
            references molecular_therapy_response
);

create table if not exists workflow_steps
(
    workflow_id integer not null
        constraint fk_workflow_steps_workflow
            references workflow,
    steps_id    integer not null
        constraint fk_workflow_steps_steps
            references step
);

create table if not exists kc_report
(
    id         uuid not null
        constraint kc_report_pkey
            primary key,
    created_at timestamp(6) with time zone,
    deleted_at timestamp(6) with time zone,
    file_name  varchar(255),
    episode_id uuid
        constraint fk_kc_report_episode
            references episode,
    updated_at timestamp(6) with time zone
);


create table if not exists ihc_report
(
    id                         uuid not null
        constraint ihc_report_pkey
            primary key,
    created_at                 timestamp(6) with time zone,
    updated_at                 timestamp(6) with time zone,
    deleted_at                 timestamp(6) with time zone,
    episode_id                 uuid
        constraint fk_ihc_report_episode
            references episode,
    specimen_id                uuid
        constraint fk_ihc_report_specimen
            references specimen,
    date                       date,
    journal_id                 varchar(255),
    block_id                   varchar(255),
    protein_expression_results jsonb,
    msi_mmr_results            jsonb
);

-- Step 0: This lines could be deleted in docker repository. I thought it would be a good idea to have this in our script
CREATE OR REPLACE FUNCTION extract_and_decode_json_object(encoded_bytea bytea)
    RETURNS jsonb AS
$$
BEGIN
    RETURN (
        convert_from(
                decode(substring(encode(encoded_bytea, 'escape') FROM '\{.*\}'), 'escape'),
                'UTF8'
        )::jsonb
        );
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION extract_and_decode_json_array(encoded_bytea bytea)
    RETURNS jsonb AS
$$
BEGIN
    RETURN (
        convert_from(
                decode(substring(encode(encoded_bytea, 'escape') FROM '\[.*\]'), 'escape'),
                'UTF8'
        )::jsonb
        );
END;
$$ LANGUAGE plpgsql;
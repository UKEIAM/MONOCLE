-- Create health_insurance table
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

-- Create workflow table
create table if not exists workflow
(
    id integer not null
        constraint workflow_pk
            primary key
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

create table if not exists workflow_steps
(
    workflow_id integer not null
        constraint fk_workflow_steps_workflow
            references workflow,
    steps_id    integer not null
        constraint fk_workflow_steps_steps
            references step
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

-- Insert into workflow table if not exists
INSERT INTO workflow (id)
VALUES (1)
ON CONFLICT (id) DO NOTHING;

-- Insert steps into step table
INSERT INTO step (id, name, skippable, parent_step_id)
VALUES
    (0, 'Klinische Daten', false, NULL),
    (1, 'Anforderung', false, NULL),
    (2, 'Genetische Daten', false, NULL),
    (3, 'MTB-Beschluss und MTB-Report', false, NULL),
    (4, 'Übermittlung', false, NULL)
ON CONFLICT (id) DO NOTHING;

-- Insert into workflow_steps table if not exists
INSERT INTO workflow_steps (workflow_id, steps_id)
VALUES
    (1, 0),
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4)
ON CONFLICT DO NOTHING;



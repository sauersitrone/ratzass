
CREATE TABLE public.addresses (
    ismain boolean NOT NULL,
    createdat timestamp(6) without time zone,
    id bigint NOT NULL,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    city character varying(255),
    country character varying(255),
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    phone character varying(255),
    picture character varying(255),
    postcode character varying(255),
    salutation character varying(255),
    signature character varying(255),
    street character varying(255),
    title character varying(255),
    type character varying(255)
);


CREATE TABLE public.adults (
    birdthdate date,
    createdat timestamp(6) without time zone,
    id bigint NOT NULL,
    secondarykey bigint,
    tamagotchiid bigint,
    updatedat timestamp(6) without time zone,
    education character varying(255),
    email character varying(255),
    firstname character varying(255),
    gender character varying(255),
    healthcondition character varying(255),
    lastname character varying(255),
    location character varying(255),
    maritalstatus character varying(255),
    phone character varying(255),
    picture character varying(255),
    preferredlanguage character varying(255),
    relationship character varying(255),
    interests character varying(255),
    ocupation character varying(255),
    personality character varying(255),
    ingestedat timestamp(6) without time zone,
    lat double precision,
    lon double precision,
    silmamode boolean,
    timelineid bigint,
    CONSTRAINT adults_gender_check CHECK (((gender)::text = ANY (ARRAY[('WOMAN'::character varying)::text, ('MAN'::character varying)::text])))
);

CREATE TABLE public.adults_adulttriages (
    adult_id bigint NOT NULL,
    triage_id bigint NOT NULL
);


CREATE TABLE public.adulttriages (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    anxiety integer,
    attention integer,
    communityinvolvement integer,
    confidence integer,
    continuousgrowth integer,
    exercise integer,
    explanation text,
    fear integer,
    healthycomunication integer,
    hope integer,
    loneliness integer,
    mentalstimulation integer,
    motivation integer,
    nutrition integer,
    relationships integer,
    routines integer,
    satisfaction integer,
    selfreflection integer,
    sleepquality integer,
    sourcetext text,
    stress integer,
    stressmanager integer,
    timeinnature integer,
    trust integer
);

CREATE TABLE public.apisourcedocuments (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    apisource character varying(255),
    description character varying(255),
    ingestedat timestamp(6) without time zone,
    lastcheck timestamp(6) without time zone,
    lat double precision,
    location character varying(255),
    lon double precision,
    text text,
    CONSTRAINT apisourcedocuments_apisource_check CHECK (((apisource)::text = ANY (ARRAY[('tweetsApis'::character varying)::text, ('weatherGoogleApis'::character varying)::text, ('placesGoogleApis'::character varying)::text, ('rssFeeds'::character varying)::text])))
);

CREATE TABLE public.books (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    chapter character varying(255),
    paragraph text,
    question character varying(255),
    section character varying(255)
);

CREATE TABLE public.calendarentries (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    allday boolean NOT NULL,
    backgroundcolor character varying(255),
    bordercolor character varying(255),
    color character varying(255),
    description character varying(255),
    displaymode character varying(255),
    durationeditable boolean NOT NULL,
    editable boolean NOT NULL,
    eventconstraint character varying(255),
    eventend timestamp(6) without time zone,
    eventstart timestamp(6) without time zone,
    groupid character varying(255),
    overlap boolean NOT NULL,
    recurringenddate date,
    recurringendtime character varying(255),
    recurringstartdate date,
    recurringstarttime character varying(255),
    starteditable boolean NOT NULL,
    textcolor character varying(255),
    title character varying(255)
);


CREATE TABLE public.calendarentry_recurringdaysofweek (
    calendarentry_id bigint NOT NULL,
    recurringdaysofweek character varying(255),
    CONSTRAINT calendarentry_recurringdaysofweek_recurringdaysofweek_check CHECK (((recurringdaysofweek)::text = ANY (ARRAY[('MONDAY'::character varying)::text, ('TUESDAY'::character varying)::text, ('WEDNESDAY'::character varying)::text, ('THURSDAY'::character varying)::text, ('FRIDAY'::character varying)::text, ('SATURDAY'::character varying)::text, ('SUNDAY'::character varying)::text])))
);


CREATE TABLE public.chatchannels (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    avatar character varying(255),
    lastmessage character varying(255),
    name character varying(255),
    newmessages integer NOT NULL,
    senderclass character varying(255),
    senderid bigint
);

CREATE TABLE public.chatlogs (
    createdat timestamp(6) without time zone,
    id bigint NOT NULL,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    message text,
    sender character varying(255),
    evaluatedat timestamp(6) without time zone
);

CREATE TABLE public.chatmessages (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    avatar character varying(255),
    isread boolean NOT NULL,
    message text,
    sender character varying(255),
    toid bigint,
    mark character varying(255)
);


CREATE TABLE public.histories (
    height integer,
    weight integer,
    createdat timestamp(6) without time zone,
    id bigint NOT NULL,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    mood character varying(255),
    note character varying(255),
    type character varying(255)
);

CREATE TABLE public.medias (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    authorname character varying(255),
    authorurl character varying(255),
    description text,
    mediaurl character varying(255),
    mimetype character varying(255),
    name character varying(255),
    thumbnailurl character varying(255),
    type character varying(255),
    tags character varying(255),
    ingestedat timestamp(6) without time zone,
    CONSTRAINT medias_type_check CHECK (((type)::text = ANY (ARRAY[('REMOTE'::character varying)::text, ('LOADED'::character varying)::text])))
);


CREATE TABLE public."public.tamagotchies" (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    updatedat timestamp(6) without time zone,
    age integer NOT NULL,
    alive boolean NOT NULL,
    avatar character varying(255),
    bedtime time(6) without time zone,
    cleanliness integer NOT NULL,
    currentemotion character varying(255),
    energy integer NOT NULL,
    experience integer NOT NULL,
    generalemotion integer NOT NULL,
    happiness integer NOT NULL,
    health integer NOT NULL,
    hunger integer NOT NULL,
    level integer NOT NULL,
    name character varying(255),
    sleeping boolean NOT NULL,
    weight integer NOT NULL,
    details text,
    personality character varying(255),
    strengths character varying(255),
    weaknesses character varying(255),
    secondarykey bigint
);

CREATE TABLE public.relatives (
    birdthdate date,
    createdat timestamp(6) without time zone,
    id bigint NOT NULL,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    contanctfrequency character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    location character varying(255),
    picture character varying(255),
    relation character varying(255),
    relationshipdistance character varying(255),
    interests character varying(255),
    ocupation character varying(255),
    lat double precision,
    lon double precision
);

CREATE TABLE public.reminders (
    addbyai boolean NOT NULL,
    quantity integer,
    calendarwhen timestamp(6) without time zone,
    createdat timestamp(6) without time zone,
    drug_id bigint,
    id bigint NOT NULL,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    calendarremind character varying(255),
    calendarrepeat character varying(255),
    contraindications character varying(255),
    cronstring character varying(255),
    dosage character varying(255),
    indications character varying(255),
    remindertype character varying(255),
    text character varying(255),
    CONSTRAINT reminders_calendarremind_check CHECK (((calendarremind)::text = ANY (ARRAY[('PT1M'::character varying)::text, ('PT5M'::character varying)::text, ('PT15M'::character varying)::text, ('PT30M'::character varying)::text, ('PT1H'::character varying)::text, ('PT4H'::character varying)::text, ('PT24H'::character varying)::text]))),
    CONSTRAINT reminders_calendarrepeat_check CHECK (((calendarrepeat)::text = ANY (ARRAY[('ONCE'::character varying)::text, ('DAILY'::character varying)::text, ('WEEK_DAY'::character varying)::text, ('WEEKLY'::character varying)::text, ('MONTHLY_DATE'::character varying)::text]))),
    CONSTRAINT reminders_remindertype_check CHECK (((remindertype)::text = ANY (ARRAY[('PERSONAL'::character varying)::text, ('MEDICATION'::character varying)::text, ('EXERCISE'::character varying)::text, ('SHOPPING'::character varying)::text, ('CALENDAR'::character varying)::text, ('DIET'::character varying)::text])))
);

CREATE TABLE public.rssfeeds (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    description character varying(255),
    imageurl character varying(255),
    language character varying(255),
    link character varying(255),
    title character varying(255),
    lastfeedcheck timestamp(6) without time zone,
    isactive boolean,
    lastfeeditems integer,
    ingestedat timestamp(6) without time zone
);


CREATE TABLE public.rssitems (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    author character varying(255),
    categories text,
    comments text,
    content text,
    description text,
    guid character varying(255),
    link text,
    pubdate timestamp(6) with time zone,
    title text,
    updateddate timestamp(6) with time zone
);


--
-- TOC entry 247 (class 1259 OID 16555)
-- Name: tamagotchies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tamagotchies (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    avatar character varying(255),
    communityinvolvement integer,
    conflict double precision NOT NULL,
    continuousgrowth integer,
    curiosity double precision NOT NULL,
    emotionalstatus double precision NOT NULL,
    exercise integer,
    healthycomunication integer,
    joy double precision NOT NULL,
    mentalburden double precision NOT NULL,
    mentalclarity double precision NOT NULL,
    mentalstimulation integer,
    motivation double precision NOT NULL,
    name character varying(255),
    nutrition integer,
    personality character varying(255),
    relationships integer,
    respect double precision NOT NULL,
    routines integer,
    selfreflection integer,
    sleepquality integer,
    specie character varying(255),
    status character varying(255),
    stressmanager integer,
    timeinnature integer,
    CONSTRAINT tamagotchies_specie_check CHECK (((specie)::text = ANY (ARRAY[('Robot'::character varying)::text, ('Cat'::character varying)::text, ('Dog'::character varying)::text]))),
    CONSTRAINT tamagotchies_status_check CHECK (((status)::text = ANY (ARRAY[('Sleeping'::character varying)::text, ('Awake'::character varying)::text])))
);

CREATE TABLE public.techs (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    abilityenergycost integer NOT NULL,
    cantargetterrain boolean NOT NULL,
    cantargetunit boolean NOT NULL,
    gasprice integer NOT NULL,
    mineralprice integer NOT NULL,
    name character varying(255),
    ordername character varying(255),
    race character varying(255),
    requiredbuildingname character varying(255),
    researchbuildingname character varying(255),
    researchtime integer NOT NULL,
    techuser text,
    weaponname character varying(255)
);

CREATE TABLE public.timelineslides (
    id bigint NOT NULL,
    createdat timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    autolink boolean,
    backgroundalt character varying(255),
    backgroundcolor character varying(255),
    backgroundurl character varying(1024),
    displaydate character varying(255),
    enddate date,
    groupid character varying(255),
    headline character varying(255),
    media character varying(1024),
    startdate date,
    text character varying(255),
    type character varying(255),
    lastupdated timestamp(6) without time zone,
    CONSTRAINT timelineslides_type_check CHECK (((type)::text = ANY (ARRAY[('TITLE'::character varying)::text, ('EVENT'::character varying)::text])))
);

CREATE TABLE public.tools (
    isenabled boolean NOT NULL,
    createdat timestamp(6) without time zone,
    id bigint NOT NULL,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    description character varying(255),
    name character varying(255)
);

CREATE TABLE public.user_adults (
    user_id bigint NOT NULL,
    adults bigint,
    adults_order integer NOT NULL
);

CREATE TABLE public.user_internalactions (
    user_id bigint NOT NULL,
    internalactions character varying(255)
);


CREATE TABLE public.user_newsletters (
    user_id bigint NOT NULL,
    newsletters bigint
);


CREATE TABLE public.userpreferences (
    createdat timestamp(6) without time zone,
    id bigint NOT NULL,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    classname character varying(255),
    view character varying(255)
);


CREATE TABLE public.users (
    failedattemptscount integer NOT NULL,
    isemailverified boolean NOT NULL,
    isenabled boolean NOT NULL,
    ismfaactive boolean NOT NULL,
    istemporalpass boolean NOT NULL,
    istotpregistred boolean NOT NULL,
    rowsperpage integer NOT NULL,
    sessionid integer NOT NULL,
    createdat timestamp(6) without time zone,
    currentadultid bigint,
    id bigint NOT NULL,
    lastsignin timestamp(6) without time zone,
    secondarykey bigint,
    updatedat timestamp(6) without time zone,
    avatar character varying(255),
    city character varying(255),
    country character varying(255),
    email character varying(255),
    firstname character varying(255),
    invitationrequesturl character varying(255),
    lastname character varying(255),
    password character varying(255),
    phone character varying(255),
    postcode character varying(255),
    preferredlanguage character varying(255),
    preferredtheme character varying(255),
    salutation character varying(255),
    street character varying(255),
    type character varying(255),
    username character varying(255),
    usersecret character varying(255),
    role_id bigint,
    communicationchannel character varying(255),
    lastnewsletter timestamp(6) without time zone,
    status character varying(255),
    CONSTRAINT users_status_check CHECK (((status)::text = ANY (ARRAY[('UNVERIFIED'::character varying)::text, ('INCOMPLETE'::character varying)::text, ('VERIFIED'::character varying)::text, ('BLOCKED'::character varying)::text])))
);



use dsoni;


/*-------------------  Creating person table ------------------------*/

create table person (
    person_id int auto_increment primary key,
    name varchar(75) not null
);

create table person_metadata (
    person_id int,
    birth_location varchar(75),
    death_location varchar(75),
    birth_day varchar(20),
    birth_month varchar(20),
    birth_year varchar(20),
    death_day varchar(20),
    death_month varchar(20),
    death_year varchar(20),
    gender varchar(75),
    occupation varchar(75),
    constraint foreign key (person_id) references person(person_id) on delete no action
);

/*------------------------ Creating relationship_code to relationship_type table-----------------------------*/

create table relation_type (
    relationship_code int(10) primary key,
    relationship_type varchar(75) not null
);

/*------------------------ Creating relationship table----------------------------*/

create table relationship (
    person_id_1 int,
    person_id_2 int,
    relationship_code int (10),
    constraint foreign key (person_id_1) references person(person_id) on delete no action,
	constraint foreign key (person_id_2) references person(person_id) on delete no action,
    constraint foreign key (relationship_code) references relation_type(relationship_code) on delete no action
);

/*------------------------ Creating Notes and References table----------------------------*/

create table person_notesandreference (
	nr_id int not null auto_increment primary key, 
    person_id int,
    type varchar(75),
    type_content varchar(500),
    constraint foreign key (person_id) references person(person_id) on delete no action
);

/*--------------------- Creating table for media ---------------------*/

create table media_data(
    media_file_id int primary key auto_increment,
    media_file_name varchar(255),
    media_file_location varchar(255) unique not null,
    media_location varchar(255),
	date_last_updated varchar(75)
);


/*--------------------- Creating table for media_tags---------------------*/

create table media_tags(
	media_file_id int,
    tag varchar(500),
    constraint foreign key (media_file_id) references media_data(media_file_id) on delete no action
);


/*--------------------- Creating table for person shown in media---------------------*/

create table person_in_media(
	media_file_id int,
    person_id int,
    constraint foreign key (media_file_id) references media_data(media_file_id) on delete no action,
    constraint foreign key (person_id) references person(person_id) on delete no action
);













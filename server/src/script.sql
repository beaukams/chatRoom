drop database if exists chat;
create database chat;
use chat;
create table utilisateur(
	id_user int auto_increment,
	pseudo varchar(20) unique,
	nom varchar(20),
	prenom varchar(20),
        password varchar(50),
	adresse_ip varchar(15),
	port int,
	date_connexion date,
	constraint primary key pk_user (id_user)
);

create table contact(
    id_contact int,
    id_user int,
    constraint foreign key (id_user) references utilisateur(id_user) on delete cascade
);

create table room (
    id_room int auto_increment primary key,
    nom_room varchar(20),
    id_user int,
    id_contact int,
    constraint foreign key (id_user) references utilisateur(id_user) on delete cascade
);


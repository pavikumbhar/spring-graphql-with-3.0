  create table public.operating_system (
        id bigint generated by default as identity,
        kernel varchar(255) not null,
        name varchar(255) not null,
        release_date timestamp(6) not null,
        usages integer not null,
        version varchar(255) not null,
		active boolean not null,
        primary key (id)
    );

INSERT INTO operating_system (id, name, version, kernel, release_date, usages,active) VALUES (1, 'Arch Linux', '2022.03.01', '5.16.11', {ts '2022-03-01 00:10:00.69'}, 80,true);
INSERT INTO operating_system (id, name, version, kernel, release_date, usages,active) VALUES (2, 'Ubuntu', '20.04.4 LTS', '5.8', {ts '2022-02-22 00:10:00.69'}, 128,true);
INSERT INTO operating_system (id, name, version, kernel, release_date, usages,active) VALUES (3, 'Ubuntu', '21.10', '5.13', {ts '2022-01-28 00:10:00.69'}, 110,true);
INSERT INTO operating_system (id, name, version, kernel, release_date, usages,active) VALUES (4, 'CentOS', '7', '5.8', {ts '2020-11-12 00:10:00.69'}, 200,true);
INSERT INTO operating_system (id, name, version, kernel, release_date, usages,active) VALUES (5, 'CentOS', '8', '5.13', {ts '2021-11-12 00:10:00.69'}, 176,true);
INSERT INTO operating_system (id, name, version, kernel, release_date, usages,active) VALUES (6, 'EndeavourOS', '21.5', '5.15.8', {ts '2022-03-03 00:10:00.69'}, 93,true);
INSERT INTO operating_system (id, name, version, kernel, release_date, usages,active) VALUES (7, 'Deepin', '20.2.4', '5.13', {ts '2022-03-11 00:10:00.69'}, 76,true);
INSERT INTO operating_system (id, name, version, kernel, release_date, usages,active) VALUES (8, 'Deepin', '20.2.2', '5.8', {ts '2022-01-11 00:10:00.69'}, 121,true);
INSERT INTO operating_system (id, name, version, kernel, release_date, usages,active) VALUES (9, 'Red Hat', '7.9', '5.13', {ts '2022-02-01 00:10:00.69'}, 329,true);
INSERT INTO operating_system (id, name, version, kernel, release_date, usages,active) VALUES (10, 'Red Hat', '8', '5.16.11', {ts '2022-03-20 00:10:00.69'}, 283,true);
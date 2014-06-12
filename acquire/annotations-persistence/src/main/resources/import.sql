-- You can use this file to load seed data into the database using SQL statements
-- insert into Member (id, name, email, phone_number) values (0, 'John Smith', 'john.smith@mailinator.com', '2125551212')
insert into mstaging (id, name, version) values (MSTAGING_SEQ.nextval, 'pM0', 0);
insert into mstaging (id, name, version) values (2, 'pM1', 0);
insert into mstaging (id, name, version) values (3, 'pM1a', 0);
insert into mstaging (id, name, version) values (4, 'pM1b', 0);
insert into mstaging (id, name, version) values (5, 'pMX', 0);
insert into mstaging (id, name, version) values (6, 'Other', 0);

insert into nstaging (id, name, version) values (NSTAGING_SEQ.nextval, 'pN0', 0);
insert into nstaging (id, name, version) values (2, 'pN1', 0);
insert into nstaging (id, name, version) values (3, 'pN1a', 0);
insert into nstaging (id, name, version) values (4, 'pN1b', 0);
insert into nstaging (id, name, version) values (5, 'pN2', 0);
insert into nstaging (id, name, version) values (6, 'pN2a', 0);
insert into nstaging (id, name, version) values (7, 'pN2b', 0);
insert into nstaging (id, name, version) values (8, 'pN3', 0);
insert into nstaging (id, name, version) values (9, 'pN3a', 0);
insert into nstaging (id, name, version) values (10, 'pN3b', 0);
insert into nstaging (id, name, version) values (11, 'pNX', 0);
insert into nstaging (id, name, version) values (12, 'Other', 0);

insert into tstaging (id, name, version) values (TSTAGING_SEQ.nextval, 'pT0', 0);
insert into tstaging (id, name, version) values (2, 'pT1', 0);
insert into tstaging (id, name, version) values (3, 'pT1a', 0);
insert into tstaging (id, name, version) values (4, 'pT1b', 0);
insert into tstaging (id, name, version) values (5, 'pT2', 0);
insert into tstaging (id, name, version) values (6, 'pT2a', 0);
insert into tstaging (id, name, version) values (7, 'pT2b', 0);
insert into tstaging (id, name, version) values (8, 'pT3', 0);
insert into tstaging (id, name, version) values (9, 'pT3a', 0);
insert into tstaging (id, name, version) values (10, 'pT3b', 0);
insert into tstaging (id, name, version) values (11, 'pT3c', 0);
insert into tstaging (id, name, version) values (12, 'pT4', 0);
insert into tstaging (id, name, version) values (13, 'pT4a', 0);
insert into tstaging (id, name, version) values (14, 'pT4b', 0);
insert into tstaging (id, name, version) values (15, 'pTis', 0);
insert into tstaging (id, name, version) values (16, 'pTX', 0);
insert into tstaging (id, name, version) values (17, 'Other', 0);

insert into tumorstage (id, name, version) values (TUMOR_STAGE_SEQ.nextval, '0', 0);
insert into tumorstage (id, name, version) values (2, 'I', 0);
insert into tumorstage (id, name, version) values (3, 'II', 0);
insert into tumorstage (id, name, version) values (4, 'III', 0);
insert into tumorstage (id, name, version) values (5, 'IV', 0);
insert into tumorstage (id, name, version) values (6, 'Unknown', 0);
insert into tumorstage (id, name, version) values (7, 'Other', 0);

insert into tumorgrade (id, name, version) values (TUMOR_GRADE_SEQ.nextval, 'G1', 0);
insert into tumorgrade (id, name, version) values (2, 'G2', 0);
insert into tumorgrade (id, name, version) values (3, 'G3', 0);
insert into tumorgrade (id, name, version) values (4, 'G4', 0);
insert into tumorgrade (id, name, version) values (5, 'GX', 0);
insert into tumorgrade (id, name, version) values (6, 'Low', 0);
insert into tumorgrade (id, name, version) values (7, 'High', 0);



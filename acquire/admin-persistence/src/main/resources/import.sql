-- You can use this file to load seed data into the database using SQL statements
-- insert into Member (id, name, email, phone_number) values (0, 'John Smith', 'john.smith@mailinator.com', '2125551212') 
insert into identityobjectrelationshiptype (id, name) values (RELATIONSHIP_TYPE_SEQ.nextval, 'JBOSS_IDENTITY_MEMBERSHIP');
insert into identityobjectrelationshiptype (id, name) values (2, 'JBOSS_IDENTITY_ROLE');
insert into identityobjecttype (id, name) values (ID_TYPE_SEQ.nextval, 'USER');
insert into identityobjecttype (id, name) values (2, 'PROGRAM');
insert into identityobjecttype (id, name) values (3, 'COLLECTION_SITE');
insert into identityobject (id, name, identity_object_type_id) select 7, 'Program Name', id from identityobjecttype where name = 'PROGRAM';
insert into identityobject (id, name, identity_object_type_id) select IDENTITY_SEQ.nextval, 'admin@admin.com', id from identityobjecttype where name = 'USER';
insert into identityrolename (id, name) values (ROLE_SEQ.nextval, 'Admin');
insert into identityrolename (id, name) values (2, 'PHI');
insert into identityrolename (id, name) values (3, 'Non PHI');
insert into identityrolename (id, name) values (4, 'Public');
insert into identityrolename (id, name) values (5, 'Announcement Management');
insert into identityrolename (id, name) values (6, 'Pathology Tab');
insert into identityrolename (id, name) values (8, 'RAC Committee');
insert into identityrolename (id, name) values (9, 'RAC Chair');
insert into identityrolename (id, name) values (10, 'Shipment Form');
insert into identityobjectrelationship (id, name, from_identity_id, relationship_type_id, to_identity_id) values(RELATIONSHIP_SEQ.nextval, 'Admin', 7, 2, 1);




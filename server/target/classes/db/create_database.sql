-- Script to create the user and database for the project
CREATE ROLE theknife 
    WITH 
    LOGIN 
    ENCRYPTED PASSWORD 'password' 
    CREATEDB;
CREATE DATABASE theknife_db 
    WITH 
    OWNER = theknife 
    ENCODING = 'UTF8';

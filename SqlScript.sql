-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS HMSDB DEFAULT CHARACTER SET utf8 ;
USE HMSDB ;

-- -----------------------------------------------------
-- Table mydb.admin
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS HMSDB.admin (
  admin_id INT NOT NULL auto_increment,
  admin_fname VARCHAR(45) NULL,
  admin_lname VARCHAR(45) NULL,
  admin_email VARCHAR(255) NULL,
  admin_password VARCHAR(255) NULL,
  PRIMARY KEY (admin_id),
  UNIQUE INDEX admin_id_UNIQUE (admin_id ASC) VISIBLE,
  UNIQUE INDEX admin_email_UNIQUE (admin_email ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table mydb.customer
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS HMSDB.customer (
  customer_id INT NOT NULL auto_increment,
  customer_fname VARCHAR(45) NULL,
  customer_lname VARCHAR(45) NULL,
  customer_email VARCHAR(255) NULL,
  customer_password VARCHAR(255) NULL,
  customer_phone VARCHAR(45) NULL,
  customer_balance INT NULL,
  PRIMARY KEY (customer_id),
  UNIQUE INDEX customer_email_UNIQUE (customer_email ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table mydb.receptionist
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS HMSDB.receptionist (
  receptionist_id INT NOT NULL auto_increment,
  receptionist_fname VARCHAR(45) NULL,
  receptionist_lname VARCHAR(45) NULL,
  receptionist_email VARCHAR(255) NULL,
  receptionist_password VARCHAR(255) NULL,
  PRIMARY KEY (receptionist_id),
  UNIQUE INDEX receptionist_email_UNIQUE (receptionist_email ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table mydb.room
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS HMSDB.room (
  room_number INT NOT NULL auto_increment,
  room_floor INT NOT NULL,
  room_type VARCHAR(45) NULL,
  room_status boolean NULL,
  PRIMARY KEY (room_number))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table mydb.booking
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS HMSDB.booking (
  booking_id INT NOT NULL auto_increment,
  receptionist_id INT NOT NULL,
  customer_id INT NOT NULL,
  room_number INT NOT NULL,
  check_in_date TIMESTAMP NULL,
  check_out_date DATETIME NULL,
  INDEX fk_books_customer1_idx (customer_id ASC) VISIBLE,
  PRIMARY KEY (booking_id),
  INDEX fk_booking_receptionist1_idx (receptionist_id ASC) VISIBLE,
  INDEX fk_booking_room1_idx (room_number ASC) VISIBLE,
  CONSTRAINT fk_books_customer1
    FOREIGN KEY (customer_id)
    REFERENCES HMSDB.customer (customer_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_booking_receptionist1
    FOREIGN KEY (receptionist_id)
    REFERENCES HMSDB.receptionist (receptionist_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_booking_room1
    FOREIGN KEY (room_number)
    REFERENCES HMSDB.room (room_number)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table mydb.billing
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS HMSDB.billing (
  billing_id INT NOT NULL auto_increment,
  booking_id INT NOT NULL,
  billing_status boolean NULL,
  PRIMARY KEY (billing_id),
  INDEX fk_billing_booking1_idx (booking_id ASC) VISIBLE,
  CONSTRAINT fk_billing_booking1
    FOREIGN KEY (booking_id)
    REFERENCES HMSDB.booking (booking_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table mydb.room_log
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS HMSDB.room_log (
  log_id INT NOT NULL auto_increment,
  admin_id INT NOT NULL,
  room_number INT NOT NULL,
  action_type VARCHAR(45) NULL,
  action_date VARCHAR(45) NULL,
  INDEX fk_managing_admin1_idx (admin_id ASC) VISIBLE,
  INDEX fk_room_log_room1_idx (room_number ASC) VISIBLE,
  PRIMARY KEY (log_id),
  CONSTRAINT fk_managing_admin1
    FOREIGN KEY (admin_id)
    REFERENCES HMSDB.admin (admin_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_room_log_room1
    FOREIGN KEY (room_number)
    REFERENCES HMSDB.room (room_number)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table rates
-- -----------------------------------------------------
CREATE TABLE HMSDB.rates (
  room_type VARCHAR(45) PRIMARY KEY,
  room_rate INT
);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

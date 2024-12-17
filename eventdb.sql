--
-- File generated with SQLiteStudio v3.4.7 on Tue Dec 17 16:54:22 2024
--
-- Text encoding used: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: cities
CREATE TABLE IF NOT EXISTS cities (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT NOT NULL UNIQUE
              );
INSERT INTO cities (id, name) VALUES (1, 'Claveria');
INSERT INTO cities (id, name) VALUES (2, 'Lagonglong');
INSERT INTO cities (id, name) VALUES (3, 'Balingasag');
INSERT INTO cities (id, name) VALUES (4, 'Laguidingan');
INSERT INTO cities (id, name) VALUES (5, 'Villanueva');
INSERT INTO cities (id, name) VALUES (6, 'Tagoloan');
INSERT INTO cities (id, name) VALUES (7, 'Salay');
INSERT INTO cities (id, name) VALUES (8, 'Talisayan');
INSERT INTO cities (id, name) VALUES (9, 'Jasaan');
INSERT INTO cities (id, name) VALUES (10, 'Opol');
INSERT INTO cities (id, name) VALUES (11, 'Gitagum');
INSERT INTO cities (id, name) VALUES (12, 'Initao');
INSERT INTO cities (id, name) VALUES (13, 'Balingoan');
INSERT INTO cities (id, name) VALUES (14, 'Medina');
INSERT INTO cities (id, name) VALUES (15, 'Alubijid');
INSERT INTO cities (id, name) VALUES (16, 'Kinoguitan');
INSERT INTO cities (id, name) VALUES (17, 'Magsaysay');
INSERT INTO cities (id, name) VALUES (18, 'Manticao');
INSERT INTO cities (id, name) VALUES (19, 'Naawan');
INSERT INTO cities (id, name) VALUES (20, 'Sugbongcogon');

-- Table: events
CREATE TABLE IF NOT EXISTS events (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              city_id INTEGER NOT NULL,
              name TEXT NOT NULL,
              time TEXT NOT NULL,
              date TEXT NOT NULL,
              place TEXT NOT NULL,
              description TEXT NOT NULL,
              FOREIGN KEY (city_id) REFERENCES cities (id) ON DELETE CASCADE,
              CONSTRAINT unique_event UNIQUE (city_id, name, place, date)
              );
INSERT INTO events (id, city_id, name, time, date, place, description) VALUES (2, 1, 'Basketball league', '10:00 PM', '2024-12-17', 'Municapl Plaza', 'witness how to physical the pilipino play basketball');
INSERT INTO events (id, city_id, name, time, date, place, description) VALUES (3, 1, 'Maskara Republic', '7:00 PM', '2025-02-11', 'Munical Plaza', 'Enjoy and vibe with out local band');
INSERT INTO events (id, city_id, name, time, date, place, description) VALUES (4, 1, 'X-mas Fireworks Display', '12:00 AM', '2024-12-25', 'Old municipality', 'witness the wonderful fireworks display');
INSERT INTO events (id, city_id, name, time, date, place, description) VALUES (5, 2, 'New Year Fireworks', '12:00 AM', '2024-01-01', 'Lagonglong Ground', 'witness the colorful wireworks');

-- Table: pinned
CREATE TABLE IF NOT EXISTS pinned(
eventID INTEGER NOT NULL,
userID INTEGER NOT NULL,
FOREIGN KEY (eventID) REFERENCES events (id) ON DELETE CASCADE,
FOREIGN KEY (userID) REFERENCES users (id) ON DELETE CASCADE,
CONSTRAINT unique_event UNIQUE (eventID,userID));
INSERT INTO pinned (eventID, userID) VALUES (2, 3);

-- Table: users
CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        username TEXT NOT NULL UNIQUE,
        password TEXT NOT NULL,
        role TEXT NOT NULL CHECK (role IN ('admin', 'user')),
        nickname TEXT,
        contact TEXT,
        profile_pic BLOB);
INSERT INTO users (id, username, password, role, nickname, contact, profile_pic) VALUES (1, 'admin', '0jNr+9SmBVXzbf/QVjCHow==:Bp6tJddrMEQDaNidme/jeQ==', 'admin', NULL, NULL, NULL);
INSERT INTO users (id, username, password, role, nickname, contact, profile_pic) VALUES (2, 'hello', 'VAAljXRa1unRZbI0AtooUg==:oZckT/QMY08ln9kHe5+3aA==', 'user', 'Einstien', 'Gwapo', NULL);
INSERT INTO users (id, username, password, role, nickname, contact, profile_pic) VALUES (3, 'JuanDelacruz', 'zvbBGo4LCl2qxTU/t/dRfg==:jCJIDVENWGqKHqDTRVLYGw==', 'user', 'Juan Dela Cruz', 'pogi pogi pogi', NULL);
INSERT INTO users (id, username, password, role, nickname, contact, profile_pic) VALUES (4, 'ullah', 'SZD+/E2KoLO9NSv6xXyp2g==:LdgA3v5Ya5sHOuRvLQpLyg==', 'user', NULL, NULL, NULL);
INSERT INTO users (id, username, password, role, nickname, contact, profile_pic) VALUES (5, 'gripp', 'QNCOgbwVJdFwVQuBao6M1Q==:wlB7RFmSWdUNizRavm0qcg==', 'user', 'Grifaith', 'Gwapo', NULL);

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;

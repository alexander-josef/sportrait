--
-- The data-stup scripts is needed in order to initialize a fresh database with the necessary user profiles, product/price data etc.
-- To be executed after the schema-setup script that is automatically generated
--
-- Additionally, the add_on script should be executed to fix the sequences
--
-- Alexander Josef, unartig AG 2007
--


--
-- Userprofile
--
INSERT INTO userprofiles (userprofileid, username, "password", firstname, lastname, emailaddress, phone, phonemobile, title, addr1, addr2, zipcode, city, state, country, gender) VALUES (1, 'admin', 'le0nard', 'Sportrait Admin', 'unartig AG', 'admin@unartig.ch', '23', '23', 'Mischter', 'adsf', 'adsf', '2232', 'zueri', 'zh', 'ch', 'm');

--
-- Userroles
--
INSERT INTO userroles (userroleid, rolename, roledescription) VALUES (1, 'unartigadmin', 'admin fuer die gesamte plattform');
INSERT INTO userroles (userroleid, rolename, roledescription) VALUES (2, 'photographer', 'sportrait photographer');


--
-- Userprofile2roles
--
INSERT INTO userprofiles2userroles (username, rolename) VALUES ('admin', 'unartigadmin');
INSERT INTO userprofiles2userroles (username, rolename) VALUES ('admin', 'photographer');
-- Completed on 2007-05-04 20:25:35 W. Europe Daylight Time

--
-- Photographers (IMPORTANT: needed for every userprofile)
--
INSERT INTO photographers VALUES (1,'Nikon D2xs', 'www.sportrait.com', 'unartig AG, 8006 Zuerich / Switzerland');
--
-- Set up categories (Sportarten)
--
INSERT INTO GenericLevels (genericlevelid, hierarchy_level,navtitle,longtitle,description) VALUES (1, 'CATEGORY','festliche_anlaesse','Festliche Anlaesse','Festliche Anlaesse Kategorie');
INSERT INTO GenericLevels (genericlevelid, hierarchy_level,navtitle,longtitle,description) VALUES (2, 'CATEGORY','sportliche_anlaesse','Sportliche Anlaesse','Sportliche Anlaesse Kategorie');
--
--
--

--
-- productType
--
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (2, 'Digital (400x600 Pixel)', 'digital foto for download 400 by 600 pixels', true);
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (3, 'Digital (hochaufgeloest)', 'digital foto file for download in the native resolution', true);
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (4, '10x15cm Abzug', 'paper print 10 x 15 cm provided by colorplaza', false);
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (5, '11x17cm Abzug', 'paper print 11 x 17 cm  provided by colorplaza', false);
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (6, '13x19cm Abzug', 'paper print 13 x 19 cm provided by colorplaza', false);
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (7, '20x30cm Abzug', 'paper print 20 x 30 cm provided by colorplaza', false);
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (8, '30x45cm Poster', 'poster 30 x 45 cm provided by colorplaza', false);
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (9, '40x60cm Poster', 'poster 40 x 60 cm provided by colorplaza', false);
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (10, '50x75cm Poster', 'poster 50 x 75 cm provided by colorplaza', false);
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (11, 'T-Shirt', 'T-Shirt provided by colorplaza', false);
INSERT INTO producttypes (producttypeid, name, description, digitalproduct) VALUES (12, 'Mousepad', 'mousepad provided by colorplaza', false);
-- Completed on 2007-05-03 20:01:35 W. Europe Daylight Time

--
-- prices
--
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (1, 5.00, 3.30, 2.30, 29.50, '23x2-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (8, 39.50, 26.00, 18.00, 235.00, '23x2-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (7, 54.00, 35.90, 24.00, 319.00, '23x2-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (6, 42.00, 27.50, 18.90, 249.00, '23x2-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (5, 29.00, 18.90, 12.90, 169.00, '23x2-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (4, 14.00, 8.90, 6.20, 82.00, '23x2-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (3, 7.00, 4.60, 3.20, 42.00, '23x2-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (2, 5.50, 3.60, 2.45, 32.50, '23x2-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (9, 29.00, 19.00, 12.90, 179.00, '23x2-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (10, 10.00, 6.50, 4.40, 59.00, '251x-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (18, 39.00, 26.00, 17.50, 230.00, '251x-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (17, 49.50, 32.00, 22.00, 290.00, '251x-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (16, 99.00, 65.00, 44.00, 580.00, '251x-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (15, 79.00, 52.00, 35.00, 470.00, '251x-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (14, 49.00, 32.00, 22.00, 290.00, '251x-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (13, 28.00, 18.00, 12.50, 165.00, '251x-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (12, 14.00, 9.50, 6.20, 84.00, '251x-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (11, 12.00, 7.80, 5.30, 69.00, '251x-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (19, 0.50, 0.29, 0.20, 2.55, '21xx-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (20, 0.55, 0.32, 0.21, 2.65, '21xx-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (21, 1.10, 0.75, 0.55, 6.45, '21xx-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (22, 3.90, 2.30, 1.70, 19.95, '21xx-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (23, 17.00, 11.50, 8.00, 79.00, '21xx-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (24, 27.00, 15.50, 12.00, 109.00, '21xx-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (25, 37.00, 21.50, 16.00, 179.00, '21xx-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (26, 29.50, 17.90, 11.50, 139.00, '21xx-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (27, 16.00, 9.00, 6.00, 79.00, '21xx-er Liste');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (28, 2.90, 1.90, 1.10, 14.30, 'unartig price, digital product');
INSERT INTO prices (priceid, pricechf, priceeur, pricegbp, pricesek, "comment") VALUES (29, 9.90, 6.50, 4.60, 48.80, 'unartig price, digital');
-- Completed on 2007-05-03 19:59:00 W. Europe Daylight Time

--
-- Prices2Producttypes
--
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (2, 28);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (2, 22);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (2, 19);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (3, 26);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (3, 29);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (3, 2);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (4, 1);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (4, 10);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (4, 19);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (5, 2);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (5, 11);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (5, 20);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (6, 3);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (6, 12);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (6, 21);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (7, 4);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (7, 13);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (7, 22);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (8, 5);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (8, 14);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (8, 23);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (9, 6);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (9, 15);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (9, 24);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (10, 7);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (10, 16);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (10, 25);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (12, 9);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (12, 18);
INSERT INTO prices2producttypes (producttypeid, priceid) VALUES (12, 27);
-- Completed on 2007-05-03 20:03:47 W. Europe Daylight Time
--
-- PostgreSQL sportrait database : initial data setup
--

-- To be applied after step 02 - schema creation

-- use this script after a new database schema for sportrait has been freshly setup. It contains all initially needed
-- configuration data

-- Dumped from database version 9.5.12
-- Dumped by pg_dump version 9.5.12


-- use db "sportrait" with user "sportrait"
\c sportrait sportrait


SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;





--
-- Data for Name: prices; Type: TABLE DATA; Schema: public; Owner: sportrait
--


COPY public.prices (priceid, pricechf, priceeur, pricegbp, pricesek, comment) FROM stdin;
1	5.00	3.30	2.30	29.50	23x2-er Liste
8	39.50	26.00	18.00	235.00	23x2-er Liste
7	54.00	35.90	24.00	319.00	23x2-er Liste
6	42.00	27.50	18.90	249.00	23x2-er Liste
5	29.00	18.90	12.90	169.00	23x2-er Liste
4	14.00	8.90	6.20	82.00	23x2-er Liste
3	7.00	4.60	3.20	42.00	23x2-er Liste
2	5.50	3.60	2.45	32.50	23x2-er Liste
9	29.00	19.00	12.90	179.00	23x2-er Liste
10	10.00	6.50	4.40	59.00	251x-er Liste
18	39.00	26.00	17.50	230.00	251x-er Liste
17	49.50	32.00	22.00	290.00	251x-er Liste
16	99.00	65.00	44.00	580.00	251x-er Liste
15	79.00	52.00	35.00	470.00	251x-er Liste
14	49.00	32.00	22.00	290.00	251x-er Liste
13	28.00	18.00	12.50	165.00	251x-er Liste
12	14.00	9.50	6.20	84.00	251x-er Liste
11	12.00	7.80	5.30	69.00	251x-er Liste
19	0.50	0.29	0.20	2.55	21xx-er Liste
20	0.55	0.32	0.21	2.65	21xx-er Liste
21	1.10	0.75	0.55	6.45	21xx-er Liste
22	3.90	2.30	1.70	19.95	21xx-er Liste
23	17.00	11.50	8.00	79.00	21xx-er Liste
24	27.00	15.50	12.00	109.00	21xx-er Liste
25	37.00	21.50	16.00	179.00	21xx-er Liste
26	29.50	17.90	11.50	139.00	21xx-er Liste
27	16.00	9.00	6.00	79.00	21xx-er Liste
28	2.90	1.90	1.10	14.30	unartig price, digital product
29	9.90	6.50	4.60	48.80	unartig price, digital
30	0.00	0.00	0.00	0.00	free download
\.


--
-- Data for Name: producttypes; Type: TABLE DATA; Schema: public; Owner: sportrait
--

COPY public.producttypes (producttypeid, name, description, digitalproduct) FROM stdin;
2	Digital (400x600 Pixel)	digital foto for download 400 by 600 pixels	t
3	Digital (hochaufgeloest)	digital foto file for download in the native resolution	t
4	10x15cm Abzug	paper print 10 x 15 cm provided by colorplaza	f
5	11x17cm Abzug	paper print 11 x 17 cm  provided by colorplaza	f
6	13x19cm Abzug	paper print 13 x 19 cm provided by colorplaza	f
7	20x30cm Abzug	paper print 20 x 30 cm provided by colorplaza	f
8	30x45cm Poster	poster 30 x 45 cm provided by colorplaza	f
9	40x60cm Poster	poster 40 x 60 cm provided by colorplaza	f
10	50x75cm Poster	poster 50 x 75 cm provided by colorplaza	f
11	T-Shirt	T-Shirt provided by colorplaza	f
12	Mousepad	mousepad provided by colorplaza	f
\.



--
-- Data for Name: userprofiles; Type: TABLE DATA; Schema: public; Owner: sportrait
-- table contains google ID for google auth
--

COPY public.userprofiles (userprofileid, username, password, firstname, lastname, emailaddress, phone, phonemobile, title, addr1, addr2, zipcode, city, state, country, gender) FROM stdin;
1	106605843591930483394	le0nard	SPORTrait	unartig AG	admin@unartig.ch	23	23	Mischter	adsf	adsf	2232	zueri	zh	ch	m
2	108686018750314217878	blabla	Philipp	Knellwolf	philipp@knellwolf.net	444444	44444	\N	44444		9000	St. Gallen	\N	CH	m
\.


--
-- Data for Name: photographers; Type: TABLE DATA; Schema: public; Owner: sportrait
--

COPY public.photographers (photographerid, cameramodel, website, contactinformation) FROM stdin;
1	Nikon Z6	www.sportrait.com	unartig AG, 8006 Zuerich / Switzerland
2	Nikon D5	www.sportrait.com	unartig AG, 8006 Zuerich / Switzerland
\.


--
-- Data for Name: prices2producttypes; Type: TABLE DATA; Schema: public; Owner: sportrait
--

COPY public.prices2producttypes (producttypeid, priceid) FROM stdin;
2	28
2	22
2	19
3	26
3	29
3	2
4	1
4	10
4	19
5	2
5	11
5	20
6	3
6	12
6	21
7	4
7	13
7	22
8	5
8	14
8	23
9	6
9	15
9	24
10	7
10	16
10	25
12	9
12	18
12	27
3	30
\.


--
-- Data for Name: userprofiles2userroles; Type: TABLE DATA; Schema: public; Owner: sportrait
--

COPY public.userprofiles2userroles (username, rolename) FROM stdin;
1	unartigadmin
1	photographer
2	unartigadmin
\.


--
-- Data for Name: userroles; Type: TABLE DATA; Schema: public; Owner: sportrait
--

COPY public.userroles (userroleid, rolename, roledescription) FROM stdin;
1	unartigadmin	admin fuer die gesamte plattform
2	photographer	sportrait photographer
\.



--
-- Name: sequence_photographerid; Type: SEQUENCE SET; Schema: public; Owner: sportrait
--


--
-- Name: sequence_priceid; Type: SEQUENCE SET; Schema: public; Owner: sportrait
--

SELECT pg_catalog.setval('public.sequence_priceid', 100, false);




--
-- Name: sequence_producttypeid; Type: SEQUENCE SET; Schema: public; Owner: sportrait
--

SELECT pg_catalog.setval('public.sequence_producttypeid', 100, false);


--
-- Name: sequence_userprofileid; Type: SEQUENCE SET; Schema: public; Owner: sportrait
--

SELECT pg_catalog.setval('public.sequence_userprofileid', 100, true);


--
-- Name: sequence_userroleid; Type: SEQUENCE SET; Schema: public; Owner: sportrait
--

SELECT pg_catalog.setval('public.sequence_userroleid', 100, false);



--
-- PostgreSQL database creation for initial sportrait setup data complete
--


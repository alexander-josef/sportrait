--
-- PostgreSQL sportrait database docker container : initial test data setup
--

-- to be run after step 03 - initial setup stub data creation (admin users, products and necessary mappings, etc.)


-- use db "sportrait" with user "sportrait"

\c sportrait sportrait


SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;



--
-- Data for Name: genericlevels; Type: TABLE DATA; Schema: public; Owner: sportrait
-- Keep order of the following three insert in order to avoid foreign key constraint errors!
--

COPY public.genericlevels (genericlevelid, hierarchy_level, navtitle, longtitle, description, quickaccess, isprivate, publish, privateaccesscode, categoryid, zipcode, city, eventdate, weblink, eventgroupid, albumtypestring, photographerid, eventid, eventcategoryid) FROM stdin;
2	EVENTGROUP	\N	\N	\N	\N	\N	f	\N	\N	9500	Wil	\N	\N	\N	\N	\N	\N	\N
1	SPORTSEVENT	anlass-1-mb-pro-2018	Anlass 1	\N	\N	\N	f	\N	\N	\N	\N	2018-05-20	www.sportrait.com	2	\N	\N	\N	\N
\.

--
-- Data for Name: eventcategories; Type: TABLE DATA; Schema: public; Owner: sportrait
--

COPY public.eventcategories (eventcategoryid, title, description, eventid, category_position) FROM stdin;
1	uno	\N	1	0
2	duo	\N	1	1
3	tre	\N	1	2
4	quattro	\N	1	3
5	cinque	\N	1	4
\.



--
-- Data for Name: genericlevels; Type: TABLE DATA; Schema: public; Owner: sportrait
--

COPY public.genericlevels (genericlevelid, hierarchy_level, navtitle, longtitle, description, quickaccess, isprivate, publish, privateaccesscode, categoryid, zipcode, city, eventdate, weblink, eventgroupid, albumtypestring, photographerid, eventid, eventcategoryid) FROM stdin;
3	SPORTSALBUM	uno	uno	anlass-1-mb-pro-2018; uno	\N	\N	t	\N	\N	\N	\N	\N	\N	\N	\N	1	1	1
\.



--
-- Data for Name: photos; Type: TABLE DATA; Schema: public; Owner: sportrait
--

COPY public.photos (photoid, filename, displaytitle, widthpixels, heightpixels, picturetakendate, uploaddate, albumid) FROM stdin;
1	sola12_e11_mm_2000.jpg	sola12_e11_mm_2000.jpg	3184	2120	2012-05-05 16:52:23	2018-05-20 22:48:24.302	3
2	sola12_e11_mm_2002.jpg	sola12_e11_mm_2002.jpg	2120	3184	2012-05-05 17:23:38	2018-05-20 22:48:24.664	3
3	sola12_e11_mm_2003.jpg	sola12_e11_mm_2003.jpg	2120	3184	2012-05-05 17:24:33	2018-05-20 22:48:25.031	3
4	sola12_e11_mm_2004.jpg	sola12_e11_mm_2004.jpg	2120	3184	2012-05-05 17:26:14	2018-05-20 22:48:25.265	3
5	sola12_e11_mm_2005.jpg	sola12_e11_mm_2005.jpg	2120	3184	2012-05-05 17:26:41	2018-05-20 22:48:25.534	3
6	sola12_e11_mm_2006.jpg	sola12_e11_mm_2006.jpg	2120	3184	2012-05-05 17:27:14	2018-05-20 22:48:25.823	3
7	sola12_e11_mm_2007.jpg	sola12_e11_mm_2007.jpg	2120	3184	2012-05-05 17:27:38	2018-05-20 22:48:26.074	3
8	sola12_e11_mm_2008.jpg	sola12_e11_mm_2008.jpg	2120	3184	2012-05-05 17:28:04	2018-05-20 22:48:26.388	3
9	sola12_e11_mm_2009.jpg	sola12_e11_mm_2009.jpg	2120	3184	2012-05-05 17:28:18	2018-05-20 22:48:26.738	3
10	sola12_e11_mm_2010.jpg	sola12_e11_mm_2010.jpg	2120	3184	2012-05-05 17:28:22	2018-05-20 22:48:27.618	3
11	sola12_e11_mm_2011.jpg	sola12_e11_mm_2011.jpg	2120	3184	2012-05-05 17:29:04	2018-05-20 22:48:28.165	3
12	sola12_e11_mm_2012.jpg	sola12_e11_mm_2012.jpg	2120	3184	2012-05-05 17:30:01	2018-05-20 22:48:28.464	3
13	sola12_e11_mm_2013.jpg	sola12_e11_mm_2013.jpg	2120	3184	2012-05-05 17:30:12	2018-05-20 22:48:28.716	3
14	sola12_e11_mm_2014.jpg	sola12_e11_mm_2014.jpg	2120	3184	2012-05-05 17:30:42	2018-05-20 22:48:29.042	3
15	sola12_e11_mm_2015.jpg	sola12_e11_mm_2015.jpg	2120	3184	2012-05-05 17:30:47	2018-05-20 22:48:29.316	3
16	sola12_e11_mm_2016.jpg	sola12_e11_mm_2016.jpg	2120	3184	2012-05-05 17:30:50	2018-05-20 22:48:29.614	3
17	sola12_e11_mm_2017.jpg	sola12_e11_mm_2017.jpg	2120	3184	2012-05-05 17:31:16	2018-05-20 22:48:29.928	3
18	sola12_e11_mm_2018.jpg	sola12_e11_mm_2018.jpg	2120	3184	2012-05-05 17:31:34	2018-05-20 22:48:30.177	3
19	sola12_e11_mm_2019.jpg	sola12_e11_mm_2019.jpg	2120	3184	2012-05-05 17:31:38	2018-05-20 22:48:30.462	3
\.



--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: sportrait
--

COPY public.products (productid, productname, inactive, priceid, producttypeid, albumid) FROM stdin;
101	\N	\N	30	3	3
\.

--
-- Adjust sequences:
--


SELECT pg_catalog.setval('public.sequence_genericLevelId', 100, false);

SELECT pg_catalog.setval('public.sequence_photoId', 100, false);

SELECT pg_catalog.setval('public.sequence_productId', 100, false);

SELECT pg_catalog.setval('public.sequence_eventCategoryId', 100, false);
